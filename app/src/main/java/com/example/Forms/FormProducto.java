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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.Lists.ListAlergenos;
import com.example.pojos.Alergeno;
import com.example.pojos.Producto;
import com.example.proyector.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class FormProducto extends AppCompatActivity {
    FirebaseFirestore db= FirebaseFirestore.getInstance();
    CollectionReference myRef = db.collection("productos");
    static String coleccion = "productos";
    Producto p_anterior = null;
    Producto p_nuevo = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_producto);

        Button btnBorrar = (Button) findViewById(R.id.btn_aBorrar);
        Button btnGuardar = (Button) findViewById(R.id.btn_dGuardar);
        ImageButton btnAlergenos = (ImageButton) findViewById(R.id.btn_pAlergenos);

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // recupera la información introducida por el usuario
                EditText xNombre = (EditText) findViewById(R.id.et_dNombre);
                EditText xDescrip = (EditText) findViewById(R.id.et_pDescripcion);
                EditText xPrecio = (EditText) findViewById(R.id.et_pPrecio);

                p_nuevo = new Producto(true, xNombre.getText().toString(),xDescrip.getText().toString(),Float.parseFloat( xPrecio.getText().toString()));
                // Actualizar Alergeno o añadir nuevo
                if(p_anterior!=null) {
                    // Actualizar
                    myRef.whereEqualTo("nombre",p_anterior.getNombre())
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        if(task.getResult().getDocuments().size()>0) {
                                            DocumentSnapshot d = task.getResult().getDocuments().get(task.getResult().size()-1);
                                            myRef.document( d.getId()).update("nombre",p_nuevo.getNombre()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    Toast.makeText(FormProducto.this, "El producto se modificó correctamente", Toast.LENGTH_SHORT).show();
                                                    Intent resultIntent = new Intent();
                                                    resultIntent.putExtra("update", p_nuevo);
                                                    setResult(RESULT_OK, resultIntent);
                                                    finish();
                                                }
                                            });

                                        } else {
                                            Log.d(TAG,"Documento Producto no econtrado para su modificación");
                                        }
                                    } else {
                                        Log.d(TAG, "Error getting documents: ", task.getException());
                                    }
                                }
                            });

                } else {
                    // Nuevo Producto
                    db.collection(coleccion).document().set(p_nuevo).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Intent resultIntent = new Intent();
                            resultIntent.putExtra("new", p_nuevo);
                            setResult(RESULT_OK, resultIntent);
                            Toast.makeText(FormProducto.this, "El Producto se añadio correctamente", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                }


            }
        });

        btnBorrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myRef.whereEqualTo("nombre",p_anterior.getNombre())
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
                                                Toast.makeText(FormProducto.this, "El Producto ha sido eliminado correctamente", Toast.LENGTH_SHORT).show();
                                                Intent resultIntent = new Intent();
                                                resultIntent.putExtra("delete", p_anterior);
                                                setResult(RESULT_OK, resultIntent);
                                                finish();
                                            }
                                        });
                                    } else {
                                        Log.d(TAG,"Documento Alergeno no econtrado para su modificación");
                                    }
                                } else {
                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                }
                            }
                        });
            }
        });
        ActivityResultLauncher<Intent> startActivityAlergeno= registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            // Recibe objeto alergeno
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
                //startActivityAlergeno.launch(intent);
                startActivity(intent);
            }
        });



    }
}