package com.example.diploma.config.word;


import com.example.diploma.models.Contract;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;

@Configuration
public class WordGenerator {

    public static byte[] generateWordDocument(Contract contract) throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            XWPFDocument document = new XWPFDocument();

            // Заголовок документа
            XWPFParagraph titleParagraph = document.createParagraph();
            titleParagraph.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun titleRun = titleParagraph.createRun();
            titleRun.setText("ДОГОВОР НА ПОСТАВКУ ТОВАРОВ");
            titleRun.setFontFamily("Times New Roman");
            titleRun.setFontSize(16);
            titleRun.setBold(true);

            // Добавляем данные из контракта
            addContractData(document, "1. Стороны договора", "Настоящий договор заключен между " +
                    "Поставщиком " + contract.getSupplier().getCompanyName() +
                    " и Заказчиком " + contract.getOrder().getLastName() + " " + contract.getOrder().getFirstName() +
                    " о поставке товаров на сумму:"+contract.getCost());

            addContractData(document, "2. Предмет договора", "Поставщик обязуется поставить, а Заказчик " +
                    "обязуется принять и оплатить товары, согласно условиям настоящего договора.");

            addContractData(document, "3. Условия поставки", "Товары по настоящему договору должны быть " +
                    "поставлены Поставщиком по адресу и в сроки, указанные в заказе Заказчика.");

            addContractData(document, "4. Стоимость товаров", "Стоимость товаров определяется в соответствии " +
                    "с ценами, указанными в приложенном к настоящему договору счете-фактуре."+"Стоимость договора составляет: "+contract.getCost()+"руб.");

            addContractData(document, "5. Оплата", "Заказчик обязуется оплатить стоимость товаров в течение " +
                    "указанного срока после получения счета-фактуры от Поставщика.");

            addContractData(document, "6. Сроки поставки", "Сроки поставки товаров устанавливаются в соответствии " +
                    "с графиком, согласованным сторонами.");

            // Добавляем данные о поставщике
            addContractData(document, "7. Данные о поставщике", "Название компании поставщика: " +
                    contract.getSupplier().getCompanyName() +
//                    ", ИНН: " + contract.getSupplier().getInn() +
                    ", Адрес: " + contract.getSupplier().getAddress());

            // Заключительные положения
            addContractData(document, "8. Заключительные положения", "Настоящий договор считается заключенным и вступает в " +
                    "силу с момента подписания его обеими сторонами.");


            // Заключительные положения
            addContractData(document, "9. Заключительные положения", "Все изменения и дополнения к настоящему договору " +
                    "действительны только в письменной форме и подписываются обеими сторонами.");

            // Подписи
            addContractData(document, "10. Подписи сторон", "Настоящий договор считается заключенным и вступает в " +
                    "силу с момента подписания его обеими сторонами.");

            // Записываем документ в выходной поток
            // Создание параграфов для подписей и печати
            XWPFParagraph signaturesParagraph = document.createParagraph();
            signaturesParagraph.setAlignment(ParagraphAlignment.LEFT);

            XWPFParagraph stampParagraph = document.createParagraph();
            stampParagraph.setAlignment(ParagraphAlignment.RIGHT);

// Добавление подписей сторон
            XWPFRun signatureRun = signaturesParagraph.createRun();
            signatureRun.setFontFamily("Times New Roman");
            signatureRun.setFontSize(14);
            signatureRun.setText("Подписи сторон:");
            signatureRun.addBreak();

            XWPFRun supplierSignatureRun = signaturesParagraph.createRun();
            supplierSignatureRun.setText("Подпись поставщика: _________________________");
            supplierSignatureRun.addBreak();
            supplierSignatureRun.setFontFamily("Times New Roman");
            supplierSignatureRun.setFontSize(14);
            XWPFRun orderSignatureRun = signaturesParagraph.createRun();
            orderSignatureRun.setText("Подпись заказчика: _________________________");
            orderSignatureRun.addBreak();
            orderSignatureRun.addBreak();
            orderSignatureRun.setFontFamily("Times New Roman");
            orderSignatureRun.setFontSize(14);

// Получение пути к изображению печати
            String imagePath = "D:\\2564.png"; // Замените на фактический путь к вашему изображению

// Вставка изображения печати
            try (FileInputStream inputStream = new FileInputStream(imagePath)) {
                XWPFRun stampRun = stampParagraph.createRun();
                stampRun.addBreak();

                // Вставляем изображение в документ
                int format = XWPFDocument.PICTURE_TYPE_PNG; // Формат изображения (PNG)
                stampRun.addPicture(inputStream, format, imagePath, Units.toEMU(150), Units.toEMU(150));
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InvalidFormatException e) {
                throw new RuntimeException(e);
            }

            document.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    private static void addContractData(XWPFDocument document, String key, String value) {
        XWPFParagraph paragraph = document.createParagraph();
        paragraph.setAlignment(ParagraphAlignment.LEFT);
        XWPFRun run = paragraph.createRun();
        run.setText(key + " " + value);
        run.setFontFamily("Times New Roman");
        run.setFontSize(14);
    }
}
