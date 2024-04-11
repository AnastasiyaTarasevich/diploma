package com.example.diploma.controllers;


import com.example.diploma.models.*;
import com.example.diploma.repos.SupplierRepo;
import com.example.diploma.repos.SupplierReviewRepo;
import com.example.diploma.repos.UserRepo;
import com.example.diploma.services.CategoryService;
import com.example.diploma.services.ProductService;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Date;

import org.apache.poi.openxml4j.opc.internal.ContentType;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ExcelImportController {

    private final SupplierRepo supplierRepo;
    private final SupplierReviewRepo supplierReviewRepo;
    private final UserRepo userRepo;
    private final ProductService productService;
    private final CategoryService categoryService;

    @PostMapping("/importReviews")
    public ResponseEntity<String> importReviews(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {

        supplierReviewRepo.deleteAll();
        try (InputStream inputStream = file.getInputStream()) {
            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheetAt(0);

            Iterator<Row> iterator = sheet.iterator();
            while (iterator.hasNext()) {
                Row currentRow = iterator.next();

                if (currentRow.getRowNum() == 0) {
                    continue;
                }

                SupplierReview supplierReview = new SupplierReview();
                 currentRow.getCell(0).setCellType(CellType.STRING);
                 String name= (currentRow.getCell(0).getStringCellValue());
                Supplier supplier=supplierRepo.findSupplierByCompanyName(name);
                supplierReview.setSupplier(supplier);
                supplierReview.setGrade(currentRow.getCell(1).getNumericCellValue());
                supplierReview.setComments(currentRow.getCell(2).getStringCellValue());
                supplierReview.setDateOfCreate(new Date(currentRow.getCell(3).getDateCellValue().getTime()));
               supplierReviewRepo.save(supplierReview);

            }

            return ResponseEntity.status(HttpStatus.FOUND)
                    .header("Location", "/rate")
                    .body("Отзывы успешно импортированы.");
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ошибка при импорте отзывов.");
        }
    }


}