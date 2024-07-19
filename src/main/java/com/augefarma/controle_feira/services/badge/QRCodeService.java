package com.augefarma.controle_feira.services.badge;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.stereotype.Service;
import java.awt.image.BufferedImage;

@Service
public class QRCodeService {

    /**
     * Generates a QR code image for the given CPF.
     *
     * @param cpf the CPF to encode in the QR code
     * @return a BufferedImage representing the QR code
     * @throws WriterException if an error occurs during QR code generation
     */
    public BufferedImage generateQRCode(String cpf) throws WriterException {
        // Create an instance of QRCodeWriter to generate the QR code
        QRCodeWriter qrCodeWriter = new QRCodeWriter();

        // Encode the CPF into a QR code with specified width and height (200x200)
        BitMatrix bitMatrix = qrCodeWriter.encode(cpf, BarcodeFormat.QR_CODE, 200, 200);

        // Convert the BitMatrix to a BufferedImage and return it
        return MatrixToImageWriter.toBufferedImage(bitMatrix);
    }
}
