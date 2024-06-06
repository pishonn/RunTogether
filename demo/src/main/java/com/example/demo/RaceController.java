package com.example.demo;

import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
public class RaceController {

    @Autowired
    private RaceService raceService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CrewRepository crewRepository;

    @Autowired
    private RoomRepository roomRepository;

    @GetMapping("/raceRoom/{id}")
    public String getRaceRoomPage(@PathVariable Long id, Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        String userId = (String) session.getAttribute("userId");
        System.out.println("userId dasdasdas: " + userId);

        if (userId == null) {
            redirectAttributes.addFlashAttribute("message", "로그인이 필요합니다.");
            return "redirect:/login";
        }

        User_info user = userRepository.findByUserId(userId).orElse(null);
        Crew crew = crewRepository.findById(id).orElse(null);
        if (!crew.isMember(user)) {
            redirectAttributes.addFlashAttribute("message", "크루 멤버만 접근 가능합니다.");
            return "redirect:/mainMenu";
        }
        
        List<Room> rooms = raceService.getRoomsByCrew(id);
        model.addAttribute("crew", crew);
        model.addAttribute("rooms", rooms);

        return "raceRoom"; 
    }

    @GetMapping("/roomCreate/{crewId}")
    public String getCreateRoomPage(@PathVariable Long crewId, Model model, HttpSession session, RedirectAttributes redirectAttributes) {

        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            redirectAttributes.addFlashAttribute("message", "로그인이 필요합니다.");
            return "redirect:/login";
        }

        User_info user = userRepository.findByUserId(userId).orElse(null);
        Crew crew = crewRepository.findById(crewId).orElse(null);
        if (!crew.isMember(user)) {
            redirectAttributes.addFlashAttribute("message", "크루 멤버만 접근 가능합니다.");
            return "redirect:/mainMenu";
        }

        Long uid = user.getId();
        model.addAttribute("crewId", crewId);
        model.addAttribute("userId", uid);
        return "createRoom";
    }

    @PostMapping("/roomCreate/{crewId}")
    public String createRoom(@PathVariable Long crewId, @RequestParam Long userId, @RequestParam int capacity, 
    @RequestParam String startLocation, @RequestParam String destination, RedirectAttributes redirectAttributes) {

        User_info admin = userRepository.findById(userId).orElse(null);
        Crew crew = crewRepository.findById(crewId).orElse(null);

        if (admin == null) {
            redirectAttributes.addFlashAttribute("message", "유저 정보가 없습니다.");
            return "redirect:/login";
        }

        if (crew == null) {
            redirectAttributes.addFlashAttribute("message", "크루 정보가 없습니다.");
            return "redirect:/mainMenu";
        }

        if (!crew.isMember(admin)) {
            redirectAttributes.addFlashAttribute("message", "크루 멤버만 접근 가능합니다.");
            return "redirect:/mainMenu";
        }
        
     
        System.out.println("Admin Room: " + (admin.getRoom() != null ? admin.getRoom().getId() : "null"));

        Room existedRoom = roomRepository.roomCotainsUser(crewId, admin).orElse(null);
        System.out.println("Existed Room: " + (existedRoom != null ? existedRoom.getId() : "null"));
        
        if (admin.getRoom() != null || existedRoom != null) {
            redirectAttributes.addFlashAttribute("message", "이미 참가중인 방이 있습니다.");
            return "redirect:/raceRoom/" + crewId;
        }

        Room room = new Room();
        room.setAdmin(admin);
        room.setCrew(crew);
        room.setCreatedDate(LocalDateTime.now());
        room.setCapacity(capacity);
        room.setStartLocation(startLocation);
        room.setDestination(destination);
        room.setRaceStarted(false);
        room.setParticipantsReady(new ArrayList<>());
        room.setParticipants(new ArrayList<>());

        room.getParticipants().add(admin);
        admin.setRoom(room); 
        roomRepository.save(room);

        redirectAttributes.addFlashAttribute("message", "방이 생성되었습니다.");
        return "redirect:/raceRoom/" + crewId;
    }

    @GetMapping("/roomDelete/{crewId}/{roomId}")
    public String deleteRoom(@PathVariable Long crewId, @PathVariable Long roomId, HttpSession session, RedirectAttributes redirectAttributes) {
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            redirectAttributes.addFlashAttribute("message", "로그인이 필요합니다.");
            return "redirect:/login";
        }

        User_info user = userRepository.findByUserId(userId).orElse(null);
        Crew crew = crewRepository.findById(crewId).orElse(null);
        if (!crew.isMember(user)) {
            redirectAttributes.addFlashAttribute("message", "크루 멤버만 접근 가능합니다.");
            return "redirect:/mainMenu";
        }

        Room room = roomRepository.findById(roomId).orElse(null);
        if (!user.equals(room.getAdmin())) {
            redirectAttributes.addFlashAttribute("message", "삭제 권한이 없습니다.");
            return "redirect:/raceRoom/" + crewId;
        }

        roomRepository.unsetRoomForUsers(roomId);
        roomRepository.deleteById(roomId);
        redirectAttributes.addFlashAttribute("message", "방이 삭제되었습니다.");
        return "redirect:/raceRoom/" + crewId;
    }

    @PostMapping("/roomJoin/{crewId}/{roomId}")
    public ResponseEntity<Room> joinRoom(@PathVariable Long crewId, @PathVariable Long roomId, @RequestParam Long userId) {
        Room room = raceService.joinRoom(crewId, roomId, userId);
        return ResponseEntity.ok(room);
    }

    @PostMapping("/raceReady/{crewId}/{roomId}")
    public ResponseEntity<Room> ready(@PathVariable Long crewId, @PathVariable Long roomId, @RequestParam Long userId) {
        Room room = raceService.ready(crewId, roomId, userId);
        return ResponseEntity.ok(room);
    }

    @PostMapping("/raceStart/{crewId}/{roomId}")
    public ResponseEntity<Room> startRace(@PathVariable Long crewId, @PathVariable Long roomId) {
        Room room = raceService.startRace(crewId, roomId);
        return ResponseEntity.ok(room);
    }

    @PostMapping("/raceFinish/{crewId}/{roomId}")
    public ResponseEntity<Room> finishRace(@PathVariable Long crewId, @PathVariable Long roomId) {
        Room room = raceService.finishRace(crewId, roomId);
        return ResponseEntity.ok(room);
    }

}
