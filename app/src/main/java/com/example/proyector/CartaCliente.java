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
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import com.google.android.gms.tasks.Tasks;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.grpc.Context;

public class CartaCliente extends AppCompatActivity {
    FirebaseFirestore db= FirebaseFirestore.getInstance();
    CollectionReference rootRef;
    CollectionReference productoRef;
    CollectionReference ticketRef;

    ArrayList<eDepartamento> departamentos = new ArrayList<>();
    ArrayList<eProducto> productos;
    ArrayList<cAlergeno> cAlergenos = new ArrayList<>();

    ArrayList<String> listDeps;
    ArrayList<String> listProds;

    final long MAX_IMAGESIZE = 1024 * 1024;
    FirebaseStorage storage = FirebaseStorage.getInstance();

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
        tNumMesa = (TextView) findViewById(R.id.txNumMesacc);
        tAlergs = (TextView) findViewById(R.id.txAlergenos);

        ImageButton btnFiltrar = (ImageButton) findViewById(R.id.btnFiltrar);

        Button bntTicketCliente = (Button) findViewById(R.id.bntTicketCliente);

        // recupera ticket
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

        // recupera datos restaurante


        storage.getReference().child("departamentos").listAll().addOnCompleteListener(new OnCompleteListener<ListResult>() {
            @Override
            public void onComplete(@NonNull Task<ListResult> task) {
                if(task.isSuccessful()) {
                    listDeps = new ArrayList<>();
                    for (StorageReference item : task.getResult().getItems()) {
                        listDeps.add(item.getName());
                        Log.d("itemDep",item.getName());
                    }

                    storage.getReference().child("productos").listAll().addOnCompleteListener(new OnCompleteListener<ListResult>() {
                        @Override
                        public void onComplete(@NonNull Task<ListResult> task) {
                            if(task.isSuccessful()){
                                listProds = new ArrayList<>();
                                for (StorageReference item : task.getResult().getItems()) {
                                    listProds.add(item.getName());
                                    Log.d("itemPro",item.getName());
                                }
                                Log.d("totalProds",listProds.size()+"");
                            }
                        }
                    });
                }
            }
        }).addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {

            rootRef=  db.collection("restaurante").document(restaurante1.getId()).collection("Carta").document("carta").collection("Departamentos");

            rootRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        Log.d("OnComplete","1");
                        if (task.isSuccessful()) {
                            Log.d("Succesfull","1");
                            StorageReference imgRef;
                            // leer todos los departamentos del restaurante
                            for (DocumentSnapshot document : task.getResult()) {
                                eDepartamento d = new eDepartamento(document.toObject(Departamento.class));
                                departamentos.add(d);
                                // cargar imagen departamento
                                if(listDeps.indexOf(d.getId()+".jpg")!=-1) {
                                    storage.getReference().child("departamentos").child(d.getId() + ".jpg")
                                            .getBytes(MAX_IMAGESIZE).addOnCompleteListener(new OnCompleteListener<byte[]>() {
                                                @Override
                                                public void onComplete(@NonNull Task<byte[]> task) {
                                                    if(task.isSuccessful()){
                                                        byte[] bytes = task.getResult();
                                                        Bitmap b =BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                                        d.setImagen(b);
                                                        Log.d("setImage departamento",d.getImagen().getByteCount()+" <= "+b.getByteCount());
                                                    }
                                                }
                                            });
                                }
                                productos = new ArrayList<>();
                                productoRef = rootRef.document(d.getId()).collection("productos");
                                productoRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                                eProducto p = new eProducto(doc.toObject(Producto.class));
                                                Log.d("p",p.getNombre()+" "+d.getnombre());
                                                d.getProductos().add(p);
                                                Log.d("producto add",d.getProductos().size()+" "+p.getNombre());
                                                Log.d("Prods->indexOf",p.getId()+".jpg"+" "+listProds.indexOf(p.getId()+".jpg")+"");
                                                if(listProds.indexOf(p.getId()+".jpg")!=-1){
                                                    storage.getReference().child("productos").child(p.getId()+".jpg")
                                                        .getBytes(MAX_IMAGESIZE).addOnCompleteListener(new OnCompleteListener<byte[]>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<byte[]> task) {
                                                                if(task.isSuccessful()){
                                                                    byte[] bytes = task.getResult();
                                                                    Bitmap b =BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                                                    p.setImagen(b);
                                                                    Log.d("setImage producto",p.getImagen().getByteCount()+" <= "+b.getByteCount());
                                                                }
                                                                productos.add(p);
                                                                Log.d("productos.add",p.getNombre());
                                                            }
                                                        });
                                                }
                                            }
                                        }
                                    }
                                });
                            }
                        }
                    }
                }).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    Log.d("OnComplete","3");
                    if(task.isSuccessful()) {
                        Log.d("Succesfull","3");

                        // adaptadores
                        LinearLayoutManager linlayout = new LinearLayoutManager(CartaCliente.this, LinearLayoutManager.HORIZONTAL, false);
                        adapterDep = new AdapterCartaDep(departamentos);
                        viewDep = findViewById(R.id.horizontalRv);
                        viewDep.setLayoutManager(linlayout);
                        viewDep.setAdapter(adapterDep);

                        adapterDep.setOnClickListener(new AdapterCartaDep.OnClickListener() {
                            @Override
                            public void onClick(int position, eDepartamento departamento) {
                                mostrarProductos(position);
                            }
                        });

                        // cargar alaergenos
                        CollectionReference alRef = db.collection("alergenos");
                        alRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        cAlergeno a = new cAlergeno(document.toObject(Alergeno.class), false);
                                        cAlergenos.add(a);
                                    }
                                }
                            }
                        });
                    }
                }
            }).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    AlertDialog.Builder dialogOK = new AlertDialog.Builder(CartaCliente.this);
                    dialogOK.setMessage("BIENVENIDO A\r\n"+restaurante1.getNombre());
                    dialogOK.setPositiveButton( "ENTRAR" , new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            mostrarProductos(0);
                        }
                    });

                    dialogOK.create().show();
                }
            });

            }
        });

        bntTicketCliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CartaCliente.this, TicketCliente.class);
                intent.putExtra("ticket", ticket1);
                startActivity(intent);
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
                        mostrarProductos(0);
                        dialog.dismiss();
                    }

                });
                dialog1.create();
                dialog1.show();
            }
        });


    }

    private void mostrarProductos(Integer pos){
        Log.d("productos size",departamentos.get(pos).getProductos().size()+"");
        for(eProducto p: departamentos.get(pos).getProductos()) {
            Log.d("for dep",pos+" "+departamentos.get(pos).getnombre()+" "+p.getNombre());
            if (!p.getActivo()) { // filtra si esta desactivado
                p.setFiltered(true);
            } else {
                for (Alergeno a1 : p.getAlergenos()) {
                    for (cAlergeno a2 : cAlergenos) {
                        if (a1.getId().equals(a2.getAlergeno().getId()) && a2.getChecked()) {
                            p.setFiltered(true); // true si contiene alergeno
                            break;
                        } else {
                            p.setFiltered(false);
                        }
                    }
                }
            }
            Log.d("mostrarProductos for","add "+p.getId()+" "+p.getNombre());
        }

        adapterPro = new AdapterCartaProducto(CartaCliente.this, departamentos.get(pos).getProductos());
        listView1.setAdapter(adapterPro);

        listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // obtenemos el producto
                eProducto producto1 = departamentos.get(pos).getProductos().get(position);
                // buscamos si ya tiene mas unidades solicitadas y generamos la linea ticket correspondiente
                // con las unidades ya solicitadas
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
                if(lineaT != null) {
                    txUdsPedidas.setText("Unidades pedidas: " + lineaT.getCantidad());

                    // cargar imagen

                    if(producto1.getImagen()!=null) {
                        Bitmap b = Bitmap.createScaledBitmap(producto1.getImagen(), 500, 500, false);
                        image.setImageBitmap(b);
                    }
                    imageDialog.setView(layout);
                    imageDialog.setPositiveButton("PEDIR", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            ticketRef.document(ticket1.getId()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    Integer cantidad = Integer.parseInt(eCantidad.getText().toString());

                                    if (lineaT.getProducto() == null) {
                                        // nueva linea
                                        lineaT = new Lineas_Ticket(producto1, cantidad);
                                        ticket1.addLinea_ticket(lineaT);
                                    } else {
                                        Log.d("producto", producto1.toString());
                                        ticket1.addCantidad(producto1, cantidad);
                                    }

                                    ArrayList<Lineas_Ticket> saveLineas = new ArrayList<>();
                                    for(Lineas_Ticket l: ticket1.getLineas_ticket()){
                                        saveLineas.add(l);
                                        saveLineas.get(saveLineas.size()-1).setProducto(new Producto(l.getProducto()));
                                    }
                                    ticketRef.document(ticket1.getId()).update("lineas_ticket", saveLineas).addOnCompleteListener(new OnCompleteListener<Void>() {
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

    public class eDepartamento extends Departamento{
        Bitmap imagen;
        ArrayList<eProducto> productos;

        public eDepartamento() {
        }

        public eDepartamento(Departamento d){
            super(d.getId(),d.getnombre());
            productos = new ArrayList<>();
        }

        public eDepartamento(Departamento d, Bitmap imagen) {
            super(d.getId(),d.getnombre());
            this.imagen = imagen;
            productos = new ArrayList<>();
        }

        public ArrayList<eProducto> getProductos() {
            return productos;
        }

        public void setProductos(ArrayList<eProducto> productos) {
            this.productos = productos;
        }

        public Bitmap getImagen() {
            return imagen;
        }

        public void setImagen(Bitmap imagen) {
            this.imagen = imagen;
        }
    }

    public class eProducto extends Producto{
        Bitmap imagen;
        Boolean filtered;

        public eProducto() {
        }

        public eProducto(String nombre, Float precio, String id) {
            super(nombre,precio,id);
            filtered = false;
        }

        public eProducto(Producto p){
            super(p);
            filtered = false;
        }

        public Boolean getFiltered() {
            return filtered;
        }

        public void setFiltered(Boolean filtered) {
            this.filtered = filtered;
        }

        public Bitmap getImagen() {
            return imagen;
        }

        public void setImagen(Bitmap imagen) {
            this.imagen = imagen;
        }

    }

}
