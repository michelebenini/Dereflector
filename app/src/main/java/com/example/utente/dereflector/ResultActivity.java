package com.example.utente.dereflector;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;

public class ResultActivity extends AppCompatActivity {
    Bitmap image;
    int start;
    int end;
    int thresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        setContentView(R.layout.activity_result);
        final SeekBar seekbar=(SeekBar)findViewById(R.id.seekBar);
        final ImageView img=(ImageView)findViewById(R.id.img);


        String picturePath = intent.getStringExtra("name");
        final Bitmap origin = BitmapFactory.decodeFile(picturePath);
        final int w = origin.getWidth();
        int h = origin.getHeight();
        final Bitmap result = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        start = 0;
        thresh = w*seekbar.getProgress()/100;
        end = w;
        image =  result.copy(origin.getConfig(),true);
        img.setImageBitmap(image);

        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
                int th = w*seekbar.getProgress()/100;
                if(thresh < th){
                    start = thresh;
                    end = th;
                    thresh = th;
                }else{
                    start = th;
                    end = thresh;
                    thresh = th;
                }
                createImage(origin,result);
                img.setImageBitmap(image);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
                int th = w*seekbar.getProgress()/100;
                if(thresh < th){
                    start = thresh;
                    end = th;
                    thresh = th;
                }else{
                    start = th;
                    end = thresh;
                    thresh = th;
                }
                createImage(origin,result);
                img.setImageBitmap(image);
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
                // TODO Auto-generated method stub
                int th = w*seekbar.getProgress()/100;
                if(thresh < th){
                    start = thresh;
                    end = th;
                    thresh = th;
                }else{
                    start = th;
                    end = thresh;
                    thresh = th;
                }
                createImage(origin,result);
                img.setImageBitmap(image);
            }
        });
    }

    private void createImage(Bitmap origin, Bitmap result){
        int w = origin.getWidth();
        int h = origin.getHeight();

        for(int i = 0; i < h; i++){
            for(int j = start; j<end;j++){
                if(j < thresh){
                    image.setPixel(j,i,origin.getPixel(j,i));
                }
                else{
                    image.setPixel(j,i, result.getPixel(j,i));
                }
            }
        }
        return;
    }
    @Override
    protected void onPause() {
        super.onPause();

    }
}
