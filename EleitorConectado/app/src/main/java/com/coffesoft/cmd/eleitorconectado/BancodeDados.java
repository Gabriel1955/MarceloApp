package com.coffesoft.cmd.eleitorconectado;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by GabrielPaulino on 17/08/2016.
 */

public class BancodeDados extends SQLiteOpenHelper {

        private static final String BANCO_DADOS = "eleitorconectado";
        private static int VERSAO = 1;
        public BancodeDados(Context context) {
            super(context, BANCO_DADOS, null, VERSAO);
        }
        @Override
        public void onCreate(SQLiteDatabase db){
            db.execSQL("CREATE TABLE IF NOT EXISTS agenda (mes TEXT, dia TEXT, hora TEXT, titulo TEXT, descricao TEXT,local TEXT, id INTEGER);");
            db.execSQL("CREATE TABLE IF NOT EXISTS propostas (area TEXT,titulo TEXT, texto TEXT,linkfoto TEXT, id INTEGER );");
            db.execSQL("CREATE TABLE IF NOT EXISTS mudancas (tipo TEXT,titulo TEXT,texto TEXT,linkfoto TEXT, id INTEGER );");
            db.execSQL("CREATE TABLE IF NOT EXISTS obras (longitude TEXT, latitude TEXT, titulo TEXT,descricao TEXT,imagemlink TEXT,icon TEXT, id INTEGER );");
            db.execSQL("CREATE TABLE IF NOT EXISTS paginas (nome TEXT, link TEXT, iconlink TEXT, id INTEGER );");
            db.execSQL("CREATE TABLE IF NOT EXISTS textomarcelo (texto TEXT );");
        }
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion,int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS meu_dados");
            onCreate(db);
        }

        public void excluir_tabela(SQLiteDatabase db, String table) {
            db.execSQL("DELETE FROM " + table);
        }
        public List<Map<String,Object>> carregaDados(String tabela, String[] campos){
            Cursor cursor;
            List<Map<String,Object>> listmap= new ArrayList<>();
            SQLiteDatabase db =this.getReadableDatabase();
            cursor = db.query(tabela,campos, null, null, null, null, null, null);
            if(cursor.moveToFirst()) {
                do{
                    Map<String,Object> map= new HashMap<>();
                    for (int i = 0; i < campos.length; i++) {
                        map.put(campos[i], cursor.getString(cursor.getColumnIndex(campos[i])));
                    }
                    listmap.add(map);
                }while(cursor.moveToNext());
            }
            db.close();
            return listmap;
        }



}
