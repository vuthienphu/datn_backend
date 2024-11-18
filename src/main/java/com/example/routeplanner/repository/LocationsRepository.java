package com.example.routeplanner.repository;

import com.example.routeplanner.model.DistanceMatrix;
import com.example.routeplanner.model.Locations;
import jakarta.persistence.PreRemove;
import jakarta.persistence.Transient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface LocationsRepository extends JpaRepository<Locations,Integer> {

    
}
