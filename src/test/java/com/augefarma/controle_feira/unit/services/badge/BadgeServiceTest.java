package com.augefarma.controle_feira.unit.services.badge;

import com.augefarma.controle_feira.entities.laboratory.LaboratoryEntity;
import com.augefarma.controle_feira.entities.laboratory.LaboratoryMemberEntity;
import com.augefarma.controle_feira.entities.pharmacy_representative.PharmacyRepresentativeEntity;
import com.augefarma.controle_feira.services.badge.BadgeService;
import com.augefarma.controle_feira.services.badge.QRCodeService;
import com.google.zxing.WriterException;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class BadgeServiceTest {

    @Mock
    private QRCodeService qrCodeService;

    @InjectMocks
    private BadgeService badgeService;

    @BeforeEach
    void setup() {
        // Initialize mocks
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGenerateBadgeForPharmacyRepresentative_Success() throws WriterException, IOException {
        // Arrange: Create a dummy PharmacyRepresentativeEntity
        PharmacyRepresentativeEntity pharmacyRepresentative = new PharmacyRepresentativeEntity();
        pharmacyRepresentative.setName("John Doe");
        pharmacyRepresentative.setCorporateReason("Pharma Inc.");
        pharmacyRepresentative.setCpf("123456789");

        // Mock the QR code generation method to return a sample image
        BufferedImage mockQRCode = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        when(qrCodeService.generateQRCode(anyString())).thenReturn(mockQRCode);

        // Act: Call the method to generate the badge PDF
        byte[] badgePdf = badgeService.generateBadge(pharmacyRepresentative);

        // Assert: Ensure the generated PDF is not null and contains content
        assertNotNull(badgePdf, "The generated badge PDF should not be null.");
        assertValidPDF(badgePdf);

        // Verify that the QR code generation was called with the correct CPF
        verify(qrCodeService, times(1)).generateQRCode("123456789");
    }

    @Test
    void testGenerateBadgeForLaboratoryMember_Success() throws WriterException, IOException {
        // Arrange: Create a dummy LaboratoryMemberEntity
        LaboratoryMemberEntity laboratoryMember = new LaboratoryMemberEntity();
        laboratoryMember.setName("Jane Doe");

        LaboratoryEntity laboratory = new LaboratoryEntity();
        laboratory.setCorporateReason("LabTech");

        laboratoryMember.setLaboratory(laboratory);
        laboratoryMember.setCpf("987654321");

        // Mock the QR code generation method to return a sample image
        BufferedImage mockQRCode = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        when(qrCodeService.generateQRCode(anyString())).thenReturn(mockQRCode);

        // Act: Call the method to generate the badge PDF
        byte[] badgePdf = badgeService.generateBadge(laboratoryMember);

        // Assert: Ensure the generated PDF is not null and contains content
        assertNotNull(badgePdf, "The generated badge PDF should not be null.");
        assertValidPDF(badgePdf);

        // Verify that the QR code generation was called with the correct CPF
        verify(qrCodeService, times(1)).generateQRCode("987654321");
    }

    @Test
    void testGenerateBadge_ThrowsWriterException() throws WriterException, IOException {
        // Arrange: Create a dummy PharmacyRepresentativeEntity
        PharmacyRepresentativeEntity pharmacyRepresentative = new PharmacyRepresentativeEntity();
        pharmacyRepresentative.setName("John Doe");
        pharmacyRepresentative.setCorporateReason("Pharma Inc.");
        pharmacyRepresentative.setCpf("123456789");

        // Mock the QR code generation to throw a WriterException
        when(qrCodeService.generateQRCode(anyString())).thenThrow(new WriterException("QR Code generation error"));

        // Act & Assert: Ensure the exception is thrown when trying to generate the badge
        assertThrows(WriterException.class, () -> {
            badgeService.generateBadge(pharmacyRepresentative);
        });

        // Verify that the QR code generation was called
        verify(qrCodeService, times(1)).generateQRCode("123456789");
    }

    // Helper method to validate the generated PDF
    private void assertValidPDF(byte[] pdfContent) throws IOException {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(pdfContent)) {
            PdfDocument pdfDocument = new PdfDocument(new PdfReader(byteArrayInputStream));
            assertNotNull(pdfDocument, "The PDF document should be valid.");
            pdfDocument.close();
        }
    }
}
