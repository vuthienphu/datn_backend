package com.example.routeplanner.controller;

import com.example.routeplanner.model.Config;
import com.example.routeplanner.model.Route;
import com.example.routeplanner.model.VehicleNumber;
import com.example.routeplanner.repository.RouteRepository;
import com.example.routeplanner.service.VehicleNumberService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class VehicleNumberController {

    @Autowired
    private VehicleNumberService vehicleNumberService;

    @Autowired
    private RouteRepository routeRepository;

    @PostMapping("/api/vehiclenumber")
    public ResponseEntity<?> saveVehicleNumber(@RequestBody Map<String, Object> request) {

        try {
            // Lấy routeCode và vehicleNumber từ request
            String routeCode = ((String) request.get("routeCode")).trim();
            int vehicleNumber = Integer.parseInt(request.get("vehicleNumber").toString());

            // Log dữ liệu từ request
            System.out.println("Received routeCode: " + routeCode);
            System.out.println("Received vehicleNumber: " + vehicleNumber);

            // Tìm Route bằng routeCode
            Route route = routeRepository.findFirstByRouteCode(routeCode);
            if (route == null) {
                System.out.println("Route not found for routeCode: " + routeCode);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Route with code '" + routeCode + "' not found.");
            }

            // Tạo và lưu VehicleNumber
            VehicleNumber vehicleNumberEntity = new VehicleNumber();
            vehicleNumberEntity.setRoute(route);
            vehicleNumberEntity.setVehicleNumber(vehicleNumber);

            System.out.println("Saving VehicleNumber: " + vehicleNumberEntity);

            VehicleNumber savedVehicleNumber = vehicleNumberService.saveVehicleNumber(vehicleNumberEntity);

            return new ResponseEntity<>(savedVehicleNumber, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace(); // Log lỗi
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error saving vehicle number: " + e.getMessage());
        }

    }
}