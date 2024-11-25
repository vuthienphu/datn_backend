package com.example.routeplanner.service.implement;

import com.example.routeplanner.model.Config;
import com.example.routeplanner.repository.ConfigRepository;
import com.example.routeplanner.service.ConfigService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ConfigServiceImplement implements ConfigService {

    @Autowired
    private ConfigRepository configRepository;

    @Override
    public List<Config> findAllConfig() {
        return configRepository.findAll();
    }

    @Override
    public Config createConfig(Config config) {
        return configRepository.save(config);
    }

    @Override
    public Config updateConfig(Integer id, Config config) {
        Optional<Config> configData = configRepository.findById(id);

        if (configData.isPresent()) {
Config updatedConfig = configData.get();
            updatedConfig.setConfigName(config.getConfigName());
            updatedConfig.setDescription(config.getDescription());
            updatedConfig.setValue(config.getValue());
            updatedConfig.setStatus(config.getStatus());
            return configRepository.save(updatedConfig);
        }
        else{
            throw new EntityNotFoundException("Config not found");
        }
    }

    @Override
    public void deleteConfigById(Integer id) {
        Optional<Config> configData = configRepository.findById(id);
        if (configData.isPresent()) {
            configRepository.delete(configData.get());
        }
        else {
            throw new EntityNotFoundException("Config with id " + id + " not found");
        }
    }


}
