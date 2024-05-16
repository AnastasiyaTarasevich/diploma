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
    @Basic
    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private DeliveryPayment deliveryPayment;

    @Basic
    @Column(nullable = true)
    private String priceComparison;

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
            Supplier supplier = review.getSupplier();
            if (supplier != null && supplier.equals(this)) {
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
        for (SupplierDefect defect : defects) {
            if (defect.getShipment().getSupplier().equals(this)) {
                count += defect.getQuantity();
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

    public DeliveryPayment getDeliveryPayment() {
        return deliveryPayment;
    }

    public void setDeliveryPayment(DeliveryPayment deliveryPayment) {
        this.deliveryPayment = deliveryPayment;
    }

    public String getPriceComparison() {
        return priceComparison;
    }

    public void setPriceComparison(double ratePrice) {
        if (ratePrice ==1)
        {
           this.priceComparison="Выше среднерыночной на 4-5%";
        } else if (ratePrice==2) {
            this.priceComparison="Выше среднерыночной на 3%";
        } else if (ratePrice==3) {
            this.priceComparison="Выше среднерыночной на 1-2%";
        } else if (ratePrice==4) {
            this.priceComparison="Соответствует среднерыночной";
        } else if(ratePrice==5) {
            this.priceComparison="Ниже среднерыночной";
        }

    }

    public double rateDefects(int totalProducts, int defectsCount)
    {
        double defectPercentage = (double) defectsCount / totalProducts * 100.0;
        if (defectPercentage <2)
        {
            return  5; // 5 баллов
        } else if (defectPercentage >= 2 && defectPercentage<=5) {
            return  4; // 4 балла
        } else if (defectPercentage > 5 && defectPercentage<=10) {
            return  3; // 3 балла
        } else if (defectPercentage >= 10 && defectPercentage<=20) {
            return  2; // 2 балла
        } else {
            return 1; // 1 балл
        }
    }

    public double rateFailures(int totalDeliveries,int failuresCount)
    {
        double deliveriesPercentage = (double) failuresCount / totalDeliveries * 100.0;
        if (deliveriesPercentage <  5) {
            return  5; // 5 баллов
        } else if (deliveriesPercentage >= 5 && deliveriesPercentage<=10) {
            return  4; // 4 балла
        } else if (deliveriesPercentage >= 11 && deliveriesPercentage<=20) {
            return  3; // 3 балла
        } else if (deliveriesPercentage >= 21 && deliveriesPercentage<=30) {
            return  2; // 2 балла
        } else {
            return 1; // 1 балл
        }
    }

    public double ratePrice(String priceComparison)
    {
        if (priceComparison.equals("Ниже среднерыночной")) {
            return  5; // 5 баллов
        } else if (priceComparison.equals("Соответствует среднерыночной")) {
            return  4; // 4 балла
        } else if (priceComparison.equals("Выше среднерыночной на 1-2%")) {
            return  3; // 3 балла
        } else if (priceComparison.equals("Выше среднерыночной на 3%")) {
            return  2; // 2 балла
        } else {
            return 1; // 1 балл
        }
    }

    public double ratePayment(String payment) {
        if (DeliveryPayment.valueOf(payment).equals(DeliveryPayment.ALL_POSSIBLE)) {
            return 5; // 5 баллов
        } else if (DeliveryPayment.valueOf(payment).equals(DeliveryPayment.UPON_DELIVERY)) {
            return 4; // 4 балла
        } else if (DeliveryPayment.valueOf(payment).equals(DeliveryPayment.PARTIAL_PAYMENT)) {
            return 3; // 3 балла
        } else if (DeliveryPayment.valueOf(payment).equals(DeliveryPayment.HALF_PAYMENT)) {
            return 2; // 2 балла
        } else  {
            return 1; // 1 балл
        }
    }

    public double rateReviews(double reviews)
    {
        if (reviews>=4.5 && reviews<=5) {
            return 5; // 5 баллов
        } else if (reviews>=3.5 && reviews<=4.4) {
            return 4; // 4 балла
        } else if (reviews>=2.5 && reviews<=3.4) {
            return 3; // 3 балла
        } else if (reviews>=1.5 && reviews<=2.4) {
            return 2; // 2 балла
        } else  {
            return 1; // 1 балл
        }
    }

}
