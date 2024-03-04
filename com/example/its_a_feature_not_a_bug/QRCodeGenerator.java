package com.example.its_a_feature_not_a_bug;

public class QRCodeGenerator {
    public static String generateQRCode(Event event) {
        return "QR Code for " + event.getName() + " has been generated!";
    }

}
