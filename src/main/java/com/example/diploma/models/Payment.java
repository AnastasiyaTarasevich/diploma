package com.example.diploma.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Payment {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "idpayment")
    private int idpayment;
    @Basic
    @Column(name = "cost")
    private double cost;

    @ManyToOne
    private Contract contract;
    @PrePersist
    @PreUpdate
    public void setCostFromContract() {
        if (contract != null) {
            this.cost = contract.getCost();
        }
    }
}
