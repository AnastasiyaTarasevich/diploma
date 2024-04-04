package com.example.diploma.repos;


import com.example.diploma.models.Shipment;
import com.example.diploma.models.Supplier;
import com.example.diploma.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShipmentRepo extends JpaRepository<Shipment,Integer> {
    List<Shipment> findBySupplier(Supplier supplier);

    @Query("SELECT DISTINCT s FROM Shipment s JOIN FETCH s.orderItems oi WHERE oi.order.user = :user")
    List<Shipment> findDistinctShipmentsByUser(@Param("user") User user);


}
