package com.example.Forms;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.pojos.User;
import com.example.proyector.R;
import com.google.firebase.firestore.FirebaseFirestore;

public class FormUser extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    static String coleccion = "users";
    User x;

    EditText vNomuser;
    EditText vEmail;
    EditText vPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_user);

        vNomuser = (EditText) findViewById(R.id.logNomuser);
        vEmail = (EditText) findViewById(R.id.emailuser);
        vPassword = (EditText) findViewById(R.id.logpassword);

        Button btn_save = (Button) findViewById(R.id.btnSave);
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User u = new User(vNomuser.getText().toString(),vEmail.getText().toString(),1, vPassword.getText().toString());
                db.collection(coleccion).document().set(u);
                finish();
            }
        });

    }
}