package com.example.demo;

import java.util.List;
import java.util.Set;

public class RoomDTO {
    private Long id;
    private String startLocation;
    private String destination;
    private double distance;
    private String placeData;
    private List<User_info> participants;
    private User_info admin;
    private Set<User_info> participantsReady;

    // Constructor
    public RoomDTO(Room room) {
        this.id = room.getId();
        this.startLocation = room.getStartLocation();
        this.destination = room.getDestination();
        this.distance = room.getDistance();
        this.placeData = room.getPlaceData();
        this.participants = room.getParticipants();
        this.admin = room.getAdmin();
        this.participantsReady = room.getParticipantsReady();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getPlaceData() {
        return placeData;
    }

    public void setPlaceData(String placeData) {
        this.placeData = placeData;
    }

    public List<User_info> getParticipants() {
        return participants;
    }

    public void setParticipants(List<User_info> participants) {
        this.participants = participants;
    }

    public User_info getAdmin() {
        return admin;
    }

    public void setAdmin(User_info admin) {
        this.admin = admin;
    }

    public Set<User_info> getParticipantsReady() {
        return participantsReady;
    }

    public void setParticipantsReady(Set<User_info> participantsReady) {
        this.participantsReady = participantsReady;
    }

    @Override
    public String toString() {
        return "RoomDTO [id=" + id + ", startLocation=" + startLocation + ", destination=" + destination + ", distance=" + distance + ", placeData=" + placeData + ", participants=" + participants + "]";
    }
}
