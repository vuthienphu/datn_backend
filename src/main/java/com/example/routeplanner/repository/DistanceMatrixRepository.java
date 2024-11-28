package com.example.routeplanner.repository;

import com.example.routeplanner.model.DistanceMatrix;
import com.example.routeplanner.model.Route;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DistanceMatrixRepository extends JpaRepository<DistanceMatrix, Integer> {

}
