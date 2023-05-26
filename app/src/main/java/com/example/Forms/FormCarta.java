package com.example.Forms;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.example.Lists.pojos.Restaurante;
import com.example.adapters.AdapterDepartamento;
import com.example.Lists.pojos.Departamento;
import com.example.proyector.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class FormCarta extends AppCompatActivity {
    FirebaseFirestore db= FirebaseFirestore.getInstance();
    CollectionReference myRef;

    ListView listview1;
    AdapterDepartamento adapter;
    ArrayList<Departamento> lista = new ArrayList<>();
    Restaurante restaurante1;
    Departamento departamento;

    int pos=-1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_carta);
        getSupportActionBar().hide();

        Intent intent = getIntent();
        if(intent.getExtras()!=null){
            if(intent.getExtras().containsKey("restaurante")) {
                restaurante1 = intent.getSerializableExtra("restaurante", Restaurante.class);
            }
        } else {
            Toast.makeText(this, "ERROR: Restaurante desconocido", Toast.LENGTH_SHORT).show();
            finish();
        }

        myRef =  db.collection("restaurante").document(restaurante1.getId()).collection("Carta").document("carta").collection("Departamentos");

        Button btnNuevoDep = (Button) findViewById(R.id.btnNuevoDep);

        listview1 = (ListView) findViewById(R.id.lista_prods);
        adapter = new AdapterDepartamento(FormCarta.this,lista);
        listview1.setAdapter(adapter);

        // Mostrar departamentos

        myRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(DocumentSnapshot document:task.getResult()){
                        Departamento d = document.toObject(Departamento.class);
                        lista.add(d);
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    Log.d("ERROR","No se pudo obtener la lista de departamentos");
                }
            }
        });

        // Result activity departamento
        ActivityResultLauncher<Intent> startActivityDepartamentos= registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            // Recibe objeto producto
                            Intent intent =  result.getData();
                            if(intent.getExtras() != null) {
                                if(intent.getExtras().containsKey("new")){
                                    Departamento d = intent.getSerializableExtra("new", Departamento.class);
                                    lista.add(d);
                                    adapter.notifyDataSetChanged();
                                } else if(intent.getExtras().containsKey("delete")){
                                    lista.remove(lista.get(pos));
                                    adapter.notifyDataSetChanged();
                                }
                            }
                        }else{
                            //No recibe información.

                        }
                    }
                });




        // editar productos departamento

        listview1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(FormCarta.this, FormDepartamento.class);
                intent.putExtra("restaurante",restaurante1);
                intent.putExtra("departamento", lista.get(position));
                startActivityDepartamentos.launch(intent);
            }
        });

        // nuevo departamento
        btnNuevoDep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FormCarta.this, FormDepartamento.class);
                intent.putExtra("restaurante",restaurante1);
                startActivityDepartamentos.launch(intent);
            }
        });


        ImageButton backButton = findViewById(R.id.backBtn);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

}