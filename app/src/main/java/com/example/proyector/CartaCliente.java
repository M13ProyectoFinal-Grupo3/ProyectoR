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
import android.text.TextUtils;
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
import android.widget.Toast;

import com.example.Lists.pojos.Alergeno;
import com.example.Lists.pojos.Departamento;
import com.example.Lists.pojos.Lineas_Ticket;
import com.example.Lists.pojos.Producto;
import com.example.Lists.pojos.Restaurante;
import com.example.Lists.pojos.Ticket;
import com.example.Lists.pojos.cAlergeno;
import com.example.adapters.AdapterCartaDep;
import com.example.adapters.AdapterCartaProducto;
import com.example.adapters.AdapterCheckAls;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;

public class CartaCliente extends AppCompatActivity {
    FirebaseFirestore db= FirebaseFirestore.getInstance();
    CollectionReference rootRef;
    CollectionReference productoRef;
    CollectionReference ticketRef;

    ArrayList<Departamento> departamentos = new ArrayList<>();
    ArrayList<Producto> productos = new ArrayList<>();
    ArrayList<cAlergeno> cAlergenos = new ArrayList<>();

    AdapterCartaProducto adapterPro;
    AdapterCartaDep adapterDep;
    AdapterCheckAls adaptercheck;

    RecyclerView viewDep;
    ListView listView1;

    Ticket ticket1;
    Lineas_Ticket lineaT;
    TextView tNomRest;
    TextView tNumMesa;
    TextView tAlergs;

    Restaurante restaurante1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carta_cliente);
        getSupportActionBar().hide();

        tNomRest = (TextView) findViewById(R.id.tx_nombrerest2);
        tNumMesa = (TextView) findViewById(R.id.tx_numesa);
        tAlergs = (TextView) findViewById(R.id.txAlergenos);

        ImageButton btnFiltrar = (ImageButton) findViewById(R.id.btnFiltrar);

        ticketRef = db.collection("tickets");

        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            ticket1 = intent.getExtras().getSerializable("ticket", Ticket.class);
            if (ticket1 != null) {
                restaurante1 = ticket1.getRestaurante();
                tNomRest.setText( restaurante1.getNombre());
                tNumMesa.setText("Mesa: " + ticket1.getNum_mesa());
            }
        } else {
            Toast.makeText(getApplicationContext(),"Ticket no identificado", Toast.LENGTH_LONG).show();
            finish();
        }

        listView1 = (ListView) findViewById(R.id.listviewCarta);

        rootRef=  db.collection("restaurante").document(restaurante1.getId()).collection("Carta").document("carta").collection("Departamentos");

        rootRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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
                        Log.d("click","mostrarproductos");
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
                                cAlergeno a = new cAlergeno( document.toObject(Alergeno.class),false);
                                cAlergenos.add(a);
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

                adaptercheck = new AdapterCheckAls(getApplicationContext(), cAlergenos);
                listview2.setAdapter(adaptercheck);

                dialog1.setView(layout);
                dialog1.setPositiveButton("APLICAR", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mostrarAls();
                        mostrarProductos(departamentos.get(0));
                        dialog.dismiss();
                    }

                });
                dialog1.create();
                dialog1.show();
            }
        });
    }


    private void mostrarProductos(Departamento departamento){
        productoRef = rootRef.document(departamento.getId()).collection("productos");
        productoRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                productos.removeAll(productos);
                for(DocumentSnapshot document:task.getResult()){
                    Log.d("size",task.getResult().size()+"");
                    Producto p = document.toObject(Producto.class);
                    // producto activo
                    Boolean activo = p.getActivo();
                    Log.d("activo",p.getActivo()+"");
                    // filtrar alergenos
                    if(activo) {
                        for (Alergeno a1 : p.getAlergenos()) {
                            for (cAlergeno a2 : cAlergenos) {
                                if (a1.getId().equals(a2.getAlergeno().getId()) && a2.getChecked()) {
                                    activo = false;
                                    Log.d("a2",""+activo);
                                    break;
                                }
                            }
                        }
                    }

                    if(activo) productos.add(p);
                }

                adapterPro = new AdapterCartaProducto(CartaCliente.this, productos);
                listView1.setAdapter(adapterPro);

                listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        // obtenemos el producto
                        Producto producto1 = productos.get(position);
                        // buscamos si ya tiene mas unidades solicitadas y generamos la linea ticket correspondiente
                        // con las unidades ya solicitadas
                        Log.d("producto",producto1.toString());
                        lineaT = ticket1.buscarLinea(producto1);

                        // generar Dialog
                        AlertDialog.Builder imageDialog = new AlertDialog.Builder(CartaCliente.this );
                        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

                        View layout = inflater.inflate(R.layout.dialog_carta, (ViewGroup) findViewById(R.id.layout_root));
                        ImageView image = (ImageView) layout.findViewById(R.id.imgDialogProd);
                        TextView txNomProducto = (TextView) layout.findViewById(R.id.txDialog1);
                        TextView txUdsPedidas = (TextView) layout.findViewById(R.id.TxUdsPedidas);
                        EditText eCantidad = (EditText) layout.findViewById(R.id.etCantidad);

                        txNomProducto.setText(producto1.getNombre());
                        if(lineaT != null){txUdsPedidas.setText("Unidades pedidas: "+lineaT.getCantidad());}

                        // cargar imagen

                        final long MAX_IMAGESIZE = 1024 * 1024;

                        FirebaseStorage storage = FirebaseStorage.getInstance();
                        StorageReference imgRef = storage.getReference();
                        imgRef.child("productos").child(getProductoImgName(producto1)).getBytes(MAX_IMAGESIZE)
                                .addOnCompleteListener(new OnCompleteListener<byte[]>() {
                                    @Override
                                    public void onComplete(@NonNull Task<byte[]> task) {
                                        if( task.isSuccessful() ) {
                                            byte[] bytes = task.getResult();
                                            Bitmap b = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                            b =Bitmap.createScaledBitmap(b, 50, 50, true);
                                            image.setImageBitmap(b);
                                        } else {
                                            image.setImageResource(R.drawable.platocomida);
                                        }
                                    }
                                })
                                .addOnCompleteListener(new OnCompleteListener<byte[]>() {
                                    @Override
                                    public void onComplete(@NonNull Task<byte[]> task) {
                                        imageDialog.setView(layout);
                                        imageDialog.setPositiveButton("PEDIR", new DialogInterface.OnClickListener(){
                                            public void onClick(DialogInterface dialog, int which) {

                                                ticketRef.document(ticket1.getId()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                        Integer cantidad = Integer.parseInt(eCantidad.getText().toString());

                                                        if(lineaT.getProducto() == null) {
                                                            // nueva linea
                                                            lineaT = new Lineas_Ticket(producto1, cantidad);
                                                            ticket1.addLinea_ticket(lineaT);
                                                        }else {
                                                            ticket1.addCantidad(producto1,cantidad);
                                                        }
                                                        HashMap<String, Object> data = new HashMap<String, Object>() {
                                                        };
                                                        data.put("lineas_ticket",ticket1.getLineas_ticket());
                                                        ticketRef.document(ticket1.getId()).update(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                Toast.makeText(CartaCliente.this, "Petición en curso", Toast.LENGTH_LONG).show();
                                                            }
                                                        });

                                                        dialog.dismiss();
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(CartaCliente.this, "Ticket finalizado", Toast.LENGTH_SHORT).show();
                                                        finish();
                                                    }
                                                });
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

    //mostrar Alergenos

    void mostrarAls(){
        String s = "";
        ArrayList<String> result = adaptercheck.getCheckedS();
        if(result.size()>0) s = TextUtils.join(",", result);
        tAlergs.setText(s);
    }

    private String getProductoImgName(Producto p){
        return p.getId()+".jpg";
    }

    private Bitmap reducirImagen(byte[] imageAsBytes){
        Bitmap b = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
        return Bitmap.createScaledBitmap(b, 60, 60, true);
    }

}