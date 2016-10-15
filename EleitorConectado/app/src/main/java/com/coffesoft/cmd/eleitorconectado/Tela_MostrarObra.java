package com.coffesoft.cmd.eleitorconectado;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by GabrielPaulino on 19/08/2016.
 */

public class Tela_MostrarObra extends FragmentActivity {
    private TextView textViewtitulo, textViewdescricao;
    private ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tela_mostrarobra);
        textViewtitulo = (TextView) findViewById(R.id.textotitulo);
        textViewdescricao = (TextView) findViewById(R.id.textodescricao);
        imageView = (ImageView) findViewById(R.id.imagemobra);
        setTela();
    }
    public void setTela(){
        Bundle bundle= getIntent().getExtras();
        if(bundle.containsKey("titulo")){
            textViewtitulo.setText(bundle.getString("titulo"));
        }
        if(bundle.containsKey("descricao")){
            textViewdescricao.setText(bundle.getString("descricao"));
        }
        if(bundle.containsKey("imagemlink")){
            String link = bundle.getString("imagemlink");
            if(!link.equals("")) {
                Picasso.with(this).load(link).into(imageView);
            }
        }
    }
}
