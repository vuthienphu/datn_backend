package com.example.routeplanner.service.implement;

import com.example.routeplanner.model.VehicleNumber;
import com.example.routeplanner.repository.VehicleNumberRepository;
import com.example.routeplanner.service.VehicleNumberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VehicleNumberServiceImplement implements VehicleNumberService {

    @Autowired
    private VehicleNumberRepository vehicleNumberRepository;

    @Override
    public VehicleNumber saveVehicleNumber(VehicleNumber vehicleNumber) {
        return vehicleNumberRepository.save(vehicleNumber);
    }
}
