package com.example.routeplanner.repository;

import com.example.routeplanner.model.DistanceMatrix;
import com.example.routeplanner.model.Locations;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DistanceMatrixRepository extends JpaRepository<DistanceMatrix,Long> {

    boolean existsByOriginIdAndDestinationId(Locations originId, Locations destinationId);
}
