package com.example.demo;

import java.util.List;

public class UserSettingsDTO {
    private String selectedMode;
    private List<String> selectedPlaces;
    private int searchRadius;
    private double minDistance;

    // 게터, 세터 메서드 생략
    public UserSettingsDTO() {
    }


    public UserSettingsDTO(String selectedMode, List<String> selectedPlaces, int searchRadius, double minDistance) {
        this.selectedMode = selectedMode;
        this.selectedPlaces = selectedPlaces;
        this.searchRadius = searchRadius;
        this.minDistance = minDistance;
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

}
