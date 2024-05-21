package com.example.demo;

public class ScoreRequestDto {
    private String type;
    private int points;
    private String sName;
    private String eName;

    // Constructors, getters and setters

    public ScoreRequestDto() {
    }

    public ScoreRequestDto(String type, int points, String sName, String eName) {
        this.type = type;
        this.points = points;
        this.sName = sName;
        this.eName = eName;
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

    @Override
    public String toString() {
        return "ScoreRequestDto{" +
                ", type='" + type + '\'' +
                ", points=" + points +
                ", sName='" + sName + '\'' +
                ", eName='" + eName + '\'' +
                '}';
    }

    
}
