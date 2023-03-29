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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.Lists.ListAlergenos;
import com.example.adapters.AdapterAlergeno;
import com.example.adapters.AdapterProducto;
import com.example.pojos.Alergeno;
import com.example.pojos.Departamento;
import com.example.pojos.Producto;
import com.example.proyector.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class FormDepartamento extends AppCompatActivity {
    FirebaseFirestore db= FirebaseFirestore.getInstance();
    CollectionReference myRef = db.collection("departamentos");
    static String coleccion = "departamentos";
    Departamento d_anterior = null;
    Departamento d_nuevo = null;

    ListView listview1;
    AdapterProducto adapter;
    ArrayList<Producto> lista = new ArrayList<>();
    Integer pos=0;

    ActivityResultLauncher<Intent> activityForm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_departamento);

        listview1 = (ListView) findViewById(R.id.list_prods);
        adapter = new AdapterProducto( FormDepartamento.this,lista);
        listview1.setAdapter(adapter);

        listview1.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                pos=position;
                Intent intent = new Intent(FormDepartamento.this, FormProducto.class);
                intent.putExtra("producto",lista.get(position));
                activityForm.launch(intent);
                return false;
            }
        });

        myRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        lista.add(document.toObject(Producto.class));
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });

        Button btnNuevoProd = (Button) findViewById(R.id.btn_dNuevoProd);
        Button btnGuardar = (Button) findViewById(R.id.btn_dGuardar);
        Button btnBorrar= (Button) findViewById(R.id.btn_dBorrar);

        btnNuevoProd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FormDepartamento.this, FormProducto.class);
                startActivity(intent);
            }
        });

        btnBorrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myRef.whereEqualTo("nombre",d_anterior.getNombre())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    if(task.getResult().getDocuments().size()>0) {
                                        DocumentSnapshot d = task.getResult().getDocuments().get(task.getResult().size()-1);
                                        myRef.document( d.getId()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Toast.makeText(FormDepartamento.this, "El Departamento ha sido eliminado correctamente", Toast.LENGTH_SHORT).show();
                                                Intent resultIntent = new Intent();
                                                resultIntent.putExtra("delete", d_anterior);
                                                setResult(RESULT_OK, resultIntent);
                                                finish();
                                            }
                                        });
                                    } else {
                                        Log.d(TAG,"Documento Departamento no econtrado para su modificación");
                                    }
                                } else {
                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                }
                            }
                        });
            }
        });

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // recupera la información introducida por el usuario
                EditText xNombre = (EditText) findViewById(R.id.et_dNombre);

                d_nuevo = new Departamento(xNombre.getText().toString(),lista.toArray(new Producto[lista.size()]));
                // Actualizar Alergeno o añadir nuevo
                if(d_anterior!=null) {
                    // Actualizar
                    myRef.whereEqualTo("nombre",d_anterior.getNombre())
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        if(task.getResult().getDocuments().size()>0) {
                                            DocumentSnapshot d = task.getResult().getDocuments().get(task.getResult().size()-1);
                                            myRef.document( d.getId()).update("nombre",d_nuevo.getNombre(), "productos", d_nuevo.getProductos()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    Toast.makeText(FormDepartamento.this, "El Departamento se modificó correctamente", Toast.LENGTH_SHORT).show();
                                                    Intent resultIntent = new Intent();
                                                    resultIntent.putExtra("update", d_nuevo);
                                                    setResult(RESULT_OK, resultIntent);
                                                    finish();
                                                }
                                            });

                                        } else {
                                            Log.d(TAG,"Documento Departamento no econtrado para su modificación");
                                        }
                                    } else {
                                        Log.d(TAG, "Error getting documents: ", task.getException());
                                    }
                                }
                            });

                } else {
                    // Nuevo Departamento
                    db.collection(coleccion).document().set(d_nuevo).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Intent resultIntent = new Intent();
                            resultIntent.putExtra("new", d_nuevo);
                            setResult(RESULT_OK, resultIntent);
                            Toast.makeText(FormDepartamento.this, "El Departamento se añadio correctamente", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                }
            }
        });



        activityForm= registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent intent = result.getData();
                            if(intent.getSerializableExtra("update")!=null){
                                Producto p_dev = (Producto) intent.getSerializableExtra("update");
                                lista.set(pos,p_dev);
                                adapter.notifyDataSetChanged();
                            } else if(intent.getSerializableExtra("new")!=null){
                                Producto p_dev = (Producto) intent.getSerializableExtra("new");
                                lista.add(p_dev);
                                adapter.notifyDataSetChanged();
                            } else if(intent.getSerializableExtra("delete")!=null){
                                Producto p_dev = (Producto) intent.getSerializableExtra("delete");
                                lista.remove(pos);
                                adapter.notifyDataSetChanged();
                            }

                        }
                    }
                });
    }
}