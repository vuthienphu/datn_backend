package com.example.routeplanner.service;

import com.example.routeplanner.model.VehicleNumber;
import org.springframework.stereotype.Service;

@Service
public interface VehicleNumberService {
VehicleNumber saveVehicleNumber(VehicleNumber vehicleNumber);
}
