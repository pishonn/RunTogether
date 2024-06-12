package com.example.demo;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RoomScoreHistoryRepository extends JpaRepository<RoomScoreHistory, Long> {
    Optional<RoomScoreHistory> findByRoom(Room room);
    Optional<RoomScoreHistory> findByRoomId(Long roomId);
}
