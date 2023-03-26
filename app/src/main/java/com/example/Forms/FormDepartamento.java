package com.example.Forms;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.proyector.R;

public class FormDepartamento extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_departamento);

        Button btnGuardar = (Button) findViewById(R.id.btn_aGuardar);
        Button btnNuevoProd = (Button) findViewById(R.id.btn_dNuevoProd);

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnNuevoProd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FormDepartamento.this, FormProducto.class);
                startActivity(intent);
            }
        });
    }
}