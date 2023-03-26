package com.example.Forms;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.pojos.Alergeno;
import com.example.cruds.FirebaseCrud;
import com.example.proyector.R;

public class FormAlergenos extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_alergenos);

        FirebaseCrud db = new FirebaseCrud();

        Button btnGuardqar = (Button) findViewById(R.id.btn_aGuardar);
        Button btnBorrar = (Button) findViewById(R.id.btn_aBorrar);

        btnGuardqar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText aNombre = (EditText) findViewById(R.id.et_aNombre);
                Alergeno a = new Alergeno(aNombre.getText().toString());
                db.write("alergenos",a);
                Toast.makeText(FormAlergenos.this, "El Alergeno se añadio correctamente", Toast.LENGTH_SHORT).show();
                setReturn(a);
                finish();
            }
        });

        btnBorrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    private void setReturn(Alergeno a) {
        Intent intent = new Intent();
        intent.putExtra("alergeno",a);
        setResult(Activity.RESULT_OK, intent);

    }
}