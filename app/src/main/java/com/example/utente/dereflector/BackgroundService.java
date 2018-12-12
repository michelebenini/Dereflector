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
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.JobIntentService;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.Toast;



public class BackgroundService extends IntentService {
    private final String TAG = "SERVICE ";
    int id;
    private Handler mPeriodicEventHandler;
    private final int PERIODIC_EVENT_TIMEOUT = 5000;

    String CHANNEL_ID = "prova";
    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.icon2)
            .setContentTitle("heyy")
            .setContentText("hwwyyy")
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

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        for(int i=0;i<10;i++) {
            // notificationId is a unique int for each notification that you must define
            notificationManager.notify(i, mBuilder.build());
        }
        //onTaskRemoved(intent);

        return START_STICKY;
    }

    private Runnable doPeriodicTask = new Runnable()
    {
        public void run()
        {
            Log.v(TAG,"ESECUZIONE PERIODICA "+id);
            startNotificationListener();
            id++;
            mPeriodicEventHandler.postDelayed(doPeriodicTask, PERIODIC_EVENT_TIMEOUT);
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            PendingIntent pIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);

        }
    };

    @Override
    public void onCreate() {
        startNotificationListener();
        super.onCreate();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(3, mBuilder.build());

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
}
