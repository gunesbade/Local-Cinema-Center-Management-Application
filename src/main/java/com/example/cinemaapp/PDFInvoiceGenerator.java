package com.example.cinemaapp;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

import java.io.IOException;
import java.math.BigDecimal;

public class PDFInvoiceGenerator {

    public static void generateInvoice(
            String fileName,
            String customerDetails,
            String ticketDetails,
            String productDetails,
            BigDecimal ticketTotal,
            BigDecimal productTotal
    ) {
        try (PDDocument document = new PDDocument()) {
            // Yeni bir sayfa oluştur
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            // İçeriği yazmak için ContentStream kullan
            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                // Başlık
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 18);
                contentStream.newLineAtOffset(50, 750);
                contentStream.showText("Cinema Invoice");
                contentStream.endText();

                // İçerik
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA, 12);
                contentStream.newLineAtOffset(50, 720);
                contentStream.showText("Customer Details: " + customerDetails);
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Tickets: " + ticketDetails);
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Products: " + productDetails);
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Ticket Total: $" + ticketTotal);
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Product Total: $" + productTotal);
                contentStream.newLineAtOffset(0, -20);

                BigDecimal grandTotal = ticketTotal.add(productTotal);
                contentStream.showText("Grand Total: $" + grandTotal);
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Thank you for your purchase!");
                contentStream.endText();
            }

            // PDF'yi kaydet
            document.save(fileName);
            System.out.println("Invoice saved as: " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
