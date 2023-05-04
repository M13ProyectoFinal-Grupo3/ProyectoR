package com.example.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.Lists.pojos.Alergeno;
import com.example.proyector.R;

import java.util.ArrayList;

public class AdapterAlergeno extends ArrayAdapter<Alergeno> {
    public AdapterAlergeno(Context context, ArrayList<Alergeno> alergenos) {
        super( context,0,alergenos);
    }

    private static class ViewHolder {
        TextView nombre;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Alergeno a = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_alergeno, parent, false);
        }
        TextView xNombre = (TextView) convertView.findViewById(R.id.txDescrip);
        xNombre.setText(a.getNombre());

        return convertView;
    }
}

