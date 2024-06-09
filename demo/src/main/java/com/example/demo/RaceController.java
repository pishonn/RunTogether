package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.http.HttpStatus;

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

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/readyStatus")
    public void updateReadyStatus(Map<String, Object> update) {
        Long userId = Long.valueOf(update.get("userId").toString());
        Long roomId = Long.valueOf(update.get("roomId").toString());
        boolean ready = Boolean.valueOf(update.get("ready").toString());

        Optional<User_info> optionalUser = userRepository.findById(userId);
        Optional<Room> optionalRoom = roomRepository.findById(roomId);

        if (optionalUser.isPresent() && optionalRoom.isPresent()) {
            User_info user = optionalUser.get();
            Room room = optionalRoom.get();

            if (ready) {
                room.addParticipantsReady(user);
            } else {
                room.removeParticipantsReady(user);
            }

            roomRepository.save(room);

            // 브로드캐스트할 데이터를 구성합니다.
            Map<String, Object> response = new HashMap<>();
            response.put("userId", user.getId());
            response.put("ready", ready);

            // 모든 클라이언트에게 업데이트를 브로드캐스트합니다.
            messagingTemplate.convertAndSend("/topic/readyStatus", response);
        } else {
            System.err.println("Invalid user or room ID");
        }
    }

    @PostMapping("/toggleReadyStatus")
    public ResponseEntity<?> toggleReadyStatus(@RequestBody Map<String, Object> request) {
        Long userId = Long.valueOf(request.get("userId").toString());
        Long roomId = Long.valueOf(request.get("roomId").toString());
        boolean ready = Boolean.valueOf(request.get("ready").toString());

        Optional<User_info> optionalUser = userRepository.findById(userId);
        Optional<Room> optionalRoom = roomRepository.findById(roomId);

        if (optionalUser.isPresent() && optionalRoom.isPresent()) {
            User_info user = optionalUser.get();
            Room room = optionalRoom.get();

            if (ready) {
                room.addParticipantsReady(user);
            } else {
                room.removeParticipantsReady(user);
            }

            roomRepository.save(room);
            Map<String, Object> response = new HashMap<>();
            response.put("userId", user.getId());
            response.put("ready", ready);
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid user or room ID");
        }
    }

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
        Optional<Room> userRoom = roomRepository.roomCotainsUser(crew.getId(), user);

        model.addAttribute("userRoomId", userRoom.isPresent() ? userRoom.get().getId() : null);
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
        int maxParticipants = crew.getCapacity();
        model.addAttribute("crewId", crewId);
        model.addAttribute("userId", uid);
        model.addAttribute("maxParticipants", maxParticipants);

        
        List<ScoreHistory> scoreHistoryList = user.getScoreHistory();
        System.out.println("Score history list: " + scoreHistoryList);
        int totalPoints = scoreHistoryList.stream().mapToInt(ScoreHistory::getPoints).sum();

        System.out.println("Total points calculated: " + totalPoints);

        model.addAttribute("userData", user);
        model.addAttribute("totalPoints", totalPoints);
        return "createRoom";
    }

    @PostMapping("/roomCreate/{crewId}")
    public String createRoom(@PathVariable Long crewId, @RequestParam Long userId, @RequestParam int capacity, 
    @RequestParam String startLocation, @RequestParam String destination, @RequestParam double distance, @RequestParam String placeData, RedirectAttributes redirectAttributes, Model model) {

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
        room.setParticipantsReady(new HashSet<>());
        room.setParticipants(new ArrayList<>());
        room.setDistance(distance);
        room.setPlaceData(placeData);

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

    @GetMapping("/roomJoin/{crewId}/{roomId}")
    public String joinRoom(@PathVariable Long crewId, @PathVariable Long roomId, HttpSession session, RedirectAttributes redirectAttributes) {
        String userId = (String) session.getAttribute("userId");

        if (userId == null) {
            redirectAttributes.addFlashAttribute("message", "로그인이 필요합니다.");
            return "redirect:/login";
        }

        User_info user = userRepository.findByUserId(userId).orElse(null);
        Long uid = user.getId();

        Room room = roomRepository.findById(roomId).orElse(null);

        if (room == null) {
            redirectAttributes.addFlashAttribute("message", "방 정보가 없습니다.");
            return "redirect:/raceRoom/" + crewId;
        }

        else if (room.isRaceStarted()) {
            redirectAttributes.addFlashAttribute("message", "레이스가 시작된 방은 참가할 수 없습니다.");
        }

        else if (room.getParticipants().contains(user)) {
            redirectAttributes.addFlashAttribute("message", "이미 참가중인 방입니다.");
            return "redirect:/raceRoom/" + crewId;
        }

        else if (room.getParticipants().size() >= room.getCapacity()) {
            redirectAttributes.addFlashAttribute("message", "방이 꽉 찼습니다.");
            return "redirect:/raceRoom/" + crewId;
        }

        

        raceService.joinRoom(crewId, roomId, uid);
        redirectAttributes.addFlashAttribute("message", "방에 참가되었습니다. 대기 페이지로 이동하겠습니다!");
        return "redirect:/raceRoom/" + crewId;
    }

    @GetMapping("/roomLeave/{crewId}/{roomId}")
    public String leave(@PathVariable Long crewId, @PathVariable Long roomId, HttpSession session, RedirectAttributes redirectAttributes) {
        String userId = (String) session.getAttribute("userId");

        if (userId == null) {
            redirectAttributes.addFlashAttribute("message", "로그인이 필요합니다.");
            return "redirect:/login";
        }

        User_info user = userRepository.findByUserId(userId).orElse(null);
        Long uid = user.getId();

        Room room = roomRepository.findById(roomId).orElse(null);

        if (room == null) {
            redirectAttributes.addFlashAttribute("message", "방 정보가 없습니다.");
            return "redirect:/raceRoom/" + crewId;
        }

        else if (!room.getParticipants().contains(user)) {
            redirectAttributes.addFlashAttribute("message", "참가 중인 방이 아닙니다.");
            return "redirect:/raceRoom/" + crewId;
        }

        else if (room.isRaceStarted()) {
            redirectAttributes.addFlashAttribute("message", "레이스가 시작된 방은 나갈 수 없습니다.");
        }

        else if (room.getAdmin().equals(user)) {
            redirectAttributes.addFlashAttribute("message", "리더는 방을 나갈 수 없습니다. 삭제 버튼을 이용해주세요.");
            return "redirect:/raceRoom/" + crewId;
        }

        

        raceService.leaveRoom(crewId, roomId, uid);
        redirectAttributes.addFlashAttribute("message", "방을 나갔습니다.");
        return "redirect:/raceRoom/" + crewId;
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
