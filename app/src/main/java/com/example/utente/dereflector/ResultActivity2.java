package com.example.utente.dereflector;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.squareup.picasso.Picasso;

public class ResultActivity2 extends AppCompatActivity {
    Bitmap image;
    int start;
    int end;
    int thresh;
    private static int heigth;
    private int width;
    private final String TAG = "RESULT ACTIVITY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_activity_result);

        Intent intent = getIntent();
        String picturePath = intent.getStringExtra("name");

        final ImageView img=(ImageView)findViewById(R.id.img);
        final Bitmap src = BitmapFactory.decodeFile(picturePath);

        final SeekBar seekbar=(SeekBar)findViewById(R.id.seekBar);
        int width = (int) dipToPixels(this, 347);
        int heigth = (int) dipToPixels(this, 434);
        final Bitmap origin = Bitmap.createScaledBitmap( src, width , heigth, true);
        final int w = origin.getWidth();
        int h = origin.getHeight();
        final Bitmap result = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        start = 0;
        thresh = w*seekbar.getProgress()/100;
        end = w;
        image =  result.copy(origin.getConfig(),true);

        //img.setImageBitmap(image);
        Picasso.get().load("file://" + image).into(img);


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
                //Picasso.get().load("file://" + image).into(img);
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
                //Picasso.get().load("file://" + image).into(img);
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

    public static float dipToPixels(Context context, float dipValue) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

}
