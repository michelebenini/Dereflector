package com.example.utente.dereflector;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.SeekBar;


public class ResultActivity extends AppCompatActivity {
    Bitmap image;
    int start;
    int end;
    int thresh;
    private final String TAG = "RESULT ACTIVITY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_activity_result);

        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String pathI = Environment.getExternalStorageDirectory().toString()+"/Dereflection/Images/"+name;
        String pathR = Environment.getExternalStorageDirectory().toString()+"/Dereflection/Result/"+name;


        final ImageView img= findViewById(R.id.img);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getResources(), R.id.img, options);

        final SeekBar seekbar=findViewById(R.id.seekBar);
        int width = 300;//(int) dipToPixels(this, 347);
        int heigth = 300;//(int) dipToPixels(this, 434);
        final Bitmap origin = Bitmap.createScaledBitmap(decodeSampledBitmapFromPath(pathI, width, heigth), width, heigth, false);
        final Bitmap result = Bitmap.createScaledBitmap(decodeSampledBitmapFromPath(pathR, width, heigth), width, heigth, false);

        final int w = origin.getWidth();
        final int h = origin.getHeight();
        Log.v(TAG,"w = "+w);
        Log.v(TAG,"w = "+h);

        start = 0;
        thresh = w*seekbar.getProgress()/100;
        end = w;
        image =  result.copy(origin.getConfig(),true);


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

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public Bitmap decodeSampledBitmapFromPath(String path, int reqWidth,  int reqHeight) {

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        Bitmap bmp = BitmapFactory.decodeFile(path, options);
        return bmp;
    }


}
