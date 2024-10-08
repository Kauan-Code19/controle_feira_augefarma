package com.augefarma.controle_feira.integration.services.badge;

import com.augefarma.controle_feira.entities.laboratory.LaboratoryEntity;
import com.augefarma.controle_feira.entities.laboratory.LaboratoryMemberEntity;
import com.augefarma.controle_feira.entities.pharmacy_representative.PharmacyRepresentativeEntity;
import com.augefarma.controle_feira.repositories.laboratory.LaboratoryMemberRepository;
import com.augefarma.controle_feira.repositories.pharmacy_representative.PharmacyRepresentativeRepository;
import com.augefarma.controle_feira.services.badge.BadgeService;
import com.augefarma.controle_feira.services.badge.QRCodeService;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
public class BadgeServiceTest {

    @Autowired
    private BadgeService badgeService;

    @Autowired
    private QRCodeService qrCodeService;

    @MockBean
    private PharmacyRepresentativeRepository pharmacyRepresentativeRepository;

    @MockBean
    private LaboratoryMemberRepository laboratoryMemberRepository;

    private PharmacyRepresentativeEntity pharmacyRepresentative;
    private LaboratoryMemberEntity laboratoryMember;

    @BeforeEach
    void setup() {
        // Test data setup for a pharmacy representative
        pharmacyRepresentative = new PharmacyRepresentativeEntity();
        pharmacyRepresentative.setName("Maria Silva");
        pharmacyRepresentative.setCorporateReason("Farmácia Silva Ltda");
        pharmacyRepresentative.setCpf("123.456.789-00");

        // Test data setup for a laboratory member
        LaboratoryEntity laboratory = new LaboratoryEntity();
        laboratory.setCorporateReason("Laboratório XYZ");

        laboratoryMember = new LaboratoryMemberEntity();
        laboratoryMember.setName("Carlos Lima");
        laboratoryMember.setCpf("987.654.321-00");
        laboratoryMember.setLaboratory(laboratory);
    }

    @Test
    void testGenerateBadgeForPharmacyRepresentative_Success() throws Exception {
        // Calls the method to generate the badge for the pharmacy representative
        byte[] badgePdf = badgeService.generateBadge(pharmacyRepresentative);

        // Validates if the PDF was generated correctly
        assertNotNull(badgePdf, "Badge PDF should not be null");
        assertTrue(badgePdf.length > 0, "Badge PDF should contain data");

        // Calls the helper method to verify if the PDF is valid
        assertValidPDF(badgePdf);
    }

    @Test
    void testGenerateBadgeForLaboratoryMember_Success() throws Exception {
        // Calls the method to generate the badge for the laboratory member
        byte[] badgePdf = badgeService.generateBadge(laboratoryMember);

        // Validates if the PDF was generated correctly
        assertNotNull(badgePdf, "Badge PDF should not be null");
        assertTrue(badgePdf.length > 0, "Badge PDF should contain data");

        // Calls the helper method to verify if the PDF is valid
        assertValidPDF(badgePdf);
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
