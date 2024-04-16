package com.example.diploma.controllers;

import com.example.diploma.models.*;
import com.example.diploma.repos.*;
import com.example.diploma.services.MyMailSender;
import com.example.diploma.services.ShipmentService;
import com.example.diploma.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class LogisticsController {
    private final OrderItemRepo orderItemRepo;
    private final OrderRepo orderRepo;
    private final ContractRepo contractRepo;
    private final PaymentRepo paymentRepo;
    private final ShipmentService shipmentService;
    private final ShipmentRepo shipmentRepo;
    private final ShipmentFailersRepo shipmentFailersRepo;
    private final UserService userService;
    private final MyMailSender myMailSender;
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
    public ResponseEntity<String> signContract(@RequestParam("contractId") int contractId) throws MessagingException, UnsupportedEncodingException {
        // Логика для подписания контракта
        Contract contract = contractRepo.getById(contractId);
        contract.setStatus(ContractStatus.ПОДПИСАН);
        contractRepo.save(contract);
        Payment payment = new Payment();

        // Присваиваем контракт платежу
        payment.setContract(contract);

        // Сохраняем платеж в базе данных
        paymentRepo.save(payment);
        int id_user=contract.getSupplier().getIdUser();
        User user=userService.findById(id_user);
        myMailSender.sendContractToSupplier(contract,user.getEmail());

        return ResponseEntity.ok("Контракт успешно подписан и оплачен");
    }
    @PostMapping("/changeStatus")
    public String updateOrderItemStatus(@RequestParam("orderId") int id)
    {
        Order order = orderRepo.getById(id);
        List<OrderItem> orderItems = order.getOrderItems();
        List<OrderItem> orderItems1=new ArrayList<>();

        for (OrderItem orderItem : orderItems) {
                orderItem.setStatus(updateItemStatus(orderItem.getStatus()));
                orderItemRepo.save(orderItem);
                if (orderItem.getStatus()==OrderStatus.ПЕРЕДАН_В_ДОСТАВКУ)
                {
                    orderItems1.add(orderItem);
                }
            }

        if (!orderItems1.isEmpty()) {
            shipmentService.createShipmentForOrderItem(orderItems1);
        }
        return "redirect:/all_orders";
    }
    private OrderStatus updateItemStatus(OrderStatus currentStatus) {
        switch (currentStatus) {
            case ОЖИДАЕТ_КОНТРАКТ:
                return OrderStatus.В_СБОРКЕ;
            case В_СБОРКЕ:
                return OrderStatus.ПЕРЕДАН_В_ДОСТАВКУ;
            default:
                return currentStatus;
        }
    }
    @GetMapping("/list_shipments")
    public String getSupplierShipments(@AuthenticationPrincipal User userSession, Model model) {

        List<Shipment> shipments=shipmentRepo.findAll();
        model.addAttribute("supplierShipments", shipments);
        return "list_shipments";
    }
    @PostMapping("/changeShStatus")
    public String changeStatusToDelivered(@RequestParam("shipmentId") int shipmentId) {
        Shipment shipment=shipmentRepo.getById(shipmentId);
        shipment.setStatus(ShipmentStatus.ДОСТАВЛЕНО);
        shipment.setArrivalDate(new Date(System.currentTimeMillis()));
        for (OrderItem orderItem : shipment.getOrderItems()) {
            orderItem.setStatus(OrderStatus.ДОСТАВЛЕН);
        }
        shipmentRepo.save(shipment);
        return "redirect:/list_shipments";
    }

    @PostMapping("/problemsWithDelivery")
    public String delayShipment(@RequestParam("shipmentId") int shipmentId,@RequestParam("delay") int delay, @RequestParam("desc") String desc )
    {
        Shipment shipment=shipmentRepo.getById(shipmentId);
        int t= shipment.getDeliveryDelay()+delay;
        shipment.setDeliveryDelay(t);
        shipment.setArrivalDate(shipment.calculateDeliveryDelayDate());
        shipment.setStatus(ShipmentStatus.ПРОБЛЕМЫ_С_ДОСТАВКОЙ);
        shipmentRepo.save(shipment);
        ShipmentsFailures shipmentsFailures=new ShipmentsFailures();
        shipmentsFailures.setShipment(shipment);
        shipmentsFailures.setDescription("Задержка доставки на " + delay + " дней по причине:"+desc);
        shipmentsFailures.setDate(new Date(System.currentTimeMillis()));
        shipmentFailersRepo.save(shipmentsFailures);
        return "redirect:/list_shipments";
    }
    @GetMapping("/logisticsProfile_update")
    public String editProfile(Model model, @AuthenticationPrincipal User user)
    {
        model.addAttribute("username",user.getLogin());

        model.addAttribute("email",user.getEmail());
        return "/logisticsProfile_update";
    }
    @PostMapping("/logisticsProfile_update")
    public String updateProfileInfo(
            @AuthenticationPrincipal User user,
            @RequestParam String password,
            @RequestParam String email,
            @RequestParam String username
    )
    {
        userService.updateProfile(user, password, email, username);
        return "redirect:/logistics/";
    }

}
