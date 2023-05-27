package com.example.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.Lists.pojos.Lineas_Ticket;
import com.example.Lists.pojos.Ticket;
import com.example.proyector.R;

import java.util.ArrayList;

public class AdapterLineaTicketCliente extends ArrayAdapter<Lineas_Ticket> {
    public AdapterLineaTicketCliente(Context context, ArrayList<Lineas_Ticket> lineasticket) {
        super(context, 0, lineasticket);
    }

    private AdapterLineaTicketCliente.OnClickListener onClickListener;
    /*
    private static class ViewHolder {
        TextView nombre;
    }
    */

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Lineas_Ticket lineaTicket = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_linea_ticket_cliente, parent, false);
        }
        TextView xNombre = (TextView) convertView.findViewById(R.id.tvProducto);
        xNombre.setText(lineaTicket.getProducto().getNombre());

        Log.d("adaptador", lineaTicket.toString());

        TextView xCantidad = (TextView) convertView.findViewById(R.id.tvAmount);
        xCantidad.setText("" + lineaTicket.getCantidad() + "");

        TextView xPrecio = (TextView) convertView.findViewById(R.id.tvPrecioUnitario);
        xPrecio.setText("" + lineaTicket.getProducto().getPrecio() + "");

        TextView xTotal = (TextView) convertView.findViewById(R.id.tvPrecioTotal);
        xTotal.setText("" + (lineaTicket.getProducto().getPrecio() * lineaTicket.getCantidad())+ "");

        return convertView;
    }

    public void setOnClickListener(AdapterLineaTicketCliente.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public interface OnClickListener {
        void onClick(int position, Ticket ticket);
    }
}
