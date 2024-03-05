package its-a-feature-not-a-bug.app.src.main.java.com;

import android.os.Bundle;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.common.BitMatrix;
//  implementation 'com.google.zxing:core:3.4.1'

public void createEvent(String eventName, String eventDate, String eventLocation, String eventQRCode) {
        if (eventQRCode == null) {
        eventQRCode = generateQRCode(eventName);
        }
        Event event = new Event(eventName, eventDate, eventLocation, eventQRCode);
        // Save the event to your database
        }
        }

private String generateQRCode(String eventName) {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        try {
        BitMatrix bitMatrix = qrCodeWriter.encode(eventName, BarcodeFormat.QR_CODE, 200, 200);
        // Convert the BitMatrix to a bitmap and return as a string
        } catch (WriterException e) {
        e.printStackTrace();
        }
        return null;
        }