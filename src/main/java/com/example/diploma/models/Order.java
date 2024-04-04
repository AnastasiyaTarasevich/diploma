package com.example.diploma.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@ToString
@Table(name= "orders")
public class Order {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "idorder", nullable = false)
    private int idorder;
    @Basic
    @Column(name = "city", nullable = false, length = 255)
    private String city;
    @Basic
    @Column(name = "street", nullable = false, length = 255)
    private String street;
    @Basic
    @Column(name = "n_house", nullable = false)
    private int nHouse;

    @Basic
    @Column(name = "corpus", nullable = true)
    private Integer corpus;
    @Basic
    @Column(name = "price_of_order", nullable = false, precision = 0)
    private double priceOfOrder;
    @Basic
    @Column(name = "total_price", nullable = false, precision = 0)
    private double totalPrice;
    @Basic
    @Column(name = "first_name", nullable = false, length = 255)
    private String firstName;
    @Basic
    @Column(name = "last_name", nullable = false, length = 255)
    private String lastName;
    @Basic
    @Column(name = "phone_number", nullable = false, length = 255)
    private String phoneNumber;
    @Basic
    @Column(name = "date", nullable = false)
    private LocalDate date;

    @OrderColumn
    @ManyToMany( fetch = FetchType.EAGER)
    private List<Product> products;

    @ManyToOne
    private User user;

    public Order(User user) {
        this.date = LocalDate.now();
        this.user = user;
        this.products= new ArrayList<>();
    }
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();
    public void addProduct(Product product, int quantity, Supplier supplier) {
        OrderItem orderItem = new OrderItem(this, product,OrderStatus.В_ОЖИДАНИИ, quantity, supplier);
        orderItems.add(orderItem);
    }
//    @OneToOne
//    Shipment shipment;

    public void removeProduct(Product product) {
        orderItems.removeIf(orderItem -> orderItem.getProduct().equals(product));
    }
}
