package com.example.demo;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "room_score_history")
public class RoomScoreHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private Room room;

    @OneToOne
    @JoinColumn(name = "admin_id", nullable = false)
    private User_info admin;

    private LocalDateTime createdDate;

    private int participantsCount;

    private String startLocation;
    
    private String destination;

    private double distance;

    @OneToMany(mappedBy = "roomScoreHistory", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserScoreHistory> userScores = new HashSet<>()
    ;
    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

  
    public User_info getAdmin() {
        return admin;
    }

    public void setAdmin(User_info admin) {
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


}
