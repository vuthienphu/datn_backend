package com.example.routeplanner.service;

import org.springframework.stereotype.Service;

@Service
public interface DistanceMatrixService {
   void calculateAndSaveDistanceMatrix();
}
