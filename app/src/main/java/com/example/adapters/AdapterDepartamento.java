package com.example.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.pojos.Alergeno;
import com.example.pojos.Departamento;
import com.example.proyector.R;

import java.util.ArrayList;

public class AdapterDepartamento extends ArrayAdapter<Departamento> {
    public AdapterDepartamento(Context context, ArrayList<Departamento> departamentos) {
        super( context,0,departamentos);
    }

    private static class ViewHolder {
        TextView nombre;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Departamento d = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_alergeno, parent, false);
        }
        TextView xNombre = (TextView) convertView.findViewById(R.id.txANombre);
        xNombre.setText(d.getNombre());

        return convertView;
    }
}

