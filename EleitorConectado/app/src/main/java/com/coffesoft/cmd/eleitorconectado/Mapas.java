package com.coffesoft.cmd.eleitorconectado;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_HYBRID;

/**
 * Created by GabrielPaulino on 17/08/2016.
 */

public class Mapas extends Classe_metodos implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener{
    GoogleMap mapa;
    List<Map<String, Object>> List_dados = new ArrayList<Map<String, Object>>();
    List<Marker> markerLis = new ArrayList<Marker>();
    Map<String,Integer> mapicon = new HashMap<String,Integer>();
    boolean ATUALIZOU = false, ATUALIZANDO=true;

    @Override
    protected  void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tela_mapa);
        setmapicon();
        Listar_json();
        carregardados();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }
    @Override
    public void onMapReady(GoogleMap map) {
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(-9.765703,-36.552638),12));
        this.mapa=map;
        setMarker();
        mapa.setMapType(MAP_TYPE_HYBRID);
    }
    public void setMarker(){
        mapa.setOnMarkerClickListener(this);
        for(int i=0;i<List_dados.size();i++){
            markerLis.add(addMarker(i));
        }
    }
    @Override
    public boolean onMarkerClick(Marker marker) {
        if(ATUALIZANDO){
            Toast.makeText(this,"Atualizando obras, aguarde por favor",Toast.LENGTH_SHORT).show();
        }
        else {
            int position = markerLis.indexOf(marker);
            Intent intent = new Intent(this, Tela_MostrarObra.class);
            intent.putExtra("titulo", (String) List_dados.get(position).get("titulo"));
            intent.putExtra("imagemlink", (String) List_dados.get(position).get("imagemlink"));
            intent.putExtra("descricao", (String) List_dados.get(position).get("descricao"));
            startActivity(intent);
        }
        return true;

    }
    public Marker addMarker(int position){
        Double Longitude=Double.parseDouble((String)List_dados.get(position).get("longitude")),
                Latitude=Double.parseDouble((String)List_dados.get(position).get("latitude"));
        Marker marker =  mapa.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(mapicon.get((String)List_dados.get(position).get("icon"))))
                .anchor(0.0f,1.0f)
                .position(new LatLng(Latitude,Longitude))
                .title((String)List_dados.get(position).get("title"))
        );
        return  marker;
    }
    public void carregardados(){
        final Context context = this;
        class Webservice extends AsyncTask<Void,Void, Void> {
            String[] categorias = new String[]{"longitude", "latitude", "titulo","descricao","imagemlink","icon", "id"};
            @Override
            protected Void doInBackground(Void... params) {
              ATUALIZOU =   atualizarBD("obras",categorias,context);
                return null;
            }
            @Override
            protected void onPostExecute(Void aVoid) {
                ATUALIZANDO = false;
                Listar_json();
                setMarker();
                if(!ATUALIZOU)
                    Toast.makeText(context,"Erro ao atualizar dados, verifique sua conexão",Toast.LENGTH_LONG).show();
            }
        }
        Webservice webService = new Webservice();
        webService.execute();
    }
    public void Listar_json() {
            BancodeDados bancodeDados = new BancodeDados(this);
            List_dados = bancodeDados.carregaDados("obras", new String[]{"longitude", "latitude", "titulo","descricao","imagemlink","icon", "id"});
    }
    public void setmapicon(){
        mapicon.put("escola",R.drawable.icon_escola);
        mapicon.put("quadra",R.drawable.icon_quadra);
        mapicon.put("creche",R.drawable.icon_escola);
        mapicon.put("praça",R.drawable.icon_praca);
        mapicon.put("saude",R.drawable.icon_saude);
        mapicon.put("agua",R.drawable.icon_agua);
        mapicon.put("estrada",R.drawable.icon_asfalto);
        mapicon.put("casa",R.drawable.icon_casa);
        mapicon.put("onibus",R.drawable.icon_bus);
    }
}
