package com.example.routeplanner.service.implement;

import com.example.routeplanner.model.Locations;
import com.example.routeplanner.model.OptimizeRouteDTO;
import com.example.routeplanner.model.Route;
import com.example.routeplanner.repository.LocationsRepository;
import com.example.routeplanner.repository.RouteRepository;
import com.example.routeplanner.repository.VehicleNumberRepository;
import com.example.routeplanner.service.RouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RouteServiceImplement implements RouteService {

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private LocationsRepository locationsRepository;

    @Autowired
    private VehicleNumberRepository vehicleNumberRepository;

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

    @Override
    public OptimizeRouteDTO getRouteByRouteCode(String routeCode) {
        List<Route> routes = routeRepository.findAllByRouteCode(routeCode);

        // Chuyển đổi danh sách Route thành OptimizeRouteDTO
        OptimizeRouteDTO dto = new OptimizeRouteDTO();
        dto.setRouteCode(routeCode);
        dto.setVehicleNumber(vehicleNumberRepository.findVehicleNumber(routeCode)); // Số lượng phương tiện
        dto.setOptimizeRouteCoordinates(
                routes.stream()
                        .map(route -> route.getPointCode().getPointCode()) // Lấy mã pointCode
                        .collect(Collectors.toList())
        );

        return dto;
    }
}

