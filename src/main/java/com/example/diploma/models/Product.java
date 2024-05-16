package com.example.diploma.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name= "product")
public class Product {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id_product")
    private int idProduct;
    @Basic
    @Column(name = "name")
    private String name;
    @Basic
    @Column(name = "price")
    private double price;
    @Basic
    @Column(name = "supplier_id")
    private Integer supplierId;
    @Basic
    @Column(name = "description")
    private String description;
    @Basic
    @Column(name = "vendor_code", nullable = false)
    private String vendor_code;
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<PriceInTime> prices;

    @Basic
    @Column(name="status", nullable = true)
    private String status;
    public void addPrice(PriceInTime price) {
        prices.add(price);
        price.setProduct(this); // Устанавливаем ссылку на этот продукт у добавленной цены
    }
    @PrePersist
    private void init()
    {
        dateOfCreated = LocalDateTime.now();
    }
    public void addImagetoProduct(Image image)
    {
        image.setProduct(this);
        images.add(image);
    }

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "product")
    private List<Image> images = new ArrayList<>();
    private Long previewImageId;
    private LocalDateTime dateOfCreated;

    @ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.EAGER)
    private Category category;
    @ManyToOne
    @JoinColumn(name = "supplier_id", referencedColumnName = "idsupplier", insertable = false, updatable = false)
    private Supplier supplier;
    @OneToMany(mappedBy = "product")
    private List<OrderItem> orderItems;


}
