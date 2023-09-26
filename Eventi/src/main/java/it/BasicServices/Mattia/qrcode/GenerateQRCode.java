package it.BasicServices.Mattia.qrcode;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Mattia Marino
 */

public class GenerateQRCode {
    private static byte[] generateQRCode(String data, String charset, int h, int w, Map<EncodeHintType, ErrorCorrectionLevel> map) throws WriterException, IOException {
        BitMatrix matrix = new MultiFormatWriter().encode(new String(data.getBytes(charset), charset), BarcodeFormat.QR_CODE, w, h, map);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(matrixToImage(matrix), "png", outputStream);
        return outputStream.toByteArray();
    }

    private static BufferedImage matrixToImage(BitMatrix matrix) {
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, matrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
            }
        }
        return image;
    }

    public static byte[] generate(String data) throws WriterException, IOException {
        String charset = "UTF-8";
        int width = 200;
        int height = 200;

        Map<EncodeHintType, ErrorCorrectionLevel> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);

        return generateQRCode(data, charset, width, height, hints);
    }
}