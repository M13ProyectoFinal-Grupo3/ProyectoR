package com.example.cruds;

import static android.content.ContentValues.TAG;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.pojos.Alergeno;
import com.example.pojos.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.model.Document;

import java.util.ArrayList;
import java.util.Locale;

public class CrudAlergenos {
    FirebaseFirestore db;
    DocumentSnapshot doc;
    static String coleccion = "alergenos";

    public CrudAlergenos(){
        db = FirebaseFirestore.getInstance();
    }

    public void write(Alergeno a){
        db.collection(coleccion).document().set(a);
    }

    public void delete(DocumentSnapshot d){
        db.collection(coleccion).document(d.getReference().getPath()).delete();
    }

    public DocumentSnapshot find(String clave, String valor){
        CollectionReference myRef = db.collection(coleccion);
        myRef.whereEqualTo(clave,valor)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        Log.d("Complete","ok");
                        if (task.isSuccessful()) {
                            doc = task.getResult().getDocuments().get(0);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
        return doc;
    }

    public ArrayList<Alergeno> query(String clave, String valor) {
                ArrayList<Alergeno> result = new ArrayList<>();
                CollectionReference myRef = db.collection(coleccion);

                Log.d("--"+clave,valor);
                myRef.whereEqualTo(clave,valor)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                Log.d("Complete","ok");
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        result.add(document.toObject(Alergeno.class));
                                        Log.d("--query",document.getId()+" "+document.getData());
                                    }
                                } else {
                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                }
                            }
                        });

                return result;
        }

    public ArrayList<Alergeno> list(){
        ArrayList<Alergeno> result = new ArrayList<>();
        CollectionReference myRef = db.collection(coleccion);
        
        ListenerRegistration listener = db.collection("alergenos")
                .addSnapshotListener(new EventListener<QuerySnapshot>(){
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e){
                        if(e != null){
                            return;
                        }
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots){
                            result.add(document.toObject(Alergeno.class));
                        }
                    }
                });

        return result;
    }
}
