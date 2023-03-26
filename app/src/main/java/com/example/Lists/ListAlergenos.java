package com.example.Lists;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.example.cruds.CrudAlergenos;
import com.example.cruds.FirebaseCrud;
import com.example.pojos.Alergeno;
import com.example.proyector.R;
import com.google.firebase.firestore.CollectionReference;

import java.util.ArrayList;

public class ListAlergenos extends AppCompatActivity {
    CrudAlergenos db = new CrudAlergenos();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_alergenos);
        ArrayList<Alergeno> lista =  db.list();

        Button btnSeleccion = (Button) findViewById(R.id.btn_)
    }

    private void AdapterAlergenos(ArrayList<Alergeno> array) {
        ArrayList<String> lista = new ArrayList<>();
        for(Alergeno a: array){
                lista.add(a.getNombre());
        }

        //Definimos el adaptador
        ArrayAdapter<String> aArrayAlergenos= new ArrayAdapter <String>
                (getApplicationContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,lista);


        //Creamos variable que apunte al ListView del Layout
        ListView listview1 = (ListView) findViewById(R.id.list_alergenos);
        //Inflamos los valores del ListView usando el adaptador
        listview1.setAdapter(aArrayAlergenos);


    }
}