package com.augefarma.controle_feira.services.badge;

import com.augefarma.controle_feira.entities.client.ClientEntity;
import com.augefarma.controle_feira.entities.laboratory.LaboratoryEntity;
import com.google.zxing.WriterException;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class BadgeService {

    private final QRCodeService qrCodeService;

    @Autowired
    public BadgeService(QRCodeService qrCodeService) {
        this.qrCodeService = qrCodeService;
    }

    public byte[] generateBadge(ClientEntity client) throws WriterException, IOException {
        return createBadgePDF(client.getFullName(), client.getCorporateReason(), client.getCpf());
    }

    public byte[] generateBadge(LaboratoryEntity laboratory) throws WriterException, IOException {
        return createBadgePDF(laboratory.getName(), laboratory.getCorporateReason(), laboratory.getCpf());
    }

    private byte[] createBadgePDF(String fullName, String corporateReason, String cpf)
            throws WriterException, IOException {

        BufferedImage qrCodeImage = qrCodeService.generateQRCode(cpf);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        PdfWriter pdfWriter = new PdfWriter(byteArrayOutputStream);
        PdfDocument pdfDocument = new PdfDocument(pdfWriter);
        Document document = new Document(pdfDocument);

        Image qrImage = new Image(ImageDataFactory.create(qrCodeImage, null));

        document.add(qrImage);

        document.add(new Paragraph(fullName).setFontSize(20));
        document.add(new Paragraph(corporateReason).setFontSize(18));

        document.close();

        return byteArrayOutputStream.toByteArray();
    }
}
