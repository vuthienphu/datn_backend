package com.example.routeplanner.model;

import lombok.Data;

import java.util.List;

@Data
public class DistanceMatrixDTO {
    private String routeCode;
    private List<String> distancematrixCoordinates;

    // Getters and Setters
    public String getRouteCode() {
        return routeCode;
    }

    public void setRouteCode(String routeCode) {
        this.routeCode = routeCode;
    }

    public List<String> getDistancematrixCoordinates() {
        return distancematrixCoordinates;
    }

    public void setDistancematrixCoordinates(List<String> distancematrixCoordinates) {
        this.distancematrixCoordinates = distancematrixCoordinates;
    }
}
