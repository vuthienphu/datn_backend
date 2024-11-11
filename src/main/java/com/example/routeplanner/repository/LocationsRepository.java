package com.example.routeplanner.repository;

import com.example.routeplanner.model.Locations;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface LocationsRepository extends JpaRepository<Locations,Long> {
    @Query("SELECT l.name FROM Locations l")
    Optional<Locations> findByName(String name);
    @Query("SELECT l.longitude,l.latitude FROM Locations l")
    List<Double[]> getCoordinates();

}
