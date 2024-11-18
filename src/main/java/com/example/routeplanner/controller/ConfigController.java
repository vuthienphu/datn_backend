package com.example.routeplanner.controller;

import com.example.routeplanner.model.Config;
import com.example.routeplanner.service.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")  // Địa chỉ front-end
@RestController
public class ConfigController {

    @Autowired
    private ConfigService configService;

    @GetMapping("/api/config")
    public List<Config> getAllConfigs() {
        return configService.findAllConfig();
    }


    @PostMapping("/api/config")
    public ResponseEntity<Config> createConfig(@RequestBody Config config) {
        return new ResponseEntity<Config>(configService.createConfig(config),HttpStatus.CREATED);
    }
    @PutMapping("/api/config/{id}")
    public ResponseEntity<Config> updateConfig(@PathVariable("id") Integer id, @RequestBody Config config) {
        return new ResponseEntity<Config>(configService.updateConfig(id,config) ,HttpStatus.OK);
    }

    @DeleteMapping("/api/config/{id}")
    public ResponseEntity<String> deleteConfigById(@PathVariable("id") Integer id) {
        configService.deleteConfigById(id);
        return new ResponseEntity<String>("Delete successfully",HttpStatus.OK);
    }
}
