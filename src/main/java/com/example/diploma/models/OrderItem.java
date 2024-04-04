package com.example.diploma.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    private Order order;

    @ManyToOne
    private Product product;

    private int quantity;
    @ManyToOne
    private Supplier supplier;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatus status;


    public OrderItem(Order order, Product product,OrderStatus status, int quantity, Supplier supplier) {
        this.order = order;
        this.product = product;
        this.status=status;
        this.quantity = quantity;
        this.supplier = supplier;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

}