package com.coffesoft.cmd.eleitorconectado;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by GabrielPaulino on 03/09/2016.
 */

public class MarceloPerfil extends Classe_metodos {
    BancodeDados bancodeDados = new BancodeDados(this);
    boolean ATUALIZOU = false;
    private TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.marceloperfil);
        textView = (TextView) findViewById(R.id.textomarcelo);
        GetTexto();
        carregardados();
    }
    public void GetTexto(){
        List<Map<String, Object>> List_dados = new ArrayList<Map<String, Object>>();
        List_dados= bancodeDados.carregaDados("textomarcelo",new String[]{"texto"});
        if(List_dados.size()>0){
            textView.setText((String)List_dados.get(0).get("texto"));
        }
    }
    public void carregardados(){
        final Context context = this;
        class Webservice extends AsyncTask<Void,Void, Void> {
            String[] categorias = new String[]{"texto"};
            @Override
            protected Void doInBackground(Void... params) {
                ATUALIZOU =  atualizarBD("textomarcelo",categorias,context);
                return null;
            }
            @Override
            protected void onPostExecute(Void aVoid) {
                if(ATUALIZOU) {
                   GetTexto();
                }
                else {
                    Toast.makeText(context, "Erro ao atualizar dados, verifique sua conexão", Toast.LENGTH_LONG).show();
                }
            }
        }
        Webservice webService = new Webservice();
        webService.execute();
    }
}
