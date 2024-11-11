package com.example.routeplanner.service.implement;

import com.example.routeplanner.model.Locations;
import com.example.routeplanner.repository.LocationsRepository;
import com.example.routeplanner.service.DistanceMatrixService;
import com.example.routeplanner.service.LocationsService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LocationsServiceImplement implements LocationsService {

    @Autowired
    private LocationsRepository locationsRepository;

    @Autowired
    private DistanceMatrixService distanceMatrixService;

    @Override
    public List<Locations> getAllLocations() {
        return locationsRepository.findAll();
    }

    @Override
    public Locations getLocationById(Long id) {
        Optional<Locations> locationData = locationsRepository.findById(id);
        if(locationData.isPresent()){
            return locationData.get();
        }
        else{
            throw new EntityNotFoundException("Location with id " + id + " not found");
        }
    }

    @Override
    public Locations creatLocation(Locations locations) {
       return locationsRepository.save(locations);


    }


}
