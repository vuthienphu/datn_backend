package com.example.routeplanner.service;

import com.example.routeplanner.model.Config;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public interface ConfigService {
    List<Config> findAllConfig();
    Config createConfig(Config config);
Config updateConfig(Integer id, Config config);
    void deleteConfigById(Integer id);
}
