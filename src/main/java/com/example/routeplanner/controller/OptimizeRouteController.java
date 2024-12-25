package com.example.routeplanner.controller;

import com.example.routeplanner.model.OptimizeRoute;
import com.example.routeplanner.model.OptimizeRouteDTO;
import com.example.routeplanner.service.implement.OptimizeRouteServiceImplement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class OptimizeRouteController {
    @Autowired
    private OptimizeRouteServiceImplement optimizeRouteServiceImplement;

    @PostMapping("api/route/optimize")
    public ResponseEntity<?> optimizeRoute(@RequestBody OptimizeRouteDTO optimizeRouteDTO) {

        try {
            List<List<String>> optimizedRoutes = optimizeRouteServiceImplement.optimizeRoute(
                    optimizeRouteDTO.getRouteCode(),
                    optimizeRouteDTO.getOptimizeRouteCoordinates(),
                    optimizeRouteDTO.getVehicleNumber()
            );

            return ResponseEntity.ok(optimizedRoutes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing the route optimization: " + e.getMessage());
        }



    }


    @GetMapping("/api/route/route-code")
    public List<String> getAllRouteCodes (){
        return optimizeRouteServiceImplement.getAllRouteCodes();
    }
    @GetMapping("/api/route/{routeCode}")
    public OptimizeRouteDTO getOptimizeRoute(@PathVariable String routeCode) {
        return optimizeRouteServiceImplement.getOptimizeRouteByRouteCode(routeCode);
    }
    @DeleteMapping("/api/route/{routeCode}")
    public ResponseEntity<String> deleteRouteByRouteCode(@PathVariable("routeCode") String routeCode) {
        optimizeRouteServiceImplement.deleteRouteByRouteCode(routeCode);
        return new ResponseEntity<String>("Delete successfully",HttpStatus.OK);
    }
    @DeleteMapping("/api/route/edit/{routeCodeEdit}")
    public ResponseEntity<String> deleteRoute(@PathVariable("routeCodeEdit") String routeCodeEdit) {
        optimizeRouteServiceImplement.deleteRouteByRouteCode(routeCodeEdit);
        return new ResponseEntity<String>("Delete successfully",HttpStatus.OK);
    }
}
