package com.example.diploma.repos;


import com.example.diploma.models.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepo extends JpaRepository<Payment,Integer> {

    List<Payment> findByContract_Supplier_Idsupplier(int supplierId);
}
