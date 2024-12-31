package com.example.routeplanner.repository;

import com.example.routeplanner.model.Route;
import com.example.routeplanner.model.VehicleNumber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;

@Repository
public interface VehicleNumberRepository extends JpaRepository<VehicleNumber, Integer> {
    @Query(value = "SELECT v.id AS v_id, v.route_code AS v_route_code, v.vehicle_number AS v_vehicle_number " +
            "FROM vehiclenumber v " +
            "JOIN route r ON v.route_code = r.route_code " +
            "WHERE r.route_code = :routeCode LIMIT 1", nativeQuery = true)
    Map<String, Object> findVehicleNumberByRouteCode(@Param("routeCode") String routeCode);
}

