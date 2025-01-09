package com.example.routeplanner.service;

import com.example.routeplanner.model.OptimizeRoute;
import com.example.routeplanner.model.OptimizeRouteDTO;
import com.example.routeplanner.model.OptimizeRouteData;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface OptimizeRouteService {
    /*
    List<String> optimizeRoute(String routeCode, List<String> pointCodes, int vehicleNumber) throws Exception;
    List<String> getAllRouteCodes();

     */

    List<List<String>> optimizeRoute(String routeCode, List<String> pointCodes, int vehicleNumber) throws Exception;
    List<String> getAllRouteCodes();


    OptimizeRouteData getOptimizeRouteByRouteCode(String routeCode);
}
