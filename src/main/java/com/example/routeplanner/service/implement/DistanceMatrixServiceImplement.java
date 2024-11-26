package com.example.routeplanner.service.implement;

import com.example.routeplanner.service.DistanceMatrixService;
import org.springframework.stereotype.Service;
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

    @Override
    public double[][] calculateDistanceMatrix(List<List<Double>> coordinates) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://api.openrouteservice.org/v2/matrix/driving-car";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> requestPayload = Map.of("locations", coordinates, "metrics", List.of("distance"));
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestPayload, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
        Map<String, Object> responseBody = response.getBody();

        if (responseBody == null || !responseBody.containsKey("distances")) {
            throw new Exception("Failed to retrieve distances from OpenRouteService API.");
        }

        // Lấy danh sách khoảng cách và chuyển sang mảng double[][]
        List<List<Double>> distances = (List<List<Double>>) responseBody.get("distances");
        int size = distances.size();
        double[][] distanceMatrix = new double[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                distanceMatrix[i][j] = distances.get(i).get(j);
            }
        }

        LOGGER.info("Distance matrix retrieved and converted to double[][] successfully.");
        return distanceMatrix;
    }
}
