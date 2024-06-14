package com.example.demo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface JoinRequestRepository extends JpaRepository<JoinRequest, Long> {
    List<JoinRequest> findByCrew(Crew crew);
    @Query("SELECT COUNT(jr) FROM JoinRequest jr WHERE jr.crew.id = :crewId")
    long countByCrewId(@Param("crewId") Long crewId);

    @Query("SELECT CASE WHEN COUNT(j) > 0 THEN true ELSE false END FROM JoinRequest j WHERE j.user.id = :userId")
    boolean existsByUserId(@Param("userId") Long userId);

}
