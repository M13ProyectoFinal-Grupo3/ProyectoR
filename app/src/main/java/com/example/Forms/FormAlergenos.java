package com.example.Forms;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.Lists.ListAlergenos;
import com.example.pojos.Alergeno;
import com.example.proyector.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class FormAlergenos extends AppCompatActivity {
    FirebaseFirestore db= FirebaseFirestore.getInstance();
    CollectionReference myRef = db.collection("alergenos");
    static String coleccion = "alergenos";
    Alergeno a_anterior = null;
    Alergeno a_nuevo = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_alergenos);

        Button btnGuardqar = (Button) findViewById(R.id.btn_dGuardar);
        Button btnBorrar = (Button) findViewById(R.id.btn_aBorrar);

        EditText enombre = (EditText) findViewById(R.id.t_cNombre);

        Intent intent = getIntent();
        if(intent.getExtras()!=null) {
            a_anterior = (Alergeno) getIntent().getExtras().get("alergeno");
            enombre.setText(a_anterior.getNombre());
        }

        btnGuardqar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // recupera la información introducida por el usuario
                EditText xNombre = (EditText) findViewById(R.id.t_cNombre);
                a_nuevo = new Alergeno(xNombre.getText().toString());
                // Actualizar Alergeno o añadir nuevo
                if(a_anterior!=null) {
                    // Actualizar
                    myRef.whereEqualTo("nombre",a_anterior.getNombre())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    if(task.getResult().getDocuments().size()>0) {
                                        DocumentSnapshot d = task.getResult().getDocuments().get(task.getResult().size()-1);
                                        myRef.document( d.getId()).update("nombre",a_nuevo.getNombre()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Toast.makeText(FormAlergenos.this, "El Alergeno se modificó correctamente", Toast.LENGTH_SHORT).show();
                                                Intent resultIntent = new Intent();
                                                resultIntent.putExtra("update", a_nuevo);
                                                Log.d("return update","ok");
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

                } else {
                    // Nuevo Alergeno
                    db.collection(coleccion).document().set(a_nuevo).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Intent resultIntent = new Intent();
                            resultIntent.putExtra("new", a_nuevo);
                            setResult(RESULT_OK, resultIntent);
                            Toast.makeText(FormAlergenos.this, "El Alergeno se añadio correctamente", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                }


            }
        });

        btnBorrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myRef.whereEqualTo("nombre",a_anterior.getNombre())
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
                                                Toast.makeText(FormAlergenos.this, "El Alergeno ha sido eliminado correctamente", Toast.LENGTH_SHORT).show();
                                                Intent resultIntent = new Intent();
                                                resultIntent.putExtra("delete", a_anterior);
                                                setResult(RESULT_OK, resultIntent);
                                                Intent intent = new Intent(FormAlergenos.this, ListAlergenos.class);
                                                startActivity(intent);
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

    }

}