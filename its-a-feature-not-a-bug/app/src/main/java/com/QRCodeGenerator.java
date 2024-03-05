package com;

public class QRCodeGenerator {
    public static String generateQRCode(Event event) {
        return "QR Code for " + event.getEventName() + " has been generated!";
    }

}
