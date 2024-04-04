package com.example.diploma.services;

import com.example.diploma.models.*;
import com.example.diploma.repos.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Base64;
import java.util.Comparator;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class ProductService {
    private final CategoryRepo categoryRepo;
    private final ProductRepo productRepo;
    private final ImageRepo imageRepo;
    private final SupplierRepo supplierRepo;
    private final UserRepo userRepo;
    private final CategoryService categoryService;
    public void saveProduct(Product product, MultipartFile file1, String category) throws IOException {
        Image image1;
        if(file1.getSize()!=0)
        {
            image1=addImage(file1);
            image1.setIsPreviewImage(true);
            product.addImagetoProduct(image1);
        }

        log.info("Saving new product. ",product.getName(),product.getPrice());
        Category categoryFromDB = categoryRepo.findByName(category).orElse(null);
        product.setCategory(categoryFromDB);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String nameUser = authentication.getName();
        User user=userRepo.findByLogin(nameUser);
        int idUser= user.getIdUser();
        Supplier supplier = supplierRepo.findByIdUser(idUser);
        if (supplier != null) {
            product.setSupplierId(supplier.getIdsupplier());
        }
        Product product1=productRepo.save(product);
        product1.setPreviewImageId(product1.getImages().get(0).getIdimage());
        productRepo.save(product);
    }
    private Image addImage(MultipartFile file) throws IOException
    {
        Image image =new Image();
        image.setName(file.getName());
        image.setOriginalFileName(file.getOriginalFilename());
        image.setContentType(file.getContentType());
        image.setSize(file.getSize());
        byte[]bytes=  file.getBytes();
        String byt= Base64.getEncoder().encodeToString(bytes);
        image.setBytes(byt);
        return image;

    }
    public List<Product> findAll()
    {
        Iterable<Product> filteredProducts = productRepo.findAll();
        return (List<Product>) filteredProducts;
    }
    public Product getProductById(int id) {
        Product product = productRepo.findById(id).orElse(null);

        return product;
    }

    public void updateProduct(int id, Product updatedProduct,MultipartFile file1,String category) throws IOException {
        Image image1;
        Category categoryFromDB = categoryRepo.findByName(category).orElse(null);
        updatedProduct.setCategory(categoryFromDB);
        Product product=getProductById(id);
        if (!product.getCategory().equals(updatedProduct.getCategory()))
        {
            product.setCategory(updatedProduct.getCategory());
        }
        if (!product.getName().equals(updatedProduct.getName()))
        {
            product.setName(updatedProduct.getName());
        }
        if (!product.getVendor_code().equals(updatedProduct.getVendor_code()))
        {
            product.setVendor_code(updatedProduct.getVendor_code());
        }

        if (!product.getDescription().equals(updatedProduct.getDescription()))
        {
            product.setDescription(updatedProduct.getDescription());
        }

        if (product.getPrice()!=updatedProduct.getPrice())
        {
            product.setPrice(updatedProduct.getPrice());
        }

        if(file1!=null&&!file1.isEmpty())
        {
            image1=addImage(file1);
            image1.setIsPreviewImage(true);
            product.addImagetoProduct(image1);

            if(product.getPreviewImageId()!=null)
            {
                imageRepo.deleteById(product.getPreviewImageId());

            }
            product.setPreviewImageId(image1.getIdimage());
        }
        productRepo.save(product);

    }

//    public List<Product> searchProducts(String query)
//    {
//        List<Product> products=productRepo.search(query);
//        return products;
//    }
    public BigDecimal minProductPrice() {
        return productRepo.minProductPrice();
    }
    public BigDecimal maxProductPrice() {
        return productRepo.maxProductPrice();
    }
    public List<Product> getFilteredProducts(List <Product>products,String sortBy)
    {
        // Применяем сортировку
        if (sortBy != null && !sortBy.isEmpty()) {
            switch (sortBy) {
                case "price_asc":
                    products.sort(Comparator.comparingDouble(Product::getPrice));
                    break;
                case "price_desc":
                    products.sort(Comparator.comparingDouble(Product::getPrice).reversed());
                    break;
                case "good_asc":
                    products.sort(Comparator.comparing(Product::getName));
                    break;
                case "good_desc":
                    products.sort(Comparator.comparing(Product::getName).reversed());
                    break;
                default:
                    break;
            }

        }

        return products;
    }

    public List<Product> findProductByCategory(Category category)
    {
        return productRepo.findProductByCategory(category);
    }
}
