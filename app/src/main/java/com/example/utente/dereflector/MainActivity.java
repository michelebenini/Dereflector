package com.example.utente.dereflector;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "DEBUG";
    private static final int RESULT_LOAD_IMAGE = 1;
    private static final int INTERNET = 3;
    String picturePath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageView add = findViewById(R.id.add);
        Button send = findViewById(R.id.send);
        add.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Log.v(TAG, "Click on image!");

                try {
                    if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, RESULT_LOAD_IMAGE);
                    } else {
                        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        send.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Log.v(TAG, "Click on button!");
                try {
                    if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.INTERNET}, INTERNET);
                    } else {
                        startActivityForResult(new Intent(), INTERNET);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            picturePath = cursor.getString(columnIndex);
            if(picturePath.compareTo("")!=0){
                Log.v(TAG, "set"+picturePath);
                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("image", picturePath); // here string is the value you want to save
                editor.commit();
            }
            cursor.close();

            ImageView imageView = (ImageView) findViewById(R.id.add);
            imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
            if(imageView.getDrawable() != null){
                Button send = findViewById(R.id.send);
                send.setVisibility(View.VISIBLE);
            }

        }
        else if(requestCode == INTERNET){
            request();
        }
    }

    public void createNotification() {
        // Prepare intent which is triggered if the
        // notification is selected
        Intent intent = new Intent(this, ResultActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);

        // Build notification
        // Actions are just fake
        Notification noti = new Notification.Builder(this).setSmallIcon(R.drawable.icon).setContentTitle("IMAGE OBTAINED").build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // hide the notification after its selected
        noti.flags |= Notification.FLAG_AUTO_CANCEL;

        notificationManager.notify(new Random().nextInt(), noti);
    }
    public void request(){
        String address = "";
        int port = 0;
        final String result = "";
        final Client myClient = new Client(address, port, picturePath, result);
        myClient.execute();
        /*Intent serviceIntent = new Intent();
        serviceIntent.setAction("com.example.utente.dereflector.SendImage");
        Bundle extras = serviceIntent.getExtras();
        String key = "IMAGE";
        Log.v(TAG,key+" : "+picturePath);
        extras.putString(key, picturePath);
        startService(serviceIntent);*/
    }


    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        picturePath = settings.getString("image", "");
        Log.v(TAG, "take: "+picturePath);
        if(picturePath.compareTo("")!=0){
            ImageView imageView = (ImageView) findViewById(R.id.add);
            imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
            if(imageView.getDrawable() != null){
                Button send = findViewById(R.id.send);
                send.setVisibility(View.VISIBLE);
            }
        }
    }

}

