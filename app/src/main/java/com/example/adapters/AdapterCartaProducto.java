package com.example.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.Lists.pojos.Producto;
import com.example.proyector.R;

import java.util.ArrayList;

public class AdapterCartaProducto extends ArrayAdapter<Producto> {
    public AdapterCartaProducto(Context context, ArrayList<Producto> productos) {
        super(context,0,productos);
    }

    private static class ViewHolder {
        ImageView img;
        TextView nombre;
        TextView descrip;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Producto producto = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_producto, parent, false);
        }

        ImageView xImg = (ImageView) convertView.findViewById(R.id.img_prod);
        TextView xNombre = (TextView) convertView.findViewById(R.id.txNombre);
        TextView xDescrip = (TextView) convertView.findViewById(R.id.txDescrip);

        //xImg.setImageBitmap(producto.getImagen_carta());
        xNombre.setText(producto.getNombre());
        xDescrip.setText(producto.getDescripcion());


        return convertView;
    }
}

