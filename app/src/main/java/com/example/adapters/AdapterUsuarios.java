package com.example.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.Lists.pojos.Alergeno;
import com.example.Lists.pojos.Usuarios;
import com.example.proyector.R;

import java.util.ArrayList;

public class AdapterUsuarios extends ArrayAdapter<Usuarios> {
    public AdapterUsuarios(Context context, ArrayList<Usuarios> usuarios) {
        super( context,0,usuarios);
    }

    private static class ViewHolder {
        TextView nombre;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Usuarios u = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_usuario, parent, false);
        }
        TextView xNombre = (TextView) convertView.findViewById(R.id.txDescrip);
        xNombre.setText(u.getPerfil());

        return convertView;
    }
}

