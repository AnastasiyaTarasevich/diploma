package com.example.diploma.repos;


import com.example.diploma.models.Contract;
import com.example.diploma.models.Order;
import com.example.diploma.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContractRepo extends JpaRepository<Contract,Integer> {

    Contract getContractByOrderAndUser(Order order, User user);

    List<Contract> findContractByUser(User userFromDB);
}
