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
@Table(name = "supplier_review")
public class SupplierReview {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "idsupplier_review")
    private int idsupplierReview;
    @Basic
    @Column(name = "grade")
    private double grade;
    @Basic
    @Column(name = "comments")
    private String comments;
    @Basic
    @Column(name = "date_of_create")
    private Date dateOfCreate;
    @ManyToOne
    @JoinColumn(name = "id_supplier", referencedColumnName = "idsupplier")
    private Supplier supplier;
}
