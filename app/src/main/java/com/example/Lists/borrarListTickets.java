package com.example.Lists;

import android.content.Intent;
import android.widget.ListView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import com.example.Lists.pojos.Ticket;
import com.example.adapters.AdapterAlergeno;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class borrarListTickets extends AppCompatActivity {
    //TODO CAMBIAR POR EMPTY ACTIVITY
    FirebaseFirestore db= FirebaseFirestore.getInstance();
    CollectionReference myRef = db.collection("tickets");

    ListView listview1;
    ArrayList<Ticket> lista = new ArrayList<>();
    AdapterAlergeno adapter;
    Integer pos=0;

    ActivityResultLauncher<Intent> activityForm;
/*
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_alergenos);

        listview1 = (ListView) findViewById(R.id.list_alergenos);
        adapter = new AdapterAlergeno(ListAlergenos.this, lista);
        listview1.setAdapter(adapter);

//*/
}
