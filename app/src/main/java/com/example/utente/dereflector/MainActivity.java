package com.example.utente.dereflector;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
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

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "DEBUG";
    private static final int RESULT_LOAD_IMAGE = 1;
    private static final int NOTIFY = 2;
    String picturePath = "";
    private static int id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageView add = findViewById(R.id.add);
        Button send = findViewById(R.id.send);
        id = 0;
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
                Intent serviceIntent = new Intent();
                serviceIntent.setAction("SendImage.class");
                Bundle extras = serviceIntent.getExtras();
                String key = "IMAGE";
                String value = picturePath;
                extras.putString(key, value);
                //startService(serviceIntent);
                String address = "";
                int port = 0;
                String data = value;
                final String result = "";
                final Client myClient = new Client(address, port, data, result);
                myClient.execute();
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
            if(imageView.getDrawable() != null){
                Button send = findViewById(R.id.send);
                send.setVisibility(View.VISIBLE);
            }

        }
        else if(requestCode == NOTIFY){
            createNotification();
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

        notificationManager.notify(0, noti);
    }
}
