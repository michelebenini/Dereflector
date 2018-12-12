package com.example.utente.dereflector;

import android.Manifest;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.JobIntentService;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.app.Service;

import java.util.Random;

import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "DEBUG";
    private static final int RESULT_LOAD_IMAGE = 1;
    private static final int INTERNET = 3;

    private static final int RSS_JOB_ID = 1000;

    String picturePath = "";

    String CHANNEL_ID = "prova";
    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.icon2)
            .setContentTitle("heyy")
            .setContentText("hwwyyy")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createNotificationChannel();
        setContentView(R.layout.activity_main);
        ImageView add = findViewById(R.id.add);
        Button send = findViewById(R.id.send);
        Button show = findViewById(R.id.show);

        //startService(new Intent(this, BackgroundService.class));

        Intent mServiceIntent =  new Intent();
        mServiceIntent.setClass(this, BackgroundService.class);
        startService(mServiceIntent);



        if(savedInstanceState != null){
            picturePath = savedInstanceState.getString("image");
            ImageView imageView = (ImageView) findViewById(R.id.add);
            imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
            if(imageView.getDrawable() != null){
                send = findViewById(R.id.send);
                send.setVisibility(View.VISIBLE);
            }
        }
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
                /*Log.v(TAG, "Click on button!");
                Intent intent = new Intent(MainActivity.this, ResultActivity.class);
                intent.putExtra("image",picturePath);
                startActivity(intent);
                */
                try {
                    if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.INTERNET}, INTERNET);
                    } else {
                        request();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        show.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Log.v(TAG,"SHOW clicked");
                Intent intent = new Intent(MainActivity.this, ListImageActivity.class);
                startActivity(intent);
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
            cursor.close();

            ImageView imageView = (ImageView) findViewById(R.id.add);
            imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
            Log.v(TAG,"picturepath : "+picturePath);
            if(imageView.getDrawable() != null){
                Button send = findViewById(R.id.send);
                send.setVisibility(View.VISIBLE);
            }

        }

    }

    public void request(){
        Log.v(TAG,"REQUEST SEND");
        String address = "10.0.2.2";
        int port = 1234;
        final String result = "";
        final Client myClient = new Client(address, port, picturePath, result);
        myClient.execute();
        Toast.makeText(this, "Image Send!",Toast.LENGTH_LONG).show();

    }


    @Override public void onSaveInstanceState(Bundle savedInstanceState) { // NOTE: with the implementation of this method inherited from // Activity, some widgets save their state in the bundle by default. // Once the user interface contains AT LEAST one non-autosaving // element, you should provide a custom implementation of // the method
        savedInstanceState.putString("image", picturePath);
        super.onSaveInstanceState(savedInstanceState);
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = ("channelname");
            String description = ("channelDescr");
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}

