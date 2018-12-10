package com.example.utente.dereflector;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client extends AsyncTask<Void, Void, String> {

    String dstAddress;
    int dstPort;
    String response = "";
    String data;
    Bitmap img;
    String result;

    Client(String addr, int port, String data, String result) {
        dstAddress = addr;
        dstPort = port;
        this.data = data;
        img =  BitmapFactory.decodeFile(data);
        this.result = result;
    }
    @Override
    protected String doInBackground(Void... params) {
        Socket socket = null;

        try {
            socket = new Socket(dstAddress, dstPort);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            InputStream inputStream = socket.getInputStream();

            String operation = "SendImage";
            String img64 = toBase64(img);
            byte[] buffer = new byte[img64.getBytes().length];

            byteArrayOutputStream.write(operation.getBytes());
            operation = data;
            byteArrayOutputStream.write(operation.getBytes());
            byteArrayOutputStream.write(img64.getBytes());

            response = "";
            while ( inputStream.read(buffer) != -1) {
                response += byteArrayOutputStream.toString("UTF-8");
            }

        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            response = "UnknownHostException: " + e.toString();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            response = "IOException: " + e.toString();
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return response;
    }

    @Override
    protected void onPostExecute(String result) {
        // TODO Auto-generated method stub
        result = response;
        super.onPostExecute(result);

    }

    public String toBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

}