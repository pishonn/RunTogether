package com.example.demo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
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
    @JoinColumn(name = "crew_id")
    private Crew crew;

    private LocalDateTime createdDate;
    private LocalDateTime startTime; 

    private int capacity;

    private String startLocation;
    
    private String destination;

    private boolean isRaceStarted;

    private double distance;

    private boolean isRaceEnded;

    @Column(columnDefinition = "LONGTEXT") // JSON 데이터를 저장할 필드 길이 늘림
    private String placeData;
    //@ElementCollection
    @ManyToMany
    @JoinTable(
        name = "room_participants_ready",
        joinColumns = @JoinColumn(name = "room_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )

    @JsonManagedReference
    private Set<User_info> participantsReady;

    @JsonManagedReference
    @OneToMany(mappedBy = "room")
    private List<User_info> participants;

    private int participantsAtRaceStart;
    
    
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

    public Set<User_info> getParticipantsReady() {
        return participantsReady;
    }

    public void setParticipantsReady(Set<User_info> participantsReady) {
        this.participantsReady = participantsReady;
    }

    public void addParticipantsReady(User_info user) {
        participantsReady.add(user);
    }

    public void removeParticipantsReady(User_info user) {
        participantsReady.remove(user);
    }

    public int getParticipantsCount() {
        return participants.size();
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getPlaceData() {
        return placeData;
    }

    public void setPlaceData(String placeData) {
        this.placeData = placeData;
    }

    public boolean isRaceEnded() {
        return isRaceEnded;
    }

    public void setRaceEnded(boolean isRaceEnded) {
        this.isRaceEnded = isRaceEnded;
    }

    public int getParticipantsAtRaceStart() {
        return participantsAtRaceStart;
    }

    public void setParticipantsAtRaceStart(int participantsAtRaceStart) {
        this.participantsAtRaceStart = participantsAtRaceStart;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }
   
    @Override
    public String toString() {
        return "Room [id=" + id + ", admin=" + admin + ", crew=" + crew + ", createdDate=" + createdDate + ", capacity=" + capacity + ", startLocation=" + startLocation + ", destination=" + destination + ", isRaceStarted=" + isRaceStarted +"]";
    }
}
