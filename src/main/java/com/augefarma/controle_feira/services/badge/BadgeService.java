package com.augefarma.controle_feira.services.badge;

import com.augefarma.controle_feira.entities.laboratory.LaboratoryMemberEntity;
import com.augefarma.controle_feira.entities.pharmacy_representative.PharmacyRepresentativeEntity;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outputStream));
        pdfDoc.setDefaultPageSize(new PageSize(576, 360));

        Document document = new Document(pdfDoc);

        // Definir larguras das colunas (em porcentagem)
        float[] columnWidths = {2f, 1f}; // 2: maior largura para o nome e razão, 1: menor largura para o QR Code

        // Criação da tabela com 2 colunas e definição das larguras
        Table table = new Table(columnWidths);
        table.setWidth(UnitValue.createPercentValue(100)); // Define largura total da tabela em 100%
        table.setHorizontalAlignment(HorizontalAlignment.CENTER);

        // Add full name
        Paragraph reasonParagraph = new Paragraph(new Text(corporateReason).setFontSize(20).setBold());
        reasonParagraph.setTextAlignment(TextAlignment.CENTER);

        // Add corporate reason
        Paragraph nameParagraph = new Paragraph(fullName).setFontSize(18).setTextAlignment(TextAlignment.CENTER);

        // Add both paragraphs to a single cell
        Cell nameReasonCell = new Cell();
        nameReasonCell.setVerticalAlignment(VerticalAlignment.MIDDLE);
        nameReasonCell.setBorder(Border.NO_BORDER);
        nameReasonCell.setPadding(0);
        nameReasonCell.add(reasonParagraph);
        nameReasonCell.add(nameParagraph);
        nameReasonCell.setMarginRight(0);

        // Add cell to the first column of the table
        table.addCell(nameReasonCell);

        // Generate QR code
        int qrCodeWidth = 120;
        BitMatrix bitMatrix = new QRCodeWriter().encode(cpf, BarcodeFormat.QR_CODE, qrCodeWidth, qrCodeWidth);
        ByteArrayOutputStream qrCodeOutputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", qrCodeOutputStream);

        // Cria a imagem do código QR no PDF
        byte[] qrCodeBytes = qrCodeOutputStream.toByteArray();
        ImageData imageData = ImageDataFactory.create(qrCodeBytes);
        Image qrCodeImage = new Image(imageData);
        qrCodeImage.scaleToFit(125, 125);
        qrCodeImage.setHorizontalAlignment(HorizontalAlignment.LEFT);

        Cell qrCodeCell = new Cell();
        qrCodeCell.add(qrCodeImage);
        qrCodeCell.setPadding(0);
        qrCodeCell.setBorder(Border.NO_BORDER); // Remover a borda da célula do QR code
        qrCodeCell.setMarginLeft(0);

        // Add QR code image to the second column
        table.addCell(qrCodeCell);

        // Calcular a altura da página e adicionar margem superior para centralizar
        float pageHeight = pdfDoc.getDefaultPageSize().getHeight();
        float tableHeight = 180f; // Defina um valor aproximado para a altura da tabela, se necessário
        float marginTop = (pageHeight - tableHeight) / 2;

        // Adicionar um parágrafo vazio com a altura da margem superior
        document.add(new Paragraph().setHeight(marginTop));

        // Adicionar a tabela ao documento
        document.add(table);

        document.close();

        return outputStream.toByteArray();
    }
}
