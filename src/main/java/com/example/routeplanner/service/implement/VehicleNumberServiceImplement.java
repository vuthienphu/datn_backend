package com.example.routeplanner.service.implement;

import com.example.routeplanner.model.Route;
import com.example.routeplanner.model.VehicleNumber;
import com.example.routeplanner.repository.RouteRepository;
import com.example.routeplanner.repository.VehicleNumberRepository;
import com.example.routeplanner.service.VehicleNumberService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class VehicleNumberServiceImplement implements VehicleNumberService {

    @Autowired
    private VehicleNumberRepository vehicleNumberRepository;

    @Autowired
    private RouteRepository routeRepository;

    @Override
    public VehicleNumber saveVehicleNumber(VehicleNumber vehicleNumber) {
        return vehicleNumberRepository.save(vehicleNumber);
    }

    public Map<String, Object> getVehicleNumberByRouteCode(String routeCode) {
        // Lấy dữ liệu từ truy vấn SQL native
        Map<String, Object> result = vehicleNumberRepository.findVehicleNumberByRouteCode(routeCode);
        if (result == null || result.isEmpty()) {
            return null; // Trả về null nếu không tìm thấy dữ liệu
        }

        // Chuẩn bị JSON response
        Map<String, Object> response = new HashMap<>();
        response.put("routeCode", result.get("v_route_code"));
        response.put("vehicleNumber", result.get("v_vehicle_number"));

        return response;
    }
}
