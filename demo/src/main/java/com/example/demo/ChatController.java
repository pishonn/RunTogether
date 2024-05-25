package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@Controller
public class ChatController {

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private CrewRepository crewRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private ScheduledTasks scheduledTasks;

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessageDTO sendMessage(@Payload ChatMessageDTO chatMessageDTO, SimpMessageHeaderAccessor headerAccessor) {
        Crew crew = crewRepository.findById(chatMessageDTO.getCrewId()).orElseThrow(() -> new IllegalArgumentException("Invalid crew ID"));

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setContent(chatMessageDTO.getContent());
        chatMessage.setSender(chatMessageDTO.getSender());
        chatMessage.setProfileImage(chatMessageDTO.getProfileImage());
        chatMessage.setCrew(crew);

        chatMessage = chatMessageRepository.save(chatMessage);

        chatMessageDTO.setId(chatMessage.getId());
        chatMessageDTO.setTimestamp(chatMessage.getTimestamp()); // 저장된 메시지의 타임스탬프 설정
        chatMessageDTO.setSenderId(chatMessageDTO.getSenderId()); // senderId 설정

        return chatMessageDTO;
    }

    // 메서드: ChatGPT 메시지를 전송하는 메서드 추가
    public void sendChatGptMessage(Long crewId, String content) {
        Crew crew = crewRepository.findById(crewId).orElseThrow(() -> new IllegalArgumentException("Invalid crew ID"));

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setContent(content);
        chatMessage.setSender("ChatGPT"); // ChatGPT로 설정
        chatMessage.setProfileImage("/img/chatgpt-profile.jpg"); // ChatGPT의 기본 프로필 이미지 설정
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
    
    // 특정 크루에 대한 ChatGPT 요청을 트리거하는 엔드포인트
    @PostMapping("/chat/trigger-gpt")
    public void triggerChatGpt(@RequestParam Long crewId) {
        scheduledTasks.sendRunningDestinationRecommendations(crewId);
    }
}
