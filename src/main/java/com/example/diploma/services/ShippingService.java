package com.example.diploma.services;

import com.example.diploma.models.Shipping;
import com.example.diploma.repos.ShippingRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ShippingService
{
    private final ShippingRepo shippingRepo;


    public Shipping getShippingById(int id) {
        return shippingRepo.findById(id).orElse(null);
    }

    public void updateShipping(int id, Shipping updSh)
    {
        Shipping shipping=getShippingById(id);

        if (!shipping.getCity().equals(updSh.getCity()))
        {
            shipping.setCity(updSh.getCity());
        }
        if (shipping.getPrice()!=updSh.getPrice())
        {
            shipping.setPrice(updSh.getPrice());
        }
        shippingRepo.save(shipping);
    }
    public double getPriceForCity(String city) {
        return shippingRepo.getPriceforCity(city);
    }


}
