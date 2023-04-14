package com.example.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.pojos.Carta;
import com.example.proyector.R;

import java.util.ArrayList;

public class AdapterCarta extends ArrayAdapter<Carta> {
    public AdapterCarta(Context context, ArrayList<Carta> carta) {
        super( context,0,carta);
    }

    private static class ViewHolder {
        TextView departamento;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Carta c = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_alergeno, parent, false);
        }
        TextView xNombre = (TextView) convertView.findViewById(R.id.txANombre);
        xNombre.setText(c.getDepartamento());

        return convertView;
    }
}

