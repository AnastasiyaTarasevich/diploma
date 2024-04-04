package com.example.diploma.repos;


import com.example.diploma.models.Order;
import com.example.diploma.models.OrderItem;
import com.example.diploma.models.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepo extends JpaRepository<OrderItem, Integer> {
    List<OrderItem> findBySupplier(Supplier supplier);
    List <OrderItem> findByOrder(Order order);
    OrderItem findByProduct_Name(String name);
}
