package com.example.utente.dereflector;

import android.app.Application;
import android.app.IntentService;
import android.app.ListActivity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.JobIntentService;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

import javax.xml.transform.Result;


public class BackgroundService extends IntentService {
    private final String TAG = "SERVICE ";
    int id;
    private Handler mPeriodicEventHandler;
    private final int PERIODIC_EVENT_TIMEOUT = 5000;
    private Context CONTEXT;
    String CHANNEL_ID = "prova";
    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.icon)
            .setContentTitle("New Image!")
            .setContentText("A new image was elaborate!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT);


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
        //startNotificationListener();
        super.onCreate();
        this.CONTEXT = this;
            }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        //Intent restartServiceIntent = new Intent(getApplicationContext(),this.getClass());
        //restartServiceIntent.setPackage(getPackageName());
        //startService(restartServiceIntent);
        //super.onTaskRemoved(rootIntent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void startNotificationListener() {
        //start's a new thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                //fetching notifications from server
                //if there is notifications then call this method
                PushNotification();

            }
        }).start();
    }

    public void PushNotification()
    {
        Log.v(TAG,"NOTIFICA ");
        Context context = getApplicationContext();
        NotificationManager nm = (NotificationManager)context.getSystemService(NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(context);
        Intent notificationIntent = new Intent(context, ListActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context,0,notificationIntent,0);

        //set
        builder.setContentIntent(contentIntent);
        builder.setSmallIcon(R.drawable.icon);
        builder.setContentText("Contents");
        builder.setContentTitle("title");
        builder.setAutoCancel(true);
        builder.setDefaults(Notification.DEFAULT_ALL);

        Notification notification = builder.build();
        nm.notify(id,notification);

    }
    private synchronized boolean checkNewFile(){
        boolean done = false;
        String result = "";
        final Client myClient = new Client(CONTEXT,5,"", result);
        try {
            result = myClient.execute().get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.v(TAG,"List: "+result);
        if(result ==null || result.length()<3){
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
            return false;
        }

        if(!fileR.exists()){
            Log.v(TAG,"RESULT does not exist!");
            return false;
        }

        return true;
    }
}
