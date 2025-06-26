package com.modules.invoicer.invoice.application;

import com.modules.invoicer.invoice.domain.Invoice;
import com.modules.invoicer.invoice.domain.InvoiceItem;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;

@Service
public class InvoicePdfService {

    private static final Logger logger = LoggerFactory.getLogger(InvoicePdfService.class);

    /**
     * Generates a PDF document with basic invoice information.
     * @param invoice the invoice to render
     * @return PDF as byte array
     */
    public byte[] generateInvoicePdf(Invoice invoice) {
        logger.info("Generating PDF for invoice {}", invoice.getInvoiceNumber());
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            PdfWriter.getInstance(document, out);
            document.open();

            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 12);

            document.add(new Paragraph("Factura " + invoice.getInvoiceNumber(), headerFont));
            document.add(new Paragraph("Cliente: " + invoice.getCustomer().getName(), normalFont));
            document.add(new Paragraph("Fecha: " + invoice.getInvoiceDate(), normalFont));
            document.add(new Paragraph("Vencimiento: " + invoice.getDueDate(), normalFont));
            document.add(Chunk.NEWLINE);

            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.addCell(new PdfPCell(new Phrase("Descripción", headerFont)));
            table.addCell(new PdfPCell(new Phrase("Cantidad", headerFont)));
            table.addCell(new PdfPCell(new Phrase("Precio", headerFont)));
            table.addCell(new PdfPCell(new Phrase("Total", headerFont)));

            for (InvoiceItem item : invoice.getItems()) {
                table.addCell(new PdfPCell(new Phrase(item.getDescription(), normalFont)));
                table.addCell(new PdfPCell(new Phrase(format(item.getQuantity()), normalFont)));
                table.addCell(new PdfPCell(new Phrase(format(item.getUnitPrice()), normalFont)));
                table.addCell(new PdfPCell(new Phrase(format(item.getTotal()), normalFont)));
            }

            document.add(table);
            document.add(Chunk.NEWLINE);
            document.add(new Paragraph("Subtotal: " + format(invoice.getSubtotal()), normalFont));
            document.add(new Paragraph("Impuestos: " + format(invoice.getTaxAmount()), normalFont));
            document.add(new Paragraph("Total: " + format(invoice.getTotalAmount()), headerFont));
        } catch (DocumentException e) {
            logger.error("Error generating PDF", e);
        } finally {
            document.close();
        }
        return out.toByteArray();
    }

    private String format(BigDecimal value) {
        if (value == null) {
            return "";
        }
        return value.setScale(2, BigDecimal.ROUND_HALF_UP).toString();
    }
}
