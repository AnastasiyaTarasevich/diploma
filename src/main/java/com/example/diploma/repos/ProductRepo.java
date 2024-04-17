package com.example.diploma.repos;


import com.example.diploma.models.Category;
import com.example.diploma.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProductRepo extends JpaRepository<Product, Integer> {
    @Query("SELECT p FROM Product p WHERE (p.name LIKE %?1% OR p.vendor_code LIKE %?1%) AND p.supplierId = ?2")
    List<Product> search(String keyword, int supplierId);

    List <Product> findProductByCategoryAndSupplierId(Category category, int id);
    @Query(value = "SELECT min(price) FROM Product ")
    BigDecimal minProductPrice();

    @Query(value = "SELECT max(price) FROM Product ")
    BigDecimal maxProductPrice();
    List<Product> findProductsBySupplierId(int supplierId);


}
