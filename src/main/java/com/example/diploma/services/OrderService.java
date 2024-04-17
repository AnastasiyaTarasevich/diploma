package com.example.diploma.services;

import com.example.diploma.models.Order;
import com.example.diploma.models.OrderItem;
import com.example.diploma.models.OrderStatus;
import com.example.diploma.repos.OrderItemRepo;
import com.example.diploma.repos.OrderRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepo orderRepo;
    private final OrderItemRepo orderItemRepo;

    @Autowired
    public OrderService(OrderRepo orderRepo, OrderItemRepo orderItemRepo) {
        this.orderRepo = orderRepo;
        this.orderItemRepo = orderItemRepo;
    }

    public Order addOrder(Order order) {
        return orderRepo.save(order);
    }

    public void saveOrderItem(OrderItem orderItem) {
        orderItemRepo.save(orderItem);
    }

    public void updateOrder(Order order) {
        orderRepo.save(order);
    }
    public List<Order> findAll() {
        return orderRepo.findAll();
    }
//    public void updateOrderStatus(int orderId, OrderStatus newStatus) {
//        Optional<Order> optionalOrder = orderRepo.findById(orderId);
//
//        optionalOrder.ifPresent(order -> {
//            order.setStatus(newStatus);
//            orderRepo.save(order);
//        });
//    }
public List<Order> getOrdersByDeliveryDateAndStatus(LocalDate startDate) {
    // Выполняем запрос к репозиторию для получения заказов по дате поставки и статусу
    return orderRepo.findOrdersByDeliveryDateAndStatus(startDate);
}
}
