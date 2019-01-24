package com.example.utente.dereflector;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;


public class BackgroundService extends IntentService {
    private final String TAG = "SERVICE ";
    int id;
    private Handler mPeriodicEventHandler;
    private final int PERIODIC_EVENT_TIMEOUT = 5000;
    private Context CONTEXT;
    String CHANNEL_ID = "NewImage";
    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.icon)
            .setContentTitle("New Image!")
            .setContentText("A new image was elaborate!")
            .setPriority(2);


    @Override
    protected void onHandleIntent(Intent workIntent) {
        // Gets data from the incoming Intent
        String dataString = workIntent.getDataString();


    }

    public BackgroundService() {
        super("backgrounService");
        id = 0;
        mPeriodicEventHandler = new Handler();
        mPeriodicEventHandler.postDelayed(doPeriodicTask, PERIODIC_EVENT_TIMEOUT);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v(TAG,"onStartCommand");

        return START_STICKY;
    }

    private Runnable doPeriodicTask = new Runnable()
    {
        public void run()
        {
            Log.v(TAG,"ESECUZIONE PERIODICA "+id);
            //startNotificationListener();
            if(checkNewFile()){
                Log.v(TAG,"NOTIFICA");
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(CONTEXT);
                notificationManager.notify(3, mBuilder.build());
            }
            mPeriodicEventHandler.postDelayed(doPeriodicTask, PERIODIC_EVENT_TIMEOUT);


        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        this.CONTEXT = this;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private synchronized boolean checkNewFile(){
        boolean done = false;
        String result = "";
        final Client myClient = new Client(CONTEXT,5,"", result);
        myClient.execute();
        try {
            result = myClient.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.v(TAG,"List: "+result);
        if(result == null || result.length()<3){
            return false;
        }
        result = result.substring(1,result.length()-1);
        ArrayList<String> ds = new ArrayList<>();
        Scanner scan = new Scanner(result).useDelimiter(", ");
        while(scan.hasNext()){
            ds.add(scan.next());
        }
        for(int i = 0; i < ds.size(); i++){
            done = checkfile(ds.get(i));
            if(done){
                return done;
            }
        }
        return done;
    }
    private boolean checkfile(String s){

        File dirImg = new File(Environment.getExternalStorageDirectory(), "Dereflection/Images/");
        File dirRes = new File(Environment.getExternalStorageDirectory(), "Dereflection/Result/");

        //Get the text file
        File fileI = new File(dirImg,s);
        File fileR = new File(dirRes,s);

        if(!fileI.exists()){
            Log.v(TAG,"IMAGE does not exist!");
            String result = "";
            final Client myClient = new Client(this,3,s, result);
            myClient.execute();

            try {
                result = myClient.get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return true;
        }

        if(!fileR.exists()){
            Log.v(TAG,"RESULT does not exist!");
            String result = "";
            final Client myClient = new Client(this,3,s, result);
            myClient.execute();
            try {
                result = myClient.get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return true;
        }

        return false;
    }
}
