package com.example.routeplanner.model;

import lombok.Data;

import java.util.List;

@Data
public class RouteDTO {

    private String routeCode;
    private List<String> coordinates;

    // Getters and Setters
    public String getRouteCode() {
        return routeCode;
    }

    public void setRouteCode(String routeCode) {
        this.routeCode = routeCode;
    }

    public List<String> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<String> coordinates) {
        this.coordinates = coordinates;
    }
}
