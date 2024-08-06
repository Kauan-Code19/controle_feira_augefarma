package com.augefarma.controle_feira.services.badge;

import com.augefarma.controle_feira.entities.laboratory.LaboratoryMemberEntity;
import com.augefarma.controle_feira.entities.pharmacy_representative.PharmacyRepresentativeEntity;
import com.google.zxing.WriterException;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
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
     * @param pharmacyRepresentative the client entity containing information for the badge
     * @return a byte array representing the generated badge PDF
     * @throws WriterException if an error occurs during QR code generation
     * @throws IOException     if an error occurs during PDF creation
     */
    public byte[] generateBadge(PharmacyRepresentativeEntity pharmacyRepresentative)
            throws WriterException, IOException {
        return createBadgePDF(
                pharmacyRepresentative.getName(),
                pharmacyRepresentative.getCorporateReason(),
                pharmacyRepresentative.getCpf());
    }

    /**
     * Generates a badge for a laboratory.
     *
     * @param laboratoryMember the laboratory entity containing information for the badge
     * @return a byte array representing the generated badge PDF
     * @throws WriterException if an error occurs during QR code generation
     * @throws IOException     if an error occurs during PDF creation
     */
    public byte[] generateBadge(LaboratoryMemberEntity laboratoryMember)
            throws WriterException, IOException {
        return createBadgePDF(
                laboratoryMember.getName(),
                laboratoryMember.getLaboratory().getCorporateReason(),
                laboratoryMember.getCpf());
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

        // Set the page size to 9 cm x 5 cm (converted to points)
        PageSize pageSize = new PageSize(9 * 72 / 2.54f, 5 * 72 / 2.54f);
        pdfDocument.setDefaultPageSize(pageSize);
        document.setMargins(0, 0, 0, 0);

        // Define column widths: 60% for text, 40% for QR code
        float columnWidth = pageSize.getWidth() * 0.6f;
        float qrCodeWidth = pageSize.getWidth() * 0.4f;
        float[] columnWidths = {columnWidth, qrCodeWidth};

        // Create a table with two columns and set its width to 100%
        Table table = new Table(columnWidths).setWidth(UnitValue.createPercentValue(100));

        // Convert text to uppercase for consistent styling
        String upperCaseCorporateReason = corporateReason.toUpperCase();
        String upperCaseFullName = fullName.toUpperCase();

        // Create a cell for the corporate reason and full name
        Cell textCell = new Cell()
                .add(new Paragraph(upperCaseCorporateReason)
                        .setFontSize(12)
                        .setFixedLeading(14)  // Line spacing
                        .setTextAlignment(TextAlignment.LEFT)
                        .setMarginBottom(4))  // Space between paragraphs
                .add(new Paragraph(upperCaseFullName)
                        .setFontSize(10)
                        .setTextAlignment(TextAlignment.LEFT)
                        .setFixedLeading(12)  // Line spacing
                        .setMinHeight(15)  // Prevents text from being too close together
                        .setMargin(0))  // Removes additional margin
                .setBorder(Border.NO_BORDER)
                .setPaddingLeft(12)  // Adds padding on the left
                .setVerticalAlignment(VerticalAlignment.MIDDLE);  // Center content vertically

        // Convert the BufferedImage QR code to an iText Image
        Image qrImage = new Image(ImageDataFactory.create(qrCodeImage, null))
                .setWidth(80)  // Fixed width for the QR code
                .setHeight(80)
                .setHorizontalAlignment(HorizontalAlignment.RIGHT);  // Align QR code to the right

        // Create a cell for the QR code
        Cell qrCodeCell = new Cell()
                .add(qrImage)
                .setBorder(Border.NO_BORDER)
                .setPadding(0)
                .setVerticalAlignment(VerticalAlignment.MIDDLE);  // Center content vertically

        // Add the cells to the table
        table.addCell(textCell);
        table.addCell(qrCodeCell);

        // Calculate margin top for vertical centering of the table
        float marginTop = (pageSize.getHeight() - 80) / 2;

        // Set fixed position for the table to ensure vertical centering
        table.setFixedPosition(0, marginTop, pageSize.getWidth());

        // Add the table to the document
        document.add(table);

        // Close the document to finalize it
        document.close();

        // Return the byte array containing the generated PDF
        return byteArrayOutputStream.toByteArray();
    }
}
