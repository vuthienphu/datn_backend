package com.example.routeplanner.service.implement;

import com.example.routeplanner.model.Locations;
import com.example.routeplanner.model.Route;
import com.example.routeplanner.repository.LocationsRepository;
import com.example.routeplanner.repository.RouteRepository;
import com.example.routeplanner.service.RouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RouteServiceImplement implements RouteService {

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private LocationsRepository locationsRepository;


    @Override
    public Route saveRoute(String routeCode, List<String> coordinates) {
        try {
            Route savedRoute = null;

            for (String pointCode : coordinates) {
                Locations locations = locationsRepository.findByPointCode(pointCode);
                if (locations != null) {
                    Route route = new Route();
                    route.setRouteCode(routeCode);
                    route.setPointCode(locations);
                    savedRoute = routeRepository.save(route);
                } else {
                    throw new RuntimeException("Location with point_code " + pointCode + " not found.");
                }
            }

            return savedRoute;
        } catch (Exception e) {
            throw new RuntimeException("An error occurred while saving the route.", e);
        }
    }
    }

