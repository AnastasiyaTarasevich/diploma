package com.example.diploma.models;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class Shipping {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "idshipping", nullable = false)
    private int idshipping;
    @Basic
    @Column(name = "city", nullable = false, length = 255)
    private String city;
    @Basic
    @Column(name = "price", nullable = false, precision = 0)
    private double price;
    @Basic
    @Column(name="id_supplier", nullable = false)
    private int idSupplier;
    @ManyToOne
    @JoinColumn(name = "id_supplier", referencedColumnName = "idsupplier", insertable = false, updatable = false)
    private Supplier supplier;

}
