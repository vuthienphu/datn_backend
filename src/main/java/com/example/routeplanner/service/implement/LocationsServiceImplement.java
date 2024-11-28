package com.example.routeplanner.service.implement;

import com.example.routeplanner.model.Locations;
import com.example.routeplanner.repository.LocationsRepository;
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



    @Override
    public List<Locations> getAllLocations() {
        return locationsRepository.findAll();
    }

    @Override
    public Locations getLocationById(Integer id) {
        Optional<Locations> locationData = locationsRepository.findById(id);
        if(locationData.isPresent()){
            return locationData.get();
        }
        else{
            throw new EntityNotFoundException("Location with id " + id + " not found");
        }
    }

    @Override
    public Locations createLocation(Locations locations) {


        Locations savedLocation = locationsRepository.save(locations);


        //distanceMatrixService.calculateAndSaveDistanceMatrix();

        return savedLocation;
    }

    @Override
    public Locations updateLocation(Integer id, Locations locations) {
        Optional<Locations> locationData = locationsRepository.findById(id);
        if(locationData.isPresent()){
            Locations updateLocation = locationData.get();
            updateLocation.setPointCode(locations.getPointCode());
            updateLocation.setPointName(locations.getPointName());
            updateLocation.setAddress(locations.getAddress());
            updateLocation.setLongitude(locations.getLongitude());
            updateLocation.setLatitude(locations.getLatitude());
            return locationsRepository.save(updateLocation);
        }
        else{
            throw new EntityNotFoundException("Location with id " + id + " not found");
        }
    }

    @Override

    public void deleteLocationById(Integer id) {
        Optional<Locations> locationData = locationsRepository.findById(id);
        if(locationData.isPresent()){
            Locations location = locationData.get();
            //distanceMatrixRepository.deleteByOriginPointCode(location);
            //distanceMatrixRepository.deleteByDestinationPointCode(location);
            locationsRepository.delete(location);
        }
        else {
            throw new EntityNotFoundException("Location with id " + id + " not found");
        }
    }


}



