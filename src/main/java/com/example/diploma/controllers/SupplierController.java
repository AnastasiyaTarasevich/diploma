package com.example.diploma.controllers;


import com.example.diploma.config.WordGenerator;
import com.example.diploma.models.*;
import com.example.diploma.repos.*;
import com.example.diploma.services.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.security.Principal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class SupplierController
{
    private final ProductRepo productRepo;
    private final ImageRepo imageRepo;
    private final ProductService productService;
    private final CategoryService categoryService;
    private final CategoryRepo categoryRepo;
    private final SupplierRepo supplierRepo;
    private final UserRepo userRepo;
    private final ShippingRepo shippingRepo;
    private final UserService userService;
    private final ShippingService shippingService;
    private final OrderRepo orderRepo;
    private final SupplierService supplierService;
    private final OrderItemRepo orderItemRepo;
    private final OrderService orderService;
    private final ContractRepo contractRepo;
    private final PaymentRepo paymentRepo;
    private final ShipmentService shipmentService;
    private final ShipmentRepo shipmentRepo;
    private final ShipmentFailersRepo shipmentFailersRepo;
    @GetMapping("/catalog")
    public String getCatalog(@RequestParam(name="name",required = false) String name, Principal principal, Model model)
    {
        String supplierName = principal.getName();
        User user=userRepo.findByLogin(supplierName);
        Supplier supplier=supplierRepo.findByIdUser(user.getIdUser());
        if (supplier != null) {
            // Получение каталога продуктов только для данного поставщика
            Iterable<Product> products = productRepo.findProductsBySupplierId(supplier.getIdsupplier()); // Предполагается, что у вас есть репозиторий productRepo для работы с продуктами

            model.addAttribute("products", products);
            model.addAttribute("title", "Каталог товаров");
            return "catalog";
        } else {
            // Обработка случая, когда поставщик не найден
            // Возвращение соответствующего представления или сообщения об ошибке
            return "error";
        }
    }
    @GetMapping("/add_prod")
    public String add_pr(Model model)
    {

        List<Category> categories = (List<Category>) categoryService.findAll();
        if (categories.isEmpty()) {
            // Если список категорий пуст, то создаем новую категорию
            Category newCategory = new Category();
            model.addAttribute("category", newCategory);
        } else {
            model.addAttribute("category", categories);
        }
        return"add_prod";
    }
    @PostMapping("/add_prod")
    public String getAddPr(@RequestParam("first_file") MultipartFile file1,
                           Product product, @RequestParam ("cat") String name, @RequestParam("category1") String category, Model model) throws IOException {
        if (name != null && !name.isEmpty() ) {

            category = name;
        }
        categoryService.saveCat(product.getIdProduct(), category);
        productService.saveProduct(product,file1,category);


        return "redirect:/catalog";
    }

    @GetMapping("/prod_details/{id}")
    public String productInfo(@PathVariable int id, Model model)
    {
        Product product =productService.getProductById(id);
        Iterable<Image> image = imageRepo.findAll();
        model.addAttribute("product",product);
        model.addAttribute("image", image);
        return "prod_details";

    }
    @GetMapping("/prod_edit/{id}")
    public String editProduct(@PathVariable int id,Model model)
    {
        Product product =productService.getProductById(id);
        model.addAttribute("product",product);
        List<Category> categories = categoryRepo.findAll();
        model.addAttribute("category", categories);
        model.addAttribute("tittle","Редактирование товара");
        return "prod_edit";
    }
    @PostMapping("/prod_edit/{id}")
    public String productEdit(@PathVariable int id, @ModelAttribute Product updatedProduct, @RequestParam("category1") String category, @RequestParam("first_file") MultipartFile file1) throws IOException {
        productService.updateProduct(id,updatedProduct,file1,category);
        return"redirect:/prod_details/{id}";
    }
    @PostMapping("/list_prod/{id}")
    public String productDelete(@PathVariable int id,Model model)
    {
        Product product=productRepo.findById(id).orElseThrow();
        product.setCategory(null);
        productRepo.save(product);
        productRepo.delete(product);
        return "redirect:/catalog";
    }
    @GetMapping("/list_prod/search")
    public String productFind(@RequestParam (name="keyword")String key, Principal principal,Model model)
    {

        String supplierName = principal.getName(); // Предполагается, что имя поставщика используется в качестве имени пользователя

        User user=userRepo.findByLogin(supplierName);
        Supplier supplier=supplierRepo.findByIdUser(user.getIdUser());


            // Выполнение поиска продуктов по ключевому слову и идентификатору поставщика
            List<Product> products = productRepo.search(key, supplier.getIdsupplier());

            model.addAttribute("products", products);
            model.addAttribute("title", "Результаты поиска");
            return "catalog";

    }
    @GetMapping("/shipping_list")
    public String ListShipping(Model model, Principal principal)
    {
        String supplierName = principal.getName();
        User user=userRepo.findByLogin(supplierName);
        Supplier supplier=supplierRepo.findByIdUser(user.getIdUser());
        Iterable<Shipping> shippings = shippingRepo.findShippingByIdSupplier(supplier.getIdsupplier());

        model.addAttribute("shipping", shippings);
        model.addAttribute("title", "Список доставок поставщика");
        return "shipping_list";
    }
    @GetMapping("/profileEdit")
    public String getProfileInfo(Model model, @AuthenticationPrincipal User user)
    {
        model.addAttribute("login",user.getLogin());
        model.addAttribute("email",user.getEmail());
        return "profileEdit";
    }
    @GetMapping("/updateSupplierProfile")
    public String getEdiProfileForm(Model model, @AuthenticationPrincipal User user)
    {
        model.addAttribute("username",user.getLogin());

        model.addAttribute("email",user.getEmail());
        return "/updateSupplierProfile";
    }

    @PostMapping("/updateSupplierProfile")
    public String updateProfileInfo(
            @AuthenticationPrincipal User user,
            @RequestParam String password,
            @RequestParam String email,
             @RequestParam String username
    )
    {
        userService.updateProfile(user, password, email, username);
        return "redirect:/supplier/";
    }

    @GetMapping("/shippingAdd")
    public String add_sh(Model model)
    {
        return "shippingAdd";
    }
    @PostMapping ("/shippingAdd")
    public String getAddSh(Shipping shipping, Principal principal) throws IOException {
        String supplierName = principal.getName();
        User user=userRepo.findByLogin(supplierName);
        Supplier supplier=supplierRepo.findByIdUser(user.getIdUser());
        shipping.setIdSupplier(supplier.getIdsupplier());
        shippingRepo.save(shipping);
        return "redirect:/shipping_list";
    }

    @GetMapping("/shippingEdit/{id}")
    public String shipEditForm(Model model, @PathVariable int id) {
        Shipping shipping =shippingService.getShippingById(id);
        model.addAttribute("shipping", shipping);
        return "shippingEdit";
    }
    @PostMapping("/shippingEdit/{id}")
    public String shipEdit( @PathVariable int id,@ModelAttribute Shipping updSh) {
        shippingService.updateShipping(id,updSh);
        return "redirect:/shipping_list";
    }

    @PostMapping("/shipping/{id}")
    public String shipDel(@PathVariable int id)
    {
        Shipping shipping =shippingService.getShippingById(id);
        shippingRepo.delete(shipping);
        return "redirect:/shipping_list";
    }
    @PostMapping("/removeAllSh")
    public String AllShipDelete()
    {
        shippingRepo.deleteAll();
        return "redirect:/shipping_list";
    }
    @GetMapping("/supplier_orders")
    public String getSupplierOrdersList(@AuthenticationPrincipal User userSession, Model model) {
        User userFromDB = userRepo.findByLogin(userSession.getUsername());
        Supplier supplier=supplierRepo.findByIdUser(userFromDB.getIdUser());
        List<OrderItem> supplierOrders=orderItemRepo.findBySupplier(supplier);
        model.addAttribute("supplierOrders", supplierOrders);
        return "supplier_orders";
    }

    @PostMapping("/acceptOrder")
    public String acceptOrder(@RequestParam("orderId") int orderid,@AuthenticationPrincipal User userSession, RedirectAttributes redirectAttributes)
    {
//        orderService.updateOrderStatus(orderid,OrderStatus.ОЖИДАЕТ_КОНТРАКТ);
        Order order = orderRepo.getById(orderid);
        List<OrderItem> orderItems = order.getOrderItems();
        User userFromDB = userRepo.findByLogin(userSession.getUsername());
        Supplier supplier=supplierRepo.findByIdUser(userFromDB.getIdUser());
        for (OrderItem orderItem : orderItems) {
          Supplier  supplier1 = orderItem.getSupplier();
            if (supplier1.getIdsupplier() == supplier.getIdsupplier()) {
                orderItem.setStatus(OrderStatus.ОЖИДАЕТ_КОНТРАКТ);
                orderItemRepo.save(orderItem);
            }
        }
        redirectAttributes.addAttribute("orderId", orderid);
        return "redirect:/createContract";
    }
    @PostMapping("/donotAccept")
    public String donotacceptOrder(@RequestParam("orderId") int orderid,@AuthenticationPrincipal User userSession, RedirectAttributes redirectAttributes)
    {
//        orderService.updateOrderStatus(orderid,OrderStatus.ОЖИДАЕТ_КОНТРАКТ);
        Order order = orderRepo.getById(orderid);
        List<OrderItem> orderItems = order.getOrderItems();
        User userFromDB = userRepo.findByLogin(userSession.getUsername());
        Supplier supplier=supplierRepo.findByIdUser(userFromDB.getIdUser());
        for (OrderItem orderItem : orderItems) {
            Supplier  supplier1 = orderItem.getSupplier();
            if (supplier1.getIdsupplier() == supplier.getIdsupplier()) {
                orderItem.setStatus(OrderStatus.ОТКЛОНЕН);
                orderItemRepo.save(orderItem);
            }
        }
        redirectAttributes.addAttribute("orderId", orderid);
        return "redirect:/supplier_orders";
    }

    @GetMapping("/createContract")
    public String ShowFormcreateContract(@RequestParam("orderId") int orderId,Model model)
    {
        model.addAttribute("orderId", orderId);
        model.addAttribute("tittle", "Создание нового контракта");

        return "createContract";
    }
    @PostMapping("/createContract")
    public String createContract(@RequestParam (name="orderId") int orderId, @RequestParam(name="type") String type, @RequestParam(name="cost") double cost, Authentication authentication, RedirectAttributes redirectAttributes) throws IOException {
        String supplierName = authentication.getName();
        User user=userRepo.findByLogin(supplierName);
        Supplier supplier=supplierRepo.findByIdUser(user.getIdUser());
        Contract contract=new Contract();
        contract.setType(type);
        contract.setCost(cost);
        contract.setSupplier(supplier);
        contract.setStatus(ContractStatus.ОЖИДАЕТ_ПОДПИСАНИЯ);
        contract.setDateOfConclusion(Date.valueOf(LocalDate.now()));
        Order order=orderRepo.getById(orderId);
        contract.setOrder(order);
        contract.setUser(order.getUser());
       byte [] generateDoc= WordGenerator.generateWordDocument(contract);
       contract.setDocuments(generateDoc);
        contractRepo.save(contract);
        redirectAttributes.addAttribute("contractId", contract.getIdcontract());
        return "redirect:/successContract";
    }
    @GetMapping("/successContract")
    public String getSuccessContract(@RequestParam(name="contractId") int contractId, Model model)
    {
        model.addAttribute("contractId", contractId);
        return "successContract";
    }
    @GetMapping("/downloadContract/{contractId}")
    public ResponseEntity<byte[]> downloadContract(@PathVariable int contractId, HttpServletResponse response) throws IOException {
        // Получаем контракт из базы данных
        Contract contract = contractRepo.getById(contractId);

        // Получаем байты документа
        byte[] documentBytes = contract.getDocuments();

        // Определяем директорию для сохранения файла
        String directoryPath = "D:/contracts";

        // Создаем директорию, если она не существует
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Создаем файл в директории
        String filePath = directoryPath + "contract_" + contractId + ".docx";
        File file = new File(filePath);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(documentBytes);
        }

        // Отправляем файл клиенту
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", file.getName());

        return ResponseEntity.ok()
                .headers(headers)
                .body(Files.readAllBytes(file.toPath()));
    }

    @GetMapping("/list_payments")
    public String viewPayments(@AuthenticationPrincipal User user, Model model) {
        String supplierName = user.getUsername();
        User user1=userRepo.findByLogin(supplierName);
        Supplier supplier=supplierRepo.findByIdUser(user1.getIdUser());
        List<Payment> supplierPayments = paymentRepo.findByContract_Supplier_Idsupplier(supplier.getIdsupplier());
        model.addAttribute("payments", supplierPayments);
        return "list_payments";
    }
    @PostMapping("/changeStatus")
    public String updateOrderItemStatus(@RequestParam("orderId") int id, @AuthenticationPrincipal User userSession)
    {
        Order order = orderRepo.getById(id);
        List<OrderItem> orderItems = order.getOrderItems();
        List<OrderItem> orderItems1=new ArrayList<>();
        User userFromDB = userRepo.findByLogin(userSession.getUsername());
        Supplier supplier = supplierRepo.findByIdUser(userFromDB.getIdUser());
        for (OrderItem orderItem : orderItems) {
            Supplier supplier1 = orderItem.getSupplier();
            if (supplier1.getIdsupplier() == supplier.getIdsupplier()) {
                // Изменяем статус товара в соответствии с вашей логикой
                orderItem.setStatus(updateItemStatus(orderItem.getStatus()));
                orderItemRepo.save(orderItem);
                if (orderItem.getStatus()==OrderStatus.ПЕРЕДАН_В_ДОСТАВКУ)
                {
                   orderItems1.add(orderItem);
                }
            }


        }
        if (!orderItems1.isEmpty()) {
            shipmentService.createShipmentForOrderItem(orderItems1);
        }
        return "redirect:/supplier_orders";
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
        User userFromDB = userRepo.findByLogin(userSession.getUsername());
        Supplier supplier=supplierRepo.findByIdUser(userFromDB.getIdUser());
        List<Shipment> shipments=shipmentRepo.findBySupplier(supplier);
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
        shipmentRepo.save(shipment);
        ShipmentsFailures shipmentsFailures=new ShipmentsFailures();
        shipmentsFailures.setShipment(shipment);
        shipmentsFailures.setDescription("Задержка доставки на " + delay + " дней по причине:"+desc);
        shipmentsFailures.setDate(new Date(System.currentTimeMillis()));
        shipmentFailersRepo.save(shipmentsFailures);
        return "redirect:/list_shipments";
    }


}
