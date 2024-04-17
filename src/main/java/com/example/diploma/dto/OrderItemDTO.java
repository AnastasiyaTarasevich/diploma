package com.example.diploma.dto;

public class OrderItemDTO {

    private String companyName;

    public OrderItemDTO(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
}

