package com.example.diploma.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.time.DateUtils;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class Shipment {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "idshipment")
    private int idshipment;
    @Basic
    @Column(name = "date")
    private Date creationDate;
    @Basic
    @Column(name = "arrival_date")
    private Date arrivalDate;
    @Basic
    @Column(name = "delivery_delay")
    private int deliveryDelay;
    @ManyToOne
    private Supplier supplier;
    @ManyToMany
    @JoinTable(
            name = "shipment_orderitem",
            joinColumns = @JoinColumn(name = "idshipment"),
            inverseJoinColumns = @JoinColumn(name = "id_orderitem")
    )
    private List<OrderItem> orderItems;

    @Enumerated(EnumType.STRING)
    private ShipmentStatus status;
    @PrePersist
    public void prePersist() {
        // Устанавливаем дату прибытия как "дата создания + 2 дня"
        if (creationDate != null) {
            this.arrivalDate = (Date) DateUtils.addDays(creationDate, 2);
        }
    }
    public Date calculateDeliveryDelayDate() {

            return  DateUtils.addDays(arrivalDate, deliveryDelay);

    }

}
