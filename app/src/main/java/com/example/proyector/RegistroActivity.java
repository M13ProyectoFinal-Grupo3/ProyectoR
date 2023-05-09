package com.example.proyector;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegistroActivity extends AppCompatActivity {

    private TextInputEditText userEmail, userPassword, userPAssword2;
    private ProgressBar loadingProgress;
    private Button regBtn;
    private FirebaseAuth mAuth;
    private AutoCompleteTextView spinnerPerfil;
    private String opcionSeleccionada;
    ArrayAdapter<String> adapter;
    FirebaseFirestore db;
    private CollectionReference collectionRef;
    String restaurante;
    FirebaseUser currentUser;
    String[] opciones;
    String nombreRestaurante;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        getSupportActionBar().hide();

        userEmail = findViewById(R.id.regMail);
        userPassword = findViewById(R.id.regPassword);
        userPAssword2 = findViewById(R.id.regPassword2);
        loadingProgress = findViewById(R.id.regProgressBar);
        regBtn = findViewById(R.id.regBtn);
        spinnerPerfil = findViewById(R.id.spinner);
        ImageButton backButton = findViewById(R.id.backBtn);

        Intent intent = getIntent();
        if (intent.getExtras() != null && intent.getStringExtra("perfilUsuario").equals("administrador")) {
            opciones = new String[]{"Administrador"};
            nombreRestaurante = intent.getStringExtra("nombreRestaurante");
        }else{
            opciones = new String[]{"Camarero", "Cocinero"};
        }

        adapter = new ArrayAdapter<String>(this, R.layout.drop_down_item, opciones);
        spinnerPerfil.setAdapter(adapter);

        loadingProgress.setVisibility(View.INVISIBLE);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        collectionRef = db.collection("usuarios");

        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                regBtn.setVisibility(View.INVISIBLE);
                loadingProgress.setVisibility(View.VISIBLE);
                final String email = userEmail.getText().toString();
                final String password = userPassword.getText().toString();
                final String password2 = userPAssword2.getText().toString();

                if (email.isEmpty() || password.isEmpty() || !password.equals(password2) || opcionSeleccionada == null) {
                    showMessage("Por favor compruebe todos los campos");
                    regBtn.setVisibility(View.VISIBLE);
                    loadingProgress.setVisibility(View.INVISIBLE);
                } else {
                    CreateUserAccount(email, password);
                }
            }
        });

        spinnerPerfil.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                opcionSeleccionada = (String) parent.getItemAtPosition(position);
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void CreateUserAccount(String email, String password) {

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            FirebaseUser currentUser = mAuth.getCurrentUser();
                            String uid = null;
                            if (currentUser != null) {
                                uid = currentUser.getUid();
                            }

                            String finalUid = uid;
                            Map<String, Object> userData = new HashMap<>();
                            userData.put("UID", finalUid);
                            userData.put("Perfil", opcionSeleccionada);
                            if(opciones.length > 1){
                            userData.put("Restaurante", restaurante);
                            }else {
                                userData.put("Restaurante", nombreRestaurante);
                            }

                            db.collection("usuarios").add(userData).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });

                            Toast.makeText(getApplicationContext(), "Registro completado", Toast.LENGTH_LONG).show();

                            Intent intent = new Intent(RegistroActivity.this, MainActivity.class);
                            startActivity(intent);

                        } else {

                            showMessage("El proceso de registro ha fallado: " + task.getException().getMessage());
                            regBtn.setVisibility(View.VISIBLE);
                            loadingProgress.setVisibility(View.INVISIBLE);

                        }
                    }
                });
    }

    private void showMessage(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onStart() {
        super.onStart();

        currentUser = mAuth.getCurrentUser();

        String uid = null;
        if (currentUser != null) {
            uid = currentUser.getUid();
        }
        String finalUid = uid;

        collectionRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> snapshotList = queryDocumentSnapshots.getDocuments();
                for (DocumentSnapshot snapshot : snapshotList) {
                    if (finalUid.equals(snapshot.getString("UID"))) {
                        restaurante = snapshot.getString("Restaurante");
                    }
                }
            }
        });

    }

}