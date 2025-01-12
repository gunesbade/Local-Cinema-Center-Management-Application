package com.example.cinemaapp;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * Utility class for generating PDF invoices for cinema purchases.
 * Uses Apache PDFBox to create and format the PDF document.
 */
public class PDFInvoiceGenerator {

    /**
     * Generates a PDF invoice and saves it to the specified file name.
     *
     * @param fileName       the name of the file to save the invoice as
     * @param customerDetails details of the customer (e.g., name, contact info)
     * @param ticketDetails  details of the purchased tickets
     * @param productDetails details of the purchased products
     * @param ticketTotal    the total cost of the tickets
     * @param productTotal   the total cost of the products
     */
    public static void generateInvoice(
            String fileName,
            String customerDetails,
            String ticketDetails,
            String productDetails,
            BigDecimal ticketTotal,
            BigDecimal productTotal
    ) {
        try (PDDocument document = new PDDocument()) {
            // Create a new page
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            // Use ContentStream to write content to the page
            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                // Title
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 18);
                contentStream.newLineAtOffset(50, 750);
                contentStream.showText("Cinema Invoice");
                contentStream.endText();

                // Content
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

            // Save the PDF
            document.save(fileName);
            System.out.println("Invoice saved as: " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
