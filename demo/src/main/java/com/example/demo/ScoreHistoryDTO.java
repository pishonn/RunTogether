package com.example.demo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ScoreHistoryDTO {
    private String type;
    private int points;
    private String time;
    private double distance;

    @JsonProperty("sName")
    private String startName;

    @JsonProperty("eName")
    private String endName;

    public ScoreHistoryDTO() {
    }

    public ScoreHistoryDTO(String type, int points, String sName, String eName, double distance, String time) {
        this.type = type;
        this.points = points;
        this.time = time;
        this.startName = sName;
        this.endName = eName;
        this.distance = distance;
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
        return startName;
    }

    public void setSName(String sName) {
        this.startName = sName;
    }

    public String getEName() {
        return endName;
    }

    public void setEName(String eName) {
        this.endName = eName;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }


    public ScoreHistory toEntity(User_info user) {
        return new ScoreHistory(type, points, startName, endName, distance, time, user);
    }
}
