package com.example.utente.dereflector;



import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;


public class Client extends AsyncTask<Void, Void, String> {
    private final String TAG = "CLIENT";

    private final String dstAddress = "192.168.30.11"; // hotspot con pc, indirizzo gateway telefono
    private final int dstPort = 1234;
    private String user;
    String response = "";
    String data;
    Bitmap img;
    String result;
    Context context;

    int flag;

    Client(Context context, int flag, String data, String result) {
        this.context = context;
        this.flag = flag;
        this.data = data;
        this.result = result;
        this.user = getUser();
    }
    private String getUser(){
       return "Jane.Doe";
    }
    @Override
    protected String doInBackground(Void... params) {
        Socket socket = new Socket();
        Log.v(TAG,"Do in background Server: "+dstAddress+":"+dstPort);
        try {
            socket = new Socket();
            socket.connect(new InetSocketAddress(dstAddress, dstPort), 2000);
            //socket.setSoTimeout(1000);
            Log.v(TAG,"Socket created");

            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            if(flag == 1) {
                String operation = "SendImage";
                String name = new String();
                Scanner scan = new Scanner(data).useDelimiter("/");
                while (scan.hasNext()) {
                    name = scan.next();
                }
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                img =  BitmapFactory.decodeFile(data);
                img.compress(Bitmap.CompressFormat.PNG, 50, stream);
                byte[] buffer = stream.toByteArray();

                oos.writeObject(operation);
                oos.writeObject(user);
                oos.writeObject(name);
                oos.writeObject(buffer);
                oos.flush();
                Log.v(TAG, "Data send");
                boolean r =(boolean)ois.readObject();
                response = "TRUE";
                if(!r)
                    response = "FALSE";
            }else if(flag == 2) {
                String operation = "List";
                oos.writeObject(operation);
                oos.writeObject(user);
                oos.flush();
                Log.v(TAG, "Data send");

                ArrayList<String> ls =(ArrayList<String>)ois.readObject();
                if(ls == null){
                    response = null;
                }
                else {
                    response = ls.toString();
                }
            }else if(flag == 3) {
                oos.writeObject("getImage");
                oos.writeObject(user);
                oos.writeObject(data);

                byte[] obj = (byte[]) ois.readObject();
                File dirImg = new File(Environment.getExternalStorageDirectory(), "Dereflection/Images/");
                File file = new File(dirImg,data);
                if (file.exists ()) file.delete ();
                try {
                    FileOutputStream out = new FileOutputStream(file);
                    out.write(obj);
                    out.flush();
                    out.close();
                    response = "TRUE";

                } catch (Exception e) {
                    e.printStackTrace();
                    response = "FALSE";
                }
                oos.close();
                ois.close();
            }else if(flag == 4) {
                oos.writeObject("getResult");
                oos.writeObject(user);
                oos.writeObject(data);

                byte[] obj = (byte[]) ois.readObject();
                File dirRes = new File(Environment.getExternalStorageDirectory(), "Dereflection/Result/");
                File file = new File(dirRes,data);
                if (file.exists ()) file.delete ();
                try {
                    FileOutputStream out = new FileOutputStream(file);
                    out.write(obj);
                    out.flush();
                    out.close();

                } catch (Exception e) {
                    e.printStackTrace();
                }
                oos.close();
                ois.close();
                response = "TRUE";
            }else if(flag == 5) {
                String operation = "ListNew";
                oos.writeObject(operation);
                oos.writeObject(user);
                oos.flush();
                Log.v(TAG, "Data send");

                ArrayList<String> ls =(ArrayList<String>)ois.readObject();
                if(ls == null){
                    response = null;
                }
                else{
                    response = ls.toString();
                }
            }
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            response = null;
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            response = null;
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            response = null;
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