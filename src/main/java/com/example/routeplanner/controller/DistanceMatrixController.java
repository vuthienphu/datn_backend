package com.example.routeplanner.controller;

import com.example.routeplanner.model.DistanceMatrixDTO;
import com.example.routeplanner.service.DistanceMatrixService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class DistanceMatrixController {
    @Autowired
    private DistanceMatrixService distanceMatrixService;

    @PostMapping("/api/distance-matrix")
    public ResponseEntity<?> calculateDistanceMatrix(@RequestBody DistanceMatrixDTO distanceMatrixDTO) {
        try {
            String routeCode = distanceMatrixDTO.getRouteCode();
            List<String> pointCodes = distanceMatrixDTO.getDistancematrixCoordinates();

            if (routeCode == null || routeCode.isEmpty()) {
                return ResponseEntity.badRequest().body("Route code is missing or empty.");
            }
            if (pointCodes == null || pointCodes.isEmpty()) {
                return ResponseEntity.badRequest().body("Point codes are missing or empty.");
            }

            long[][] distanceMatrix = distanceMatrixService.calculateDistanceMatrix(routeCode, pointCodes);
            return ResponseEntity.ok(distanceMatrix);
        } catch (Exception e) {
            // Log the exception for debugging
            e.printStackTrace(); // Thêm dòng này để log lỗi
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }
}