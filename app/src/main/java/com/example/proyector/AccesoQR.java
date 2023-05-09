package com.example.proyector;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import com.example.Lists.pojos.Ticket;

public class AccesoQR extends Fragment {

    private ifAccesoQR iAccesoQR;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_acceso_qr, container, false);

        ImageView imageView1 = (ImageView) view.findViewById(R.id.imageView1);

        imageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                escanear();
            }
        });

        Button btnCarta = (Button) view.findViewById(R.id.btn_cartaTemp);

        btnCarta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                Bundle bundle = new Bundle();
                bundle.putSerializable("ticket", new Ticket(1));
                getParentFragmentManager().setFragmentResult("requestKey",bundle);
                */
                iAccesoQR.if_btnCarta();
            }

        });

        return view;
    }

    // escanear qr
    public void escanear(){
        ScanOptions options = new ScanOptions();
        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE);
        options.setPrompt("ESCANEAR QR");
        options.setCameraId(0);
        options.setOrientationLocked(false);
        options.setBeepEnabled(true);
        options.setCaptureActivity(CapActivity.class);
        options.setBarcodeImageEnabled(false);
        barcodeLauncher.launch(options);

    }

    // contrato leer qr
    private final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(new ScanContract(), result->{
        if (result.getContents() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Result");
            builder.setMessage(result.getContents());
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            }).show();
        }});

    public interface ifAccesoQR{
        public void if_btnCarta();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ifAccesoQR) {
            iAccesoQR = (ifAccesoQR) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentCommunicationListener");
        }
    }
    @Override
    public void onDetach() {
        super.onDetach();
        iAccesoQR = null;
    }

}