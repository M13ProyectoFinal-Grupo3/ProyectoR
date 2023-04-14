package com.example.Forms;

import static android.content.ContentValues.TAG;

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
import android.widget.ListView;
import android.widget.Toast;

import com.example.adapters.AdapterCarta;
import com.example.pojos.Carta;
import com.example.proyector.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class FormCarta extends AppCompatActivity {
    FirebaseFirestore db= FirebaseFirestore.getInstance();
    CollectionReference myRef =  db.collection("carta").document("Carta").collection("departamentos");

    ListView listview1;
    AdapterCarta adapter;
    ArrayList<Carta> lista = new ArrayList<>();
    Carta carta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_carta);

        Button btnNuevoDep = (Button) findViewById(R.id.btn_nuevoprod);

        listview1 = (ListView) findViewById(R.id.lista_prods);
        adapter = new AdapterCarta(FormCarta.this,lista);
        listview1.setAdapter(adapter);


        // Mostrar departamentos

        myRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(DocumentSnapshot document:task.getResult()){
                        lista.add(document.toObject(Carta.class));
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
                String anterior = lista.get(position).getDepartamento();
                input.setText(anterior);
                builder.setView(input);

                builder.setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        carta = new Carta(input.getText().toString());

                        //comprueba que el departamento no haya sido creado anteriormente
                        myRef.whereNotEqualTo("departamento",carta.getDepartamento())
                                        .get()
                                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                            @Override
                                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                myRef.whereEqualTo("departamento", anterior)
                                                        .get()
                                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                if (task.isSuccessful()) {
                                                                    if (task.getResult().getDocuments().size() > 0) {
                                                                        DocumentSnapshot d = task.getResult().getDocuments().get(task.getResult().size() - 1);
                                                                        myRef.document(d.getId()).update("departamento", carta.getDepartamento()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                Toast.makeText(FormCarta.this, "El Departamento se modificó correctamente", Toast.LENGTH_SHORT).show();
                                                                                lista.set(position, carta);
                                                                                adapter.notifyDataSetChanged();
                                                                            }
                                                                        });

                                                                    } else {
                                                                        Log.d(TAG, "Documento Departamento no econtrado para su modificación");
                                                                    }
                                                                }
                                                            }
                                                        });
                                            }
                                        });

                    }
                });

                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.setNeutralButton("Eliminar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Carta anterior = lista.get(position);
                        myRef.whereEqualTo("departamento",lista.get(position).getDepartamento())
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if(task.isSuccessful()) {
                                            DocumentSnapshot d = task.getResult().getDocuments().get(task.getResult().size()-1);
                                            myRef.document(d.getId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Toast.makeText(FormCarta.this,"El departamento se eliminó correctament",Toast.LENGTH_LONG).show();
                                                    lista.remove(anterior);
                                                    adapter.notifyDataSetChanged();
                                                }
                                            });
                                        } else {
                                            Log.d(TAG, "Documento no ha podido ser eiliminado");
                                        }
                                    }
                                });
                    }
                });

                builder.show();


                return false;
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
                        carta = new Carta(input.getText().toString());

                        myRef.whereNotEqualTo("departamento",carta.getDepartamento())
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        myRef.document().set(carta).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                lista.add(carta);
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