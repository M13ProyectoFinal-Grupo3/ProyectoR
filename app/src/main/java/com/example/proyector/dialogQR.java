package com.example.proyector;


import android.app.Activity;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class dialogQR extends Dialog {

    public Activity c;
    public Dialog d;
    public ImageView imgview1;

    public dialogQR(Activity a) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_qr);
        imgview1 = (ImageView) findViewById(R.id.imageQR);
        imgview1.setImageBitmap(generateQRCodeImage("999")); // TODO - que sea ticketPrueba.getId()


    }

    // genera codigo QR
    private Bitmap generateQRCodeImage (String text){
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        Bitmap bmp = null;
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(text, BarcodeFormat.QR_CODE, 500, 500);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            bmp = barcodeEncoder.createBitmap(bitMatrix);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return bmp;
    }
}