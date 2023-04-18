package com.example.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.Lists.pojos.Restaurante;
import com.example.proyector.R;

import java.util.ArrayList;

public class AdapterRestaurante extends ArrayAdapter<Restaurante> {
    public AdapterRestaurante(Context context, ArrayList<Restaurante> restaurantes) {
        super(context, 0, restaurantes);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Restaurante a = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_restaurante, parent, false);
        }
        TextView xNombre = (TextView) convertView.findViewById(R.id.txANombre);
        xNombre.setText(a.getNombre());

        return convertView;
    }
}
