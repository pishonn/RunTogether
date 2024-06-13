package com.example.demo;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;


@Entity
@Table(name = "room_score_history")
public class RoomScoreHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "room_id", nullable = false)
    private Long roomId;

    @Column(name = "admin_id", nullable = false)
    private Long admin;

    @ManyToOne
    @JoinColumn(name = "crew_id", nullable = false)
    private Crew crew;

    private LocalDateTime createdDate;
    private LocalDateTime startTime; 


    private int participantsCount;

    private String startLocation;
    
    private String destination;

    private double distance;

    @OneToMany(mappedBy = "roomScoreHistory", cascade = CascadeType.ALL)
    private Set<UserScoreHistory> userScores = new HashSet<>()
    ;
    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

  
    public Long getAdmin() {
        return admin;
    }

    public void setAdmin(Long admin) {
        this.admin = admin;
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

    public Set<UserScoreHistory> getUserScores() {
        return userScores;
    }

    public void setUserScores(Set<UserScoreHistory> userScores) {
        this.userScores = userScores;
    }

    public Crew getCrew() {
        return crew;
    }

    public void setCrew(Crew crew) {
        this.crew = crew;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    @Override
    public String toString() {
        return "RoomScoreHistory{" +
                "id=" + id +
                ", roomId=" + roomId +
                ", admin=" + admin +
                ", crew=" + crew +
                ", createdDate=" + createdDate +
                ", startTime=" + startTime +
                ", participantsCount=" + participantsCount +
                ", startLocation='" + startLocation + '\'' +
                ", destination='" + destination + '\'' +
                ", distance=" + distance +
                ", userScores=" + userScores +
                '}';
    }

}
