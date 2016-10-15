package com.coffesoft.cmd.eleitorconectado;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.VideoView;


import com.google.firebase.iid.FirebaseInstanceId;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by GabrielPaulino on 29/08/2016.
 */

public class Tela_denuncia extends Classe_metodos {
    private TextView textView_data, textView_hora;
    private EditText editText_povoado,editText_estado,editText_cidade,editText_pontoreferencia,editText_ocorrido;
    private ImageView imageView_foto,imageView_cancelar;
    private VideoView videoView;
    private boolean videoSet=false, fotoSet=false;
    private String[] NomesMeses = new String[]{"jan","fev","mar","abr","maio","jun","jul","ago","set","out","nov","dez"};
    private int FOTO=1,VIDEO=2,CANCELAR=0;
    private Bitmap FotoBitmap;
    private Uri VideoUri,ImagemUri;
    private String selectedPath;
    private ContentValues Dados;
    private String[] Id_Dados;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tela_denuncia);
        textView_data = (TextView) findViewById(R.id.texto_data);
        textView_hora = (TextView) findViewById(R.id.texto_hora);
        editText_povoado = (EditText) findViewById(R.id.povoado);
        editText_estado = (EditText) findViewById(R.id.estado);
        editText_cidade = (EditText) findViewById(R.id.cidade);
        editText_pontoreferencia = (EditText) findViewById(R.id.pontoreferencia);
        editText_ocorrido = (EditText) findViewById(R.id.ocorrido);
        imageView_foto = (ImageView) findViewById(R.id.imagem);
        imageView_cancelar = (ImageView) findViewById(R.id.cancelarmidia);
        videoView = (VideoView) findViewById(R.id.video);

        Calendar calendar = Calendar.getInstance();
        String DataAtual = DateFormat.getDateInstance().format(new Date());
        SimpleDateFormat dateFormat_hora = new SimpleDateFormat("HH:mm");
        Date date = calendar.getTime();
        String HoraAtual = dateFormat_hora.format(date);

        textView_data.setText(DataAtual);
        textView_hora.setText(HoraAtual);
        selectedPath = Uri.parse("android.resource://com.example.cmd.eleitorconectado/drawable/icon_marcelo").toString();
    }
    static final int REQUEST_IMAGE_OPEN = 1;

    public void selectImage(View view) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, REQUEST_IMAGE_OPEN);
    }
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public void selectVideo(View view){
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent.createChooser(intent, "Complete action using"),3);
    }
    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_OPEN && resultCode == RESULT_OK) {
            ImagemUri = data.getData();
            try {
                FotoBitmap= MediaStore.Images.Media.getBitmap(this.getContentResolver(), ImagemUri);
                fotoSet=true;
                setMedia(FOTO);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (requestCode == 3 && resultCode == RESULT_OK) {
            VideoUri = data.getData();
            setMedia(VIDEO);
        }
    }
    public void setMedia(int opcao) {
        imageView_cancelar.setVisibility(View.VISIBLE);
        if (opcao == FOTO) {
            videoSet = false;
            fotoSet = true;
            videoView.setVisibility(View.GONE);
            imageView_foto.setVisibility(View.VISIBLE);
            imageView_foto.setImageBitmap(FotoBitmap);
            selectedPath = getPath(ImagemUri);
        }
        else if (opcao == VIDEO) {
            videoSet = true;
            fotoSet = false;
            imageView_foto.setVisibility(View.GONE);
            videoView.setVisibility(View.VISIBLE);
            videoView.setVideoURI(VideoUri);
            videoView.setMediaController(new MediaController(this));
            selectedPath = getPath(VideoUri);
        }
    else if (opcao==CANCELAR){
        videoSet=false;
        fotoSet=false;
        imageView_cancelar.setVisibility(View.GONE);
        videoView.setVisibility(View.GONE);
        imageView_foto.setVisibility(View.GONE);

    }
}
    public void calendario(View view){
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.calendario);
        CalendarView cal= (CalendarView) dialog.findViewById(R.id.calendarView1);
        cal.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month,
                                            int dayOfMonth) {
                textView_data.setText(dayOfMonth+" de "+NomesMeses[month]+" de "+year);
                dialog.cancel();
            }
        });
        dialog.show();
    }
    public void relogio(View view){
        TimePickerDialog tpd = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        textView_hora.setText(hourOfDay + ":" + minute);
                    }
                }, 0, 12, false);
        tpd.show();
    }
    public void lista(View view){
        List<Map<String, Object>> List_dados = new ArrayList<Map<String, Object>>();
        List_dados=getListPovoados();

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.listview_generic);
        ListView listView = (ListView) dialog.findViewById(R.id.listview);
        SimpleAdapter simpleAdapter = new SimpleAdapter(this, List_dados, R.layout.layout_listview_povoados,
                new String[] {"nome"}, new int[]
                {R.id.povoados});
        listView.setAdapter(simpleAdapter);
        final List<Map<String, Object>> finalList_dados1 = List_dados;
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                editText_povoado.setText((String) finalList_dados1.get(position).get("nome"));
                dialog.cancel();
            }
        });
        dialog.show();
    }
    public void CancelarMedia(View view){
        setMedia(CANCELAR);
    }
    public List<Map<String, Object>> getListPovoados(){
        List<Map<String, Object>> List_povoados = new ArrayList<Map<String, Object>>();
        String[] povoados =
                new String[]{
                        "Area Branca","Areia Vermelha","Baixa da Areia","Baixa Seca","Baixinha",
                        "Boca da Mata","Brejo","Cacimbas","Cadoz","Cajueiro","Camadanta","Campestre","Canto",
                        "Chã do Arame","Chã do Miranda","Craíbas","Estiva","Gravatá","Gulandim","Jacaré","Jequiá do Sá",
                        "Lagoa do Mato","Limoeiro Centro","Mamoeiro","Miracema","Mucambo","Nicácia","Oití","Olho D`Água da Pedra",
                        "Olho D`Água Grande","Olho D`Água de Baixo","Olho D`Água do Luiz Carlos","Papa-Farinha",
                        "Pau-Amarelo","Pau-Ferro","Pé-Leve Novo","Pé-Leve Velho","Periperi","Poção","Poço Comprido",
                        "Poço da Julha","Poço da Pedra","Tamanduá","Terra Nova","Timbó de Baixo","Timbó de Cima",
                        "Tipí","Urtiga","Vazinha"

                };
        for (int i=0;i<povoados.length;i++){
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("nome",povoados[i]);
            List_povoados.add(map);
        }
        return List_povoados;
    }
    public void finishtela(View view){
        finish();
    }
    public String getPath(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();
        String path = null;
        if(fotoSet) {
            cursor = getContentResolver().query(
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
            cursor.moveToFirst();
            path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        }
        else if(videoSet){
            cursor = getContentResolver().query(
                    android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
            cursor.moveToFirst();
            path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
        }
        cursor.close();

        return path;
    }
    private void uploadVideo() {

        class UploadVideo extends AsyncTask<Void, Void, String> {
            ProgressDialog uploading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                getDados();
                uploading = ProgressDialog.show(Tela_denuncia.this, "Enviando denúncia", "Aguarde por favor...", false, false);
            }
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                uploading.dismiss();
                FinalizarEnvio(s);
            }
            @Override
            protected String doInBackground(Void... params) {
                if(videoSet||fotoSet) {
                    Upload u = new Upload();
                    u.SetDados(Id_Dados, Dados);
                    String msg = u.uploadVideo(selectedPath);
                    return msg;
                }
                else{
                    SetPost(Dados,Id_Dados);
                    String url = "http://www.educaisa.com/LimoeiroDeAnadia/webservice/setdenuncia.php";
                   return Getwebservice(url);
                }
            }
        }
        UploadVideo uv = new UploadVideo();
        uv.execute();
    }

    public void Enviar(View view){
        uploadVideo();
    }
    public void getDados(){
        Id_Dados = new String[]{"estado","cidade","povoado","pontodereferencia","data","hora","ocorrido","token"};
        ContentValues contentValues = new ContentValues();
        contentValues.put("estado",editText_estado.getText().toString());
        contentValues.put("cidade",editText_cidade.getText().toString());
        contentValues.put("povoado",editText_povoado.getText().toString());
        contentValues.put("pontodereferencia",editText_pontoreferencia.getText().toString());
        contentValues.put("data",textView_data.getText().toString());
        contentValues.put("hora",textView_hora.getText().toString());
        contentValues.put("ocorrido",editText_ocorrido.getText().toString());
        contentValues.put("token",FirebaseInstanceId.getInstance().getToken());
        Dados = contentValues;
    }
    public void FinalizarEnvio(String saida){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("Denúncia");
        builder.setMessage(saida);
        builder.setPositiveButton("OK", new
                DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
        builder.show();
    }
}


