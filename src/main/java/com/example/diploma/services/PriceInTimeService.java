package com.example.diploma.services;

import com.example.diploma.models.PriceInTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PriceInTimeService {

    private final EntityManager entityManager;

    @Autowired
    public PriceInTimeService(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public List<Map<String, Object>> getAllPriceHistoryForAllSuppliers() {
        List<Map<String, Object>> priceHistory = new ArrayList<>();

        // Здесь предполагается, что ваш класс PriceInTime имеет связь с классом Supplier
        // и имеет доступ к информации о поставщиках

        // Запрос для получения данных об изменении цен для всех поставщиков
        // Предположим, что у вас есть таблица price_in_time, которая содержит информацию об изменении цен
        String queryString = "SELECT s.companyName AS supplier, p.name AS product, pit.oldPrice, pit.newPrice, pit.changeDate " +
                "FROM PriceInTime pit " +
                "JOIN pit.product p " +
                "JOIN p.supplier s " +
                "ORDER BY s.companyName, p.name, pit.changeDate";

        Query query = entityManager.createQuery(queryString);
        List<Object[]> results = query.getResultList();

        // Группировка данных по поставщикам и продуктам
        Map<String, List<Map<String, Object>>> supplierMap = new HashMap<>();
        for (Object[] result : results) {
            String supplierName = (String) result[0];
            String productName = (String) result[1];
            double oldPrice = (double) result[2];
            double newPrice = (double) result[3];
            String changeDate = result[4].toString(); // Предположим, что changeDate имеет тип java.time.LocalDate

            // Создание объекта цены
            Map<String, Object> priceData = new HashMap<>();
            priceData.put("name", productName);
            priceData.put("prices", List.of(Map.of("changeDate", changeDate, "newPrice", newPrice))); // Создание списка цен для продукта

            // Добавление цены в список цен для поставщика
            if (!supplierMap.containsKey(supplierName)) {
                supplierMap.put(supplierName, new ArrayList<>());
            }
            supplierMap.get(supplierName).add(priceData);
        }

        // Преобразование Map в список для удобства
        for (Map.Entry<String, List<Map<String, Object>>> entry : supplierMap.entrySet()) {
            Map<String, Object> supplierData = new HashMap<>();
            supplierData.put("supplier", entry.getKey());
            supplierData.put("products", entry.getValue());
            priceHistory.add(supplierData);
        }

        return priceHistory;
    }
}
