package com.example.routeplanner.repository;

import com.example.routeplanner.model.VehicleNumber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VehicleNumberRepository extends JpaRepository<VehicleNumber, Integer> {

}
