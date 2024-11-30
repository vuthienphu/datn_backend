package com.example.routeplanner.service;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface OptimizeRouteService {
    List<String> optimizeRoute(String routeCode, List<String> pointCodes, int vehicleNumber) throws Exception;
}
