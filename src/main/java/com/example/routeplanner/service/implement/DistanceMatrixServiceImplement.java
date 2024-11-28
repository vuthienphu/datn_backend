package com.example.routeplanner.service.implement;

import com.example.routeplanner.model.DistanceMatrix;
import com.example.routeplanner.model.Locations;
import com.example.routeplanner.model.Route;
import com.example.routeplanner.repository.DistanceMatrixRepository;
import com.example.routeplanner.repository.LocationsRepository;
import com.example.routeplanner.repository.RouteRepository;
import com.example.routeplanner.service.DistanceMatrixService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;
import java.util.logging.Logger;

@Service
public class DistanceMatrixServiceImplement implements DistanceMatrixService {
    private static final Logger LOGGER = Logger.getLogger(DistanceMatrixServiceImplement.class.getName());

    @Value("${openrouteservice.api.key}")
    private String apiKey;

    @Autowired
    private LocationsRepository locationsRepository;

    @Autowired
    private DistanceMatrixRepository distanceMatrixRepository;

    @Autowired
    private RouteRepository routeRepository;

    @Override
    public long[][] calculateDistanceMatrix(String routeCode, List<String> pointCodes) throws Exception {
        List<Locations> locations = new ArrayList<>();

        for (String pointCode : pointCodes) {
            // Sử dụng findFirstByPointCode để chỉ lấy bản ghi đầu tiên
            Optional<Locations> foundLocation = locationsRepository.findFirstByPointCode(pointCode);
            if (foundLocation.isEmpty()) {
                throw new Exception("No location found for point code: " + pointCode);
            }
            locations.add(foundLocation.get());
        }

        // Kiểm tra các mã điểm không tồn tại trong cơ sở dữ liệu
        Set<String> foundCodes = locations.stream().map(Locations::getPointCode).collect(Collectors.toSet());
        Set<String> missingCodes = new HashSet<>(pointCodes);
        missingCodes.removeAll(foundCodes);

        if (!missingCodes.isEmpty()) {
            throw new Exception("The following point codes were not found in the database: " + missingCodes);
        }

        // Tạo danh sách tọa độ
        List<List<Double>> coordinates = locations.stream()
                .map(location -> Arrays.asList(location.getLongitude(), location.getLatitude()))
                .collect(Collectors.toList());

        // Gọi OpenRouteService API để tính toán ma trận khoảng cách
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://api.openrouteservice.org/v2/matrix/driving-car";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> requestPayload = Map.of("locations", coordinates, "metrics", List.of("distance"));
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestPayload, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
        Map<String, Object> responseBody = response.getBody();

        if (response.getStatusCodeValue() != 200) {
            throw new Exception("Error from OpenRouteService API: " + response.getStatusCode());
        }

        if (responseBody == null || !responseBody.containsKey("distances")) {
            throw new Exception("Failed to retrieve distances from OpenRouteService API.");
        }

        // Chuyển đổi ma trận khoảng cách thành mảng long[][]
        List<List<Double>> distances = (List<List<Double>>) responseBody.get("distances");
        int size = distances.size();
        long[][] distanceMatrix = new long[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                distanceMatrix[i][j] = Math.round(distances.get(i).get(j));
            }
        }

        // Lưu vào bảng distance_matrix
        saveDistanceMatrix(routeCode, pointCodes, distanceMatrix);

        LOGGER.info("Distance matrix retrieved and saved successfully.");
        return distanceMatrix;
    }

    @Transactional
    public void saveDistanceMatrix(String routeCode, List<String> pointCodes, long[][] distanceMatrix) throws Exception {
        // Tìm tất cả các bản ghi với cùng route_code
        List<Route> routes = routeRepository.findAllByRouteCode(routeCode);
        if (routes.isEmpty()) {
            throw new Exception("No route found for route code: " + routeCode);
        }
        // Lấy bản ghi đầu tiên hoặc xử lý danh sách kết quả
        Route route = routes.get(0);  // Lấy bản ghi đầu tiên

        // Lấy danh sách Locations từ pointCodes
        List<Locations> locations = new ArrayList<>();
        for (String pointCode : pointCodes) {
            // Sử dụng findFirstByPointCode để chỉ lấy bản ghi đầu tiên
            Optional<Locations> foundLocation = locationsRepository.findFirstByPointCode(pointCode);
            if (foundLocation.isEmpty()) {
                throw new Exception("No location found for point code: " + pointCode);
            }
            locations.add(foundLocation.get());
        }

        // Tạo danh sách DistanceMatrix để lưu
        List<DistanceMatrix> distanceMatrixList = new ArrayList<>();
        for (int i = 0; i < pointCodes.size(); i++) {
            for (int j = 0; j < pointCodes.size(); j++) {
                DistanceMatrix distanceMatrixEntity = new DistanceMatrix();
                distanceMatrixEntity.setRoute(route);
                distanceMatrixEntity.setOriginPointCode(locations.get(i));
                distanceMatrixEntity.setDestinationPointCode(locations.get(j));
                distanceMatrixEntity.setDistance(distanceMatrix[i][j]);

                distanceMatrixList.add(distanceMatrixEntity);
            }
        }

        // Lưu vào cơ sở dữ liệu
        if (!distanceMatrixList.isEmpty()) {
            distanceMatrixRepository.saveAll(distanceMatrixList);
        } else {
            throw new Exception("No distance data to save.");
        }
    }
}
