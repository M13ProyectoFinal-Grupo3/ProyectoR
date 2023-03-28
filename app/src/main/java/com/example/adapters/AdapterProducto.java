package com.example.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pojos.Producto;
import com.example.proyector.R;

import java.util.ArrayList;

public class AdapterProducto extends ArrayAdapter<Producto> {
    public AdapterProducto(Context context, ArrayList<Producto> productos) {
        super(context,0,productos);
    }

    private static class ViewHolder {
        ImageView img;
        TextView nombre;
        TextView descrip;
    }
    // El método getView se llamará tantas veces como registros tengan los datos a visualizar.
    // Si el array usado posee 10 valores el getView se llamará 10 veces
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // En a variable position tenemos la posición del array que estoy pintando.
        // El getItem es un método propio del ArrayAdapter, en este caso el tipo de adaptador usado es el de la clase "User"
        // por lo tanto el getItem nos devolverá un objeto de tipo "User" que está en la posición "position"
        // En los usos básicos de adaptadores en los spinner se usa un ArrayAdapter<string>
        // //, por lo tanto el getItem nos devolvería un String
        Producto producto = getItem(position);
        // Validamos si nos pasan por parámetro la vista a visualizar
        // en caso que esté vacía usaremos la vista (el layout) que hemos creado para visualizar los elementes
        // el inflater se encarga de pintarlo.
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_producto, parent, false);
        }
        // Creamos las variables que apuntan a los TextView definidos en el layout "item_frutas.xml"
        //ImageView xImg = (ImageView) convertView.findViewById(R.id.img_fruta);
        TextView xNombre = (TextView) convertView.findViewById(R.id.txNombre);
        TextView xDescrip = (TextView) convertView.findViewById(R.id.txANombre);
        // Informamos los valores de los TextView
        //xImg.setImageBitmap(producto.getImagen_carta());
        xNombre.setText(producto.getNombre());
        xDescrip.setText(producto.getDescripcion());
        //Podemos añadir eventos dentro de los elementos
        // En este caso he añadido un botón y creo el listener para que mustre un mensage con TOAST
        ImageButton btn_del= (ImageButton)  convertView.findViewById(R.id.btnBorrarP);
        ImageButton btn_edit = (ImageButton) convertView.findViewById(R.id.btnEditP);
        // Defino una varieble para poder saber el contexto
        //View finalConvertView_jmh = convertView;
        btn_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                int cantidad = Integer.valueOf(xDescrip.getText().toString());
                cantidad++;
                xDescrip.setText(Integer.toString(cantidad));*/
            }
        });


        // Devolvemos la vista para que se pinte (render) por la pantalla
        return convertView;
    }
}

