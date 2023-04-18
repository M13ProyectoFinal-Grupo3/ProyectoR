package com.example.Lists;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.widget.ListView;

import com.example.adapters.AdapterCartaProducto;
import com.example.Lists.pojos.Producto;
import com.example.proyector.R;

import java.util.ArrayList;

public class ListCarta extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_carta);
        ShowAdapterProductos();
    }


    private void ShowAdapterProductos() {
        // Creamos los datos
        ArrayList<Producto> arrProducto = new ArrayList<Producto>();
        Context c = this.getApplicationContext();

        /*arrProducto.add(new Producto("0","Bravas","Patatas, salsa brava"));
        arrProducto.add(new Producto("1","Ensaladilla rusa", "Zanahoria, patatas, guisantes, mayonesa"));
        arrProducto.add(new Producto("2","Sandwich mixto", "Pan, jamon york, queso"));
        arrProducto.add(new Producto("3","Tortilla de patatas", "Huevos, patatas"));
        arrProducto.add(new Producto("4","Ensalada cesar","Lechuga, queso, picatostes, salsa cesar"));
        arrProducto.add(new Producto("5","Tiramisú","Mascarpone, café, bizcocho, cacao en polvo"));
        arrProducto.add(new Producto("6","Tarta de manzana","Hojaldre, crema catalana, manzana"));
*/
        // Definimos el adaptador propio. En este caso no posee layout.
        AdapterCartaProducto adapter_prod = new AdapterCartaProducto(this, arrProducto);
        // Attach the adapter to a ListView
        ListView viewProd = (ListView) findViewById(R.id.list_Product);
        viewProd.setAdapter(adapter_prod);
        // Limpiar el adaptador
        //adapter_jmh.clear();
    }
}