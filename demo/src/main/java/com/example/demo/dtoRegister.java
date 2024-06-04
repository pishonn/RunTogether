package com.example.demo;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

public class dtoRegister {
    
    String name;
    String userId;
    String pw;
    String email;
    int profileImage = 1;
    String selectedMode = "auto";  
    List<String> selectedPlaces = new ArrayList<>(Arrays.asList("park"));  
    int searchRadius = 1000;  
    double minDistance = 0.2;  
    List<ScoreHistory> scoreHistory = new ArrayList<>();  
    Crew crew = null;
    int totalPoints = 0;
    double totalDistance = 0.0;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setPw(String pw) {
        this.pw = pw;
    }

    public String getPw() {
        return pw;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public String getSelectedMode() {
        return selectedMode;
    }

    public void setSelectedMode(String selectedMode) {
        this.selectedMode = selectedMode;
    }

    public List<String> getSelectedPlaces() {
        return selectedPlaces;
    }

    public void setSelectedPlaces(List<String> selectedPlaces) {
        this.selectedPlaces = selectedPlaces;
    }

    public int getSearchRadius() {
        return searchRadius;
    }

    public void setSearchRadius(int searchRadius) {
        this.searchRadius = searchRadius;
    }

    public double getMinDistance() {
        return minDistance;
    }

    public void setMinDistance(double minDistance) {
        this.minDistance = minDistance;
    }

    public List<ScoreHistory> getScoreHistory() {
        return scoreHistory;
    }

    public void setScoreHistory(List<ScoreHistory> scoreHistory) {
        this.scoreHistory = scoreHistory;
    }
    
    public Crew getCrew() {
        return crew;
    }

    public void setCrew(Crew crew) {
        this.crew = crew;
    }

    public int getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(int profileImage) {
        this.profileImage = profileImage;
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints(int totalPoints) {
        this.totalPoints = totalPoints;
    }

    public double getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(double totalDistance) {
        this.totalDistance = totalDistance;
    }

    // toEntity method updated to handle new fields
    public User_info toEntity() {
        User_info userInfo = new User_info();
        userInfo.setName(this.name);
        userInfo.setUserId(this.userId);
        userInfo.setPw(this.pw);
        userInfo.setEmail(this.email);
        userInfo.setProfileImage(this.profileImage);
        userInfo.setSelectedMode(this.selectedMode);
        userInfo.setSelectedPlaces(this.selectedPlaces);
        userInfo.setSearchRadius(this.searchRadius);
        userInfo.setMinDistance(this.minDistance);
        userInfo.setScoreHistory(this.scoreHistory);
        userInfo.setCrew(this.crew);
        userInfo.setTotalPoints(this.totalPoints);
        userInfo.setTotalDistance(this.totalDistance);
        return userInfo;
    }

    @Override
    public String toString() {
        return "dtoRegister{" +
                "name='" + name + '\'' +
                ", userId='" + userId + '\'' +
                ", pw='" + pw + '\'' +
                ", email='" + email + '\'' +
                ", profileImage=" + profileImage +
                ", selectedMode='" + selectedMode + '\'' +
                ", selectedPlaces=" + selectedPlaces +
                ", searchRadius=" + searchRadius +
                ", minDistance=" + minDistance +
                ", scoreHistory=" + scoreHistory +
                ", crew='" + crew + '\'' +
                ", totalPoints=" + totalPoints +
                ", totalDistance=" + totalDistance +
                '}';
    }
}
