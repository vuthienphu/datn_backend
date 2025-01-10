package com.example.routeplanner.service.implement;

import com.example.routeplanner.model.*;
import com.example.routeplanner.repository.*;
import com.example.routeplanner.service.DistanceMatrixService;
import com.example.routeplanner.service.OptimizeRouteService;
import com.example.routeplanner.service.VehicleNumberService;
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

    @Autowired
    private VehicleNumberRepository vehicleNumberRepository;

    @Autowired
    private VehicleNumberService vehicleNumberService;
/*
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
*/

    public List<List<String>> optimizeRoute(
            String routeCode,
            List<String> pointCodes,
            int vehicleNumber
    ) throws Exception {
        // Lấy cấu hình
        Double maxDistanceVehicles = configRepository.findConfig("max_distance_vehicles");
        Double costCoefficient = configRepository.findConfig("cost_coefficient");

        // Kiểm tra trạng thái cấu hình
        if (!Boolean.TRUE.equals(configRepository.findConfigStatus("max_distance_vehicles"))) {
            throw new Exception("Configuration for max_distance_vehicles is not active.");
        }
        if (!Boolean.TRUE.equals(configRepository.findConfigStatus("cost_coefficient"))) {
            throw new Exception("Configuration for cost_coefficient is not active.");
        }

        // Tính toán ma trận khoảng cách
        long[][] distanceMatrix = distanceMatrixService.calculateDistanceMatrix(routeCode, pointCodes);
        if (distanceMatrix == null || distanceMatrix.length == 0) {
            throw new Exception("Distance matrix is invalid or empty.");
        }

        // Khởi tạo dữ liệu đầu vào cho solver
        int[] startDepots = new int[vehicleNumber];
        int[] endDepots = new int[vehicleNumber];
        for (int i = 0; i < vehicleNumber; i++) {
            startDepots[i] = 0; // Điểm bắt đầu là index 0
            endDepots[i] = 0;   // Điểm kết thúc quay lại depot
        }

        // Tạo RoutingIndexManager và RoutingModel
        RoutingIndexManager manager = new RoutingIndexManager(distanceMatrix.length, vehicleNumber, startDepots, endDepots);
        RoutingModel routing = new RoutingModel(manager);

        // Đăng ký callback để tính chi phí giữa các điểm
        final int transitCallbackIndex = routing.registerTransitCallback((long fromIndex, long toIndex) -> {
            int fromNode = manager.indexToNode(fromIndex);
            int toNode = manager.indexToNode(toIndex);
            return distanceMatrix[fromNode][toNode];
        });

        // Thiết lập chi phí
        routing.setArcCostEvaluatorOfAllVehicles(transitCallbackIndex);

        // Thêm ràng buộc khoảng cách
        routing.addDimension(
                transitCallbackIndex,
                0, // Không có thời gian chờ
                maxDistanceVehicles.longValue(),
                true,
                "Distance"
        );
        RoutingDimension distanceDimension = routing.getMutableDimension("Distance");
        distanceDimension.setGlobalSpanCostCoefficient(costCoefficient.longValue());

        // Cài đặt chiến lược tìm kiếm
        RoutingSearchParameters searchParameters = main.defaultRoutingSearchParameters()
                .toBuilder()
                .setFirstSolutionStrategy(FirstSolutionStrategy.Value.PATH_CHEAPEST_ARC)
                .setLogSearch(true)
                .build();

        // Giải quyết bài toán
        Assignment solution = routing.solveWithParameters(searchParameters);
        if (solution == null) {
            throw new Exception("No solution found for the routing problem.");
        }

        // Lấy kết quả
        List<List<String>> optimizedRoutes = getSolution(manager, routing, solution, pointCodes, vehicleNumber);

        // Lưu kết quả vào cơ sở dữ liệu
        for (List<String> route : optimizedRoutes) {
            saveOptimizedRoute(routeCode, route);
        }

        System.out.println("Objective (total cost): " + solution.objectiveValue());
        return optimizedRoutes;
    }

    public List<List<String>> getSolution(
            RoutingIndexManager manager,
            RoutingModel routing,
            Assignment solution,
            List<String> pointCodes,
            int vehicleNumber
    ) {
        List<List<String>> vehicleRoutes = new ArrayList<>();

        for (int i = 0; i < vehicleNumber; ++i) {
            List<String> routeForVehicle = new ArrayList<>();
            long index = routing.start(i); // Lấy chỉ số điểm xuất phát của xe

            // Lặp qua các điểm trong tuyến đường của xe
            while (!routing.isEnd(index)) { // Lặp cho đến khi đến điểm kết thúc
                int nodeIndex = manager.indexToNode(index); // Chuyển index thành node index
                routeForVehicle.add(pointCodes.get(nodeIndex)); // Thêm mã điểm vào danh sách
                index = solution.value(routing.nextVar(index)); // Lấy điểm tiếp theo
            }

            // Kiểm tra và chỉ thêm điểm quay lại depot (HTC) nếu nó chưa có ở cuối
            String depot = pointCodes.get(manager.indexToNode(routing.start(i)));

            // Chỉ thêm depot vào đầu danh sách nếu nó không phải là điểm đầu tiên
            if (routeForVehicle.isEmpty() || !routeForVehicle.get(0).equals(depot)) {
                routeForVehicle.add(0, depot); // Thêm điểm quay lại depot ở đầu
            }

            // Loại bỏ điểm depot trùng lặp ở đầu danh sách
            if (routeForVehicle.size() > 1 && routeForVehicle.get(1).equals(depot)) {
                routeForVehicle.remove(1); // Xóa điểm depot trùng lặp
            }

            // Kiểm tra và chỉ thêm điểm quay lại depot (HTC) nếu nó chưa có ở cuối
            if (!routeForVehicle.get(routeForVehicle.size() - 1).equals(depot)) {
                routeForVehicle.add(depot); // Thêm điểm quay lại depot chỉ khi chưa có
            }

            vehicleRoutes.add(routeForVehicle); // Thêm tuyến đường của xe vào danh sách tổng
        }

        // In tuyến đường của từng xe để kiểm tra
        for (int i = 0; i < vehicleRoutes.size(); i++) {
            System.out.println("Route for vehicle " + i + ": " + vehicleRoutes.get(i));
        }

        return vehicleRoutes; // Trả về danh sách các tuyến
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
 public OptimizeRouteData getOptimizeRouteByRouteCode(String routeCode) {
     // Lấy dữ liệu tuyến đường từ cơ sở dữ liệu
     List<OptimizeRoute> routeData = optimizeRouteRepository.findByRouteCodeWithRoute(routeCode);


     // Lấy số xe từ VehicleNumber (nếu cần thiết, để kiểm tra số tuyến)
     int vehicleNumber = vehicleNumberRepository.findVehicleNumber(routeCode);
     System.out.println("Vehicle Number (số tuyến): " + vehicleNumber);

     OptimizeRouteData optimizeRouteData = new OptimizeRouteData();
     optimizeRouteData.setRouteCode(routeCode);

     // Khởi tạo danh sách các tuyến đường
     List<List<String>> optimizeRouteCoordinates = new ArrayList<>();
     List<String> currentRoute = new ArrayList<>();
     Set<String> addedRoutes = new HashSet<>(); // Set để theo dõi các tuyến đã thêm

     for (OptimizeRoute route : routeData) {
         String currentPoint = route.getPointCode().getPointCode();

         // Nếu sequence = 1, bắt đầu một tuyến mới
         if (route.getSequence() == 1 && currentRoute.size() > 1) {
             // Nếu currentRoute không rỗng, thêm vào danh sách tuyến
             if (!currentRoute.isEmpty()) {
                 // Nếu tuyến khép kín (điểm đầu = điểm cuối), loại bỏ điểm cuối trùng điểm đầu
                 if (currentRoute.size() > 1 && currentRoute.get(0).equals(currentRoute.get(currentRoute.size() - 1))) {
                     currentRoute.remove(currentRoute.size() - 1);
                 }
                 // Thêm điểm đầu vào cuối để tạo thành vòng khép kín
                 if (currentRoute.size() > 1) {
                     currentRoute.add(currentRoute.get(0));
                 }

                 // Chuyển đổi currentRoute thành một chuỗi để kiểm tra trùng lặp
                 String routeKey = String.join(",", currentRoute);
                 if (!addedRoutes.contains(routeKey)) {
                     optimizeRouteCoordinates.add(new ArrayList<>(currentRoute));
                     addedRoutes.add(routeKey); // Đánh dấu tuyến đường đã thêm
                 }
                 currentRoute.clear();
             }
         }

         // Chỉ thêm điểm vào tuyến nếu nó khác điểm cuối cùng đã thêm
         if (currentRoute.isEmpty() || !currentPoint.equals(currentRoute.get(currentRoute.size() - 1))) {
             currentRoute.add(currentPoint);
         }
     }

     // Thêm tuyến cuối cùng vào danh sách nếu có dữ liệu
     if (!currentRoute.isEmpty()) {
         // Kiểm tra nếu tuyến khép kín (điểm đầu = điểm cuối)
         if (currentRoute.size() > 1 && currentRoute.get(0).equals(currentRoute.get(currentRoute.size() - 1))) {
             currentRoute.remove(currentRoute.size() - 1);
         }
         // Thêm điểm đầu vào cuối để tạo thành vòng khép kín
         if (!currentRoute.isEmpty()) {
             currentRoute.add(currentRoute.get(0));
         }

         // Chuyển đổi currentRoute thành một chuỗi để kiểm tra trùng lặp
         String routeKey = String.join(",", currentRoute);
         if (!addedRoutes.contains(routeKey)) {
             optimizeRouteCoordinates.add(new ArrayList<>(currentRoute));
             addedRoutes.add(routeKey); // Đánh dấu tuyến đường đã thêm
         }
     }

     // Loại bỏ các tuyến trống
     optimizeRouteCoordinates.removeIf(List::isEmpty);

     // Gán số lượng phương tiện và danh sách tuyến
     optimizeRouteData.setOptimizeRouteCoordinates(optimizeRouteCoordinates);
     optimizeRouteData.setVehicleNumber(optimizeRouteCoordinates.size()); // Đảm bảo số lượng xe đúng

     return optimizeRouteData;
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
