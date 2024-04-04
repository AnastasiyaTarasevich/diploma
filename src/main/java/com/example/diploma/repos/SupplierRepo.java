package com.example.diploma.repos;


import com.example.diploma.models.Supplier;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SupplierRepo extends CrudRepository<Supplier, Integer> {

    Supplier findByIdUser(int idUser);

    Supplier findSupplierByCompanyName(String companyName);
}
