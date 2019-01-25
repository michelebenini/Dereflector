package com.example.utente.dereflector;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "DEBUG";
    private static final int RESULT_LOAD_IMAGE = 1;
    private static final String CHANNEL_ID = "NewImage";

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


        Log.d(TAG,"onCreate start!");
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
            ImageView imageView =  findViewById(R.id.add);
            imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
            String h = getHost(getApplicationContext());
            TextView host = findViewById(R.id.host);
            host.setText(h);

            if(imageView.getDrawable() != null){
                send = findViewById(R.id.send);
                send.setVisibility(View.VISIBLE);
            }
        }

        add.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Log.v(TAG, "Click on image!");
                setHost(getApplicationContext());
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
                setHost(getApplicationContext());
                request();
            }
        });

        show.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ListImageActivity.class);
                Log.v(TAG,"REQUEST SEND");
                setHost(getApplicationContext());
                try {
                    if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, RESULT_LOAD_IMAGE);
                    } else {
                        String result = "";
                        final Client myClient = new Client(getApplicationContext(),2,"", result);
                        myClient.execute();
                        try {
                            result = myClient.get();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }


                        intent.putExtra("dataset",result);

                        startActivity(intent);
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
            cursor.close();

            ImageView imageView = findViewById(R.id.add);

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
            myClient.execute();
            if(myClient.get()!=null) {
                Toast.makeText(this, "Image Send!", Toast.LENGTH_LONG).show();
                saveImage();
            }
            else {
                Toast.makeText(this, "Send error!",Toast.LENGTH_LONG).show();
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void setHost(Context context){
        EditText host = findViewById(R.id.host);
        String s =host.getText().toString();
        Log.v(TAG,"s : "+s);
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("config.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(s);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }

    }
    private String getHost(Context context) {

        String ret = "";

        try {
            InputStream inputStream = context.openFileInput("config.txt");

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }
        Log.v(TAG,"s : " +ret);
        return ret;
    }
    private void saveImage(){
        Bitmap img =  BitmapFactory.decodeFile(picturePath);
        File dirImg = new File(Environment.getExternalStorageDirectory(), "Dereflection/Images/");
        String name = new String();
        Scanner scan = new Scanner(picturePath).useDelimiter("/");
        while (scan.hasNext()) {
            name = scan.next();
        }
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        File file = new File(dirImg,name);
        img.compress(Bitmap.CompressFormat.PNG, 50, stream);
        byte[] buffer = stream.toByteArray();

        if (file.exists ()) file.delete ();
        try {
            FileOutputStream out = new FileOutputStream(file);
            out.write(buffer);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) { // NOTE: with the implementation of this method inherited from // Activity, some widgets save their state in the bundle by default. // Once the user interface contains AT LEAST one non-autosaving // element, you should provide a custom implementation of // the method
        savedInstanceState.putString("image", picturePath);
        setHost(getApplicationContext());
        super.onSaveInstanceState(savedInstanceState);
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        Log.d(TAG,"Build.VERSION.SDK_INT "+Build.VERSION.SDK_INT);
        Log.d(TAG,"Build.VERSION_CODES.O "+ Build.VERSION_CODES.O);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = ("NewImage");
            String description = ("Image ready to receive!");
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
            Log.d(TAG,"Notification channel created!");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        setHost(getApplicationContext());
    }

    @Override
    protected void onResume() {
        super.onResume();
        String h = getHost(getApplicationContext());
        TextView host = findViewById(R.id.host);
        host.setText(h);
    }
}

