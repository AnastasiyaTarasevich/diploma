package com.example.diploma;

import com.example.diploma.controllers.GraphController;
import com.example.diploma.controllers.ImageController;
import com.example.diploma.controllers.SupplierController;
import com.example.diploma.controllers.UserController;
import com.example.diploma.models.*;
import com.example.diploma.repos.*;
import com.example.diploma.services.CategoryService;
import com.example.diploma.services.ProductService;
import com.example.diploma.services.SupplierService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.Principal;
import java.time.LocalDate;
import java.util.*;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


class DiplomaApplicationTests {

@Mock
private UserRepo userRepo;
@Mock
private SupplierRepo supplierRepo;
@Mock
private ProductRepo productRepo;
@Mock
private CategoryRepo categoryRepo;
@Mock
private CategoryService categoryService;
@Mock
private SupplierService supplierService;
@Mock
Principal principal;

@InjectMocks
private SupplierController supplierController;
@InjectMocks
private UserController userController;
	@Mock
	private ImageRepo imageRepo;

	@InjectMocks
	private ImageController imageController;

	@Mock
	private OrderItemRepo orderItemRepo;

	@InjectMocks
	private GraphController graphController;
	@Mock
	private ProductService productService;

	@Mock
	private PriceInTimeRepo priceInTimeRepo;
@BeforeEach
public void setUp()
{
	MockitoAnnotations.openMocks(this);
}

	@Test
	public void testGetCatalogSuccess() {
		Model model = new ConcurrentModel();
		User user = new User();
		user.setIdUser(1);
		user.setLogin("supplierUser");

		Supplier supplier = new Supplier();
		supplier.setIdsupplier(1);

		supplier.setIdUser(user.getIdUser());

		Product product1 = new Product();
		product1.setIdProduct(1);
		product1.setSupplierId(1);

		Product product2 = new Product();
		product2.setIdProduct(2);
		product2.setSupplierId(1 );


		when(principal.getName()).thenReturn("supplierUser");
		when(userRepo.findByLogin("supplierUser")).thenReturn(user);
		when(supplierRepo.findByIdUser(1)).thenReturn(supplier);
		when(productRepo.findProductsBySupplierIdAndStatus(1, null)).thenReturn(Arrays.asList(product1, product2));

		String viewName = supplierController.getCatalog(null, principal, model);

		assertEquals("catalog", viewName);
		assertEquals("Каталог товаров", model.getAttribute("title"));
		assertEquals(Arrays.asList(product1, product2), model.getAttribute("products"));
	}

	@Test
	public void testGetCatalogSupplierNotFound() {
		Model model = new ConcurrentModel();
		User user = new User();
		user.setIdUser(1);
		user.setLogin("supplierUser");

		when(principal.getName()).thenReturn("supplierUser");
		when(userRepo.findByLogin("supplierUser")).thenReturn(user);
		when(supplierRepo.findByIdUser(1)).thenReturn(null);

		String viewName = supplierController.getCatalog(null, principal, model);

		assertEquals("error", viewName);
	}
	@Test
	public void testGetOrderStatusPercentage() {
		// Создание заказов с различными статусами
		OrderItem orderItem1 = new OrderItem();
		orderItem1.setStatus(OrderStatus.ОТКЛОНЕН);

		OrderItem orderItem2 = new OrderItem();
		orderItem2.setStatus(OrderStatus.ДОСТАВЛЕН);

		OrderItem orderItem3 = new OrderItem();
		orderItem3.setStatus(OrderStatus.В_ОЖИДАНИИ);

		List<OrderItem> orderItems = Arrays.asList(orderItem1, orderItem2, orderItem3);

		// Заглушка для orderItemRepo.findAll(), чтобы возвращать список созданных заказов
		when(orderItemRepo.findAll()).thenReturn(orderItems);

		// Ожидаемые значения процентного соотношения статусов заказов
		Map<String, Double> expectedPercentage = new HashMap<>();
		expectedPercentage.put(OrderStatus.ОТКЛОНЕН.name(), 33.33);
		expectedPercentage.put(OrderStatus.ДОСТАВЛЕН.name(), 33.33);
		expectedPercentage.put(OrderStatus.В_ОЖИДАНИИ.name(), 33.33);

		// Вызов метода getOrderStatusPercentage() контроллера
		Map<String, Double> actualPercentage = graphController.getOrderStatusPercentage();

		// Проверка, что полученные проценты соответствуют ожидаемым
		for (Map.Entry<String, Double> entry : actualPercentage.entrySet()) {
			assertEquals(expectedPercentage.get(entry.getKey()), entry.getValue(), 0.01);
		}
	}

	@Test
	public void testGetImageById() {
		// Создаем тестовый объект Image
		Image testImage = new Image();
		testImage.setIdimage(1L);
		testImage.setOriginalFileName("test_image.jpg");
		testImage.setContentType("image/jpeg");
		testImage.setSize(1024L);
		testImage.setBytes(Base64.getEncoder().encodeToString("test_bytes".getBytes()));

		// Имитируем вызов imageRepo.findById
		when(imageRepo.findById(1L)).thenReturn(Optional.of(testImage));

		// Ожидаемый результат
		ResponseEntity<InputStreamResource> expectedResponse = ResponseEntity.ok()
				.header("fileName", "test_image.jpg")
				.contentType(MediaType.IMAGE_JPEG)
				.contentLength(1024L)
				.body(new InputStreamResource(new ByteArrayInputStream(Base64.getDecoder().decode(testImage.getBytes()))));

		// Вызываем метод контроллера и проверяем результат
		ResponseEntity<?> actualResponse = imageController.getImageById(1L);

		assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode());
		assertEquals(expectedResponse.getHeaders(), actualResponse.getHeaders());
		assertEquals(expectedResponse.getBody().getClass(), actualResponse.getBody().getClass());
	}



	@Test
	public void testProductEdit() throws IOException {
		// Создаем тестовые данные
		int productId = 1;
		Double oldPrice = 10.0;
		Product updatedProduct = new Product();
		updatedProduct.setPrice(20.0);
		String category = "test_category";
		MultipartFile file1 = null; // Можно использовать Mock объект для MultipartFile

		Product existingProduct = new Product();
		existingProduct.setIdProduct(productId);
		existingProduct.setPrices(new ArrayList<>()); // Чтобы избежать NullPointerException

		PriceInTime priceInTime = new PriceInTime();
		priceInTime.setChangeDate(LocalDate.now());
		priceInTime.setProduct(existingProduct);
		priceInTime.setNewPrice(updatedProduct.getPrice());

		// Имитируем вызов метода getProductById с конкретным идентификатором продукта
		when(productService.getProductById(anyInt())).thenReturn(existingProduct);

		// Имитируем вызов метода updateProduct с использованием Mockito.any() для аргумента int
		doNothing().when(productService).updateProduct(Mockito.anyInt(), any(Product.class), any(MultipartFile.class), anyString());

		// Вызываем метод контроллера
		String result = supplierController.productEdit(productId, oldPrice, updatedProduct, category, file1);

		// Проверяем, что результатом является ожидаемая строка перенаправления
		assertEquals("redirect:/prod_details/{id}" , result);
	}

	@Test
	public void testSortCat() {
		// Создаем тестовые данные
		int supplierId = 1;
		int categoryId = 2;
		String sort = "price_asc";
		Model model = mock(Model.class);
		Category category = new Category();
		List<Product> products = new ArrayList<>();
		Iterable<Category> categories = new ArrayList<>();

		// Имитируем вызовы методов и сервисов
		when(categoryRepo.getById(categoryId)).thenReturn(category);
		when(productService.findProductByCategoryAndSupplier(category, supplierId)).thenReturn(products);
		when(categoryService.getAllCategoriesForSupplier(supplierId)).thenReturn((List<Category>) categories);
		List<Product> filteredProducts = new ArrayList<>();
		when(productService.getFilteredProducts(products, sort)).thenReturn(filteredProducts);

		// Вызываем метод контроллера
		String result = userController.sortCat(supplierId, categoryId, sort, model);

		// Проверяем, что методы и сервисы были вызваны с правильными аргументами
		verify(categoryRepo).getById(categoryId);
		verify(productService).findProductByCategoryAndSupplier(category, supplierId);
		verify(categoryService).getAllCategoriesForSupplier(supplierId);
		verify(productService).getFilteredProducts(products, sort);

		// Проверяем, что атрибуты были добавлены к модели
		verify(model).addAttribute("categories", categories);
		verify(model).addAttribute("products", filteredProducts);

		// Проверяем, что контроллер вернул правильное представление
		assertEquals("supplier_catalog", result);
	}

	@Test
	public void testRateSupplier() {
		// Создаем тестовые данные
		int supplierId = 1;
		int defectsCount = 2;
		double reviewsCount = 3.5;
		int failuresCount = 1;
		String priceComparison = "higher";
		String payment = DeliveryPayment.ALL_POSSIBLE.name();
		Supplier supplier = new Supplier();

		// Имитируем вызовы методов и сервисов
		when(supplierRepo.findById(supplierId)).thenReturn(Optional.of(supplier));
		when(supplierService.getTotalDeliveredProductsBySupplier(supplier)).thenReturn(10);
		when(supplierService.getTotalDeliveredShipmentsBySupplier(supplier)).thenReturn(5);

		// Вызываем метод контроллера
		String result = userController.rateSupplier(supplierId, defectsCount, reviewsCount, failuresCount, priceComparison, payment);

		// Проверяем, что методы и сервисы были вызваны с правильными аргументами
		verify(supplierRepo).findById(supplierId);
		verify(supplierService).getTotalDeliveredProductsBySupplier(supplier);
		verify(supplierService).getTotalDeliveredShipmentsBySupplier(supplier);
		verify(supplierRepo).save(supplier);

		// Проверяем, что контроллер вернул правильное представление
		assertEquals("redirect:/supplier_reading", result);

		// Проверяем, что оценка поставщика была правильно вычислена и сохранена
		assertEquals(2.6, supplier.getRating()); // Ваш ожидаемый результат
		assertEquals("Выше среднерыночной на 4-5%", supplier.getPriceComparison()); // Ваш ожидаемый результат
	}
	@Test
	public void testAddToCart() {
		// Создаем тестовые данные
		int productId = 1;
		User userSession = new User();
		userSession.setLogin("testUser");
		User userFromDB = new User();
		userFromDB.setLogin("testUser");
		Product product = new Product();

		// Устанавливаем пустой список продуктов у пользователя
		userFromDB.setProductList(new ArrayList<>());

		// Имитируем вызовы методов и сервисов
		when(userRepo.findByLogin(userSession.getUsername())).thenReturn(userFromDB);
		when(productRepo.findById(productId)).thenReturn(Optional.of(product));

		// Вызываем метод контроллера
		String result = userController.addToCart(productId, userSession);

		// Проверяем, что методы и сервисы были вызваны с правильными аргументами
		verify(userRepo).findByLogin(userSession.getUsername());
		verify(productRepo).findById(productId);
		verify(userRepo).save(userFromDB);

		// Проверяем, что контроллер вернул правильное представление
		assertEquals("redirect:/cart", result);

		// Проверяем, что продукт был добавлен к списку продуктов пользователя
		assertNotNull(userFromDB.getProductList());
		assertTrue(userFromDB.getProductList().contains(product));
	}
}
