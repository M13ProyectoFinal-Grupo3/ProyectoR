package com.example.Forms;

import static android.content.ContentValues.TAG;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
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
import android.widget.TextView;
import android.widget.Toast;

import com.example.adapters.AdapterProducto;
import com.example.Lists.pojos.Departamento;
import com.example.Lists.pojos.Producto;
import com.example.proyector.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class FormDepartamento extends AppCompatActivity {
    FirebaseFirestore db= FirebaseFirestore.getInstance();
    CollectionReference rootRef;
    CollectionReference myRef;

    ListView listview1;
    AdapterProducto adapter;
    ArrayList<Producto> lista = new ArrayList<>();
    Departamento departamento;
    int pos=-1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_departamento);


        Button btnNuevo= (Button) findViewById(R.id.btn_nuevoDep);
        ImageButton btnBorrar = (ImageButton) findViewById(R.id.btn_borrarDep);

        listview1 = (ListView) findViewById(R.id.lista_prods);
        adapter = new AdapterProducto(FormDepartamento.this,lista);
        listview1.setAdapter(adapter);

        // recupera Departamento y muestra sus productos
        Intent intent = getIntent();
        if(intent.getExtras()!=null) {

            rootRef = db.collection("Carta").document("carta").collection("Departamentos");
            departamento = getIntent().getExtras().getSerializable("departamento", Departamento.class);
            myRef =rootRef.document(departamento.getId()).collection("productos");

            TextView tx1 = findViewById(R.id.tx_nombrerest);
            tx1.setText(departamento.getnombre());

            // mostrar productos
            myRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()){
                        for(QueryDocumentSnapshot d: task.getResult()){
                            Producto p = d.toObject(Producto.class);
                            lista.add(d.toObject(Producto.class));
                            Log.d("lista prod",p.toString());
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Log.d(TAG,"No hay productos en este departamento");
                    }
                }
            });

        } else { finish();}

        // Result activity productos
        ActivityResultLauncher<Intent> startActivityProductos= registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            // Recibe objeto producto
                            Intent intent =  result.getData();
                            if(intent.getExtras() != null) {
                                if(intent.getExtras().containsKey("new")) {
                                    Producto p = intent.getSerializableExtra("new", Producto.class);
                                    lista.add(p);
                                    adapter.notifyDataSetChanged();
                                } else if(intent.getExtras().containsKey("update")){
                                    Producto p = intent.getSerializableExtra("update", Producto.class);
                                    Log.d("dev prod",p.toString());
                                    lista.set(pos,p);
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



        // editar producto

        listview1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                pos = position;
                Intent intent = new Intent(FormDepartamento.this, FormProducto.class);
                intent.putExtra("departamento", departamento.getnombre());
                intent.putExtra("ref", myRef.getPath());
                intent.putExtra("producto", lista.get(position));
                Log.d("position prod",position+" "+lista.get(position).getNombre());
                startActivityProductos.launch(intent);
            }
        });


        // nuevo producto
        btnNuevo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FormDepartamento.this, FormProducto.class);
                intent.putExtra("departamento", departamento.getnombre());
                intent.putExtra("ref", myRef.getPath());
                startActivityProductos.launch(intent);
            }
        });

        // borrar departamento
        btnBorrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DocumentReference depRef = rootRef.document(departamento.getId());
                CollectionReference proRef = depRef.collection("productos");
                proRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot d: task.getResult()){
                                proRef.document(d.getId()).delete();
                            }
                        }
                    }
                }).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        Log.d("departamento", ""+departamento.getId());
                        depRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(FormDepartamento.this, "El Departamento ha sido eliminado correctamente", Toast.LENGTH_SHORT).show();
                                Intent resultIntent = new Intent();
                                resultIntent.putExtra("delete", departamento);
                                setResult(RESULT_OK, resultIntent);
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(FormDepartamento.this, "El Departamento no ha podido ser eliminado correctamente", Toast.LENGTH_SHORT).show();
                                 Log.w(TAG, "Error deleting document", e);
                            }
                    });

                    }
                });
            }
        });

    }

}