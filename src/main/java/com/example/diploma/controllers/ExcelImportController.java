package com.example.diploma.controllers;


import com.example.diploma.models.Supplier;
import com.example.diploma.models.SupplierReview;
import com.example.diploma.repos.SupplierRepo;
import com.example.diploma.repos.SupplierReviewRepo;
import lombok.RequiredArgsConstructor;
import java.sql.Date;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

@RestController
@RequiredArgsConstructor
public class ExcelImportController {

    private final SupplierRepo supplierRepo;
    private final SupplierReviewRepo supplierReviewRepo;

    @PostMapping("/importReviews")
    public String importReviews(@RequestParam("file") MultipartFile file) {
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

            return "Отзывы успешно импортированы.";
        } catch (IOException e) {
            e.printStackTrace();
            return "Ошибка при импорте отзывов.";
        }
    }
}
