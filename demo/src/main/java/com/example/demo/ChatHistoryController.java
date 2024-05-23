package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
class ChatHistoryController {

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private CrewRepository crewRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/chat/history/{crewId}")
    public List<ChatMessageDTO> getChatHistory(@PathVariable Long crewId) {
        Crew crew = crewRepository.findById(crewId).orElseThrow(() -> new IllegalArgumentException("Invalid crew ID"));
        List<ChatMessage> messages = chatMessageRepository.findByCrew(crew);
        return messages.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    private ChatMessageDTO convertToDto(ChatMessage chatMessage) {
        ChatMessageDTO chatMessageDTO = new ChatMessageDTO();
        chatMessageDTO.setId(chatMessage.getId());
        chatMessageDTO.setContent(chatMessage.getContent());
        chatMessageDTO.setSender(chatMessage.getSender());
        chatMessageDTO.setProfileImage(chatMessage.getProfileImage());
        chatMessageDTO.setCrewId(chatMessage.getCrew().getId());
        chatMessageDTO.setTimestamp(chatMessage.getTimestamp());

        User_info sender = userRepository.findByName(chatMessage.getSender()).orElseThrow(() -> new IllegalArgumentException("Invalid sender name"));
        chatMessageDTO.setSenderId(sender.getId());

        return chatMessageDTO;
    }
}
