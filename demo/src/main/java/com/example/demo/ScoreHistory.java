package com.example.demo;
import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class ScoreHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id; 

    String type;
    int points;
    String sName;
    String eName;
    double distance;
    String time;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id") 
    User_info user;

    public ScoreHistory() {
    }

    public ScoreHistory(String type, int points, String sName, String eName, double distance, String time, User_info user) {
        this.type = type;
        this.points = points;
        this.sName = sName;
        this.eName = eName;
        this.distance = distance;
        this.time = time;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSName() {
        return sName;
    }

    public void setSName(String sName) {
        this.sName = sName;
    }

    public String getEName() {
        return eName;
    }

    public void setEName(String eName) {
        this.eName = eName;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public User_info getUser() {
        return user;
    }


    public void setUser(User_info user) {
        this.user = user;
        if (user != null) {
            user.getScoreHistory().add(this);
        }
    }
    

    @Override
    public String toString() {
        return "ScoreHistory{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", points=" + points + '\'' +
                ", time='" + time + '\'' +
                ", sName='" + sName + '\'' +
                ", eName='" + eName + '\'' +
                ", distance=" + distance +
                '}';
    }

}
