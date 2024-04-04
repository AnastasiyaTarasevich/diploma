package com.example.diploma.repos;


import com.example.diploma.models.Supplier;
import com.example.diploma.models.SupplierReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupplierReviewRepo extends JpaRepository<SupplierReview,Integer> {
    List<SupplierReview> findSupplierReviewBySupplier(Supplier supplier);
}
