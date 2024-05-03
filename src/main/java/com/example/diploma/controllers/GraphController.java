package com.example.diploma.controllers;

import com.example.diploma.models.*;
import com.example.diploma.repos.*;
import com.example.diploma.services.PriceInTimeService;
import com.example.diploma.services.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class GraphController {

    @Autowired
    private  SupplierDefectRepo supplierDefectRepo;
    @Autowired
    private SupplierService supplierService;
    @Autowired
    private SupplierRepo supplierRepo;
    @Autowired
    private OrderItemRepo orderItemRepo;
    @Autowired
    private ShipmentRepo shipmentRepo;
    @Autowired
    private ProductRepo productRepo;



    @GetMapping("/supplier-defects")

    public Map<String, Map<String, Double>> getDefectCountsBySupplierAndMonth() {
        List<SupplierDefect> defects = supplierDefectRepo.findAll();
        Map<String, Map<String, Integer>> defectCounts = new HashMap<>();


        for (SupplierDefect defect : defects) {
            String supplierName = defect.getShipment().getSupplier().getCompanyName();
            String month = defect.getDate().toLocalDate().getMonth().toString();
            int defectQuantity = defect.getQuantity();
            // Получаем или создаем подструктуру для данного поставщика
            Map<String, Integer> supplierDefects = defectCounts.computeIfAbsent(supplierName, k -> new HashMap<>());
            // Увеличиваем количество дефектов для данного месяца у данного поставщика
            supplierDefects.put(month, supplierDefects.getOrDefault(month, 0) + defectQuantity);
        }

        Map<String, Map<String, Double>> defectPercentage = new HashMap<>();
        for (Map.Entry<String, Map<String, Integer>> entry : defectCounts.entrySet()) {
            String supplierName = entry.getKey();
            Map<String, Integer> defectsByMonth = entry.getValue();
            Map<String, Double> defectPercentageByMonth = new HashMap<>();

            for (Map.Entry<String, Integer> defectEntry : defectsByMonth.entrySet()) {
                String month = defectEntry.getKey();
                int defectQuantity = defectEntry.getValue();
                Supplier supplier=supplierRepo.findSupplierByCompanyName(supplierName);
                int totalDeliveryQuantity = supplierService.getDeliveryQuantityBySupplierAndMonth(supplier, month);

                // Вычисляем процентное соотношение дефектов к общему числу доставленных товаров в этом месяце
                double percentage = (defectQuantity / (double) totalDeliveryQuantity) * 100.0;
                defectPercentageByMonth.put(month, percentage);
            }

            defectPercentage.put(supplierName, defectPercentageByMonth);
        }


        return defectPercentage;


    }
    @GetMapping("/delivered_categories")
    public Map<String, Integer> countProductsByCategory() {
        List<OrderItem> deliveredOrderItems = orderItemRepo.findByStatus(OrderStatus.ДОСТАВЛЕН);
        Map<String, Integer> productsByCategory = new HashMap<>();

        for (OrderItem orderItem : deliveredOrderItems) {
            String categoryName = orderItem.getProduct().getCategory().getName();
            productsByCategory.put(categoryName, productsByCategory.getOrDefault(categoryName, 0) + orderItem.getQuantity());
        }

        return productsByCategory;
    }

    @GetMapping("/order-status-count")
    public Map<String, Double> getOrderStatusPercentage() {
        List<OrderItem> orderItems = orderItemRepo.findAll();
        Map<String, Integer> statusCount = new HashMap<>();
        int totalOrders = orderItems.size();

        // Получаем количество заказов для каждого статуса
        for (OrderItem orderItem : orderItems) {
            OrderStatus status = orderItem.getStatus();
            String statusName = status.name();
            statusCount.put(statusName, statusCount.getOrDefault(statusName, 0) + 1);
        }

        // Вычисляем процентное соотношение для каждого статуса
        Map<String, Double> statusPercentage = new HashMap<>();
        for (Map.Entry<String, Integer> entry : statusCount.entrySet()) {
            String statusName = entry.getKey();
            int count = entry.getValue();
            double percentage = (double) count / totalOrders * 100;
            statusPercentage.put(statusName, percentage);
        }

        return statusPercentage;
    }

    @GetMapping("/purchase-volume-by-supplier")
    public Map<String, Double> getPurchaseVolumeBySupplier() {
        List<OrderItem> orderItems = orderItemRepo.findAll();
        Map<String, Double> purchaseVolumeBySupplier = new HashMap<>();

        // Вычисляем общий объем закупок для каждого поставщика
        for (OrderItem orderItem : orderItems) {
            String supplierName = orderItem.getSupplier().getCompanyName();
            double purchaseVolume = orderItem.getQuantity() * orderItem.getProduct().getPrice();

            // Обновляем или добавляем значение объема закупок для данного поставщика
            purchaseVolumeBySupplier.merge(supplierName, purchaseVolume, Double::sum);
        }

        return purchaseVolumeBySupplier;
    }
    @GetMapping("/average_delivery_time_by_supplier")
    public Map<String, Double> getAverageDeliveryTimeBySupplier() {
        // Получаем все отправки из базы данных
        List<Shipment> shipments = shipmentRepo.findAll();

        // Группируем отправки по поставщику и вычисляем среднее время доставки для каждого поставщика
        Map<String, Double> averageDeliveryTimeBySupplier = shipments.stream()
                .filter(shipment -> shipment.getStatus() == ShipmentStatus.ДОСТАВЛЕНО) // Рассматриваем только доставленные отправки
                .collect(Collectors.groupingBy(shipment -> shipment.getSupplier().getCompanyName(),
                        Collectors.averagingLong(shipment -> ChronoUnit.DAYS.between(shipment.getCreationDate().toInstant(), shipment.getArrivalDate().toInstant()))));

        return averageDeliveryTimeBySupplier;
    }
    @GetMapping("/all")
    public List<Map<String, Object>> getAllPriceHistoryForAllSuppliers() {
        List<Map<String, Object>> suppliersData = new ArrayList<>();

        // Получаем все продукты
        List<Product> allProducts = productRepo.findAll();

        // Группируем продукты по поставщикам
        Map<Integer, List<Product>> productsBySupplier = new HashMap<>();
        for (Product product : allProducts) {
            int supplierId = product.getSupplier().getIdsupplier();
            if (!productsBySupplier.containsKey(supplierId)) {
                productsBySupplier.put(supplierId, new ArrayList<>());
            }
            productsBySupplier.get(supplierId).add(product);
        }

        // Проходимся по каждому поставщику и формируем данные в нужном формате
        for (Map.Entry<Integer, List<Product>> entry : productsBySupplier.entrySet()) {
            int supplierId = entry.getKey();
            List<Product> products = entry.getValue();

            Map<String, Object> supplierData = new HashMap<>();
            supplierData.put("id", supplierId);
            supplierData.put("name", products.get(0).getSupplier().getCompanyName());

            List<Map<String, Object>> productsData = new ArrayList<>();
            for (Product product : products) {
                Map<String, Object> productData = new HashMap<>();
                productData.put("name", product.getName());

                List<Map<String, Object>> pricesData = new ArrayList<>();
                for (PriceInTime price : product.getPrices()) {
                    Map<String, Object> priceData = new HashMap<>();
                    priceData.put("changeDate", price.getChangeDate());
                    priceData.put("newPrice", price.getNewPrice());
                    pricesData.add(priceData);
                }
                productData.put("prices", pricesData);

                productsData.add(productData);
            }
            supplierData.put("products", productsData);

            suppliersData.add(supplierData);
        }

        return suppliersData;
    }




}




