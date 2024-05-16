package com.example.diploma.services;

import com.example.diploma.models.Category;
import com.example.diploma.models.Product;
import com.example.diploma.repos.CategoryRepo;
import com.example.diploma.repos.ProductRepo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Data
@AllArgsConstructor
@Service
public class CategoryService
{
    private final CategoryRepo categoryRepo;
    private final ProductRepo productRepo;

    public void saveCat(int idprod, String name)
    {
        Category category = categoryRepo.findByName(name).orElse(null);
        if (category==null) {
            category = new Category();
            category.setName(name);
//           Product productFromDB = productRepo.findById(idprod).orElse(null);
//           if (productFromDB != null) {
//               category.addProduct(productFromDB);
//           }
            categoryRepo.save(category);
        }
    }

    public Iterable<Category> findAll() {
        return categoryRepo.findAll();
    }

    public Category findByName(String name)
    {
        return categoryRepo.findByName(name).orElseThrow();
    }
    public List<Category> getAllCategoriesForSupplier(int supplierId) {
        // Загрузка всех товаров для указанного поставщика
        List<Product> products = productRepo.findProductsBySupplierIdAndStatus(supplierId,null);

        // Собираем уникальные категории товаров данного поставщика
        Set<Category> categories = new HashSet<>();
        for (Product product : products) {
            categories.add(product.getCategory());
        }

        return new ArrayList<>(categories);
    }

}
