package com.example.demo;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.transaction.Transactional;

import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@Controller
public class ChatController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private CrewRepository crewRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private ScheduledTasks scheduledTasks;

    private final Map<Long, Set<Long>> activeUsersByCrew = new ConcurrentHashMap<>();

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    @Transactional
    public ChatMessageDTO sendMessage(@Payload ChatMessageDTO chatMessageDTO, SimpMessageHeaderAccessor headerAccessor) {
        Crew crew = crewRepository.findById(chatMessageDTO.getCrewId()).orElseThrow(() -> new IllegalArgumentException("Invalid crew ID"));

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setContent(chatMessageDTO.getContent());
        chatMessage.setSender(chatMessageDTO.getSender());
        chatMessage.setProfileImage(chatMessageDTO.getProfileImage());
        chatMessage.setSenderId(chatMessageDTO.getSenderId());
        chatMessage.setUnreadCount(chatMessageDTO.getUnreadCount());
        chatMessage.setCrew(crew);

        User_info sender = userRepository.findById(chatMessageDTO.getSenderId()).orElseThrow(() -> new IllegalArgumentException("Invalid sender ID"));
        chatMessage.getReadBy().add(sender);

        chatMessage = chatMessageRepository.save(chatMessage);

        // 현재 채팅방에 있는 사용자에게 실시간으로 상태 업데이트
        Set<Long> activeUsers = activeUsersByCrew.get(crew.getId());
        if (activeUsers != null) {
            for (Long userId : activeUsers) {
                if (!userId.equals(chatMessageDTO.getSenderId())) {
                    messagingTemplate.convertAndSend("/topic/chat/" + crew.getId(),
                        Map.of("messageId", chatMessage.getId(), "unreadCount", chatMessage.getUnreadCount()));
                }
            }
        }

        chatMessageDTO.setId(chatMessage.getId());
        chatMessageDTO.setTimestamp(chatMessage.getTimestamp()); // 저장된 메시지의 타임스탬프 설정
        chatMessageDTO.setUnreadCount(chatMessage.getUnreadCount()); // unreadCount 설정

        return chatMessageDTO;
    }


    public void sendChatGptMessage(Long crewId, String content) {
        Crew crew = crewRepository.findById(crewId).orElseThrow(() -> new IllegalArgumentException("Invalid crew ID"));

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setContent(content);
        chatMessage.setSender("ChatGPT"); 
        chatMessage.setProfileImage("/img/chatgpt-profile.jpg"); 
        chatMessage.setCrew(crew);

        chatMessage = chatMessageRepository.save(chatMessage);

        ChatMessageDTO chatMessageDTO = new ChatMessageDTO();
        chatMessageDTO.setId(chatMessage.getId());
        chatMessageDTO.setContent(chatMessage.getContent());
        chatMessageDTO.setSender(chatMessage.getSender());
        chatMessageDTO.setProfileImage(chatMessage.getProfileImage());
        chatMessageDTO.setCrewId(chatMessage.getCrew().getId());
        chatMessageDTO.setTimestamp(chatMessage.getTimestamp());
        chatMessageDTO.setSenderId(0L); // 시스템 사용자 ID 설정

        messagingTemplate.convertAndSend("/topic/public", chatMessageDTO);
    }
    
    @PostMapping("/chat/trigger-gpt")
    public void triggerChatGpt(@RequestParam Long crewId) {
        scheduledTasks.sendRunningDestinationRecommendations(crewId);
    }

    @PostMapping("/chat/trigger-gpt2")
    public void triggerChatGpt2(@RequestParam Long crewId, @RequestParam String prompt) {
        scheduledTasks.sendInquiry(crewId, prompt);
    }



    @GetMapping("/unreadChatsCount")
    public ResponseEntity<Map<String, Object>> getUnreadChatsCount(@RequestParam Long crewId, @RequestParam Long userId) {
        User_info user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));
        long unreadCount = chatMessageRepository.countUnreadMessages(crewId, user, userId);
        System.out.println("unreadCount : " + unreadCount);
        Map<String, Object> response = new HashMap<>();
        response.put("unreadCount", unreadCount);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @PostMapping("/enterChatRoom")
    public ResponseEntity<?> enterChatRoom(@RequestParam Long crewId, @RequestParam Long userId) {
        activeUsersByCrew.computeIfAbsent(crewId, k -> Collections.synchronizedSet(new HashSet<>())).add(userId);
        updateUnreadChatsCount(crewId, userId); // 입장할 때 읽지 않은 메시지 수 업데이트
        messagingTemplate.convertAndSend("/topic/chat/" + crewId, 
            Map.of("type", "userEntered", "userId", userId));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/leaveChatRoom")
    public ResponseEntity<?> leaveChatRoom(@RequestParam Long crewId, @RequestParam Long userId) {
        Set<Long> activeUsers = activeUsersByCrew.get(crewId);
        if (activeUsers != null) {
            activeUsers.remove(userId);
            if (activeUsers.isEmpty()) {
                activeUsersByCrew.remove(crewId);
            }
        }
        updateUnreadChatsCount(crewId, userId); // 퇴장할 때 읽지 않은 메시지 수 업데이트
        messagingTemplate.convertAndSend("/topic/chat/" + crewId, 
            Map.of("type", "userLeft", "userId", userId));
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @PostMapping("/toggleChatRoom")
    public ResponseEntity<?> toggleChatRoom(@RequestParam Long crewId, @RequestParam Long userId) {
        User_info user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));
        List<ChatMessage> unreadMessages = chatMessageRepository.findUnreadMessagesByCrewAndUser(crewId, user);

        for (ChatMessage message : unreadMessages) {
            message.getReadBy().add(user);
        }
        chatMessageRepository.saveAll(unreadMessages);

        // Send the updated read status to the WebSocket subscribers
        for (ChatMessage message : unreadMessages) {
            int unreadCount = message.getUnreadCount();
            messagingTemplate.convertAndSend("/topic/chat/" + crewId, 
                Map.of("messageId", message.getId(), "unreadCount", unreadCount));
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/readAllMessages")
    public ResponseEntity<?> readAllMessages(@RequestParam Long crewId, @RequestParam Long userId) {
        User_info user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));
        List<ChatMessage> unreadMessages = chatMessageRepository.findUnreadMessagesByCrewAndUser(crewId, user);

        for (ChatMessage message : unreadMessages) {
            message.getReadBy().add(user);
        }
        chatMessageRepository.saveAll(unreadMessages);

        List<Map<String, Object>> updatedMessages = unreadMessages.stream().map(message -> {
            Map<String, Object> messageData = new HashMap<>();
            messageData.put("id", message.getId());
            messageData.put("unreadCount", message.getUnreadCount());
            messageData.put("readByCount", message.getReadBy().size());
            return messageData;
        }).collect(Collectors.toList());

        // Send the updated read status to the WebSocket subscribers
        messagingTemplate.convertAndSend("/topic/chat/" + crewId, 
            Map.of("type", "readAll", "userId", userId, "messages", updatedMessages));

        return new ResponseEntity<>(HttpStatus.OK);
    }



    private void updateUnreadChatsCount(Long crewId, Long userId) {
        User_info user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));
        long unreadCount = chatMessageRepository.countUnreadMessages(crewId, user, userId);
        messagingTemplate.convertAndSend("/topic/chat/" + crewId,
                Map.of("type", "unreadCount", "userId", userId, "unreadCount", unreadCount));
    }
}
