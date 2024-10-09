package com.augefarma.controle_feira.unit.services.badge;

import com.augefarma.controle_feira.services.badge.QRCodeService;
import com.itextpdf.barcodes.exceptions.WriterException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.awt.image.BufferedImage;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class QRCodeServiceTest {
    private QRCodeService qrCodeService;

    @BeforeEach
    void setUp() {
        // Initializes the QRCodeService before each test
        qrCodeService = new QRCodeService();
    }

    @Test
    void testGenerateQRCode_Success() throws WriterException, com.google.zxing.WriterException {
        // Define a valid CPF for testing
        String cpf = "12345678900";

        // Calls the method to generate the QR code
        BufferedImage qrCodeImage = qrCodeService.generateQRCode(cpf);

        // Verifies that the QR code is generated successfully
        assertNotNull(qrCodeImage, "QR code should not be null");
        assertEquals(200, qrCodeImage.getWidth(), "QR code width should be 200 pixels");
        assertEquals(200, qrCodeImage.getHeight(), "QR code height should be 200 pixels");
    }
}
