package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class RaceService {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CrewRepository crewRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;


    public Room createRoom(Long adminId, Long crewId, int capacity, String startLocation, String destination) {
        User_info admin = userRepository.findById(adminId).orElseThrow(() -> new IllegalArgumentException("Invalid admin Id"));
        Crew crew = crewRepository.findById(crewId).orElseThrow(() -> new IllegalArgumentException("Invalid crew Id"));
        
        Room room = new Room();
        room.setAdmin(admin);
        room.setCrew(crew);
        room.setCreatedDate(LocalDateTime.now());
        room.setCapacity(capacity);
        room.setStartLocation(startLocation);
        room.setDestination(destination);
        room.setRaceStarted(false);
        room.setParticipantsReady(new HashSet<>());
        room.setParticipants(new ArrayList<>());

        room.getParticipants().add(admin);
        admin.setRoom(room); 
        return roomRepository.save(room);
    }

    @Transactional
    public void deleteRoom(Long roomId, Long crewId) {
        Optional<Room> optionalRoom = roomRepository.findById(roomId);
        if (optionalRoom.isPresent()) {
            Room room = optionalRoom.get();
            roomRepository.delete(room);
            messagingTemplate.convertAndSend("/topic/roomUpdate/" + crewId, "Room deleted");
        }
    }


    public Room joinRoom(Long crewId, Long roomId, Long userId) {
        Crew crew = crewRepository.findById(crewId).orElseThrow(() -> new IllegalArgumentException("Invalid crew Id"));
        Room room = roomRepository.findByIdAndCrew(roomId, crew).orElseThrow(() -> new IllegalArgumentException("Invalid room Id or crew Id"));
        User_info user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("Invalid user Id"));

        if (room.getParticipants().size() >= room.getCapacity()) {
            throw new IllegalStateException("Room capacity exceeded");
        }

        room.getParticipants().add(user);
        user.setRoom(room);
        userRepository.save(user);

        return roomRepository.save(room);
    }

    public Room leaveRoom(Long crewId, Long roomId, Long userId) {
        Crew crew = crewRepository.findById(crewId).orElseThrow(() -> new IllegalArgumentException("Invalid crew Id"));
        Room room = roomRepository.findByIdAndCrew(roomId, crew).orElseThrow(() -> new IllegalArgumentException("Invalid room Id or crew Id"));
        User_info user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("Invalid user Id"));
    
        if (!room.getParticipants().contains(user)) {
            throw new IllegalStateException("User is not a participant of this room");
        }
    
        room.getParticipants().remove(user);
        user.setRoom(null);
        userRepository.save(user);

        Map<String, Object> message = new HashMap<>();
        message.put("userId", userId);
        message.put("join", false);
        
        messagingTemplate.convertAndSend("/topic/participantUpdate/" + roomId, message);
        messagingTemplate.convertAndSend("/topic/participantUpdate/" + crewId, message);

        if (room.getParticipants().isEmpty()) {
            deleteRoom(roomId, crewId);
            return null; // Room was deleted
        }
    
        return roomRepository.save(room);
    }
    
    
    @Transactional
    public Room ready(Long crewId, Long roomId, Long userId) {
        Crew crew = crewRepository.findById(crewId).orElseThrow(() -> new IllegalArgumentException("Invalid crew Id"));
        Room room = roomRepository.findByIdAndCrew(roomId, crew).orElseThrow(() -> new IllegalArgumentException("Invalid room Id or crew Id"));
        User_info user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("Invalid user Id"));
        if (!room.getParticipantsReady().contains(user)) {
            room.addParticipantsReady(null);
        }

        if (room.getParticipantsReady().size() == room.getParticipants().size()) {
            room.setRaceStarted(true);
        }

        return room;
    }

    @Transactional
    public Room startRace(Long crewId, Long roomId) {
        Crew crew = crewRepository.findById(crewId).orElseThrow(() -> new IllegalArgumentException("Invalid crew Id"));
        Room room = roomRepository.findByIdAndCrew(roomId, crew).orElseThrow(() -> new IllegalArgumentException("Invalid room Id or crew Id"));
        room.setRaceStarted(true);
        return room;
    }

    @Transactional
    public Room finishRace(Long crewId, Long roomId) {
        Crew crew = crewRepository.findById(crewId).orElseThrow(() -> new IllegalArgumentException("Invalid crew Id"));
        Room room = roomRepository.findByIdAndCrew(roomId, crew).orElseThrow(() -> new IllegalArgumentException("Invalid room Id or crew Id"));
        room.setRaceStarted(false);
        room.getParticipantsReady().clear();
        return room;
    }

    public Room getRoom(Long crewId, Long roomId) {
        Crew crew = crewRepository.findById(crewId).orElseThrow(() -> new IllegalArgumentException("Invalid crew Id"));
        return roomRepository.findByIdAndCrew(roomId, crew).orElseThrow(() -> new IllegalArgumentException("Invalid room Id or crew Id"));
    }

    public List<Room> getRoomsByCrew(Long crewId) {
        Crew crew = crewRepository.findById(crewId).orElseThrow(() -> new IllegalArgumentException("Invalid crew Id"));
        return roomRepository.findByCrew(crew);
    }
}
