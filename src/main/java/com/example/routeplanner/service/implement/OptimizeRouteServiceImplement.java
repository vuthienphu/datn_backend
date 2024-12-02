package com.example.routeplanner.service.implement;

import com.example.routeplanner.model.Locations;
import com.example.routeplanner.model.OptimizeRoute;
import com.example.routeplanner.model.OptimizeRouteDTO;
import com.example.routeplanner.model.Route;
import com.example.routeplanner.repository.*;
import com.example.routeplanner.service.DistanceMatrixService;
import com.example.routeplanner.service.OptimizeRouteService;
import com.google.ortools.Loader;
import com.google.ortools.constraintsolver.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class OptimizeRouteServiceImplement implements OptimizeRouteService {

    static {
        Loader.loadNativeLibraries();
    }

    @Autowired
    private DistanceMatrixService distanceMatrixService;

    @Autowired
    private LocationsRepository locationsRepository;

    @Autowired
    private ConfigRepository configRepository;

    @Autowired
    private OptimizeRouteRepository optimizeRouteRepository;  // Add repository for OptimizeRoute


    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private DistanceMatrixRepository distanceMatrixRepository;

    public List<String> optimizeRoute(String routeCode, List<String> pointCodes, int vehicleNumber) throws Exception {

        Double maxDistanceVehicles = configRepository.findConfig("max_distance_vehicles");
        Double costCoefficient = configRepository.findConfig("cost_coefficient");

        // Kiểm tra trạng thái của config

        try {
            Boolean isMaxDistanceVehicles = configRepository.findConfigStatus("max_distance_vehicles");

            if (isMaxDistanceVehicles == null || !isMaxDistanceVehicles) {
                throw new Exception("Configuration for max_distance_vehicles is not active.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return List.of("Error occurred while checking max_distance_vehicles configuration.");// In ra thông báo lỗi trên terminal
        }
        try {
            Boolean isCostCoefficient = configRepository.findConfigStatus("cost_coefficient");


            if (isCostCoefficient == null || !isCostCoefficient) {
                throw new Exception("Configuration for cost_coefficient is not active.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            return List.of("Error occurred while checking cost_coefficient configuration.");
        }
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
        routing.addDimension(transitCallbackIndex, 0, maxDistanceVehicles.longValue(), true, "Distance");
        RoutingDimension distanceDimension = routing.getMutableDimension("Distance");
        distanceDimension.setGlobalSpanCostCoefficient(costCoefficient.longValue());

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
        long objective = solution.objectiveValue();

        List<String> optimizedRoute = getSolution(manager, routing, solution, pointCodes, vehicleNumber);
        saveOptimizedRoute(routeCode, optimizedRoute);
        System.out.println("Objective (total cost): " + objective);
        return optimizedRoute;

    }


    private List<String> getSolution(RoutingIndexManager manager, RoutingModel routing, Assignment solution, List<String> pointCodes, int vehicleNumber) {
        List<String> flatRoute = new ArrayList<>();

        for (int i = 0; i < vehicleNumber; ++i) {
            long index = routing.start(i); // Lấy điểm xuất phát cho xe i
            boolean isFirstNode = true;

            while (!routing.isEnd(index)) {
                int nodeIndex = manager.indexToNode(index); // Chuyển đổi chỉ số index thành node index
                String pointCode = pointCodes.get(nodeIndex);

                // Kiểm tra và thêm điểm đầu tiên hoặc các điểm chưa có trong danh sách
                if (isFirstNode || !flatRoute.contains(pointCode)) {
                    flatRoute.add(pointCode);
                    isFirstNode = false; // Đã thêm điểm đầu tiên, bỏ qua kiểm tra tiếp theo
                }

                index = solution.value(routing.nextVar(index)); // Lấy node kế tiếp
            }

            // Thêm điểm xuất phát vào cuối tuyến đường để quay lại depot
            flatRoute.add(pointCodes.get(manager.indexToNode(routing.start(i))));
        }
        System.out.println("Optimize Route: " + flatRoute);
        return flatRoute;
    }

    private void saveOptimizedRoute(String routeCode, List<String> optimizedRoute) {
        List<Route> existingRoutes = routeRepository.findAllByRouteCode(routeCode);
        Route route;

        // Nếu có ít nhất một Route, lấy bản ghi đầu tiên
        if (!existingRoutes.isEmpty()) {
            route = existingRoutes.get(0); // Chọn bản ghi đầu tiên
        } else {
            // Nếu không có Route nào, tạo mới
            route = new Route();
            route.setRouteCode(routeCode);  // Gán mã tuyến đường
            route = routeRepository.save(route);  // Lưu Route vào cơ sở dữ liệu
        }

        // Lưu các entry của OptimizeRoute
        int sequence = 1;
        for (String pointCode : optimizedRoute) {
            // Tìm kiếm Location tương ứng với pointCode
            Locations location = locationsRepository.findFirstByPointCode(pointCode)
                    .orElseThrow(() -> new RuntimeException("Location not found for point code: " + pointCode));

            // Tạo đối tượng OptimizeRoute và thiết lập các thuộc tính
            OptimizeRoute optimizeRoute = new OptimizeRoute();
            optimizeRoute.setRouteCode(route);  // Liên kết với Route đã lưu
            optimizeRoute.setPointCode(location);  // Liên kết với Location tìm được
            optimizeRoute.setSequence(sequence++);  // Gán thứ tự và tăng lên sau mỗi vòng lặp

            // Lưu đối tượng OptimizeRoute
            optimizeRouteRepository.save(optimizeRoute);
        }
    }

    @Override
    public List<String> getAllRouteCodes() {
        return optimizeRouteRepository.findAllRouteCodes();
    }

 @Override
    public OptimizeRouteDTO getOptimizeRouteByRouteCode(String routeCode) {
     // Lấy danh sách tuyến đường tối ưu từ bảng route_optimize với JOIN FETCH
     List<OptimizeRoute> optimizeRoutes = optimizeRouteRepository.findByRouteCodeWithRoute(routeCode);

     // Tạo danh sách tọa độ từ các điểm trong optimize_route
     List<String> optimizeRouteCoordinates = optimizeRoutes.stream()
             .sorted((o1, o2) -> o1.getSequence().compareTo(o2.getSequence())) // Sắp xếp theo thứ tự sequence
             .map(optimizeRoute -> optimizeRoute.getPointCode().getPointCode())
             .distinct()// Lấy tên điểm từ bảng locations
             .collect(Collectors.toList());

     if (!optimizeRouteCoordinates.isEmpty() &&
             !optimizeRouteCoordinates.get(0).equals(optimizeRouteCoordinates.get(optimizeRouteCoordinates.size() - 1))) {
         optimizeRouteCoordinates.add(optimizeRouteCoordinates.get(0)); // Thêm lại điểm cuối
     }
     // Tạo và trả về OptimizeRouteDTO
     OptimizeRouteDTO optimizeRouteDTO = new OptimizeRouteDTO();
     optimizeRouteDTO.setRouteCode(routeCode);
     optimizeRouteDTO.setOptimizeRouteCoordinates(optimizeRouteCoordinates);

     return optimizeRouteDTO;
 }
    @Transactional
    public void deleteRouteByRouteCode(String routeCode) {
        // Tìm tất cả các bản ghi trong bảng route có route_code tương ứng
        optimizeRouteRepository.deleteByNativeQuery(routeCode);

        // Xóa tất cả bản ghi trong bảng distance_matrix liên quan đến routeCode
        distanceMatrixRepository.deleteByNativeQuery(routeCode);

        // Xóa tất cả bản ghi trong bảng route liên quan đến routeCode
        routeRepository.deleteByNativeQuery(routeCode);
    }
}
