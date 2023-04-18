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
import com.example.Lists.ListRestaurante;
import com.example.pojos.Mesa;
import com.example.pojos.Restaurante;
import com.example.proyector.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class FormRestaurante extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference myRef = db.collection("restaurante");
    static String coleccion = "restaurante";
    static String documento = "mesa";
    Restaurante a_anterior = null;
    Restaurante a_nuevo = null;
    Mesa a_nuevo2 = null;
    Boolean btnBorrarHabilitado=true;
    Boolean btnAgregarMesaHabilitado=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_restaurante);

        Button btnGuardar = (Button) findViewById(R.id.btn_dGuardar);
        Button btnBorrar = (Button) findViewById(R.id.btn_aBorrar);
        Button btnMesa = (Button) findViewById(R.id.btn_aMesa);

        EditText enombre = (EditText) findViewById(R.id.et_dNombre);
        EditText eRazonSocial = (EditText) findViewById(R.id.et_dRazonSocial);
        EditText eNif = (EditText) findViewById(R.id.et_dNif);
        EditText eProvincia = (EditText) findViewById(R.id.et_dProvincia);
        EditText eCiudad = (EditText) findViewById(R.id.et_dCiudad);
        EditText eCP = (EditText) findViewById(R.id.et_dCP);
        EditText eTelefono = (EditText) findViewById(R.id.et_dTelefono);

        Intent intent = getIntent();
        if (intent.getExtras() != null && intent.getExtras().size() > 2) {
            a_anterior = (Restaurante) getIntent().getExtras().get("restaurante");
            enombre.setText(a_anterior.getNombre());
            eRazonSocial.setText(a_anterior.getRazonSocial());
            eNif.setText(a_anterior.getNif());
            eProvincia.setText(a_anterior.getProvincia());
            eCiudad.setText(a_anterior.getCiudad());
            eCP.setText(a_anterior.getCodPostal());
            eTelefono.setText(a_anterior.getTelefono());
            btnBorrarHabilitado = (Boolean) getIntent().getExtras().get("btnBorrarHabilitado");
            btnAgregarMesaHabilitado = (Boolean) getIntent().getExtras().get("btnAgregarMesaHabilitado");
        } else if (intent.getExtras() != null && intent.getExtras().size() < 3) {
            btnBorrarHabilitado = (Boolean) getIntent().getExtras().get("btnBorrarHabilitado");
            btnAgregarMesaHabilitado = (Boolean) getIntent().getExtras().get("btnAgregarMesaHabilitado");
        }

        if (btnBorrarHabilitado == false) {
            btnBorrar.setVisibility(View.INVISIBLE);
        }
        if (btnAgregarMesaHabilitado == false) {
            btnMesa.setVisibility(View.INVISIBLE);
        }

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Recupera la información introducida por el usuario
                EditText xNombre = (EditText) findViewById(R.id.et_dNombre);
                EditText xRazonSocial = (EditText) findViewById(R.id.et_dRazonSocial);
                EditText xNif = (EditText) findViewById(R.id.et_dNif);
                EditText xProvincia = (EditText) findViewById(R.id.et_dProvincia);
                EditText xCiudad = (EditText) findViewById(R.id.et_dCiudad);
                EditText xCP = (EditText) findViewById(R.id.et_dCP);
                EditText xTelefono = (EditText) findViewById(R.id.et_dTelefono);
                a_nuevo = new Restaurante(xNombre.getText().toString(), xRazonSocial.getText().toString(), xNif.getText().toString(), xProvincia.getText().toString(), xCiudad.getText().toString(), xCP.getText().toString(), xTelefono.getText().toString());
                // Actualizar restaurante o añadir nuevo
                if (a_anterior != null) {
                    // Actualizar
                    myRef.whereEqualTo("nombre", a_anterior.getNombre())
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        if (task.getResult().getDocuments().size() > 0) {
                                            DocumentSnapshot d = task.getResult().getDocuments().get(task.getResult().size() - 1);
                                            myRef.document(d.getId()).update("nombre", a_nuevo.getNombre());
                                            myRef.document(d.getId()).update("razonSocial", a_nuevo.getRazonSocial());
                                            myRef.document(d.getId()).update("nif", a_nuevo.getNif());
                                            myRef.document(d.getId()).update("provincia", a_nuevo.getProvincia());
                                            myRef.document(d.getId()).update("ciudad", a_nuevo.getCiudad());
                                            myRef.document(d.getId()).update("codPostal", a_nuevo.getCodPostal());
                                            myRef.document(d.getId()).update("telefono", a_nuevo.getTelefono()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    Toast.makeText(FormRestaurante.this, "El Restaurante se modificó correctamente", Toast.LENGTH_SHORT).show();
                                                    Intent resultIntent = new Intent();
                                                    resultIntent.putExtra("update", a_nuevo);
                                                    Log.d("return update", "ok");
                                                    setResult(RESULT_OK, resultIntent);
                                                    finish();
                                                }
                                            });
                                        } else {
                                            Log.d(TAG, "Restaurante no encontrado para su modificación");
                                        }
                                    } else {
                                        Log.d(TAG, "Error getting documents: ", task.getException());
                                    }
                                }
                            });

                } else {
                    // Nuevo Restaurante
                    DocumentReference idDocumento = db.collection(coleccion).document("ID"+a_nuevo.getNombre());
                    idDocumento.set(a_nuevo).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Intent resultIntent = new Intent();
                            resultIntent.putExtra("new", a_nuevo);
                            setResult(RESULT_OK, resultIntent);
                            Toast.makeText(FormRestaurante.this, "El Restaurante se añadió correctamente", Toast.LENGTH_SHORT).show();
                            finish();
                            Intent intent = new Intent(FormRestaurante.this, ListRestaurante.class);
                            startActivity(intent);
                        }
                    });
                }
            }
        });

        btnBorrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myRef.whereEqualTo("nombre", a_anterior.getNombre())
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
                                                Toast.makeText(FormRestaurante.this, "El Restaurante ha sido eliminado correctamente", Toast.LENGTH_SHORT).show();
                                                Intent resultIntent = new Intent();
                                                resultIntent.putExtra("delete", a_anterior);
                                                setResult(RESULT_OK, resultIntent);
                                                Intent intent = new Intent(FormRestaurante.this, ListRestaurante.class);
                                                startActivity(intent);
                                            }
                                        });
                                    } else {
                                        Log.d(TAG, "Restaurante no encontrado para su modificación");
                                    }
                                } else {
                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                }
                            }
                        });
            }
        });

        btnMesa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FormRestaurante.this, ListMesa.class);
                intent.putExtra("nombreRestaurante", a_anterior.getNombre());
                startActivity(intent);
            }
        });
    }
}
