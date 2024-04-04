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
@Table(name="contract")
public class Contract {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "idcontract")
    private int idcontract;
    @Basic
    @Column(name = "date_of_conclusion")
    private Date dateOfConclusion;
    @Basic
    @Column(name = "type")
    private String type;
    @Basic
    @Column(name = "cost")
    private double cost;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ContractStatus status;
    @Basic
    @Column(name = "documents")
    private byte[] documents;
//    @OneToMany(mappedBy = "contractByIdcontract")
//    private Collection<Payment> paymentsByIdcontract;

    @OneToOne
    private Order order;

    @ManyToOne
    private Supplier supplier;

    @ManyToOne
    private User user;
}
