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

    /**
     * Generates a badge for a client.
     *
     * @param client the client entity containing information for the badge
     * @return a byte array representing the generated badge PDF
     * @throws WriterException if an error occurs during QR code generation
     * @throws IOException if an error occurs during PDF creation
     */
    public byte[] generateBadge(ClientEntity client) throws WriterException, IOException {
        return createBadgePDF(client.getFullName(), client.getCorporateReason(), client.getCpf());
    }

    /**
     * Generates a badge for a laboratory.
     *
     * @param laboratory the laboratory entity containing information for the badge
     * @return a byte array representing the generated badge PDF
     * @throws WriterException if an error occurs during QR code generation
     * @throws IOException if an error occurs during PDF creation
     */
    public byte[] generateBadge(LaboratoryEntity laboratory) throws WriterException, IOException {
        return createBadgePDF(laboratory.getName(), laboratory.getCorporateReason(), laboratory.getCpf());
    }

    /**
     * Creates a badge PDF containing a QR code, full name, and corporate reason.
     *
     * @param fullName the full name to include in the badge
     * @param corporateReason the corporate reason to include in the badge
     * @param cpf the CPF to encode in the QR code
     * @return a byte array representing the generated badge PDF
     * @throws WriterException if an error occurs during QR code generation
     * @throws IOException if an error occurs during PDF creation
     */
    private byte[] createBadgePDF(String fullName, String corporateReason, String cpf)
            throws WriterException, IOException {

        // Generate the QR code image for the provided CPF
        BufferedImage qrCodeImage = qrCodeService.generateQRCode(cpf);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        // Initialize PdfWriter with the output stream
        PdfWriter pdfWriter = new PdfWriter(byteArrayOutputStream);

        // Initialize PdfDocument with the PdfWriter
        PdfDocument pdfDocument = new PdfDocument(pdfWriter);

        // Initialize Document with the PdfDocument
        Document document = new Document(pdfDocument);

        // Convert the BufferedImage QR code to an iText Image
        Image qrImage = new Image(ImageDataFactory.create(qrCodeImage, null));

        // Add the QR code image to the document
        document.add(qrImage);

        // Add the full name to the document with font size 20
        document.add(new Paragraph(fullName).setFontSize(20));

        // Add the corporate reason to the document with font size 18
        document.add(new Paragraph(corporateReason).setFontSize(18));

        // Close the document to finalize it
        document.close();

        // Return the byte array containing the generated PDF
        return byteArrayOutputStream.toByteArray();
    }
}
