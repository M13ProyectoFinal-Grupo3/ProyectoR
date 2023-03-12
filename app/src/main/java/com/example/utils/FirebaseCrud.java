package com.example.utils;

import static android.content.ContentValues.TAG;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.pojos.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class FirebaseCrud {
    FirebaseFirestore instance;

    public FirebaseCrud(){
        instance = FirebaseFirestore.getInstance();
    }

    public void write(String c, Object o){
        instance.collection(c).document().set(o);
    }

    public void delete(String tabla, String clave, String valor){
        instance.collection(tabla).document().delete();
    }

    public ArrayList<User> query(String coleccion, String clave, String valor) {
                ArrayList<User> result = new ArrayList<>();
                CollectionReference myRef = instance.collection(coleccion);

                Log.d("--"+clave,valor);
                myRef.whereEqualTo(clave,valor)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                Log.d("Complete","ok");
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        result.add(document.toObject(User.class));
                                        Log.d("--query",document.getId()+" "+document.getData());
                                    }
                                } else {
                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                }
                            }
                        });

                return result;
        }
}
