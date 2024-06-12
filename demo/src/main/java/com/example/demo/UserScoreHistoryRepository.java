package com.example.demo;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.Set;

public interface UserScoreHistoryRepository extends JpaRepository<UserScoreHistory, Long> {
    Set<UserScoreHistory> findByRoomScoreHistoryOrderByRaceEndTimeAsc(RoomScoreHistory roomScoreHistory);
    Optional<UserScoreHistory> findByRoomScoreHistoryAndUser(RoomScoreHistory roomScoreHistory, User_info user);
    Set<UserScoreHistory> findByUser_IdAndCrew_Id(Long userId, Long crewId); 
    Set<UserScoreHistory> findByUserAndCrew(User_info userId, Crew crewId);
    Set<UserScoreHistory> findByRoomScoreHistory(RoomScoreHistory roomScoreHistory);
    
}
