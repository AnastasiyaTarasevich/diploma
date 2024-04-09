package com.example.diploma.services;

import com.example.diploma.models.Order;
import com.example.diploma.models.OrderItem;
import com.example.diploma.models.Supplier;
import com.example.diploma.repos.OrderItemRepo;
import com.example.diploma.repos.SupplierRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class SupplierService {
    private final SupplierRepo supplierRepo;
    private final OrderItemRepo orderItemRepo;
    public Supplier findById(int id) {

        return supplierRepo.findById(id).orElse(null);
    }

    public void updateSupplier(int idsupplier, Supplier updatedSupplier)
    {
        Supplier supplier=findById(idsupplier);

        if (!supplier.getCompanyName().equals(updatedSupplier.getCompanyName()))
        {
            supplier.setCompanyName(updatedSupplier.getCompanyName());
        }
        if (supplier.getAddress()!=updatedSupplier.getAddress())
        {
            supplier.setAddress(updatedSupplier.getAddress());
        }
//        if (supplier.getContactData()!=updatedSupplier.getContactData())
//        {
//            supplier.setContactData(updatedSupplier.getContactData());
//        }
        supplierRepo.save(supplier);
    }
    public void deleteSupplier(int supplierId) {
        supplierRepo.deleteById(supplierId);
    }

    public List<Supplier> getAllSuppliers() {
        Iterable<Supplier> supplierIterable = supplierRepo.findAll();
        List<Supplier> suppliers = new ArrayList<>();
        supplierIterable.forEach(suppliers::add);
        return suppliers;
    }
    public List<Order> getOrdersForSupplier(Supplier supplier) {
        List<OrderItem> orderItems = orderItemRepo.findBySupplier(supplier);
        return orderItems.stream()
                .map(OrderItem::getOrder)
                .distinct()
                .collect(Collectors.toList());
    }
}
