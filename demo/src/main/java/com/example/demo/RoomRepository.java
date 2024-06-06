package com.example.demo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findByCrew(Crew crew);
    Optional<Room> findByIdAndCrew(Long id, Crew crew);
    @Query("SELECT COUNT(r) FROM Room r WHERE r.crew.id = :crewId")
    long roomCountByCrewId(@Param("crewId") Long crewId);

    @Query("SELECT r FROM Room r WHERE r.crew.id = :crewId AND :user MEMBER OF r.participants")
    Optional<Room> roomCotainsUser(@Param("crewId") Long crewId, @Param("user") User_info user);

    @Transactional
    @Modifying
    @Query("UPDATE User_info u SET u.room = null WHERE u.room.id = :roomId")
    void unsetRoomForUsers(@Param("roomId") Long roomId);

    @Query("SELECT MAX(r.id) FROM Room r WHERE r.crew.id = :crewId")
    Long findHighestRoomIdInCrew(@Param("crewId") Long crewId);
}
