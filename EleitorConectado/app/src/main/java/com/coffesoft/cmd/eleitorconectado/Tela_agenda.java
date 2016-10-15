package com.coffesoft.cmd.eleitorconectado;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by GabrielPaulino on 17/08/2016.
 */

public class Tela_agenda extends Classe_metodos {
    private ListView listView;
    BancodeDados bancodeDados = new BancodeDados(this);
    boolean ATUALIZOU =false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tela_agenda);
        listView = (ListView) findViewById(R.id.LV_ta_lista);

        Listar_json();
        carregardados();
    }
    public void Listar_json(){
        List<Map<String, Object>> List_dados = new ArrayList<Map<String, Object>>();
        List_dados= bancodeDados.carregaDados("agenda",new String[]{"titulo","dia","mes","local","hora","descricao"});
        SimpleAdapter simpleAdapter = new SimpleAdapter(this, List_dados, R.layout.layout_listview_agenda,
                new String[] {"titulo","dia","mes","local","hora","descricao"}, new int[]
                {R.id.TV_lla_titulo,R.id.TV_lla_dia,R.id.TV_lla_mes,R.id.TV_lla_local,R.id.TV_lla_hora,R.id.TV_lla_descricao});
        listView.setAdapter(simpleAdapter);
        final List<Map<String, Object>> finalList_dados = List_dados;
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
    }
    public void carregardados(){
        final Context context = this;
        class Webservice extends AsyncTask<Void,Void, Void> {
            String[] categorias = new String[]{"titulo","dia","mes","local","hora","descricao","id"};
            @Override
            protected Void doInBackground(Void... params) {
              ATUALIZOU =  atualizarBD("agenda",categorias,context);
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
