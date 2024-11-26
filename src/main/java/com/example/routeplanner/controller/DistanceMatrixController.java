package com.example.routeplanner.controller;

import com.example.routeplanner.service.DistanceMatrixService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class DistanceMatrixController {
    @Autowired
    private DistanceMatrixService distanceMatrixService;

    @PostMapping("/api/distance-matrix")
    public ResponseEntity<?> calculateDistanceMatrix(@RequestBody Map<String, List<List<Double>>> requestBody) {
        if (!requestBody.containsKey("coordinates")) {
            return ResponseEntity.badRequest().body("Missing 'coordinates' in request body.");
        }

        List<List<Double>> coordinates = requestBody.get("coordinates");
        try {
            double[][] distanceMatrix = distanceMatrixService.calculateDistanceMatrix(coordinates);
            return ResponseEntity.ok(distanceMatrix);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error while calculating distance matrix: " + e.getMessage());
        }
    }
}
