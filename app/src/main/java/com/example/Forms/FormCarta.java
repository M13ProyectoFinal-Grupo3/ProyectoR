package com.example.Forms;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
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

import com.example.Lists.pojos.Producto;
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

    int pos=-1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_carta);
        getSupportActionBar().hide();

        Button btnNuevoDep = (Button) findViewById(R.id.btn_nuevoDep);
        //ImageButton btnBack = (ImageButton) findViewById(R.id.btn_backCarta);

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
        ActivityResultLauncher<Intent> startActivityDepartamento= registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            // Recibe objeto producto
                            Intent intent =  result.getData();
                            if(intent.getExtras() != null) {
                                if(intent.getExtras().containsKey("update")){
                                    Departamento d = intent.getSerializableExtra("update", Departamento.class);
                                    myRef.document(d.getId()).update("nombre", d.getnombre()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(FormCarta.this, "El Departamento se modificó correctamente", Toast.LENGTH_SHORT).show();
                                            lista.set(pos, d);
                                            adapter.notifyDataSetChanged();
                                        }
                                    });
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
                pos = position;
                Intent intent = new Intent(FormCarta.this, FormDepartamento.class);
                intent.putExtra("departamento", lista.get(position));
                startActivityDepartamento.launch(intent);
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