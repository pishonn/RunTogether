package com.example.demo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
public class User_info {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name = "";

    @Column(unique = true)
    private String userId = "";

    @Column
    private String pw = "";

    @Column
    private String email = "";

    @Column
    private int profileImage = 1;

    @Column
    private String selectedMode = "auto";

    @Convert(converter = JsonStringListConverter.class)
    private List<String> selectedPlaces = new ArrayList<>(Arrays.asList("park"));  

    @Column
    private int searchRadius = 1000;

    @Column
    private double minDistance = 0.2;

    @JsonManagedReference
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ScoreHistory> scoreHistory = new ArrayList<>();

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "crew_id")
    private Crew crew;

    @Column
    private int totalPoints = 0;

    @Column
    private double totalDistance = 0.0;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPw() {
        return pw;
    }

    public void setPw(String pw) {
        this.pw = pw;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public User_info() {
        super();
    }
    
    public User_info(Long id, String name, String userId, String pw, String email, int profileImage, String selectedMode, List<String> selectedPlaces, int searchRadius, double minDistance, List<ScoreHistory> scoreHistory, Crew crew, int totalPoints, double totalDistance) {
        super();
        this.id = id;
        this.name = name;
        this.userId = userId;
        this.pw = pw;
        this.email = email;
        this.profileImage = profileImage;
        this.selectedMode = selectedMode;
        this.selectedPlaces = selectedPlaces;
        this.searchRadius = searchRadius;
        this.minDistance = minDistance;
        this.scoreHistory = scoreHistory;
        this.crew = crew;
        this.totalPoints = totalPoints;
        this.totalDistance = totalDistance;
    }

    public void addScoreHistory(ScoreHistory score) {
        this.scoreHistory.add(score);
        score.setUser(this);
    }

    @Override
    public String toString() {
        return "User_info [id=" + id + ", name=" + name + ", userId=" + userId + ", pw=" + pw + ", email=" + email + ", profileImage=" + profileImage + ", selectedPlaces=" + selectedPlaces
                + ", selectedMode=" + selectedMode + ", searchRadius="
                + searchRadius + ", minDistance=" + minDistance + " totalPoints=" + totalPoints + " totalDistance=" + totalDistance + "]";
    }
}
