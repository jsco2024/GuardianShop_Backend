package com.ms_security.ms_security.utilities;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.ms_security.ms_security.service.model.dto.OrderDto;
import com.ms_security.ms_security.service.model.dto.OrderPdfDto;

import java.io.ByteArrayOutputStream;

public class PdfGenerator {

    public byte[] generateOrderPdf(OrderPdfDto orderDto) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        try {
            PdfWriter pdfWriter = new PdfWriter(byteArrayOutputStream);
            PdfDocument pdfDocument = new PdfDocument(pdfWriter);
            Document document = new Document(pdfDocument);

            PdfFont helveticaFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);

            document.add(new Paragraph("ORDER NUMBER: " + orderDto.getOrderNumber())
                    .setFont(helveticaFont)
                    .setFontColor(ColorConstants.BLACK));
            document.add(new Paragraph("UNIT PRICE: " + orderDto.getUnitPrice())
                    .setFont(helveticaFont));
            document.add(new Paragraph("PRODUCT: " + orderDto.getName())
                    .setFont(helveticaFont));
            document.add(new Paragraph("QUANTITY: " + orderDto.getQuantity())
                    .setFont(helveticaFont));
            document.add(new Paragraph("TOTAL PRICE: " + orderDto.getTotalPrice())
                    .setFont(helveticaFont));

            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return byteArrayOutputStream.toByteArray();
    }
}

