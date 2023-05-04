package com.example.proyector;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.Lists.pojos.Alergeno;
import com.example.Lists.pojos.Departamento;
import com.example.Lists.pojos.Producto;
import com.example.Lists.pojos.Restaurante;
import com.example.Lists.pojos.Ticket;
import com.example.adapters.AdapterCartaDep;
import com.example.adapters.AdapterCartaProducto;
import com.example.adapters.AdapterCheckAls;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class CartaCliente extends AppCompatActivity {
    FirebaseFirestore db= FirebaseFirestore.getInstance();
    CollectionReference myRef =  db.collection("Carta").document("carta").collection("Departamentos");
    CollectionReference pRef;

    ArrayList<Departamento> departamentos = new ArrayList<>();
    ArrayList<Producto> productos = new ArrayList<>();
    ArrayList<Alergeno> alergenos = new ArrayList<>();
    ArrayList<cAlergeno> cAlergenos = new ArrayList<>();

    AdapterCartaProducto adapterPro;
    AdapterCartaDep adapterDep;
    AdapterCheckAls adaptercheck;

    RecyclerView viewDep;
    ListView listView1;

    Ticket ticket1;
    TextView tNomRest;
    TextView tNumMesa;
    TextView tAlergs;

    Restaurante restaurante1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carta_cliente);

        TextView txNombreRest = (TextView) findViewById(R.id.tx_nombrerest2);
        tNumMesa = (TextView) findViewById(R.id.tx_numesa);

        ImageButton btnFiltrar = (ImageButton) findViewById(R.id.btnFiltrar);

        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            ticket1 = intent.getExtras().getSerializable("ticket", Ticket.class);
            if (ticket1 != null) {
                //restaurante1 = ticket1.getRestaurante();
                //txNombreRest = restaurante1.getNombre();
                tNumMesa.setText("Mesa: " + ticket1.getNum_mesa());
            }
        } else {
            //Error ticket sin identificar
        }

        listView1 = (ListView) findViewById(R.id.listviewCarta);

        myRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        Departamento d = document.toObject(Departamento.class);
                        departamentos.add(d);
                    }
                }
            }
        }).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                LinearLayoutManager linlayout = new LinearLayoutManager(CartaCliente.this, LinearLayoutManager.HORIZONTAL, false);
                adapterDep = new AdapterCartaDep(departamentos);
                viewDep = findViewById(R.id.horizontalRv);
                viewDep.setLayoutManager(linlayout);
                viewDep.setAdapter(adapterDep);

                if (departamentos.size() > 0) mostrarProductos(departamentos.get(0));

                adapterDep.setOnClickListener(new AdapterCartaDep.OnClickListener() {
                    @Override
                    public void onClick(int position, Departamento departamento) {
                        mostrarProductos(departamento);
                    }
                });
            }
        }).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                CollectionReference alRef = db.collection("alergenos");
                alRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Alergeno a = document.toObject(Alergeno.class);
                                alergenos.add(a);
                                cAlergenos.add(new cAlergeno(a,false));
                            }
                       }
                    }
                });
            }
        });


        // botón filtrar

        btnFiltrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // generar Dialog
                AlertDialog.Builder dialog1 = new AlertDialog.Builder(CartaCliente.this);
                LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

                View layout = inflater.inflate(R.layout.dialog_list, (ViewGroup) findViewById(R.id.layout_root));
                TextView text1 = (TextView) layout.findViewById(R.id.texthead1);
                ListView listview2 = (ListView) layout.findViewById(R.id.listviewCheck);
                text1.setText("Filtrar alérgenos e intolerancias");

                adaptercheck = new AdapterCheckAls(getApplicationContext(), alergenos);
                listview2.setAdapter(adaptercheck);

                listview2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        cAlergenos.get(position).setChecked(!cAlergenos.get(position).getChecked());
                    }
                });

                dialog1.setView(layout);
                dialog1.setPositiveButton("APLICAR", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        for (cAlergeno c: cAlergenos) {
                            /*
                            if(c.getChecked()){
                                tAlergs.setText(tAlergs.getText()+c.getAlergeno().getNombre());
                            }*/
                        }
                        dialog.dismiss();
                    }

                });
                dialog1.create();
                dialog1.show();
            }
        });
    }


    private void mostrarProductos(Departamento departamento){
        pRef = db.collection("Carta").document("carta").collection("Departamentos").document(departamento.getId()).collection("productos");
        pRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                productos.removeAll(productos);
                for(DocumentSnapshot document:task.getResult()){
                    Producto p = document.toObject(Producto.class);
                    // filtrar alergenos 
                    productos.add(p);
                }
                adapterPro = new AdapterCartaProducto(CartaCliente.this, productos);
                listView1.setAdapter(adapterPro);

                listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        Producto p = productos.get(position);

                        Log.d("onItemClick",p.getNombre());

                        // generar Dialog
                        AlertDialog.Builder imageDialog = new AlertDialog.Builder(CartaCliente.this );
                        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

                        View layout = inflater.inflate(R.layout.dialog_carta, (ViewGroup) findViewById(R.id.layout_root));
                        ImageView image = (ImageView) layout.findViewById(R.id.imgDialogProd);
                        TextView txNomProducto = (TextView) layout.findViewById(R.id.txDialog1);
                        txNomProducto.setText(p.getNombre());

                        // cargar imagen

                        final long MAX_IMAGESIZE = 1024 * 1024;
                        String name = p.getNombre().replace(" ","")+".jpg";
                        FirebaseStorage storage = FirebaseStorage.getInstance();
                        StorageReference imgRef = storage.getReference();
                        imgRef.child("productos").child(name).getBytes(MAX_IMAGESIZE)
                                .addOnCompleteListener(new OnCompleteListener<byte[]>() {
                                    @Override
                                    public void onComplete(@NonNull Task<byte[]> task) {
                                        if( task.isSuccessful() ) {
                                            byte[] bytes = task.getResult();
                                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                            image.setImageBitmap(bitmap);
                                        }
                                    }
                                })
                                .addOnCompleteListener(new OnCompleteListener<byte[]>() {
                                    @Override
                                    public void onComplete(@NonNull Task<byte[]> task) {
                                        imageDialog.setView(layout);
                                        imageDialog.setPositiveButton("PEDIR", new DialogInterface.OnClickListener(){
                                            public void onClick(DialogInterface dialog, int which) {
                                                EditText cantidad = (EditText) layout.findViewById(R.id.etCantidad);
                                                dialog.dismiss();
                                                // añadir cantidad y producto a ticket
                                            }

                                        });
                                        imageDialog.create();
                                        imageDialog.show();
                                    }
                                });
                    }
                });
            }
        });
    }

    class cAlergeno {
        Alergeno alergeno;
        Boolean checked;

        public cAlergeno(Alergeno alergeno, Boolean checked) {
            this.alergeno = alergeno;
            this.checked = checked;
        }

        public Alergeno getAlergeno() {
            return alergeno;
        }

        public void setAlergeno(Alergeno alergeno) {
            this.alergeno = alergeno;
        }

        public Boolean getChecked() {
            return checked;
        }

        public void setChecked(Boolean checked) {
            this.checked = checked;
        }
    }

}