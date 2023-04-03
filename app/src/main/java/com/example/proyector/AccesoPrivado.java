package com.example.proyector;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.pojos.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class AccesoPrivado extends Fragment {
    static String coleccion = "users";
    FirebaseFirestore db= FirebaseFirestore.getInstance();
    CollectionReference myRef = db.collection(coleccion);
    User u = new User();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_acceso_privado, container, false);

        EditText xNomUser;
        EditText xPassword;
        TextView xTextview1;

        xNomUser = (EditText) view.findViewById(R.id.logNomuser);
        xPassword = (EditText) view.findViewById(R.id.logpassword);
        xTextview1= (TextView) view.findViewById(R.id.tview1);

        Button btn_save = (Button) view.findViewById(R.id.btnAcceder);
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myRef.whereEqualTo("nombre",xNomUser.getText().toString())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    if(task.getResult().getDocuments().size()>0) {
                                        // recupera el último documento encontrado en la búsqueda
                                        DocumentSnapshot d = task.getResult().getDocuments().get(task.getResult().size()-1);
                                        u = d.toObject(User.class);
                                        if(xPassword.getText().toString()==u.getPassword()){
                                            Bundle result = new Bundle();
                                            result.putSerializable("user",u);
                                            getParentFragmentManager().setFragmentResult("request", result);
                                        } else {

                                        }
                                    }
                            }
                        }
            });
        }});

        return view;
    }
}

/*
        Bundle result = new Bundle();
        result.putString("bundleKey", "result");
        getParentFragmentManager().setFragmentResult("requestKey", result)
 */