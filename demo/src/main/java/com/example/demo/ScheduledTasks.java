package com.example.demo;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTasks {

    @Autowired
    private ChatGptService chatGptService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private CrewRepository crewRepository;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    private static final String ASSISTANT_ID = "asst_OR2QKy2erOH8CEXIK97wOeiP"; // 실제 어시스턴트 ID로 교체

    // @Scheduled(fixedRate = 3600000) // 1시간마다 실행
    public void sendRunningDestinationRecommendations(Long crewId) {

        Crew crew = crewRepository.findById(crewId).orElseThrow(() -> new IllegalArgumentException("Invalid crew ID"));
        String region = crew.getRegion();
        String prompt = "주기적응답" + region;
        String messageContent = chatGptService.getAssistantResponse(ASSISTANT_ID, prompt);

        // ChatMessage 엔티티 생성 및 저장
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setContent(messageContent);
        chatMessage.setSender("ChatGPT");
        chatMessage.setProfileImage("/img/chatgpt.png");
        chatMessage.setCrew(crew);
        chatMessage.setTimestamp(LocalDateTime.now());

        chatMessage = chatMessageRepository.save(chatMessage);

        // ChatMessageDTO 생성 및 설정
        ChatMessageDTO chatMessageDTO = new ChatMessageDTO();
        chatMessageDTO.setId(chatMessage.getId());
        chatMessageDTO.setContent(chatMessage.getContent());
        chatMessageDTO.setSender(chatMessage.getSender());
        chatMessageDTO.setProfileImage(chatMessage.getProfileImage());
        chatMessageDTO.setCrewId(chatMessage.getCrew().getId());
        chatMessageDTO.setTimestamp(chatMessage.getTimestamp());

        // 메시지 전송
        messagingTemplate.convertAndSend("/topic/public", chatMessageDTO);

    }
}
