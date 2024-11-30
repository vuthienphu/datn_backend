package com.example.routeplanner.repository;

import com.example.routeplanner.model.OptimizeRoute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

@Repository
public interface OptimizeRouteRepository extends JpaRepository<OptimizeRoute, Integer> {


}
