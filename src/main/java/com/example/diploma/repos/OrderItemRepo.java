package com.example.diploma.repos;


import com.example.diploma.models.Order;
import com.example.diploma.models.OrderItem;
import com.example.diploma.models.OrderStatus;
import com.example.diploma.models.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderItemRepo extends JpaRepository<OrderItem, Integer> {
    List<OrderItem> findBySupplier(Supplier supplier);
    List <OrderItem> findByOrder(Order order);
    OrderItem findByProduct_Name(String name);
    @Query("SELECT SUM(oi.quantity) FROM OrderItem oi WHERE oi.supplier = :supplier AND oi.status = :status")
    int sumQuantityBySupplierAndStatus(@Param("supplier") Supplier supplier, @Param("status") OrderStatus status);


    List<OrderItem> findBySupplierAndStatus(Supplier supplier, OrderStatus orderStatus);
}
