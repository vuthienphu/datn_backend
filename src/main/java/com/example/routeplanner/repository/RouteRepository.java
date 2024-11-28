package com.example.routeplanner.repository;

import com.example.routeplanner.model.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RouteRepository extends JpaRepository<Route, Integer> {
    Optional<Route> findByRouteCode(String routeCode);
    List<Route> findAllByRouteCode(String routeCode);
}
