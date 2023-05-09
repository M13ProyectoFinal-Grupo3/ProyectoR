package com.example.proyector;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.example.Forms.FormCarta;
import com.example.Forms.FormUser;
import com.example.Lists.ListAlergenos;
import com.example.Lists.CardViewGestionComandas;
import com.example.Lists.ListRestaurante;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;


public class Profesional extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profesional, container, false);

        Button btnGenQR = (Button) view.findViewById(R.id.btnGenQR);
        Button btnCarta = (Button) view.findViewById(R.id.btnCarta);
        Button btnRest = (Button) view.findViewById(R.id.btnRestaurante);
        Button brnAlergs = (Button) view.findViewById(R.id.btnAlergenos);
        Button btnGestionComandas = (Button) view.findViewById(R.id.btnGestionComandas);
        Button btnLogin = (Button) view.findViewById(R.id.btnLogin);
        Button btnRegistro = (Button) view.findViewById(R.id.btnRegistro);
        ImageView imageView1= (ImageView) view.findViewById(R.id.imageQR);

        btnRest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ListRestaurante.class);
                startActivity(intent);
            }
        });

        btnGenQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView1.setImageBitmap(generateQRCodeImage("12324567890"));
            }
        });

        btnCarta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), FormCarta.class);
                startActivity(intent);
            }
        });

        brnAlergs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ListAlergenos.class);
                startActivity(intent);
            }
        });


        btnGestionComandas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), CardViewGestionComandas.class);
                startActivity(intent);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
            }
        });

        btnRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), RegistroActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }


    // genera codigo QR
    private Bitmap generateQRCodeImage(String text) {
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        Bitmap bmp = null;
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(text, BarcodeFormat.QR_CODE,500,500);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            bmp = barcodeEncoder.createBitmap(bitMatrix);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return bmp;
    }
}