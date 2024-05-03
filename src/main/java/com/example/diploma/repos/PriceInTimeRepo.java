package com.example.diploma.repos;

import com.example.diploma.models.PriceInTime;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PriceInTimeRepo extends JpaRepository< PriceInTime,Long> {
    List<PriceInTime> findByProductSupplierId(int supplierId);
}
