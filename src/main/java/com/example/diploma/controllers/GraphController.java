package com.example.diploma.controllers;

import com.example.diploma.models.OrderItem;
import com.example.diploma.models.Supplier;
import com.example.diploma.models.SupplierDefect;
import com.example.diploma.repos.OrderItemRepo;
import com.example.diploma.repos.SupplierDefectRepo;
import com.example.diploma.repos.SupplierRepo;
import com.example.diploma.services.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class GraphController {

    @Autowired
    private  SupplierDefectRepo supplierDefectRepo;
    @Autowired
    private SupplierService supplierService;
    @Autowired
    private SupplierRepo supplierRepo;



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


}
