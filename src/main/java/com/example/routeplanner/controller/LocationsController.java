package com.example.routeplanner.controller;
import com.example.routeplanner.model.Locations;
import com.example.routeplanner.service.LocationsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@CrossOrigin(origins = "http://localhost:3000")  // Địa chỉ front-end
@RestController
public class LocationsController {
    @Autowired
    private LocationsService locationsService;



    @GetMapping("/api/locations")
    public List<Locations> getAllLocations() {
        return locationsService.getAllLocations();
    }

    @GetMapping("/api/locations/{id}")
    public ResponseEntity<Locations> getLocationById(@PathVariable("id") Integer id) {
        return new ResponseEntity<Locations>(locationsService.getLocationById(id), HttpStatus.OK);
    }

    @PostMapping("/api/locations")
    public ResponseEntity<Locations> createLocation(@RequestBody Locations locations){
        return new ResponseEntity<Locations>(locationsService.createLocation(locations),HttpStatus.CREATED);
    }

    @PutMapping("/api/locations/{id}")
    public ResponseEntity<Locations> updateLocation(@PathVariable("id") Integer id, @RequestBody Locations locations){
        return new ResponseEntity<Locations>(locationsService.updateLocation(id, locations),HttpStatus.OK);
    }

    @DeleteMapping("/api/locations/{id}")
    public ResponseEntity<String> deleteLocationById(@PathVariable("id") Integer id) {
        locationsService.deleteLocationById(id);
        return new ResponseEntity<String>("delete successfully", HttpStatus.OK);
    }


}
