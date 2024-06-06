package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/raceRoom")
public class RaceController {

    @Autowired
    private RaceService raceService;

    @GetMapping("/{id}")
    public String getRaceRoomPage(@PathVariable Long id, Model model) {
        List<Room> rooms = raceService.getRoomsByCrew(id);
        model.addAttribute("crewId", id);
        model.addAttribute("rooms", rooms);

        return "raceRoom"; 
    }

    @GetMapping("/create/{crewId}")
    public String getCreateRoomPage(@PathVariable Long crewId, Model model) {
        model.addAttribute("crewId", crewId);
        return "createRoom";
    }

    @DeleteMapping("/delete/{crewId}/{roomId}")
    public ResponseEntity<Void> deleteRoom(@PathVariable Long crewId, @PathVariable Long roomId) {
        raceService.deleteRoom(crewId, roomId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/join/{crewId}/{roomId}")
    public ResponseEntity<Room> joinRoom(@PathVariable Long crewId, @PathVariable Long roomId, @RequestParam Long userId) {
        Room room = raceService.joinRoom(crewId, roomId, userId);
        return ResponseEntity.ok(room);
    }

    @PostMapping("/ready/{crewId}/{roomId}")
    public ResponseEntity<Room> ready(@PathVariable Long crewId, @PathVariable Long roomId, @RequestParam Long userId) {
        Room room = raceService.ready(crewId, roomId, userId);
        return ResponseEntity.ok(room);
    }

    @PostMapping("/start/{crewId}/{roomId}")
    public ResponseEntity<Room> startRace(@PathVariable Long crewId, @PathVariable Long roomId) {
        Room room = raceService.startRace(crewId, roomId);
        return ResponseEntity.ok(room);
    }

    @PostMapping("/finish/{crewId}/{roomId}")
    public ResponseEntity<Room> finishRace(@PathVariable Long crewId, @PathVariable Long roomId) {
        Room room = raceService.finishRace(crewId, roomId);
        return ResponseEntity.ok(room);
    }

    @GetMapping("/room/{crewId}/{roomId}")
    public ResponseEntity<Room> getRoom(@PathVariable Long crewId, @PathVariable Long roomId) {
        Room room = raceService.getRoom(crewId, roomId);
        return ResponseEntity.ok(room);
    }
}
