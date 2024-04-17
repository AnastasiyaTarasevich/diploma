package com.example.diploma.repos;


import com.example.diploma.models.Order;
import com.example.diploma.models.OrderStatus;
import com.example.diploma.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

public interface OrderRepo extends JpaRepository<Order, Integer> {
    List<Order> findOrderByUser(User user);
    @Query("SELECT o FROM Order o JOIN o.orderItems oi " +
            "WHERE o.date_for_sh = :deliveryDate " +
            "AND (oi.status = 'В_СБОРКЕ' OR oi.status = 'ПЕРЕДАН_В_ДОСТАВКУ' OR oi.status = 'ДОСТАВЛЕН')")
    List<Order> findOrdersByDeliveryDateAndStatus(LocalDate deliveryDate);


}
