package com.example.diploma.repos;


import com.example.diploma.models.Shipment;
import com.example.diploma.models.SupplierDefect;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupplierDefectRepo extends JpaRepository<SupplierDefect,Integer> {

    List<SupplierDefect> findAllByShipment(Shipment shipment);
}
