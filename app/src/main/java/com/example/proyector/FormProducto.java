package com.example.proyector;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class FormProducto extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_producto);

        Button btnBorrar = (Button) findViewById(R.id.btn_pBorrar);
        Button btnGuardar = (Button) findViewById(R.id.btn_pGuardar);
        ImageButton btnAlergenos = (ImageButton) findViewById(R.id.btn_pAlergenos);
        ImageButton btnDepartamento = (ImageButton) findViewById(R.id.btn_pDepartamento);

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnBorrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnAlergenos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnDepartamento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}