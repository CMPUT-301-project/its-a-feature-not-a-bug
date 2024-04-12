// This class implements the functionality for the creation of QR codes.
// Issues: not integrated into app yet.

package com.example.its_a_feature_not_a_bug;

import android.graphics.Bitmap;
import android.net.Uri;
import android.content.Context;
import androidx.core.content.FileProvider;
import android.content.Intent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.util.Log;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

/**
 * This class is a utility for generating QR codes.
 */
public class QRCodeGenerator {

    /**
     * Generates a QR code bitmap for given content.
     *
     * @param content The content to be encoded in the QR code.
     * @param size    The width and height of the QR code.
     * @return The generated QR code bitmap.
     */
    public static Bitmap generateQR(String content, int size) {
        Bitmap bitmap = null;
        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            bitmap = barcodeEncoder.encodeBitmap(content, BarcodeFormat.QR_CODE, size, size);
        } catch (WriterException e) {
            Log.e("QRCodeGenerator", e.getMessage());
        }
        return bitmap;
    }

    /**
     * Generates a promotional QR code for an event.
     * This can include a URL or deep link to the event within the app.
     *
     * @param event The event to generate a promotional QR code for.
     * @param size  The size of the QR code.
     * @return The generated QR code bitmap.
     */
    public static Bitmap generatePromotionalQRCode(Event event, int size) {
        // Example deep link or URL that directs users to the event details in the app
        String content = "featurenotbug://promotional/" + event.getTitle(); // Adjust based on URL scheme
        return generateQR(content, size);
    }

    /**
     * Generates a unique QR code for attendee check-ins at an event.
     *
     * @param event The event to generate a check-in QR code for.
     * @param size  The size of the QR code.
     * @return The generated QR code bitmap.
     */
    public static Bitmap generateCheckInQRCode(Event event, int size) {
        // Unique identifier for the event to facilitate check-ins
        String content = "featurenotbug://checkin/" + event.getTitle(); // Adjust based on URL scheme
        return generateQR(content, size);
    }
    /**
     * Saves the generated QR code to a file and shares it via other applications.
     *
     * @param context The application context.
     * @param bitmap The QR code bitmap to share.
     * @param fileName The name of the file to save the bitmap to.
     */
    public static void shareQRCode(Context context, Bitmap bitmap, String fileName) {
        try {
            // Create a file in the external cache directory
            File cachePath = new File(context.getExternalCacheDir(), "images");
            cachePath.mkdirs();
            File file = new File(cachePath, fileName + ".png");

            // Save the bitmap to the file
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();

            // Get URI for the file using FileProvider
            Uri fileUri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", file);

            // Create and fire the intent to share the file
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
            shareIntent.setType("image/png");
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(Intent.createChooser(shareIntent, "Share QR Code"));


        } catch (IOException e) {
            Log.e("QRCodeGenerator", "Error sharing QR Code", e);
        }
    }

}