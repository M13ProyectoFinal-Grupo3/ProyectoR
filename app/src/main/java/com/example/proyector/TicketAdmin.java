package com.example.proyector;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class TicketAdmin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_admin);

        //TODO - getIntent

        Button btnGenQR = (Button) findViewById(R.id.mostrarQr);
        ImageView imageView1= (ImageView) findViewById(R.id.imageQR);


        btnGenQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView1.setImageBitmap(generateQRCodeImage("999")); // TODO - que sea ticketPrueba.getId()
            }
        });

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