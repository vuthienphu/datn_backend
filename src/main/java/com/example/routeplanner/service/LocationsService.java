package com.example.routeplanner.service;

import com.example.routeplanner.model.Locations;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public interface LocationsService {
    List<Locations> getAllLocations();
    Locations getLocationById(Integer id);

    Locations createLocation(Locations locations);
void deleteLocationById(Integer id);
}
