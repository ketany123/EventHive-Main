package com.EventHive.service;




import com.google.zxing.common.HybridBinarizer;
import org.springframework.stereotype.Service;
import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.BitMatrix;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class QrCodeService {

    private static final String QR_CODE_IMAGE_PATH = "./qr-codes/"; // Path where QR code images will be saved

    public byte[] generateQRCodeImage(String data) throws Exception {
        Map<EncodeHintType, Object> hintMap = new HashMap<>();
        hintMap.put(EncodeHintType.MARGIN, 1); // Optional: to adjust margin

        // Generate the QR code as a BitMatrix
        BitMatrix bitMatrix = new MultiFormatWriter().encode(
                data,
                BarcodeFormat.QR_CODE, 200, 200, hintMap
        );

        // Create a BufferedImage to represent the QR code
        BufferedImage image = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < 200; x++) {
            for (int y = 0; y < 200; y++) {
                image.setRGB(x, y, bitMatrix.get(x, y) ? Color.BLACK.getRGB() : Color.WHITE.getRGB());
            }
        }

        // Save the QR code image to a file locally
        saveQRCodeImageToFile(image);

        // Convert the image to a byte array to return
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "PNG", baos);
        return baos.toByteArray();
    }

    private void saveQRCodeImageToFile(BufferedImage image) throws IOException {
        // Create a directory if it doesn't exist
        File directory = new File(QR_CODE_IMAGE_PATH);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Set the file path with a unique name, e.g., based on the current timestamp
        String fileName = "QRCode_" + System.currentTimeMillis() + ".png";
        File qrCodeFile = new File(QR_CODE_IMAGE_PATH + fileName);

        // Save the BufferedImage as a PNG file on the local filesystem
        ImageIO.write(image, "PNG", qrCodeFile);

        // Log the file path for reference
        System.out.println("QR code image saved at: " + qrCodeFile.getAbsolutePath());
    }

    public String decodeQRCode(BufferedImage qrCodeImage) throws Exception {
        LuminanceSource source = new BufferedImageLuminanceSource(qrCodeImage);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
        Result result = new MultiFormatReader().decode(bitmap);
        return result.getText();  // Return the decoded QR code data
    }
}
