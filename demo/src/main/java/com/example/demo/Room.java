package com.example.demo;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;

@Entity
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "admin_id", nullable = false)
    private User_info admin;

    @ManyToOne
    @JoinColumn(name = "crew_id", nullable = false)
    private Crew crew;

    private LocalDateTime createdDate;

    private int capacity;

    private String startLocation;
    
    private String destination;

    private boolean isRaceStarted;

    @ElementCollection
    private List<Long> participantsReady;

    @OneToMany(mappedBy = "room")
    private List<User_info> participants;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User_info getAdmin() {
        return admin;
    }

    public void setAdmin(User_info admin) {
        this.admin = admin;
    }

    public Crew getCrew() {
        return crew;
    }

    public void setCrew(Crew crew) {
        this.crew = crew;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
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

    public List<User_info> getParticipants() {
        return participants;
    }

    public void setParticipants(List<User_info> participants) {
        this.participants = participants;
    }

    public boolean isRaceStarted() {
        return isRaceStarted;
    }

    public void setRaceStarted(boolean isRaceStarted) {
        this.isRaceStarted = isRaceStarted;
    }

    public List<Long> getParticipantsReady() {
        return participantsReady;
    }

    public void setParticipantsReady(List<Long> participantsReady) {
        this.participantsReady = participantsReady;
    }

    
}
