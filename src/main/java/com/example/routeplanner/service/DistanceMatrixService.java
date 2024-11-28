package com.example.routeplanner.service;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface DistanceMatrixService {
    long[][] calculateDistanceMatrix(String routeCode, List<String> pointCodes) throws Exception;
}
