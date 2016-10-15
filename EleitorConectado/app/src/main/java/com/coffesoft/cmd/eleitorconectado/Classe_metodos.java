
package com.coffesoft.cmd.eleitorconectado;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.support.v4.app.FragmentActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by POSITIVO on 18/03/2016.
 */
public class Classe_metodos extends FragmentActivity {


    HttpURLConnection urlConnection;
    StringBuilder result;
    String[] ID_DADOS;
    ContentValues DADOS = new ContentValues();
    boolean DADOS_POST= false;
    public List<Map<String, Object>> get_List(String json, String tipo, String[] dados, int inicio_posicao) throws JSONException {
        List<Map<String, Object>> Lista_dados = new ArrayList<Map<String, Object>>();
        JSONObject jsonResponse = new JSONObject(json);
        JSONArray jsonMainNode = jsonResponse.optJSONArray(tipo);
        JSONObject jsonChildNode;
        for (int i = 0; i < jsonMainNode.length(); i++) {
            HashMap<String, Object> mapa = new HashMap<String, Object>();
            jsonChildNode = jsonMainNode.getJSONObject(i);
            mapa.put("posicao", Integer.toString(i + 1 + inicio_posicao));
            for (int j = 0; j < dados.length; j++) {
                mapa.put(dados[j], jsonChildNode.optString(dados[j]));
            }
            Lista_dados.add(mapa);
        }
        return Lista_dados;
    }
    public boolean atualizarBD(String tabela, String[] colunas, Context context) {
        BancodeDados bancodeDados = new BancodeDados(context);
        String url = "http://www.educaisa.com/LimoeiroDeAnadia/webservice/" + tabela + ".php";
        List<Map<String, Object>> Lista = new ArrayList<Map<String, Object>>();
        String result = new String();
        try {
            result = Getwebservice(url);
            Lista = get_List(result, tabela, colunas, 0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        SQLiteDatabase db = bancodeDados.getWritableDatabase();
        String sql = "DELETE FROM " + tabela;
        if (!result.equals("")) {
            db.execSQL(sql);
            for (int i = 0; i < Lista.size(); i++) {
                ContentValues contentValues = new ContentValues();
                for (int j = 0; j < colunas.length; j++) {
                    contentValues.put(colunas[j], (String) Lista.get(i).get(colunas[j]));
                }
                db.insert(tabela, null, contentValues);
            }
            return true;
        }
        return false;
    }
    public String Getwebservice(String URL) {
        result = new StringBuilder();
        try {
            URL url = new URL(URL);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(5000);
            urlConnection.setReadTimeout(10000);
            if(DADOS_POST){
                urlConnection.setRequestMethod("POST");
                OutputStream os = urlConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter( new OutputStreamWriter(os,"UTF-8"));
                writer.write(getQuery());
                writer.flush();
                writer.close();
                os.close();
                DADOS_POST = false;
            }
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            urlConnection.disconnect();
        }
        return result.toString();
    }
    private String getQuery() throws UnsupportedEncodingException
    {
        StringBuilder result = new StringBuilder();
        for(int i=0;i<ID_DADOS.length;i++){
            result.append("&");
            result.append(URLEncoder.encode(ID_DADOS[i], "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode((String) DADOS.get(ID_DADOS[i]), "UTF-8"));
        }
        return result.toString();
    }
    public void SetPost(ContentValues contentValues, String[] dados){
        DADOS.clear();
        ID_DADOS = dados;
        DADOS = contentValues;
        DADOS_POST = true;
    }
}
