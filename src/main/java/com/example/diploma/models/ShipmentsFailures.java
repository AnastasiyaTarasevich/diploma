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
@Table(name = "shipments_failures")
public class ShipmentsFailures {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "idshipments_failures")
    private int idshipmentsFailures;
    @Basic
    @Column(name = "date")
    private Date date;
    @Basic
    @Column(name = "description")
    private String description;
    @ManyToOne
    private Shipment shipment;

}
