package com.example.diploma.repos;


import com.example.diploma.models.ShipmentsFailures;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShipmentFailersRepo extends JpaRepository<ShipmentsFailures,Integer>
{
}
