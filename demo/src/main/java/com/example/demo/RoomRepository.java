package com.example.demo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findByCrew(Crew crew);
    Optional<Room> findByIdAndCrew(Long id, Crew crew);
    @Query("SELECT COUNT(r) FROM Room r WHERE r.crew.id = :crewId")
    long roomCountByCrewId(@Param("crewId") Long crewId);
}
