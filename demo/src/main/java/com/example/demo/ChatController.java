package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;

@Controller
public class ChatController {

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private CrewRepository crewRepository;

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
        return chatMessageDTO;
    }
}
