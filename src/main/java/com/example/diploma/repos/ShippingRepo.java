package com.example.diploma.repos;


import com.example.diploma.models.Shipping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShippingRepo extends JpaRepository<Shipping, Integer> {

    List<Shipping> findShippingByIdSupplier(int idSupplier);

    @Query("SELECT p.price FROM Shipping p WHERE p.city LIKE %?1%")
    double getPriceforCity(String city);

}
