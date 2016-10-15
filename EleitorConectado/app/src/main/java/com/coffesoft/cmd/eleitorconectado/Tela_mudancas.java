package com.coffesoft.cmd.eleitorconectado;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by GabrielPaulino on 25/08/2016.
 */

public class Tela_mudancas extends Classe_metodos {

    private ListView listView;
    List<Map<String, Object>> Lista_filtrada = new ArrayList<Map<String, Object>>();
    List<Map<String, Object>> List_dados = new ArrayList<Map<String, Object>>();
    private EditText editText;
    private LinearLayout linearLayout;
    boolean ATUALIZOU = false;
    BancodeDados bancodeDados = new BancodeDados(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tela_mudancas);

        listView = (ListView) findViewById(R.id.LV_ta_listamudancas);
        editText = (EditText)  findViewById(R.id.filtro);
        linearLayout = (LinearLayout) findViewById(R.id.Linearmudancas);

        Listar_json();
        carregardados();
    }
    public void Listar_json(){
        if(List_dados.size()==0) {

            List_dados = bancodeDados.carregaDados("mudancas", new String[]{"tipo", "titulo", "texto", "linkfoto", "id"});
            for(int i=0;i<List_dados.size();i++)
                Lista_filtrada.add(List_dados.get(i));
        }
        else {
            SimpleAdapter simpleAdapter = new SimpleAdapter(this, Lista_filtrada, R.layout.layout_listview_mudancas,
                    new String[]{ "titulo", "texto", "linkfoto"}, new int[]
                    {R.id.mudancas_tipo
                            , R.id.mudancas_texto, R.id.mudancas_imagem,});

            simpleAdapter.setViewBinder(new MyBinder(this));
            listView.setAdapter(simpleAdapter);
        }
    }
    class  MyBinder  implements SimpleAdapter.ViewBinder {
        Context mycontext;
        public MyBinder(Context context){
            mycontext=context;
        }
        @Override
        public boolean setViewValue(View view, Object data, String textRepresentation) {
            if (view.getId() == R.id.mudancas_imagem){
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
    public void carregardados(){
        final Context context = this;
        class Webservice extends AsyncTask<Void,Void, Void> {
            String[] categorias = new String[]{"tipo","titulo","texto","linkfoto","id"};
            @Override
            protected Void doInBackground(Void... params) {
              ATUALIZOU =   atualizarBD("mudancas",categorias,context);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if(!ATUALIZOU) {
                    Toast.makeText(context, "Erro ao atualizar dados, verifique sua conexão", Toast.LENGTH_LONG).show();
                }
                else {
                    List_dados = bancodeDados.carregaDados("mudancas", new String[]{"tipo", "titulo", "texto", "linkfoto", "id"});
                }
            }
        }
        Webservice webService = new Webservice();
        webService.execute();
    }
    public void filtrar(View view){
        List<Map<String, Object>> List_dadosfiltro = new ArrayList<Map<String, Object>>();
        for(int i=0;i<List_dados.size();i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("tipo", (String) List_dados.get(i).get("tipo"));
            if (!List_dadosfiltro.contains(map)) {
                List_dadosfiltro.add(map);
            }
        }
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.listview_generic);
        final ListView listView = (ListView) dialog.findViewById(R.id.listview);
        SimpleAdapter simpleAdapter = new SimpleAdapter(this, List_dadosfiltro, R.layout.layout_listview_tiposmudancas,
                new String[] {"tipo"}, new int[]
                {R.id.tipomudancas});
        listView.setAdapter(simpleAdapter);
        final List<Map<String, Object>> finalList_dados1 = List_dadosfiltro;
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                linearLayout.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);
                setList((String) finalList_dados1.get(position).get("tipo"));
                editText.setText((String) finalList_dados1.get(position).get("tipo"));
                dialog.cancel();
            }
        });
        dialog.show();
    }
    public void setList(String filtro){
        Lista_filtrada.clear();
            for (int i = 0; i < List_dados.size(); i++) {
                if (((String) List_dados.get(i).get("tipo")).equals(filtro)) {
                    Lista_filtrada.add(List_dados.get(i));
                }
            }

        Listar_json();
    }
}
