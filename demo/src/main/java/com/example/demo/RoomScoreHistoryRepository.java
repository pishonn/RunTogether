package com.example.demo;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;


public interface RoomScoreHistoryRepository extends JpaRepository<RoomScoreHistory, Long> {
    Optional<RoomScoreHistory> findByRoomId(Long roomId);
    List<RoomScoreHistory> findByCrew(Crew crew);
}
