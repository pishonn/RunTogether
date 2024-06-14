package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
    private UserScoreHistoryRepository userScoreHistoryRepository;


    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Transactional
    @MessageMapping("/readyStatus")
    public void updateReadyStatus(Map<String, Object> update) {

        Long userId = Long.parseLong(update.get("userId").toString());
        Long roomId = Long.parseLong(update.get("roomId").toString());
        boolean ready = Boolean.parseBoolean(update.get("ready").toString());


        User_info user = userRepository.findById(userId).orElse(null);
        Room room = roomRepository.findByIdWithParticipantsReady(roomId).orElse(null);

        if (user != null && room != null && room.getParticipants().contains(user)) {

            if (ready) {
                room.addParticipantsReady(user);
            } else {
                room.removeParticipantsReady(user);
            }

            roomRepository.save(room);

            // 브로드캐스트할 데이터를 구성
            Map<String, Object> response = new HashMap<>();
            response.put("userId", user.getId());
            response.put("ready", ready);


            // 모든 클라이언트에게 업데이트를 브로드캐스트
            System.out.println("업데이트1 : " + response);
            messagingTemplate.convertAndSend("/topic/readyStatus/" + roomId, response);
        } else {
            System.err.println("방이 없습니다.");
        }
    }

    @PostMapping("/toggleReadyStatus")
    public ResponseEntity<?> toggleReadyStatus(@RequestBody Map<String, Object> request, HttpServletResponse httpResponse) {

        httpResponse.setHeader("Expires", "Sat, 6 May 1995 12:00:00 GMT");
        httpResponse.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        httpResponse.addHeader("Cache-Control", "post-check=0, pre-check=0");
        httpResponse.setHeader("Pragma", "no-cache");

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
            System.out.println("업데이트2 : " + response);
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid user or room ID");
        }
    }

    @GetMapping("/raceRoom/{id}")
    public String getRaceRoomPage(@PathVariable Long id, Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        String userId = (String) session.getAttribute("userId");
        System.out.println("userId : " + userId);

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
        model.addAttribute("crewId", crew.getId());
        model.addAttribute("userId", user.getId());

        return "raceRoom"; 
    }

    @GetMapping("/roomCreate/{crewId}")
    public String getCreateRoomPage(@PathVariable Long crewId, Model model, HttpSession session, RedirectAttributes redirectAttributes, HttpServletResponse httpResponse) {

        httpResponse.setHeader("Expires", "Sat, 6 May 1995 12:00:00 GMT");
        httpResponse.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        httpResponse.addHeader("Cache-Control", "post-check=0, pre-check=0");
        httpResponse.setHeader("Pragma", "no-cache");

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
        int maxParticipants = crew.getMemberCount();
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
    @RequestParam String startLocation, @RequestParam String destination, @RequestParam double distance, @RequestParam String placeData, RedirectAttributes redirectAttributes, Model model, HttpServletResponse httpResponse) {

        httpResponse.setHeader("Expires", "Sat, 6 May 1995 12:00:00 GMT");
        httpResponse.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        httpResponse.addHeader("Cache-Control", "post-check=0, pre-check=0");
        httpResponse.setHeader("Pragma", "no-cache");

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

        messagingTemplate.convertAndSend("/topic/roomUpdate/" + crewId, "Room created");

        redirectAttributes.addFlashAttribute("message", "방이 생성되었습니다. 대기 페이지로 이동하겠습니다!");
        return "redirect:/userDetails";
    }

    @GetMapping("/roomDelete/{crewId}/{roomId}")
    public String deleteRoom(@PathVariable Long crewId, @PathVariable Long roomId, HttpSession session, RedirectAttributes redirectAttributes, HttpServletResponse httpResponse) {

        httpResponse.setHeader("Expires", "Sat, 6 May 1995 12:00:00 GMT");
        httpResponse.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        httpResponse.addHeader("Cache-Control", "post-check=0, pre-check=0");
        httpResponse.setHeader("Pragma", "no-cache");

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

        else if (room.isRaceStarted()) {
            redirectAttributes.addFlashAttribute("message", "레이스가 시작된 방은 삭제할 수 없습니다.");
            return "redirect:/raceRoom/" + crewId;
        }

        roomRepository.unsetRoomForUsers(roomId);
        raceService.deleteRoom(roomId, crewId);

        //messagingTemplate.convertAndSend("/topic/roomUpdate/" + crewId, "Room deleted");

        redirectAttributes.addFlashAttribute("message", "방이 삭제되었습니다.");
        return "redirect:/raceRoom/" + crewId;
    }

    // @MessageMapping("/participantUpdate")
    // public void participantUpdate(Map<String, Object> update) {
    //     Long roomId = Long.parseLong(update.get("roomId").toString());
    //     Long crewId = Long.parseLong(update.get("crewId").toString());
    //     messagingTemplate.convertAndSend("/topic/participantUpdate" + roomId, update);
    //     messagingTemplate.convertAndSend("/topic/participantUpdate" + crewId, update);
    // }

    @GetMapping("/roomJoin/{crewId}/{roomId}")
    public String joinRoom(@PathVariable Long crewId, @PathVariable Long roomId, HttpSession session, RedirectAttributes redirectAttributes, HttpServletResponse httpResponse) {

        httpResponse.setHeader("Expires", "Sat, 6 May 1995 12:00:00 GMT");
        httpResponse.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        httpResponse.addHeader("Cache-Control", "post-check=0, pre-check=0");
        httpResponse.setHeader("Pragma", "no-cache");

        String userId = (String) session.getAttribute("userId");
        Crew crew = crewRepository.findById(crewId).orElse(null);
        if (userId == null) {
            redirectAttributes.addFlashAttribute("message", "로그인이 필요합니다.");
            return "redirect:/login";
        }

        User_info user = userRepository.findByUserId(userId).orElse(null);
        Long uid = user.getId();

        Room room = roomRepository.findById(roomId).orElse(null);
        Room existedRoom = roomRepository.roomCotainsUser(crewId, user).orElse(null);

        if (!crew.isMember(user))
        {
            redirectAttributes.addFlashAttribute("message", "크루 멤버만 접근 가능합니다.");
            return "redirect:/mainMenu";
        }

        if (existedRoom != null) {
            redirectAttributes.addFlashAttribute("message", "이미 참가중인 방이 있습니다.");
            return "redirect:/raceRoom/" + crewId;
        }

        if (room == null) {
            redirectAttributes.addFlashAttribute("message", "방 정보가 없습니다.");
            return "redirect:/raceRoom/" + crewId;
        }

        else if (room.isRaceStarted()) {
            redirectAttributes.addFlashAttribute("message", "레이스가 시작된 방은 참가할 수 없습니다.");
            return "redirect:/raceRoom/" + crewId;
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
        Map<String, Object> response = new HashMap<>();
        response.put("userId", user.getId());
        response.put("profileImage", user.getProfileImage());
        response.put("isReady", room.getParticipantsReady().contains(user));
        response.put("name", user.getName());
        response.put("join", true);  // 참가 여부
        messagingTemplate.convertAndSend("/topic/participantUpdate/" + roomId, response);
        messagingTemplate.convertAndSend("/topic/participantUpdate/" + crewId, response);


        redirectAttributes.addFlashAttribute("message", "방에 참가되었습니다. 대기 페이지로 이동하겠습니다!");
        System.out.println("방 참가 성공");
        return "redirect:/userDetails";
    }

    @GetMapping("/roomLeave/{crewId}/{roomId}")
    public String leave(@PathVariable Long crewId, @PathVariable Long roomId, HttpSession session, RedirectAttributes redirectAttributes, HttpServletResponse httpResponse) {

        httpResponse.setHeader("Expires", "Sat, 6 May 1995 12:00:00 GMT");
        httpResponse.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        httpResponse.addHeader("Cache-Control", "post-check=0, pre-check=0");
        httpResponse.setHeader("Pragma", "no-cache");

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

        

        Room updatedRoom = raceService.leaveRoom(crewId, roomId, uid);
        if (updatedRoom == null) {
            redirectAttributes.addFlashAttribute("message", "마지막 참가자가 방을 떠나 방이 삭제되었습니다.");
        } else {
            redirectAttributes.addFlashAttribute("message", "방을 나갔습니다.");
        }

      
        // Map<String, Object> message = new HashMap<>();
        // message.put("userId", user.getId());
        // message.put("join", false);

        // messagingTemplate.convertAndSend("/topic/participantUpdate/" + roomId, message);
        // messagingTemplate.convertAndSend("/topic/participantUpdate/" + crewId, message);
        

        return "redirect:/raceRoom/" + crewId;
    }

    @MessageMapping("/startRace")
    public void startRace(Map<String, Object> message) {
        // 모든 참가자에게 메시지 전송
        Long roomId = Long.parseLong(message.get("roomId").toString());
        Long crewId = Long.parseLong(message.get("crewId").toString());
        messagingTemplate.convertAndSend("/topic/startRace/" + roomId, message);
        messagingTemplate.convertAndSend("/topic/startRace/" + crewId, message);

    }

    @PostMapping("/startRace")
    public ResponseEntity<?> startRace2(@RequestBody Map<String, Object> request, HttpServletResponse httpResponse) {

        httpResponse.setHeader("Expires", "Sat, 6 May 1995 12:00:00 GMT");
        httpResponse.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        httpResponse.addHeader("Cache-Control", "post-check=0, pre-check=0");
        httpResponse.setHeader("Pragma", "no-cache");

        Long roomId = Long.valueOf(request.get("roomId").toString());
        Optional<Room> optionalRoom = roomRepository.findById(roomId);
        if (optionalRoom.isPresent()) {
            Room room = optionalRoom.get();
            room.setRaceStarted(true);
            room.setStartTime(LocalDateTime.now());
            room.setParticipantsAtRaceStart(room.getParticipantsCount());
            roomRepository.save(room);

            // 반환되는 JSON 응답을 간단하게 유지
            Map<String, Object> response = new HashMap<>();
            response.put("roomId", roomId);
            response.put("isRaceStarted", true);
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Room not found");
        }
    }

    

    @GetMapping("/raceState/{roomId}")
    public ResponseEntity<?> getRaceState(@PathVariable Long roomId) {
        Optional<Room> optionalRoom = roomRepository.findById(roomId);
        if (optionalRoom.isPresent()) {
            Room room = optionalRoom.get();
            Map<String, Object> response = new HashMap<>();
            response.put("isRaceStarted", room.isRaceStarted());
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Race state not found");
        }
    }


    @GetMapping("/raceResult")
    public String getRaceResults(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        String uid = (String) session.getAttribute("userId");
        if (uid == null) {
            redirectAttributes.addFlashAttribute("message", "로그인이 필요합니다.");
            return "redirect:/login";
        }

        User_info user = userRepository.findByUserId(uid).orElse(null);
        if (user == null) {
            redirectAttributes.addFlashAttribute("message", "유저 정보를 찾을 수 없습니다.");
            return "redirect:/login";
        }

        Crew crew = user.getCrew();
        if (crew == null) {
            model.addAttribute("message", "크루 정보를 찾을 수 없습니다.");
            return "mainMenu";
        }

        if (!crew.isMember(user)) {
            redirectAttributes.addFlashAttribute("message", "크루 멤버만 접근 가능합니다.");
            return "redirect:/mainMenu";
        }

        // 유저가 참여한 모든 RoomScoreHistory 찾기
        Set<UserScoreHistory> userScores = userScoreHistoryRepository.findByUser(user);
        Set<RoomScoreHistory> participatedRooms = userScores.stream()
                                                            .map(UserScoreHistory::getRoomScoreHistory)
                                                            .collect(Collectors.toSet());

        System.out.println("Participated rooms: " + participatedRooms);

        Map<Long, List<UserScoreHistory>> groupedResults = new LinkedHashMap<>();
        List<RoomScoreHistory> sortedRooms = new ArrayList<>(participatedRooms);
        sortedRooms.sort(Comparator.comparing(RoomScoreHistory::getCreatedDate).reversed()); // 내림차순 정렬

        for (RoomScoreHistory room : sortedRooms) {
            Set<UserScoreHistory> historiesSet = userScoreHistoryRepository.findByRoomScoreHistory(room);
            List<UserScoreHistory> histories = new ArrayList<>(historiesSet);

            for (UserScoreHistory history : histories) {
                Duration duration = Duration.between(room.getStartTime(), history.getRaceEndTime());
                long minutes = duration.toMinutes();
                long seconds = duration.minusMinutes(minutes).getSeconds();
                history.setDuration(String.format("%d분 %d초", minutes, seconds));
            }

            histories.sort(Comparator.comparing(UserScoreHistory::getRaceEndTime)); // 내림차순 정렬
            groupedResults.put(room.getId(), histories);
        }
    

        System.out.println("Grouped results: " + groupedResults);
        model.addAttribute("raceResults", groupedResults);
        model.addAttribute("crew", crew);
        model.addAttribute("userId", user.getId());
        
        return "raceResult";
    }




}
