package com.example.diploma.controllers;


import com.example.diploma.models.*;
import com.example.diploma.repos.*;
import com.example.diploma.services.OrderService;
import com.example.diploma.services.ProductService;
import com.example.diploma.services.SupplierService;
import com.example.diploma.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class UserController
{
    private final SupplierRepo supplierRepo;
    private final ProductRepo productRepo;
    private final ProductService productService;
    private final ImageRepo imageRepo;
    private final UserRepo userRepo;
    private final CategoryRepo categoryRepo;
    private final ShippingRepo shippingRepo;
    private final SupplierService supplierService;
    private final OrderRepo orderRepo;
    private final OrderService orderService;
    private final OrderItemRepo orderItemRepo;
    private final ContractRepo contractRepo;
    private final PaymentRepo paymentRepo;
    private final ShipmentRepo shipmentRepo;
    private final SupplierReviewRepo supplierReviewRepo;
    private final ShipmentFailersRepo shipmentFailersRepo;
    private final SupplierDefectRepo supplierDefectRepo;
    private final UserService userService;
    @GetMapping("/supplier_reading")
    public String readSupplier(Model model)
    {
        Iterable<Supplier> suppliers= supplierRepo.findAll();
        model.addAttribute("suppliers",suppliers);
        return "supplier_reading";
    }

    @GetMapping("/supplier_catalog/{id}")
    public String getCatalog(@RequestParam(name="name",required = false) String name, @PathVariable int id, Model model)
    {

            Iterable<Product> products = productRepo.findProductsBySupplierId(id);
            model.addAttribute("products", products);
            model.addAttribute("title", "Каталог товаров");
            return "supplier_catalog";

    }
    @GetMapping("/prod_details_for_user/{id}")
    public String productInfo(@PathVariable int id, Model model)
    {
        Product product =productService.getProductById(id);
        Iterable<Image> image = imageRepo.findAll();
        model.addAttribute("product",product);
        model.addAttribute("image", image);
        return "prod_details_for_user";

    }
    @PostMapping("/supplier_catalog")
    public String filters(@RequestParam(name = "sort", required = false, defaultValue = "price_asc") String sort, Model model)
    {
        List<Product> products=productService.findAll();
        // выполнить необходимые действия для обработки фильтров и сортировки
        Iterable<Category> category=categoryRepo.findAll();
        model.addAttribute("categories",category);
        List<Product> filteredProducts = productService.getFilteredProducts(products,sort);
        // добавить результаты в модель и вернуть представление
        model.addAttribute("products", filteredProducts);
        return "supplier_catalog";
    }
//    @GetMapping("/supplier_catalog{id}")
//    public String filterCat(@PathVariable int id, Model model)
//    {
//        Iterable<Product> products;
//        Category category=new Category();
//        category.setIdcategory(id);
//        products = productRepo.findProductByCategory(category);
//        model.addAttribute("products",products);
//        Iterable<Category> category1=categoryRepo.findAll();
//        model.addAttribute("categories",category1);
//        return "supplier_catalog";
//    }
//    @PostMapping("/supplier_catalog{id}")
//    public String sortCat(@PathVariable int id,@RequestParam(name = "sort", required = false, defaultValue = "price_asc") String sort, Model model)
//    {
//        Category category=new Category();
//        category.setIdcategory(id);
//        // выполнить необходимые действия для обработки фильтров и сортировки
//        List<Product>  products = productService.findProductByCategory(category);
//        Iterable<Category> category1=categoryRepo.findAll();
//        model.addAttribute("categories",category1);
//        List<Product> filteredProducts = productService.getFilteredProducts(products,sort);
//        // добавить результаты в модель и вернуть представление
//        model.addAttribute("products", filteredProducts);
//        return "supplier_catalog";
//    }
    @GetMapping("/supplier_catalog/search")
    public String productFind(@RequestParam (name="keyword")String key, @PathVariable int id, Model model)
    {
        List<Product> products=productRepo.search(key,id);
        model.addAttribute("product",products);
        return "supplier_catalog";

    }

    @GetMapping("/supplier_catalog1/{productId}")
    public String addToCart(
            @PathVariable int productId,
            @AuthenticationPrincipal User userSession
    ) {
        User userFromDB = userRepo.findByLogin(userSession.getUsername());
        Product product=productRepo.findById(productId).orElse(null);
        userFromDB.getProductList().add(product);
        userRepo.save(userFromDB);

        return "redirect:/cart";
    }



    @GetMapping("/cart")
    public String getCart(@AuthenticationPrincipal User userSession, Model model) {
        User userFromDB = userRepo.findByLogin(userSession.getUsername());
        model.addAttribute("products", userFromDB.getProductList());

        return "cart";
    }

    @PostMapping("/cart/remove")
    public String removeFromCart(
            @RequestParam(value = "productId") Product product,
            @AuthenticationPrincipal User userSession
    ) {
        User user = userRepo.findByLogin(userSession.getUsername());

        if (product != null) {
            user.getProductList().remove(product);
        }
        userRepo.save(user);

        return "redirect:/cart";
    }
    @GetMapping("/order")
    public String getOrder(@AuthenticationPrincipal User userSession,@RequestParam("totalPrice") double totalPrice,
                           @RequestParam("quantities") List<Integer> quantities,
                           @RequestParam("productIds") List<Long> productIds, Model model) {
        User userFromDB = userRepo.findByLogin(userSession.getUsername());
        Iterable<Shipping>shipping=shippingRepo.findAll();
        model.addAttribute("totalPrice", totalPrice);
        model.addAttribute("shipping",shipping);
         model.addAttribute("products", userFromDB.getProductList());
        model.addAttribute("quantities", quantities);
        model.addAttribute("productIds", productIds);

        return "order";
    }
    @GetMapping("order/price/{city}")
    @ResponseBody
    public double getPriceForCity(@PathVariable String city) {
        return shippingRepo.getPriceforCity(city);
    }


    @PostMapping("/order")
    public String addOrder(
            @RequestParam("quantities") List<Integer> quantities,
            @RequestParam("productIds") List<Integer> productIds,
            @RequestParam("companyName") List<String> supplierNames,
            @ModelAttribute Order order,
            Principal principal,
            Model model
    ) {
        // Создаем новый заказ
        Order newOrder = new Order();
        newOrder.setCity(order.getCity());
        newOrder.setStreet(order.getStreet());
        newOrder.setNHouse(order.getNHouse());
        newOrder.setCorpus(order.getCorpus());
        newOrder.setPriceOfOrder(order.getPriceOfOrder());
        newOrder.setTotalPrice(order.getTotalPrice());
        newOrder.setFirstName(order.getFirstName());
        newOrder.setLastName(order.getLastName());
        newOrder.setPhoneNumber(order.getPhoneNumber());
        newOrder.setDate_for_sh(order.getDate_for_sh());
        newOrder.setDate(LocalDate.now());
//        newOrder.setStatus(OrderStatus.В_ОЖИДАНИИ); // Установите нужный статус заказа

        // Связываем заказ с пользователем
        User userFromDB = userRepo.findByLogin(principal.getName());
        newOrder.setUser(userFromDB);
        for (String supplierName : supplierNames) {
            System.out.println("Supplier Name: " + supplierName);
        }
        for (int quantity:quantities)
        {
            System.out.println("quantity : " + quantity);
        }
        for (int productId:productIds )
        {
            System.out.println("productId"+productId);
        }
        // Проходим по товарам и сохраняем информацию о каждом товаре в OrderItem
        for (int i = 0; i < productIds.size(); i++) {
            int productId = productIds.get(i);
            int quantity = quantities.get(i);
            String supplierName = supplierNames.get(i);

            // Получаем товар и поставщика по id
            Product product = productRepo.findById(productId).orElse(null);
            Supplier supplier = supplierRepo.findSupplierByCompanyName(supplierName);


                newOrder.addProduct(product, quantity, supplier);
        }

        // Сохраняем заказ в базе данных
        orderService.addOrder(newOrder);

        // Обновляем заказ в базе данных
        orderService.updateOrder(newOrder);
        return "redirect:/finalizeOrder";
    }

    @GetMapping("/finalizeOrder")
    public String finalizeOrder(Model model)
    {
        List<Order> orderList = orderService.findAll();
        Order orderIndex = orderList.get(orderList.size() - 1);
        model.addAttribute("orderIndex", orderIndex.getIdorder());
        return "finalizeOrder";
    }
    @GetMapping("/user_orders")
    public String getUserOrdersList(@AuthenticationPrincipal User userSession, Model model) {
        User userFromDB = userRepo.findByLogin(userSession.getUsername());
        List<Order> orderList=orderRepo.findOrderByUser(userFromDB);
        List<OrderItem> userOrderItems = new ArrayList<>();
        for (Order order : orderList) {
            List<OrderItem> orderItems = orderItemRepo.findByOrder(order);
            userOrderItems.addAll(orderItems);
        }

        model.addAttribute("userOrders", userOrderItems);
        return "user_orders";
    }

    @GetMapping("/viewContract")
    public String viewUserContract(@RequestParam("orderId") int orderid, @AuthenticationPrincipal User userSession, Model model)
    {
        Order order=orderRepo.getById(orderid);
        Contract contract=contractRepo.getContractByOrderAndUser(order, userSession);
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
    @GetMapping("/list_userShipment")
    public String getUserShipments(@AuthenticationPrincipal User user, Model model)
    {
        User userFromDB = userRepo.findByLogin(user.getUsername());

        // Получаем список поставок пользователя
        List<Shipment> userShipments = shipmentRepo.findDistinctShipmentsByUser(userFromDB);

        // Передаем список поставок в модель
        model.addAttribute("userShipments", userShipments);
        return "list_userShipment";
    }
    @GetMapping("/user_contracts")
    public String getUserContractsList(@AuthenticationPrincipal User userSession, Model model) {
        User userFromDB = userRepo.findByLogin(userSession.getUsername());
        List<Contract> contracts=contractRepo.findContractByUser(userFromDB);


        model.addAttribute("userContracts", contracts);
        return "user_contracts";
    }

    @GetMapping("/AddDefect")
    public String getFormDefect(@RequestParam ("shipmentId") int sh_id,Model model)
    {
        model.addAttribute("shipmentId",sh_id);
        return "AddDefect";
    }
    @PostMapping("/AddDefect")
    public String addDefect(@RequestParam("nameProduct") String nameProduct,
                            @RequestParam("quantity") int quantity,
                            @RequestParam("shipmentId") int shipmentId,
                            @RequestParam("description") String description,
                            Model model) {

        Shipment shipment = shipmentRepo.getById(shipmentId);

        if (shipment != null)
        {
            OrderItem targetOrderItem = null;
            for (OrderItem orderItem : shipment.getOrderItems()) {
                if (nameProduct.equals(orderItem.getProduct().getName())) {
                    targetOrderItem = orderItem;
                    break;
                }
            }
            if (targetOrderItem != null && quantity <= targetOrderItem.getQuantity()) {
                SupplierDefect supplierDefect=new SupplierDefect();
                supplierDefect.setDescription(description);
                supplierDefect.setQuantity(quantity);
                supplierDefect.setShipment(shipment);
                supplierDefect.setNameProduct(nameProduct);
                supplierDefect.setDate(new Date(System.currentTimeMillis()));
                supplierDefectRepo.save(supplierDefect);

            } else {

                model.addAttribute("errorQuan", "Количество бракованного товара превышает количество заказанного товара.");
                return "redirect:/AddDefect"; // Замените "errorPage" на вашу страницу с ошибкой
            }
        } else {

            model.addAttribute("errorID", "Поставка с таким товаром  не найдена");
            return "redirect:/AddDefect";
        }

        return "redirect:/successDefect"; // Перенаправление на форму снова

        }

@GetMapping("/successDefect")
String getSuccesDefect()
{
    return "successDefect";
}

    @GetMapping("/rate")
    public String rating(Model model)
    {
        List<Supplier> suppliers= (List<Supplier>) supplierRepo.findAll();
        List<SupplierReview> supplierReviews=supplierReviewRepo.findAll();
        List<ShipmentsFailures> shipmentsFailures=shipmentFailersRepo.findAll();
        List<SupplierDefect> supplierDefects=supplierDefectRepo.findAll();
        model.addAttribute("defects", supplierDefects);
        model.addAttribute("failures", shipmentsFailures);
        model.addAttribute("reviews", supplierReviews);
        model.addAttribute("suppliers", suppliers);
        model.addAttribute("tittle","Рейтинг поставщиков");
        return "rating";
    }
    @PostMapping("/rate")
    public String rateSupplier(@RequestParam("supplierId") int supplierId,@RequestParam("defectsCount") int defectsCount, @RequestParam("reviewsCount") double reviewsCount,@RequestParam("failuresCount") int failuresCount) {

        Supplier supplier = supplierRepo.findById(supplierId).orElse(null);

        if (supplier != null) {

            // Вычислить оценку поставщика с использованием заданных весов
            double defectsWeight = 0.5;
            double reviewsWeight = 0.2;
            double failuresWeight = 0.3;

            double rating = (defectsCount * defectsWeight) + (reviewsCount * reviewsWeight) + (failuresCount * failuresWeight);

            double roundedRating = Math.round(rating * 100.0) / 100.0;

            // Сохранить округленную оценку поставщика в базе данных или выполнить другие необходимые действия
            supplier.setRating(roundedRating);
            supplierRepo.save(supplier);

            // Вернуться на страницу с таблицей поставщиков или выполнить другие необходимые действия
            return "redirect:/supplier_reading";
        }

        return "error";
    }
    @GetMapping("/userProfile_update")
    public String getEdiProfileForm(Model model, @AuthenticationPrincipal User user)
    {
        model.addAttribute("username",user.getLogin());

        model.addAttribute("email",user.getEmail());
        return "/userProfile_update";
    }

    @PostMapping("/userProfile_update")
    public String updateProfileInfo(
            @AuthenticationPrincipal User user,
            @RequestParam String password,
            @RequestParam String email,
            @RequestParam String username
    )
    {
        userService.updateProfile(user, password, email, username);
        return "redirect:/user/";
    }
}
