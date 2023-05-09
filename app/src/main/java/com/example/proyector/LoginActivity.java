package com.example.proyector;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.Lists.CardViewGestionComandas;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private EditText userMail, userPassword;
    private Button btnLogin;
    private ProgressBar loginProgress;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private CollectionReference collectionRef;
    private Intent HomeActivity;

    String userUID;
    String userPerfil;
    String userRestaurante;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        db = FirebaseFirestore.getInstance();
        collectionRef = db.collection("usuarios");

        userMail = findViewById(R.id.login_mail);
        userPassword = findViewById(R.id.login_password);
        btnLogin = findViewById(R.id.loginnBtn);
        loginProgress = findViewById(R.id.login_progress);
        ImageButton backButton = findViewById(R.id.backBtn);
        mAuth = FirebaseAuth.getInstance();
        HomeActivity = new Intent(this, CardViewGestionComandas.class);


        loginProgress.setVisibility(View.INVISIBLE);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginProgress.setVisibility(View.VISIBLE);
                btnLogin.setVisibility(View.INVISIBLE);

                final String mail = userMail.getText().toString();
                final String password = userPassword.getText().toString();

                if (mail.isEmpty() || password.isEmpty()) {
                    showMessage("Por favor compruebe todos los campos");
                    btnLogin.setVisibility(View.VISIBLE);
                    loginProgress.setVisibility(View.INVISIBLE);
                } else {
                    signIn(mail, password);
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void signIn(String mail, String password) {

        mAuth.signInWithEmailAndPassword(mail, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {

                    FirebaseUser currentUser = mAuth.getCurrentUser();
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
                                    userUID = snapshot.getString("UID");
                                    userPerfil = snapshot.getString("Perfil");
                                    userRestaurante = snapshot.getString("Restaurante");
                                }
                            }
                        }
                    });

                    if (userPerfil != null && (userPerfil.equals("Cocinero") || userPerfil.equals("Camarero"))) {
                        Intent intent = new Intent(LoginActivity.this, CardViewGestionComandas.class);
                        intent.putExtra("perfil", userPerfil);
                        intent.putExtra("restaurante", userRestaurante);
                        startActivity(intent);
                    }

                    loginProgress.setVisibility(View.INVISIBLE);
                    btnLogin.setVisibility(View.VISIBLE);
                    updateUI();

                } else {
                    showMessage(task.getException().getLocalizedMessage());
                    btnLogin.setVisibility(View.VISIBLE);
                    loginProgress.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    private void updateUI() {
        startActivity(HomeActivity);
        finish();

    }

    private void showMessage(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();

        /*if(user != null) {
            //user is already connected  so we need to redirect him to home page
            updateUI();
        }*/
    }
}
