package com.example.diploma.services;

import com.example.diploma.models.*;
import com.example.diploma.repos.ShipmentRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ShipmentService {
    private final ShipmentRepo shipmentRepo;
    public void createShipmentForOrderItem(List<OrderItem> orderItems) {
        Shipment shipment = new Shipment();
        shipment.setCreationDate(new Date(System.currentTimeMillis())); // устанавливаем текущую дату и время
        if (!orderItems.isEmpty()) {
            Supplier supplier = orderItems.get(0).getSupplier();
            shipment.setSupplier(supplier);
        }

        shipment.setOrderItems(orderItems);
        shipment.setStatus(ShipmentStatus.В_ДОСТАВКЕ);

        shipmentRepo.save(shipment);
    }

}
