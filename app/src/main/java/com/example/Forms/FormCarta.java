package com.example.Forms;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.example.adapters.AdapterDepartamento;
import com.example.Lists.pojos.Departamento;
import com.example.proyector.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class FormCarta extends AppCompatActivity {
    FirebaseFirestore db= FirebaseFirestore.getInstance();
    CollectionReference myRef =  db.collection("Carta").document("carta").collection("Departamentos");

    ListView listview1;
    AdapterDepartamento adapter;
    ArrayList<Departamento> lista = new ArrayList<>();
    Departamento departamento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_carta);

        Button btnNuevoDep = (Button) findViewById(R.id.btn_nuevoDep);
        ImageButton btnBack = (ImageButton) findViewById(R.id.btn_backCarta);

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


        // editar productos departamento

        listview1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(FormCarta.this, FormDepartamento.class);
                intent.putExtra("departamento", lista.get(position));
                startActivity(intent);
            }
        });

        // editar departamento
        listview1.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(FormCarta.this);
                builder.setTitle("Editar departamento");

                final EditText input = new EditText(FormCarta.this);

                input.setInputType(InputType.TYPE_CLASS_TEXT );
                input.setText(lista.get(position).getnombre());
                builder.setView(input);

                builder.setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Departamento d = new Departamento(lista.get(position).getId(),input.getText().toString());
                        myRef.document(d.getId()).update("nombre", d.getnombre()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(FormCarta.this, "El Departamento se modificó correctamente", Toast.LENGTH_SHORT).show();
                                lista.set(position, d);
                                adapter.notifyDataSetChanged();
                            }
                        });
                    }});


                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.setNeutralButton("Eliminar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Departamento d = lista.get(position);
                        myRef.document(d.getId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(FormCarta.this, "El departamento se eliminó correctament", Toast.LENGTH_LONG).show();
                                lista.remove(d);
                                adapter.notifyDataSetChanged();
                            }
                        });
                    }
                });

                builder.show();


                return false;
            }
        });

        // volver a activity anterior
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // nuevo departamento
        btnNuevoDep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(FormCarta.this);
                builder.setTitle("Nuevo departamento");

                final EditText input = new EditText(FormCarta.this);

                input.setInputType(InputType.TYPE_CLASS_TEXT );
                builder.setView(input);

                builder.setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Departamento d = new Departamento(input.getText().toString());

                        myRef.whereNotEqualTo("departamento",d.getnombre())
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        myRef.add(d).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentReference> task) {
                                                        d.setId(task.getResult().getId());
                                                        myRef.document(d.getId()).update("id",d.getId());
                                                        lista.add(d);
                                                        adapter.notifyDataSetChanged();
                                                    }
                                                });
                                    }});
                        }
                    });

                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });

    }

}