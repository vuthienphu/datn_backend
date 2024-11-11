package com.example.routeplanner.service.implement;

import com.example.routeplanner.model.DistanceMatrix;
import com.example.routeplanner.model.Locations;
import com.example.routeplanner.repository.DistanceMatrixRepository;
import com.example.routeplanner.repository.LocationsRepository;
import com.example.routeplanner.service.DistanceMatrixService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class DistanceMatrixServiceImplement implements DistanceMatrixService {
    private static final Logger LOGGER = Logger.getLogger(DistanceMatrixServiceImplement.class.getName());

    @Value("${openrouteservice.api.key}")
    private String apiKey;

    @Autowired
    private DistanceMatrixRepository distanceMatrixRepository;

    @Autowired
    private LocationsRepository locationRepository;

    public void calculateAndSaveDistanceMatrix() {
        List<Locations> locations = locationRepository.findAll();
        List<List<Double>> coordinates = locations.stream()
                .map(location -> Arrays.asList(location.getLongitude(), location.getLatitude()))
                .collect(Collectors.toList());

        try {
            RestTemplate restTemplate = new RestTemplate();
            String url = "https://api.openrouteservice.org/v2/matrix/driving-car";
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", apiKey);
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> requestPayload = Map.of("locations", coordinates, "metrics", List.of("distance"));
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestPayload, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
            Map<String, Object> responseBody = response.getBody();

            if (responseBody != null && responseBody.containsKey("distances")) {
                List<List<Double>> distances = (List<List<Double>>) responseBody.get("distances");

                // Save the distance matrix to the database if it doesn't exist
                for (int i = 0; i < distances.size(); i++) {
                    for (int j = 0; j < distances.get(i).size(); j++) {
                        double distance = distances.get(i).get(j);
                        Locations origin = locations.get(i);
                        Locations destination = locations.get(j);

                        // Check if the record already exists
                        if (!distanceMatrixRepository.existsByOriginIdAndDestinationId(origin, destination)) {
                            DistanceMatrix distanceMatrix = new DistanceMatrix(
                                    null, origin, destination, distance
                            );
                            distanceMatrixRepository.save(distanceMatrix);
                        }
                    }
                }
            } else {
                LOGGER.warning("Failed to retrieve distances from OpenRouteService API response.");
            }
        } catch (Exception e) {
            LOGGER.severe("An error occurred while calculating and saving the distance matrix: " + e.getMessage());
        }
    }
}
