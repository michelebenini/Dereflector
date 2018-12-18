package com.example.utente.dereflector;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
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


import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "DEBUG";
    private static final int RESULT_LOAD_IMAGE = 1;
    private static final String CHANNEL_ID = "ChannelID";

    String picturePath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), "Dereflection");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("App", "failed to create directory");
            }
        }
        File dirImg = new File(Environment.getExternalStorageDirectory(), "Dereflection/Images");
        if (!dirImg.exists()) {
            if (!dirImg.mkdirs()) {
                Log.d("App", "failed to create directory");
            }
        }
        File dirRes = new File(Environment.getExternalStorageDirectory(), "Dereflection/Result");
        if (!dirRes.exists()) {
            if (!dirRes.mkdirs()) {
                Log.d("App", "failed to create directory");
            }
        }

        createNotificationChannel();
        setContentView(R.layout.activity_main);
        ImageView add = findViewById(R.id.add);
        Button send = findViewById(R.id.send);
        Button show = findViewById(R.id.show);

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
                request();
            }
        });

        show.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ListImageActivity.class);
                Log.v(TAG,"REQUEST SEND");

                String result = "";
                final Client myClient = new Client(getApplicationContext(),2,"", result);
                try {
                    result = myClient.execute().get();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


                intent.putExtra("dataset",result);

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

            Picasso.get().load("file://" + picturePath).resize(imageView.getMeasuredWidth(), imageView.getMeasuredHeight()).centerCrop().into(imageView);
            //imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
            Log.v(TAG,"picturepath : "+picturePath);
            if(imageView.getDrawable() != null){
                Button send = findViewById(R.id.send);
                send.setVisibility(View.VISIBLE);
            }

        }

    }

    public void request(){
        Log.v(TAG,"REQUEST SEND");
        final String result = "";
        final Client myClient = new Client(this,1,picturePath, result);
        try {
            if(myClient.execute().get().compareTo("TRUE")==0) {
                Toast.makeText(this, "Image Send!", Toast.LENGTH_LONG).show();
            }
            else
                Toast.makeText(this, "Send error!",Toast.LENGTH_LONG).show();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) { // NOTE: with the implementation of this method inherited from // Activity, some widgets save their state in the bundle by default. // Once the user interface contains AT LEAST one non-autosaving // element, you should provide a custom implementation of // the method
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

