package com.example.demo;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;



public interface UserRepository extends JpaRepository<User_info, Long> {
    Optional<User_info> findByUserId(String userId);
    Optional<User_info> findByUserIdAndPw(String userId, String pw);
    List<User_info> findTop10ByOrderByTotalPointsDesc();
    List<User_info> findTop10ByOrderByTotalDistanceDesc();

    @Query("SELECT u FROM User_info u ORDER BY (u.totalPoints + u.totalDistance) DESC")
    List<User_info> findOverallRankedUsers();

    @Query("SELECT u FROM User_info u ORDER BY (u.totalPoints) DESC")
    List<User_info> findPointsRankedUsers();

    @Query("SELECT u FROM User_info u ORDER BY (u.totalDistance) DESC")
    List<User_info> findDistanceRankedUsers();

    boolean existsByName(String name);
    boolean existsByUserId(String userId);
    
}
