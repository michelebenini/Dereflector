package com.example.utente.dereflector;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Environment;
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

public class ResultActivity extends AppCompatActivity {
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
        String name = intent.getStringExtra("name");
        String pathI = Environment.getExternalStorageDirectory().toString()+"/Dereflection/Images/"+name;
        String pathR = Environment.getExternalStorageDirectory().toString()+"/Dereflection/Result/"+name;


        final ImageView img=(ImageView)findViewById(R.id.img);
        //final Bitmap src = BitmapFactory.decodeFile(pathI);
        //final Bitmap res = BitmapFactory.decodeFile(pathR);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getResources(), R.id.img, options);
        int imageHeight = options.outHeight;
        int imageWidth = options.outWidth;

        final SeekBar seekbar=(SeekBar)findViewById(R.id.seekBar);
        int width = 300;//(int) dipToPixels(this, 347);
        int heigth = 300;//(int) dipToPixels(this, 434);

        //final Bitmap origin = Bitmap.createScaledBitmap(BitmapFactory.decodeFile(pathI), width , heigth, true);
        //final Bitmap result = Bitmap.createScaledBitmap(BitmapFactory.decodeFile(pathR), width , heigth, true);

        final Bitmap origin = decodeSampledBitmapFromPath(pathI, width, heigth);
        final Bitmap result = decodeSampledBitmapFromPath(pathR, width, heigth);

        int minw = origin.getWidth();
        if(minw > result.getWidth())
            minw = result.getWidth();
        int h = origin.getHeight();
        if(h > result.getHeight())
            h = result.getHeight();
        final int w = minw;

        start = 0;
        thresh = w*seekbar.getProgress()/100;
        end = w;
        image =  result.copy(origin.getConfig(),true);

        //Picasso.get().load("file://" + image).into(img);
        createImage(origin,result);
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

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public Bitmap decodeSampledBitmapFromPath(String path, int reqWidth,
                                              int reqHeight) {

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        options.inSampleSize = calculateInSampleSize(options, reqWidth,
                reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        Bitmap bmp = BitmapFactory.decodeFile(path, options);
        return bmp;
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

}
