package com.example.diploma.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "supplier_defect")
public class SupplierDefect {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "idsupplier_defect")
    private int idsupplierDefect;
    @Basic
    @Column(name = "name_product")
    private String nameProduct;
    @Basic
    @Column(name = "date")
    private Date date;
    @Basic
    @Column(name = "description")
    private String description;
    @Basic
    @Column(name = "quantity")
    private int quantity;
    @ManyToOne
    @JoinColumn(name = "shipment_id", referencedColumnName = "idshipment")
    private Shipment shipment;
}
