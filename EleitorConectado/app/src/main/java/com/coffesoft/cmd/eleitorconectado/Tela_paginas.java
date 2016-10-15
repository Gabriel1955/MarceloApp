package com.coffesoft.cmd.eleitorconectado;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by GabrielPaulino on 20/08/2016.
 */

public class Tela_paginas extends Classe_metodos {

    private ListView listView;
    boolean ATUALIZOU = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tela_paginas);
        listView = (ListView) findViewById(R.id.LV_ta_listapaginas);
        Listar_json();
        carregardados();

    }
    public void Listar_json(){
        BancodeDados bancodeDados = new BancodeDados(this);
        List<Map<String, Object>> List_dados = new ArrayList<Map<String, Object>>();
        List_dados= bancodeDados.carregaDados("paginas",new String[]{"nome","link","iconlink"});
        SimpleAdapter simpleAdapter = new SimpleAdapter
                (this, List_dados, R.layout.layout_listview_paginas,
                new String[] {"nome","iconlink"},
                  new int[]{R.id.pagina_nome,R.id.imagempagina});

        simpleAdapter.setViewBinder(new MyBinder(this));
        listView.setAdapter(simpleAdapter);

       final List<Map<String, Object>> finalList_dados1 = List_dados;
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               abrir_navegar(position,finalList_dados1);
            }
        });
    }
    class  MyBinder  implements SimpleAdapter.ViewBinder {
        Context mycontext;
        public MyBinder(Context context){
            mycontext=context;
        }
        @Override
        public boolean setViewValue(View view, Object data, String textRepresentation) {
            if (view.getId() == R.id.imagempagina){
                String link= (String) data;
                ImageView imageView = (ImageView) view;
                Picasso.with(mycontext).load(link).into(imageView);
                return true;
            }
            return false;
        }
    }
    public void abrir_navegar(int position,List<Map<String, Object>> Lista){
        Intent it = new Intent(this,Tela_browser.class);
        it.putExtra("url", (String) Lista.get(position).get("link"));
        startActivity(it);
    }
    public void carregardados(){
        final Context context = this;
        class Webservice extends AsyncTask<Void,Void, Void>{
            String[] categorias = new String[]{"nome","link","iconlink","id"};
            @Override
            protected Void doInBackground(Void... params) {
             ATUALIZOU =  atualizarBD("paginas",categorias,context);
                return null;
            }
            @Override
            protected void onPostExecute(Void aVoid) {
                Listar_json();
                if(!ATUALIZOU)
                    Toast.makeText(context,"Erro ao atualizar dados, verifique sua conexão",Toast.LENGTH_LONG).show();
            }
        }
        Webservice webService = new Webservice();
        webService.execute();
    }
}
