package com.example.demo;

public class ScoreResult {
    private final int totalPoints;
    private final double totalDistance;

    public ScoreResult(int totalPoints, double totalDistance) {
        this.totalPoints = totalPoints;
        this.totalDistance = totalDistance;
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    public double getTotalDistance() {
        return totalDistance;
    }

    @Override
    public String toString() {
        return "ScoreResult{" +
                "totalPoints=" + totalPoints +
                ", totalDistance=" + totalDistance +
                '}';
    }
}
