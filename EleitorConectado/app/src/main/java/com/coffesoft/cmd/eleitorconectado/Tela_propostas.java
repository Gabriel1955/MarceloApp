package com.coffesoft.cmd.eleitorconectado;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by GabrielPaulino on 17/08/2016.
 */

public class Tela_propostas extends Classe_metodos{

    private ListView listView;
    List<Map<String, Object>> List_dados = new ArrayList<Map<String, Object>>();
    List<Map<String, Object>> Lista_filtrada = new ArrayList<Map<String, Object>>();
    private EditText editText;
    boolean ATUALIZOU=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tela_propostas);
        listView = (ListView) findViewById(R.id.LV_ta_listapropostas);
        editText = (EditText)findViewById(R.id.filtro);
        Listar_json();
        carregardados();
    }
    public void Listar_json(){
        if(List_dados.size()==0) {
            BancodeDados bancodeDados = new BancodeDados(this);
            List_dados = bancodeDados.carregaDados("propostas", new String[]{"area", "titulo", "texto","linkfoto"});
            for(int i=0;i<List_dados.size();i++)
                Lista_filtrada.add(List_dados.get(i));
        }

        SimpleAdapter simpleAdapter = new SimpleAdapter(this, Lista_filtrada, R.layout.layout_listview_propostas,
                new String[] {"titulo","texto","linkfoto"}, new int[]
                {R.id.area,R.id.propostas_text,R.id.propostas_imagem});
        simpleAdapter.setViewBinder(new MyBinder(this));
        listView.setAdapter(simpleAdapter);
    }
    class  MyBinder  implements SimpleAdapter.ViewBinder {
        Context mycontext;
        public MyBinder(Context context){
            mycontext=context;
        }
        @Override
        public boolean setViewValue(View view, Object data, String textRepresentation) {
            if (view.getId() == R.id.propostas_imagem){
                String link= (String) data;
                ImageView imageView = (ImageView) view;
                if(link.equals("")){
                    return true;
                }
                Picasso.with(mycontext).load(link).into(imageView);
                return true;
            }
            return false;
        }
    }
    public void filtrar(View view){
        List<Map<String, Object>> List_dadosfiltro = new ArrayList<Map<String, Object>>();
        for(int i=0;i<List_dados.size();i++){
            Map<String,Object> map = new HashMap<String, Object>();
            map.put("area",(String) List_dados.get(i).get("area"));
            if(!List_dadosfiltro.contains(map)){
                List_dadosfiltro.add(map);
            }
        }
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("area","Todos");
        List_dadosfiltro.add(map);

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.listview_generic);
        ListView listView = (ListView) dialog.findViewById(R.id.listview);
        SimpleAdapter simpleAdapter = new SimpleAdapter(this, List_dadosfiltro, R.layout.layout_listview_tiposmudancas,
                new String[] {"area"}, new int[]
                {R.id.tipomudancas});
        listView.setAdapter(simpleAdapter);
        final List<Map<String, Object>> finalList_dados1 = List_dadosfiltro;
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setList((String) finalList_dados1.get(position).get("area"));
                editText.setText((String) finalList_dados1.get(position).get("area"));
                dialog.cancel();

            }
        });
        dialog.show();
    }
    public void setList(String filtro){
        Lista_filtrada.clear();
        if(filtro.equals("Todos")){
            for(int i=0;i<List_dados.size();i++)
                Lista_filtrada.add(List_dados.get(i));
        }
        else {
            for (int i = 0; i < List_dados.size(); i++) {
                if (((String) List_dados.get(i).get("area")).equals(filtro)) {
                    Lista_filtrada.add(List_dados.get(i));
                }
            }
        }
        Listar_json();
    }
    public void carregardados(){
        final Context context = this;
        class Webservice extends AsyncTask<Void,Void, Void> {
            String[] categorias = new String[]{"area","titulo","texto","linkfoto","id"};
            @Override
            protected Void doInBackground(Void... params) {
              ATUALIZOU =  atualizarBD("propostas",categorias,context);
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
