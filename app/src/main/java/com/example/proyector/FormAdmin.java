package com.example.proyector;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.Forms.FormCarta;
import com.example.Lists.ListAlergenos;
import com.example.Lists.ListUsuarios;
import com.example.Lists.pojos.Restaurante;
import com.example.Lists.pojos.Usuarios;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class FormAdmin extends AppCompatActivity {

    Usuarios usuario;
    Restaurante restaurante;

    FirebaseFirestore db= FirebaseFirestore.getInstance();
    CollectionReference myRef = db.collection("restaurante");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_admin);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);


        Button btnCarta = (Button) findViewById(R.id.btnCarta);
        Button brnAlergs = (Button) findViewById(R.id.btnAlergenos);
        Button btnRegistro = (Button) findViewById(R.id.btnRegistro);
        TextView nomRest = (TextView) findViewById(R.id.tx_nombrerest3);

        Intent intent = getIntent();
        if(intent.getExtras()!=null) {
            if (intent.getExtras().containsKey("usuario")) {
                usuario = intent.getSerializableExtra("usuario",Usuarios.class);
                myRef.document(usuario.getRestaurante()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            restaurante = task.getResult().toObject(Restaurante.class);
                            nomRest.setText(restaurante.getNombre());
                        } else {
                            Toast.makeText(FormAdmin.this, "Restaurante desconocido", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                });
            } else {
                Toast.makeText(this, "Administrador no reconocido", Toast.LENGTH_SHORT).show();
                finish();
            }
        }


        btnCarta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), FormCarta.class);
                intent.putExtra("restaurante",restaurante);
                startActivity(intent);
            }
        });

        brnAlergs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ListAlergenos.class);
                startActivity(intent);
            }
        });

        btnRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ListUsuarios.class);
                intent.putExtra("nombreRestaurante", restaurante.getNombre());
                startActivity(intent);
            }
        });


    }
}