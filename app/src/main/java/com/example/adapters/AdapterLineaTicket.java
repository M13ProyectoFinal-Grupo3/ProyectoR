package com.example.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.Lists.pojos.Lineas_Ticket;
import com.example.Lists.pojos.Ticket;
import com.example.proyector.R;

import java.util.ArrayList;

public class AdapterLineaTicket extends ArrayAdapter<Lineas_Ticket> {
    public AdapterLineaTicket(Context context, ArrayList<Lineas_Ticket> lineasticket) {
        super( context,0,lineasticket);
    }

    private AdapterLineaTicket.OnClickListener onClickListener;
    /*
    private static class ViewHolder {
        TextView nombre;
    }
    */

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Lineas_Ticket t = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_linea_ticket, parent, false);
        }
        TextView xNombre = (TextView) convertView.findViewById(R.id.tvProducto);
        xNombre.setText(t.getProducto().getNombre());

        TextView xCantidad = (TextView) convertView.findViewById(R.id.tvAmount);
        xCantidad.setText(""+t.getCantidad()+"");

        Button botonSumar = (Button)  convertView.findViewById(R.id.mas);
        Button botonRestar = (Button)  convertView.findViewById(R.id.menos);


        botonSumar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Integer.parseInt(xCantidad.getText().toString()) == 1) {
                    botonRestar.setBackgroundResource(R.drawable.ic_menos);
                    int cantidad = Integer.parseInt(xCantidad.getText().toString());
                    int sum = cantidad + 1;
                    xCantidad.setText("" + sum + "");
                } else {
                    int cantidad = Integer.parseInt(xCantidad.getText().toString());
                    int sum = cantidad + 1;
                    xCantidad.setText("" + sum + "");
                }
            }
        });

        botonRestar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Integer.parseInt(xCantidad.getText().toString()) == 1){

                }
                if (Integer.parseInt(xCantidad.getText().toString()) == 2){
                    botonRestar.setBackgroundResource(R.drawable.ic_baseline_delete);
                    //TODO - funcion delete
                    int cantidad = Integer.parseInt(xCantidad.getText().toString());
                    int resta = cantidad - 1;
                    if (resta < 0) {
                        xCantidad.setText("" + 0 + "");
                    }else{
                        xCantidad.setText("" + resta + "");
                    }
                }else if ((Integer.parseInt(xCantidad.getText().toString()) > 2)){
                    int cantidad = Integer.parseInt(xCantidad.getText().toString());
                    int resta = cantidad - 1;
                    if (resta < 0) {
                        xCantidad.setText("" + 0 + "");
                    }else{
                        xCantidad.setText("" + resta + "");
                    }
                }

            }
        });

        return convertView;
    }

    public void setOnClickListener(AdapterLineaTicket.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public interface OnClickListener {
        void onClick(int position, Ticket ticket);
    }
}
