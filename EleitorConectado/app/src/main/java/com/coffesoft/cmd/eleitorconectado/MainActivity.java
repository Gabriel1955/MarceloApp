package com.coffesoft.cmd.eleitorconectado;

import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Classe_metodos {
    BancodeDados bancodeDados = new BancodeDados(this);
    MediaPlayer mediaplayer = new MediaPlayer();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (mediaplayer.isPlaying())
            mediaplayer.stop();
        mediaplayer = MediaPlayer.create(this,R.raw.musica);
        mediaplayer.setVolume(0.09f , 0.09f);
        mediaplayer.start();
        mediaplayer.setLooping(true);
    }
    public void agenda(View view){
        startActivity(new Intent(this, Tela_agenda.class));
    }
    public void mapa(View view){startActivity(new Intent(this, Mapas.class));}
    public void denuncia(View view){startActivity(new Intent(this,Tela_denuncia.class));}
    public void propostas(View view){startActivity(new Intent(this,Tela_propostas.class));}
    public void links(View view){startActivity(new Intent(this,Tela_paginas.class));}
    public void mudancas(View view){startActivity(new Intent(this,Tela_mudancas.class));}
    public void MarceloPerfil(View view){startActivity(new Intent(this,MarceloPerfil.class));}
}

