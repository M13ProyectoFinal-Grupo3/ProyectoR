package com.example.Forms;

import static android.content.ContentValues.TAG;

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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.Lists.ListAlergenos;
import com.example.adapters.AdapterCarta;
import com.example.adapters.AdapterProducto;
import com.example.pojos.Alergeno;
import com.example.pojos.Carta;
import com.example.pojos.Producto;
import com.example.proyector.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class FormDepartamento extends AppCompatActivity {
    FirebaseFirestore db= FirebaseFirestore.getInstance();
    CollectionReference myRef =  db.collection("carta").document("Carta").collection("departamentos");

    ListView listview1;
    AdapterProducto adapter;
    ArrayList<Producto> lista = new ArrayList<>();
    Carta carta;
    int position=-1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_departamento);

        Button btnNuevo= (Button) findViewById(R.id.btn_nuevoprod);

        listview1 = (ListView) findViewById(R.id.lista_prods);
        adapter = new AdapterProducto(FormDepartamento.this,lista);
        listview1.setAdapter(adapter);

        // recupera Departamento a editar
        Intent intent = getIntent();
        if(intent.getExtras()!=null) {
            carta = getIntent().getExtras().getSerializable("departamento", Carta.class);
            TextView tx1 = findViewById(R.id.tx_departamento);
            tx1.setText(carta.getDepartamento());
            myRef.whereEqualTo("departamento", carta.getDepartamento())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()){
                                DocumentSnapshot d = task.getResult().getDocuments().get(task.getResult().size()-1);
                                myRef = db.collection("carta").document("Carta").collection("departamentos").document(d.getId()).collection("productos");
                                // mostrar productos
                                myRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if(task.isSuccessful()){
                                            for(QueryDocumentSnapshot d: task.getResult()){
                                                lista.add(d.toObject(Producto.class));
                                            }
                                            adapter.notifyDataSetChanged();
                                        } else {
                                            Log.d(TAG,"No hay productos en este departamento");
                                        }
                                    }
                                });
                            } else {
                                Log.d(TAG,"No se ha encontrado el departamento");
                            }
                        }
                    });

        } else { finish();}

        // activity alergeno
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
                                    lista.set(position,p);
                                    adapter.notifyDataSetChanged();
                                } else if(intent.getExtras().containsKey("delete")){
                                    lista.remove(lista.get(position));
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
                Intent intent = new Intent(FormDepartamento.this, FormProducto.class);
                intent.putExtra("ref", myRef.getPath());
                intent.putExtra("producto", lista.get(position));
                startActivityProductos.launch(intent);
            }
        });


        // nuevo producto
        btnNuevo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FormDepartamento.this, FormProducto.class);
                intent.putExtra("ref", myRef.getPath());
                startActivityProductos.launch(intent);
            }
        });



    }

}