package com.example.routeplanner.repository;

import com.example.routeplanner.model.DistanceMatrix;
import com.example.routeplanner.model.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface DistanceMatrixRepository extends JpaRepository<DistanceMatrix, Integer> {
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM distance_matrix WHERE route_code = :routeCode", nativeQuery = true)
    void deleteByNativeQuery(@Param("routeCode") String routeCode);
}
