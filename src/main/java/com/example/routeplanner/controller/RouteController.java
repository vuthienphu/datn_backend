package com.example.routeplanner.controller;

import com.example.routeplanner.model.Locations;
import com.example.routeplanner.model.Route;
import com.example.routeplanner.model.RouteDTO;
import com.example.routeplanner.service.RouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class RouteController {

    @Autowired
    private RouteService routeService;

    @PostMapping("/api/route")
    public ResponseEntity<Route> saveRoute(@RequestBody RouteDTO routeDTO) {
        try {
            Route savedRoute = routeService.saveRoute(routeDTO.getRouteCode(), routeDTO.getCoordinates());
            return new ResponseEntity<>(savedRoute, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
