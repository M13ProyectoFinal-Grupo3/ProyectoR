package com.example.proyector;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

public class DialogCarta extends Dialog {

    public Activity c;
    public Dialog d;
    public Button pedir;
    public ImageView imgview1;

    public DialogCarta(Activity a) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_carta);
        imgview1 = (ImageView) findViewById(R.id.imgDialogProd);
    }

}