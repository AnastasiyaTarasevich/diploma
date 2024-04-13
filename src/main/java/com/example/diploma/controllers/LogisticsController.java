package com.example.diploma.controllers;

import com.example.diploma.models.*;
import com.example.diploma.repos.ContractRepo;
import com.example.diploma.repos.OrderItemRepo;
import com.example.diploma.repos.OrderRepo;
import com.example.diploma.repos.PaymentRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class LogisticsController {
    private final OrderItemRepo orderItemRepo;
    private final OrderRepo orderRepo;
    private final ContractRepo contractRepo;
    private final PaymentRepo paymentRepo;
    @GetMapping("/all_orders")
    public String getAllOrders(@AuthenticationPrincipal User userSession, Model model) {

        List<OrderItem> allOrders=orderItemRepo.findAll();
        model.addAttribute("allOrders", allOrders);
        return "all_orders";
    }
    @GetMapping("/viewContract")
    public String viewUserContract(@RequestParam("orderId") int orderid, @AuthenticationPrincipal User userSession, Model model)
    {
        Order order=orderRepo.getById(orderid);
        Contract contract=contractRepo.getContractByOrder(order);
        model.addAttribute("contract", contract);
        return "viewContract";
    }
    @PostMapping("/signContract")
    public ResponseEntity<String> signContract(@RequestParam("contractId") int contractId) {
        // Логика для подписания контракта
        Contract contract = contractRepo.getById(contractId);
        contract.setStatus(ContractStatus.ПОДПИСАН);
        contractRepo.save(contract);
        Payment payment = new Payment();

        // Присваиваем контракт платежу
        payment.setContract(contract);

        // Сохраняем платеж в базе данных
        paymentRepo.save(payment);
        return ResponseEntity.ok("Контракт успешно подписан и оплачен");
    }
}
