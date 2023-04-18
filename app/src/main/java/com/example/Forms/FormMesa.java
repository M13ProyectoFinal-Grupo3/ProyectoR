package com.example.Forms;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.Lists.ListMesa;
import com.example.pojos.Mesa;
import com.example.proyector.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class FormMesa extends AppCompatActivity {

   FirebaseFirestore db = FirebaseFirestore.getInstance();
    //CollectionReference myRef = db.collection("restaurante");
    CollectionReference myRef;
    static String documento = "mesa";
    Mesa a_anterior = null;
    Mesa a_nuevo = null;
    Boolean btnBorrarHabilitado=false;
    String idDocumentoRestaurante;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_mesa);

        Button btnGuardar = (Button) findViewById(R.id.btn_dGuardar);
        Button btnBorrar = (Button) findViewById(R.id.btn_aBorrar);

        EditText eNum_mesa = (EditText) findViewById(R.id.et_dNum_mesa);
        EditText eUbicacion = (EditText) findViewById(R.id.et_dUbicacion);

        Intent intent = getIntent();
        if (intent.getExtras() != null && intent.getExtras().size() > 1) {
            a_anterior = (Mesa) getIntent().getExtras().get("mesa");
            eNum_mesa.setText(a_anterior.getNum_mesa());
            eUbicacion.setText(a_anterior.getUbicacion());
            btnBorrarHabilitado = (Boolean) getIntent().getExtras().get("btnBorrarHabilitado");
            idDocumentoRestaurante = (String) getIntent().getExtras().get("idDocumentoRestaurante");
            myRef = db.collection("restaurante/" + idDocumentoRestaurante + "/mesa");
        } else if (intent.getExtras() != null && intent.getExtras().size() == 1) {
            idDocumentoRestaurante = (String) getIntent().getExtras().get("idDocumentoRestaurante");
        }

        if (btnBorrarHabilitado == false) {
            btnBorrar.setVisibility(View.INVISIBLE);
        }

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Recupera la información introducida por el usuario
                EditText xNum_mesa = (EditText) findViewById(R.id.et_dNum_mesa);
                EditText xUbicacion = (EditText) findViewById(R.id.et_dUbicacion);
                a_nuevo = new Mesa(xNum_mesa.getText().toString(), xUbicacion.getText().toString());
                // Actualizar restaurante o añadir nuevo
                if (a_anterior != null) {
                    // Actualizar
                    myRef.whereEqualTo("num_mesa", a_anterior.getNum_mesa())
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        if (task.getResult().getDocuments().size() > 0) {
                                            DocumentSnapshot d = task.getResult().getDocuments().get(task.getResult().size() - 1);
                                            myRef.document(d.getId()).update("num_mesa", a_nuevo.getNum_mesa());
                                            myRef.document(d.getId()).update("ubicacion", a_nuevo.getUbicacion()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    Toast.makeText(FormMesa.this, "La mesa se modificó correctamente", Toast.LENGTH_SHORT).show();
                                                    Intent resultIntent = new Intent();
                                                    resultIntent.putExtra("update", a_nuevo);
                                                    setResult(RESULT_OK, resultIntent);
                                                    finish();
                                                }
                                            });
                                        } else {
                                            Log.d(TAG, "Mesa no encontrada para su modificación");
                                        }
                                    } else {
                                        Log.d(TAG, "Error getting documents: ", task.getException());
                                    }
                                }
                            });

                } else {
                    // Nueva Mesa
                    DocumentReference idDocumento = db.collection("restaurante/" + idDocumentoRestaurante + "/mesa").document();
                    idDocumento.set(a_nuevo).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(FormMesa.this, "La mesa se añadió correctamente", Toast.LENGTH_SHORT).show();
                            finish();
                            Intent intent = new Intent(FormMesa.this, ListMesa.class);
                            intent.putExtra("nombreRestaurante", idDocumentoRestaurante.substring(2));
                            startActivity(intent);
                        }
                    });
                }
            }
        });

        btnBorrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myRef.whereEqualTo("num_mesa", a_anterior.getNum_mesa())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    if (task.getResult().getDocuments().size() > 0) {
                                        DocumentSnapshot d = task.getResult().getDocuments().get(task.getResult().size() - 1);
                                        myRef.document(d.getId()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Toast.makeText(FormMesa.this, "La mesa ha sido eliminada correctamente", Toast.LENGTH_SHORT).show();
                                                Intent resultIntent = new Intent();
                                                resultIntent.putExtra("delete", a_anterior);
                                                setResult(RESULT_OK, resultIntent);
                                                finish();
                                            }
                                        });
                                    } else {
                                        Log.d(TAG, "Mesa no encontrada para su modificación");
                                    }
                                } else {
                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                }
                            }
                        });
            }
        });
    }

}
