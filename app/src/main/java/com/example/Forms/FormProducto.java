package com.example.Forms;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.Lists.ListAlergenos;
import com.example.pojos.Alergeno;
import com.example.proyector.R;
import com.google.firebase.firestore.DocumentSnapshot;

public class FormProducto extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_producto);

        Button btnBorrar = (Button) findViewById(R.id.btn_aBorrar);
        Button btnGuardar = (Button) findViewById(R.id.btn_aGuardar);
        ImageButton btnAlergenos = (ImageButton) findViewById(R.id.btn_pAlergenos);

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnBorrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        ActivityResultLauncher<Intent> startActivityAlergeno= registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            // Recibe objeto
                            Intent intent =  result.getData();
                            if(intent.getExtras() != null) {
                                Alergeno a = (Alergeno) intent.getExtras().get("alergeno");
                                TextView txAler = (TextView) findViewById(R.id.tx_pDescAlergenos);
                                txAler.setText(a.getNombre());
                            }
                        }else{
                            //No recibe información.

                        }
                    }
                });

        btnAlergenos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FormProducto.this, ListAlergenos.class);
                startActivityAlergeno.launch(intent);
            }
        });



    }
}