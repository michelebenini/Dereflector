package com.example.utente.dereflector;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

public class Client extends AsyncTask<Void, Void, String> {
    private final String TAG = "CLIENT";
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
        Socket socket = new Socket();
        Log.v(TAG,"Do in background Server: "+dstAddress+":"+dstPort);
        try {
            socket = new Socket(dstAddress, dstPort);
            Log.v(TAG,"Socket created");

            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());

            String operation = "SendImage";

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            img.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] buffer = stream.toByteArray();

            oos.writeObject(operation);
            oos.flush();
            oos.writeObject(buffer);
            oos.flush();
            Log.v(TAG,"Data send");

            response = (String)ois.readObject();
            Log.v(TAG,"Received response : "+response);
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            response = "UnknownHostException: " + e.toString();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            response = "IOException: " + e.toString();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
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
        Log.v(TAG,"Received response : "+response);
        return response;
    }

    @Override
    protected void onPostExecute(String result) {
        // TODO Auto-generated method stub
        result = response;
        super.onPostExecute(result);

    }



}