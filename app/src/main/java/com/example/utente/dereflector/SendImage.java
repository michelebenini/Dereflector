package com.example.utente.dereflector;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.*;
import android.os.*;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

public class SendImage extends Service {
    private static final String TAG = "SERVICE";
    public Context context = this;
    public Handler handler = null;
    public static Runnable runnable = null;
    private String imgpath;
    Intent intent;

    @Override
    public IBinder onBind(Intent intent) {
        this.intent = intent;
        Bundle bundle = intent.getExtras();
        String key = "IMAGE";
        imgpath = (String)bundle.get(key);
        return null;
    }

    @Override
    public void onCreate() {
        Toast.makeText(this, "Service created!", Toast.LENGTH_LONG).show();
        handler = new Handler();
        String address = "";
        int port = 0;
        String data = imgpath;
        final String result = "";
        final Client myClient = new Client(address, port, data, result);
        myClient.execute();

        runnable = new Runnable() {
            public void run() {
                Log.v(TAG,"Service is still running");
                while(result.compareTo("")!= 0){
                    handler.postDelayed(runnable, 1000);
                }

                if(result.compareTo("ERROR")!=0){
                    handler.postDelayed(runnable, 10000);
                    Intent intent = new Intent(SendImage.this,MainActivity.class);


                }

            }
        };

    }

    @Override
    public void onDestroy() {
        /* IF YOU WANT THIS SERVICE KILLED WITH THE APP THEN UNCOMMENT THE FOLLOWING LINE */
        //handler.removeCallbacks(runnable);
        Log.v(TAG, "Service stopped");
    }

}
