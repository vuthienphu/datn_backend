package com.example.routeplanner.service;

import com.example.routeplanner.model.Locations;
import com.example.routeplanner.repository.LocationsRepository;
import com.google.ortools.Loader;
import com.google.ortools.constraintsolver.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class OptimizeRouteService {

    static {
        Loader.loadNativeLibraries();
    }

    @Autowired
    private DistanceMatrixService distanceMatrixService;

    @Autowired
    private LocationsRepository locationsRepository;

    public List<List<String>> optimizeRoute(String routeCode, List<String> pointCodes, int vehicleNumber) throws Exception {
        // Lấy ma trận khoảng cách từ OpenRouteService
        long[][] distanceMatrix = distanceMatrixService.calculateDistanceMatrix(routeCode, pointCodes);
        System.out.println("Distance Matrix: ");
        if (distanceMatrix != null && distanceMatrix.length > 0) {
            for (long[] row : distanceMatrix) {
                System.out.println(Arrays.toString(row));
            }
        } else {
            throw new Exception("Distance matrix is invalid or empty.");
        }
        System.out.println("Point Codes: " + pointCodes);

        // Kiểm tra ma trận khoảng cách
        if (distanceMatrix == null || distanceMatrix.length == 0) {
            throw new Exception("Distance matrix is invalid or empty.");
        }

        // Lấy thông tin các vị trí từ cơ sở dữ liệu
        List<Locations> locations = new ArrayList<>();
        for (String pointCode : pointCodes) {
            Optional<Locations> foundLocation = locationsRepository.findFirstByPointCode(pointCode);
            if (foundLocation.isPresent()) {
                locations.add(foundLocation.get());
            } else {
                throw new Exception("Location not found for point code: " + pointCode);
            }
        }


        // Khởi tạo dữ liệu với điểm xuất phát và điểm kết thúc là cùng một điểm
        int[] startDepots = new int[vehicleNumber];
        int[] endDepots = new int[vehicleNumber];
        for (int i = 0; i < vehicleNumber; i++) {
            startDepots[i] = 0; // Giả sử điểm xuất phát là index 0
            endDepots[i] = 0;   // Quay lại cùng một điểm
        }

        // Tạo RoutingIndexManager và RoutingModel từ ma trận khoảng cách
        RoutingIndexManager manager = new RoutingIndexManager(distanceMatrix.length, vehicleNumber, startDepots, endDepots);
        RoutingModel routing = new RoutingModel(manager);

        // Đăng ký callback để tính toán chi phí giữa các điểm
        final int transitCallbackIndex = routing.registerTransitCallback((long fromIndex, long toIndex) -> {
            int fromNode = manager.indexToNode(fromIndex);
            int toNode = manager.indexToNode(toIndex);
            return distanceMatrix[fromNode][toNode];  // Trả về khoảng cách từ ma trận
        });

        // Thiết lập chi phí cho các arc
        routing.setArcCostEvaluatorOfAllVehicles(transitCallbackIndex);

        // Thêm ràng buộc khoảng cách (ví dụ: giới hạn 3000 km)
        routing.addDimension(transitCallbackIndex, 0, 500000, true, "Distance");
        RoutingDimension distanceDimension = routing.getMutableDimension("Distance");
        distanceDimension.setGlobalSpanCostCoefficient(100);

        // Cài đặt chiến lược tìm giải pháp
        RoutingSearchParameters searchParameters = main.defaultRoutingSearchParameters()
                .toBuilder()
                .setFirstSolutionStrategy(FirstSolutionStrategy.Value.PATH_CHEAPEST_ARC)
                .setLogSearch(true)
                .build();
        // Giải quyết vấn đề
        Assignment solution = routing.solveWithParameters(searchParameters);

        // Kiểm tra xem có giải pháp không
        if (solution == null) {
            throw new Exception("No solution found for the routing problem.");
        }

        // Trả về các tuyến đường tối ưu dưới dạng mã điểm
        return getSolution(manager, routing, solution, pointCodes, vehicleNumber);
    }

    private List<List<String>> getSolution(RoutingIndexManager manager, RoutingModel routing, Assignment solution, List<String> pointCodes, int vehicleNumber) {
        List<List<String>> routes = new ArrayList<>();

        for (int i = 0; i < vehicleNumber; ++i) {
            List<String> route = new ArrayList<>();
            long index = routing.start(i); // Lấy điểm xuất phát cho xe i
            boolean isFirstNode = true;

            while (!routing.isEnd(index)) {
                int nodeIndex = manager.indexToNode(index); // Chuyển đổi chỉ số index thành node index
                String pointCode = pointCodes.get(nodeIndex);

                // Kiểm tra và thêm điểm đầu tiên hoặc các điểm chưa có trong danh sách
                if (isFirstNode || !route.contains(pointCode)) {
                    route.add(pointCode);
                    isFirstNode = false; // Đã thêm điểm đầu tiên, bỏ qua kiểm tra tiếp theo
                }

                index = solution.value(routing.nextVar(index)); // Lấy node kế tiếp
            }

            // Thêm điểm xuất phát vào cuối tuyến đường để quay lại depot
            route.add(pointCodes.get(manager.indexToNode(routing.start(i))));
            routes.add(route);

            System.out.println("Route for vehicle " + i + ": " + route); // In ra tuyến đường của xe i
        }

        return routes;
    }
}