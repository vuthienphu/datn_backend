package com.example.routeplanner.service;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface DistanceMatrixService {

        double[][] calculateDistanceMatrix(List<List<Double>> coordinates) throws Exception;


}
