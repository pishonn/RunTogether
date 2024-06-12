package com.example.demo;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class RoomScoreHistoryDTO {
    private Long roomId;
    private Long adminId;
    private LocalDateTime createdDate;
    private int participantsCount;
    private String startLocation;
    private String destination;
    private double distance;
    private Set<UserScoreHistoryDTO> userScores = new HashSet<>();
    // Getters and Setters
    
    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public Long getAdminId() {
        return adminId;
    }

    public void setAdminId(Long adminId) {
        this.adminId = adminId;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public int getParticipantsCount() {
        return participantsCount;
    }

    public void setParticipantsCount(int participantsCount) {
        this.participantsCount = participantsCount;
    }

    public String getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(String startLocation) {
        this.startLocation = startLocation;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public Set<UserScoreHistoryDTO> getUserScores() {
        return userScores;
    }

    public void setUserScores(Set<UserScoreHistoryDTO> userScores) {
        this.userScores = userScores;
    }

    public RoomScoreHistory toRoomScoreHistory(Room room, User_info admin) {
        RoomScoreHistory roomScoreHistory = new RoomScoreHistory();
        roomScoreHistory.setRoom(room);
        roomScoreHistory.setAdmin(admin);
        roomScoreHistory.setCreatedDate(createdDate);
        roomScoreHistory.setParticipantsCount(participantsCount);
        roomScoreHistory.setStartLocation(startLocation);
        roomScoreHistory.setDestination(destination);
        roomScoreHistory.setDistance(distance);

        Set<UserScoreHistory> userScoreHistories = new HashSet<>();
        for (UserScoreHistoryDTO dto : userScores) {
            UserScoreHistory userScoreHistory = dto.toUserScoreHistory(roomScoreHistory);
            userScoreHistories.add(userScoreHistory);
        }
        roomScoreHistory.setUserScores(userScoreHistories);

        return roomScoreHistory;
    }

    public UserScoreHistory toUserScoreHistory(User_info user, RoomScoreHistory roomScoreHistory) {
        UserScoreHistory userScoreHistory = new UserScoreHistory();
        userScoreHistory.setUser(user);
        userScoreHistory.setRoomScoreHistory(roomScoreHistory);
        return userScoreHistory;
    }

    @Override
    public String toString() {
        return "RoomScoreHistoryDTO{" +
                "roomId=" + roomId +
                ", adminId=" + adminId +
                ", createdDate=" + createdDate +
                ", participantsCount=" + participantsCount +
                ", startLocation='" + startLocation + '\'' +
                ", destination='" + destination + '\'' +
                ", distance=" + distance +
                '}';
    }
}
