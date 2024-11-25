package com.example.diploma.controllers;


import com.example.diploma.models.*;
import com.example.diploma.repos.*;
import com.example.diploma.services.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
    private final MyMailSender myMailSender;
    private final CategoryService categoryService;
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

            Iterable<Product> products = productRepo.findProductsBySupplierIdAndStatus(id,null);
            model.addAttribute("products", products);
        List<Category>category=categoryService.getAllCategoriesForSupplier(id);
        model.addAttribute("categories",category);
        model.addAttribute("idSupplier",id);
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

    @PostMapping("/supplier_catalog/{supplierId}")
    public String filters(@RequestParam(name = "sort", required = false, defaultValue = "price_asc") String sort,@PathVariable int supplierId,  Model model)
    {
        List<Product> products=productRepo.findProductsBySupplierIdAndStatus(supplierId,null);
        // выполнить необходимые действия для обработки фильтров и сортировки
      List<Category>category=categoryService.getAllCategoriesForSupplier(supplierId);
        model.addAttribute("categories",category);
        List<Product> filteredProducts = productService.getFilteredProducts(products,sort);
        // добавить результаты в модель и вернуть представление
        model.addAttribute("idSupplier",supplierId);
        model.addAttribute("products", filteredProducts);
        return "supplier_catalog";
    }
    @GetMapping("/supplier_catalog/{supplierId}/{categoryId}")
    public String filterCat(@PathVariable int supplierId,@PathVariable int categoryId, Model model)
    {
        Iterable<Product> products;
       Category category=categoryRepo.getById(categoryId);
        products = productRepo.findProductByCategoryAndSupplierId(category,supplierId);
        model.addAttribute("products",products);
        model.addAttribute("idSupplier",supplierId);
        Iterable<Category> category1=categoryService.getAllCategoriesForSupplier(supplierId);
        model.addAttribute("categories",category1);
        return "supplier_catalog";
    }
    @PostMapping("/supplier_catalog/{supplierId}/{categoryId}")
    public String sortCat(@PathVariable int supplierId,@PathVariable int categoryId, @RequestParam(name = "sort", required = false, defaultValue = "price_asc") String sort, Model model)
    {
       Category category=categoryRepo.getById(categoryId);
        // выполнить необходимые действия для обработки фильтров и сортировки
        List<Product>  products = productService.findProductByCategoryAndSupplier(category,supplierId);
        Iterable<Category> category1= categoryService.getAllCategoriesForSupplier(supplierId);
        model.addAttribute("categories",category1);
        List<Product> filteredProducts = productService.getFilteredProducts(products,sort);
        // добавить результаты в модель и вернуть представление
        model.addAttribute("products", filteredProducts);
        return "supplier_catalog";
    }

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
    ) throws MessagingException, UnsupportedEncodingException {
        // Создаем новый заказ
        Order newOrder = new Order();
        newOrder.setCity(order.getCity());
        newOrder.setStreet(order.getStreet());
        newOrder.setNHouse(order.getNHouse());
        newOrder.setCorpus(order.getCorpus());
        newOrder.setPriceOfOrder(order.getPriceOfOrder());
        newOrder.setTotalPrice(order.getTotalPrice());

        newOrder.setPhoneNumber(order.getPhoneNumber());
        newOrder.setDate_for_sh(order.getDate_for_sh());
        newOrder.setDate(LocalDate.now());


        // Связываем заказ с пользователем
        User userFromDB = userRepo.findByLogin(principal.getName());
        String name=userFromDB.getName();
        String surname=userFromDB.getSurname();
        newOrder.setFirstName(surname);
        newOrder.setLastName(name);
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
        Order savedOrder = orderService.addOrder(newOrder);
        // Обновляем заказ в базе данных
        orderService.updateOrder(newOrder);

        // Получаем список уникальных поставщиков из сохраненного заказа
        Set<Supplier> uniqueSuppliers = savedOrder.getUniqueSuppliers();

        // Отправляем сообщение каждому поставщику только один раз
        for (Supplier supplier : uniqueSuppliers) {
            int supplierIdUser = supplier.getIdUser();
            User user=userRepo.getById(supplierIdUser);
            myMailSender.sendOrderToSupplier(savedOrder, user.getEmail());
        }



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
    public String rateSupplier(@RequestParam("supplierId") int supplierId,@RequestParam("defectsCount") int defectsCount, @RequestParam("reviewsCount") double reviewsCount,
                               @RequestParam("failuresCount") int failuresCount,@RequestParam("priceComparison") String priceComparison, @RequestParam("payment") String payment) {

        Supplier supplier = supplierRepo.findById(supplierId).orElse(null);

        if (supplier != null) {

            // Вычислить оценку поставщика с использованием заданных весов
            double defectsWeight = 0.3;
            double failuresWeight = 0.2;
            double priceWeight=0.25;
            double paymentWeight=0.15;
            double reviewsWeight = 0.1;

            int TotalProducts=supplierService.getTotalDeliveredProductsBySupplier(supplier);
            int TotalDeliveries=supplierService.getTotalDeliveredShipmentsBySupplier(supplier);

            double rateDefects= supplier.rateDefects(TotalProducts,defectsCount);
            double rateFailures=supplier.rateFailures(TotalDeliveries,failuresCount);
            double ratePrice=supplier.ratePrice(priceComparison);
            double ratePayment=supplier.ratePayment(payment);
            double rateReviews=supplier.rateReviews(reviewsCount);

            double totalRating=(defectsWeight*rateDefects)+(failuresWeight*rateFailures)+(priceWeight*ratePrice)
                    +(paymentWeight*ratePayment)+(reviewsWeight*rateReviews);
            double roundedRating = Math.round(totalRating * 100.0) / 100.0;
            supplier.setPriceComparison(ratePrice);
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


    @GetMapping("/delivery_schedule")
    public String delivery_schedule(Model model)
    {
        return "delivery_schedule";
    }


    @GetMapping("/analytics")
    public String getAnalytics()
    {
        return "analytics";
    }
    @GetMapping("/supplier-defectsGraph")
    public String getSh_graph()
    {
        return "supplier-defectsGraph";
    }
    @GetMapping("/delivered_categoriesGraph")
    public String getcat_graph()
    {
        return "delivered_categoriesGraph";
    }
    @GetMapping("/orderStatusesGraph")
    public String getOrder_graph()
    {
        return "orderStatusesGraph";
    }

    @GetMapping("/orderVolume")
    public String getOrder_volume()
    {
        return "orderVolume";
    }
    @GetMapping("/average_deliveryTime")
    public String getAverageDelivery()
    {
        return "average_deliveryTime";
    }
    @GetMapping("/priceInTime_Changes")
    public String getpriceInTime_Changes()
    {
        return "priceInTime_Changes";
    }
}
