package com.example.diploma.repos;


import com.example.diploma.models.Order;
import com.example.diploma.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepo extends JpaRepository<Order, Integer> {
    List<Order> findOrderByUser(User user);
}
