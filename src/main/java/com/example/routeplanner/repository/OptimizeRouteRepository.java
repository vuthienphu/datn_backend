package com.example.routeplanner.repository;

import com.example.routeplanner.model.OptimizeRoute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface OptimizeRouteRepository extends JpaRepository<OptimizeRoute, Integer> {
    @Query("SELECT DISTINCT o.routeCode.routeCode FROM OptimizeRoute o")
    List<String> findAllRouteCodes();

    @Query("SELECT o FROM OptimizeRoute o JOIN FETCH o.routeCode WHERE o.routeCode.routeCode = :routeCode")
    List<OptimizeRoute> findByRouteCodeWithRoute(String routeCode);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM route_optimize WHERE route_code = :routeCode", nativeQuery = true)
    void deleteByNativeQuery(@Param("routeCode") String routeCode);
}
