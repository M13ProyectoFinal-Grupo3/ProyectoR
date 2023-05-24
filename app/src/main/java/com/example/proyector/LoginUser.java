package com.example.proyector;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class LoginUser extends AppCompatActivity {
    FirebaseFirestore fire;
    User u;

    EditText vNomuser;
    EditText vPassword;
    TextView tv1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_user);
        getSupportActionBar().hide();

        vNomuser = (EditText) findViewById(R.id.logNomuser);
        vPassword = (EditText) findViewById(R.id.logpassword);
        tv1 = (TextView) findViewById(R.id.tview1);

        u = new User();

        Button btn_save = (Button) findViewById(R.id.btnAcceder);
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fire= FirebaseFirestore.getInstance();
                CollectionReference myRef = fire.collection("users");

                myRef.whereEqualTo("nomuser",vNomuser.getText().toString())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                Log.d("Complete","ok");
                                if (task.isSuccessful()) {
                                    User user;
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        user = document.toObject(User.class);
                                        if(user.nomuser.equals(vNomuser.getText().toString())){
                                            Toast.makeText(LoginUser.this, "Bienvenido "+user.nomuser, Toast.LENGTH_SHORT).show();
                                            finish();
                                        } else {
                                            Log.d("--check","error");
                                        }
                                        Log.d("--query",document.getId()+" "+document.getData());
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