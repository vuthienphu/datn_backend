package com.example.routeplanner.controller;

import com.example.routeplanner.model.OptimizeRouteDTO;
import com.example.routeplanner.service.OptimizeRouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class OptimizeRouteController {
    @Autowired
    private OptimizeRouteService routeOptimizationService;

    @PostMapping("api/route/optimize")
    public ResponseEntity<?> optimizeRoute(@RequestBody OptimizeRouteDTO optimizeRouteDTO) {
        int vehicleNumber = 1;
        try {
            // Gọi service với dữ liệu từ DTO
            List<List<String>> optimizedRoutes = routeOptimizationService.optimizeRoute(
                    optimizeRouteDTO.getRouteCode(),
                    optimizeRouteDTO.getOptimizeRouteCoordinates(),
                    vehicleNumber
            );
            // Trả về danh sách các mã điểm của các tuyến đường tối ưu
            return ResponseEntity.ok(optimizedRoutes);
        } catch (Exception e) {
            // Bắt lỗi và trả về thông tin chi tiết về lỗi
            String errorMessage = "Error processing the route optimization: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
        }
    }

}
