package com.example.routeplanner.repository;

import com.example.routeplanner.model.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface RouteRepository extends JpaRepository<Route, Integer> {
    Optional<Route> findByRouteCode(String routeCode);
    List<Route> findAllByRouteCode(String routeCode);
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM route WHERE route_code = :routeCode", nativeQuery = true)
    void deleteByNativeQuery(@Param("routeCode") String routeCode);
    Route findFirstByRouteCode(String routeCode);
}
