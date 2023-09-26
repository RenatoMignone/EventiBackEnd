package it.BasicServices.Mattia.qrcode;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

import javax.imageio.ImageIO;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Mattia Marino
 */

public class ReadQRCode {
    //User-defined method that reads the QR code
    public static String readQRcode(String path, Map<DecodeHintType, Boolean> hintMap) throws IOException, NotFoundException {
        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(ImageIO.read(new FileInputStream(path)))));
        Result rslt = new MultiFormatReader().decode(binaryBitmap, hintMap);
        return rslt.getText();
    }

    public static String read(String path) throws NotFoundException, IOException {
        //Encoding charset to be used

        Map<DecodeHintType, Boolean> hintMap = new HashMap<>();
        //generates QR code with Low level(L) error correction capability
        hintMap.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);

        //Extract data from QR code using the user-defined method and print (debug)
        String data = readQRcode(path, hintMap);
        System.out.println("Data stored in the QR Code is:\n"+ data);

        return data;
    }
}