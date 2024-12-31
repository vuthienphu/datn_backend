package com.example.routeplanner.service;

import com.example.routeplanner.model.VehicleNumber;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface VehicleNumberService {
VehicleNumber saveVehicleNumber(VehicleNumber vehicleNumber);
Map<String, Object> getVehicleNumberByRouteCode(String routeCode);
}
