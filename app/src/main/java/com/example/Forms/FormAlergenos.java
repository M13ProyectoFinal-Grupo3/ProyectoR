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
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.Lists.pojos.Alergeno;
import com.example.proyector.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class FormAlergenos extends AppCompatActivity {
    static String coleccion = "alergenos";
    FirebaseFirestore db= FirebaseFirestore.getInstance();
    CollectionReference myRef = db.collection(coleccion);

    Alergeno a_anterior = null;
    Alergeno a_nuevo = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_alergenos);
        getSupportActionBar().hide();

        Button btnGuardqar = (Button) findViewById(R.id.btnGuardarAl);
        ImageButton btnBorrar = (ImageButton) findViewById(R.id.btn_borrarAl);

        EditText enombre = (EditText) findViewById(R.id.t_pNombre);

        //botón atrás
        ImageButton backButton = findViewById(R.id.backBtn);

        Intent intent = getIntent();
        if(intent.getExtras()!=null) {
            if (intent.getExtras().containsKey("alergeno")) {
                a_anterior = getIntent().getExtras().getSerializable("alergeno", Alergeno.class);
                enombre.setText(a_anterior.getNombre());
            }
        } else {
            a_anterior = null;
        }

        btnGuardqar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // recupera la información introducida por el usuario
                EditText xNombre = (EditText) findViewById(R.id.t_pNombre);
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
                   myRef.add(a_nuevo).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                       @Override
                       public void onComplete(@NonNull Task<DocumentReference> task) {
                           a_nuevo.setId(task.getResult().getId());
                           myRef.document(a_nuevo.getId()).update("id",a_nuevo.getId());
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

        //funcionalidad botón atrás
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }



}