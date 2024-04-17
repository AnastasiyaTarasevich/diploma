package com.example.diploma.controllers;

import com.example.diploma.dto.OrderItemDTO;
import com.example.diploma.models.Order;
import com.example.diploma.models.OrderItem;
import com.example.diploma.models.OrderStatus;
import com.example.diploma.models.Supplier;
import com.example.diploma.repos.SupplierRepo;
import com.example.diploma.services.OrderService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final SupplierRepo supplierRepo;

    public OrderController(OrderService orderService, SupplierRepo supplierRepo) {
        this.orderService = orderService;
        this.supplierRepo = supplierRepo;
    }



    @GetMapping
    public ResponseEntity<List<OrderItemDTO>>  getOrdersByDeliveryDateAndStatus(
            @RequestParam("deliveryDate")  @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate deliveryDate) {
        List<Order> orders = orderService.getOrdersByDeliveryDateAndStatus(deliveryDate);

        List<OrderItemDTO> orderItemDTOs = new ArrayList<>();
        for (Order order : orders) {
            List<OrderItem> orderItems = order.getOrderItems();
            for (OrderItem orderItem : orderItems) {
                OrderStatus status = orderItem.getStatus(); // Получаем статус OrderItem

                // Проверяем статус OrderItem для фильтрации
                if (isValidStatus(status)) {
                    Supplier supplier = orderItem.getSupplier(); // Получаем поставщика из OrderItem
                    if (supplier != null) {
                        String companyName = supplier.getCompanyName();
                        OrderItemDTO dto = new OrderItemDTO(companyName);
                        orderItemDTOs.add(dto);
                    }
                }

            }
        }

        return ResponseEntity.ok().body(orderItemDTOs);
    }
    private boolean isValidStatus(OrderStatus status) {
        // Проверяем, что статус соответствует одному из допустимых значений
        return status != null && (status.equals(OrderStatus.В_СБОРКЕ) || status.equals(OrderStatus.ДОСТАВЛЕН) || status.equals(OrderStatus.ПЕРЕДАН_В_ДОСТАВКУ));
    }

}
