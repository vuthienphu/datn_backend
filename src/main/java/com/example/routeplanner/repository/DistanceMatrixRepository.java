package com.example.routeplanner.repository;

import com.example.routeplanner.model.DistanceMatrix;
import com.example.routeplanner.model.Locations;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DistanceMatrixRepository extends JpaRepository<DistanceMatrix, Integer> {
    boolean existsByOriginPointCodeAndDestinationPointCode(Locations origin, Locations destination);
    void deleteByOriginPointCode(Locations originPointCode);
    void deleteByDestinationPointCode(Locations destinationPointCode);
}
