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
import android.widget.Button;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
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

    AdapterCartaProducto adapterPro;
    AdapterCartaDep adapterDep;

    RecyclerView viewDep;
    ListView listView1;

    Ticket ticket1;
    Restaurante restaurante1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carta_cliente);

        TextView txNombreRest = (TextView) findViewById(R.id.tx_nombrerest2);
        TextView txNumMesa = (TextView) findViewById(R.id.tx_numesa);

        ImageButton btnFiltrar = (ImageButton) findViewById(R.id.btnFiltrar);

        Intent intent = getIntent();
        if(intent.getExtras()!=null) {
            ticket1 = intent.getExtras().getSerializable("ticket", Ticket.class);
            if(ticket1 != null){
                //restaurante1 = ticket1.getRestaurante();
                //txNombreRest = restaurante1.getNombre();
                txNumMesa.setText("Mesa: "+ticket1.getNum_mesa());
            }
        } else {
            //Error ticket sin identificar
        }

        listView1 = (ListView) findViewById(R.id.listviewCarta);

        myRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(DocumentSnapshot document:task.getResult()){
                        Departamento d = document.toObject(Departamento.class);
                        departamentos.add(d);
                    }
                }
            }
        }).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                LinearLayoutManager linlayout= new LinearLayoutManager(CartaCliente.this, LinearLayoutManager.HORIZONTAL, false);
                adapterDep = new AdapterCartaDep(departamentos);
                viewDep = findViewById(R.id.horizontalRv);
                viewDep.setLayoutManager(linlayout);
                viewDep.setAdapter(adapterDep);

                if(departamentos.size()>0) mostrarProductos(departamentos.get(0));

                adapterDep.setOnClickListener(new AdapterCartaDep.OnClickListener() {
                    @Override
                    public void onClick(int position, Departamento departamento) {
                        mostrarProductos(departamento);
                    }
                });
            }
        });


        // botón filtrar

        btnFiltrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // añadir filtros alergenos.add();
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
                        TextView txNomProducto = (TextView) layout.findViewById(R.id.txDialogProduto);
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

}