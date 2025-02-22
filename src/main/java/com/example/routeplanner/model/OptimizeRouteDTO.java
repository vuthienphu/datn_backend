package com.example.routeplanner.model;

import lombok.Data;

import java.util.List;

@Data
public class OptimizeRouteDTO {
    private String routeCode;
    private int vehicleNumber;
    private List<String> optimizeRouteCoordinates;

    // Getters and Setters
    public String getRouteCode() {
        return routeCode;
    }

    public void setRouteCode(String routeCode) {
        this.routeCode = routeCode;
    }

    public List<String> getOptimizeRouteCoordinates() {
        return optimizeRouteCoordinates;
    }

    public void setOptimizeRouteCoordinates(List<String> optimizeRouteCoordinates) {
        this.optimizeRouteCoordinates = optimizeRouteCoordinates;
    }

    public int getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(int vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }
}
