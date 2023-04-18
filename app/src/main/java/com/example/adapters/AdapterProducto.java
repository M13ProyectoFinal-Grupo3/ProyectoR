package com.example.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.Lists.pojos.Producto;
import com.example.proyector.R;

import java.util.ArrayList;

public class AdapterProducto extends ArrayAdapter<Producto> {
    public AdapterProducto(Context context, ArrayList<Producto> productos) {
        super( context,0,productos);
    }

    private static class ViewHolder {
        TextView nombre;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Producto p = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_alergeno, parent, false);
        }
        TextView xNombre = (TextView) convertView.findViewById(R.id.txANombre);
        xNombre.setText(p.getNombre());

        return convertView;
    }
}

