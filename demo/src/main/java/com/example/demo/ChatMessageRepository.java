package com.example.demo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByCrew(Crew crew);
    
    @Query("SELECT COUNT(m) FROM ChatMessage m WHERE m.crew.id = :crewId AND :user NOT MEMBER OF m.readBy AND m.senderId != :userId")
    long countUnreadMessages(@Param("crewId") Long crewId, @Param("user") User_info user, @Param("userId") Long userId);

    @Query("SELECT m FROM ChatMessage m WHERE m.crew.id = :crewId AND :user NOT MEMBER OF m.readBy")
    List<ChatMessage> findUnreadMessagesByCrewAndUser(@Param("crewId") Long crewId, @Param("user") User_info user);


    

}
