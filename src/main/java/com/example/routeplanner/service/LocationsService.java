package com.example.routeplanner.service;

import com.example.routeplanner.model.Locations;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public interface LocationsService {
    List<Locations> getAllLocations();
    Locations getLocationById(Long id);

    Locations creatLocation(Locations locations);

}
