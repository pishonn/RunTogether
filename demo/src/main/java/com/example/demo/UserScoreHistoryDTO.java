package com.example.demo;

import java.time.LocalDateTime;

public class UserScoreHistoryDTO {
    private Long id;
    private RoomScoreHistory roomScoreHistory;
    private User_info user;
    private LocalDateTime raceEndTime;
    private int points;
    private Crew crew;

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RoomScoreHistory getRoomScoreHistory() {
        return roomScoreHistory;
    }

    public void setRoomScoreHistory(RoomScoreHistory roomScoreHistory) {
        this.roomScoreHistory = roomScoreHistory;
    }

    public User_info getUser() {
        return user;
    }

    public void setUser(User_info user) {
        this.user = user;
    }

    public LocalDateTime getRaceEndTime() {
        return raceEndTime;
    }

    public void setRaceEndTime(LocalDateTime raceEndTime) {
        this.raceEndTime = raceEndTime;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public Crew getCrew() {
        return crew;
    }

    public void setCrew(Crew crew) {
        this.crew = crew;
    }

    public UserScoreHistory toUserScoreHistory(RoomScoreHistory roomScoreHistory) {
        UserScoreHistory userScoreHistory = new UserScoreHistory();
        userScoreHistory.setRoomScoreHistory(roomScoreHistory);
        userScoreHistory.setUser(this.user);
        userScoreHistory.setRaceEndTime(this.raceEndTime);
        userScoreHistory.setPoints(this.points);
        userScoreHistory.setCrew(this.crew);
        return userScoreHistory;
    }
}
