package com.example.diploma.repos;


import com.example.diploma.models.SupplierDefect;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SupplierDefectRepo extends JpaRepository<SupplierDefect,Integer> {
}
