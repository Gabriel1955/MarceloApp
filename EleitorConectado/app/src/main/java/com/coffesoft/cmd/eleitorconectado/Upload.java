package com.coffesoft.cmd.eleitorconectado;

import android.content.ContentValues;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by RUA-UFAL on 30/08/2016.
 */

public class Upload {

    public static final String UPLOAD_URL= "http://www.educaisa.com/LimoeiroDeAnadia/webservice/setdenuncia.php";
    String[] ID_DADOS ;
    ContentValues DADOS;
    private int serverResponseCode;

    public String uploadVideo(String file) {

        String fileName = file;
        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1024 * 1024 * 1024;
        File sourceFile = new File(file);
        if (!sourceFile.isFile()) {
            Log.e("Huzza1", "Source File Does not exist");
            return null;
        }
        try {
            FileInputStream fileInputStream = new FileInputStream(sourceFile);
            URL url = new URL(UPLOAD_URL);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");

            conn.setRequestProperty("ENCTYPE", "multipart/form-data");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            conn.setRequestProperty("myFile", fileName);


            dos = new DataOutputStream(conn.getOutputStream());
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"myFile\";filename=\"" + fileName + "\"" + lineEnd);
            dos.writeBytes(lineEnd);
            bytesAvailable = fileInputStream.available();
            Log.i("Huzza2", "Initial .available : " + bytesAvailable);
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            while (bytesRead > 0) {
                dos.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }
            dos.writeBytes(lineEnd);

            for(int i=0;i<DADOS.size();i++){
                String key = ID_DADOS[i];
                String value = (String) DADOS.get(ID_DADOS[i]);

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"" + key + "\"" + lineEnd);
                dos.writeBytes("Content-Type: text/plain" + lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes(value);
                dos.writeBytes(lineEnd);
            }
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            serverResponseCode = conn.getResponseCode();
            fileInputStream.close();

            dos.flush();
            dos.close();



        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (serverResponseCode == 200) {

            StringBuilder sb = new StringBuilder();
            try {
                BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                while ((line = rd.readLine()) != null) {
                    sb.append(line);
                }
                rd.close();
            } catch (IOException ioex) {
            }
            return sb.toString();
        }else {
            return "Could not upload";
        }
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
    public void SetDados(String[] ID_DADOS, ContentValues DADOS){
        this.ID_DADOS=ID_DADOS;
        this.DADOS=DADOS;
    }
}