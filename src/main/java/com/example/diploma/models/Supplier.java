package com.example.diploma.models;

import javax.persistence.*;
import java.util.List;



@Entity

public class Supplier {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "idsupplier")
    private int idsupplier;
    @Basic
    @Column(name = "company_name")
    private String companyName;
    @Basic
    @Column(name = "address")
    private String address;
    @Basic
    @Column(name = "contact_data")
    private String contactData;
    @Basic
    @Column(name = "rating")
    private double rating;
    @Basic
    @Column(name = "id_user")
    private Integer idUser;
//    @OneToOne(cascade = CascadeType.REMOVE, mappedBy = "supplier")
//    private User user;
    public int getIdsupplier() {
        return idsupplier;
    }

    public void setIdsupplier(int idsupplier) {
        this.idsupplier = idsupplier;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContactData() {
        return contactData;
    }

    public void setContactData(String contactData) {
        this.contactData = contactData;
    }

    public Integer getIdUser() {
        return idUser;
    }

    public void setIdUser(Integer idUser) {
        this.idUser = idUser;
    }
    public double getAverageRating(List<SupplierReview> reviews) {
        if (reviews == null || reviews.isEmpty()) {
            return 0.0;
        }

        double totalRating = 0.0;
        int count = 0;
        for (SupplierReview review : reviews) {
            if (review.getSupplier().equals(this)) {
                totalRating += review.getGrade();
                count++;
            }
        }

        if (count == 0) {
            return 0.0;
        }

        return totalRating / count;
    }
    public int getFailuresCount(List<ShipmentsFailures> failures) {
        if (failures == null || failures.isEmpty()) {
            return 0;
        }

        int count = 0;
        for (ShipmentsFailures failure : failures) {
            if (failure.getShipment().getSupplier().equals(this)) {
                count++;
            }
        }

        return count;
    }
    public int getDefectsCount(List<SupplierDefect> defects) {
        if (defects == null || defects.isEmpty()) {
            return 0;
        }

        int count = 0;
        for (SupplierDefect failure : defects) {
            if (failure.getShipment().getSupplier().equals(this)) {
                count++;
            }
        }

        return count;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public double getRating() {
        return rating;
    }
}
