package com.example.routeplanner.service;

import com.example.routeplanner.model.OptimizeRouteDTO;
import com.example.routeplanner.model.Route;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface RouteService {
   Route saveRoute(String routeCode, List<String> coordinates);
   OptimizeRouteDTO getRouteByRouteCode(String routeCode);
}
