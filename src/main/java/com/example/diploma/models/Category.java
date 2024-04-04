package com.example.diploma.models;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.util.List;

@Data
@Slf4j
@Entity
public class Category {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "idcategory", nullable = false)
    private int idcategory;
    @Basic
    @Column(name = "name", nullable = false, length = 45)
    private String name;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "category")
    private List<Product> product;
    public void addProduct(Product product1)
    {
        product.add(product1);

    }

}
