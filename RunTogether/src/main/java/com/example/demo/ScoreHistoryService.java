package com.example.demo;
import org.springframework.beans.factory.annotation.Autowired;


public class ScoreHistoryService {
    @Autowired
    private ScoreHistoryRepository scoreHistoryRepository;
    
    public void createScoreHistory(ScoreHistoryDTO dto, User_info user) {
        ScoreHistory scoreHistory = dto.toEntity(user);
        scoreHistoryRepository.save(scoreHistory);
    }
}
