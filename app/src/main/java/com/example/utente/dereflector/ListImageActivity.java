package com.example.utente.dereflector;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;


public class ListImageActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private final String TAG="LIST";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        setContentView(R.layout.activity_list_image);

    }

    @Override
    protected void onStart() {
        super.onStart();

        mRecyclerView = (RecyclerView) findViewById(R.id.listview);


        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        String dataset[] = makeDataset();

        mAdapter = new ListAdapter(ListImageActivity.this,dataset);

        mRecyclerView.setAdapter(mAdapter);
    }

    private String[] makeDataset( ){
        String[]dataset = new String[8];
        dataset[0] = "/storage/emulated/0/WhatsApp/Media/IMG-20180308-WA0001.jpeg";
        dataset[1] = "/storage/emulated/0/WhatsApp/Media/IMG-20180308-WA0001.jpeg";
        dataset[2] = "/storage/emulated/0/WhatsApp/Media/IMG-20180308-WA0001.jpeg";
        dataset[3] = "/storage/emulated/0/WhatsApp/Media/IMG-20180308-WA0001.jpeg";
        dataset[4] = "/storage/emulated/0/WhatsApp/Media/IMG-20180308-WA0001.jpeg";
        dataset[5] = "/storage/emulated/0/WhatsApp/Media/IMG-20180308-WA0001.jpeg";
        dataset[6] = "/storage/emulated/0/WhatsApp/Media/IMG-20180308-WA0001.jpeg";
        dataset[7] = "/storage/emulated/0/WhatsApp/Media/IMG-20180308-WA0001.jpeg";
        return  dataset;
    }

}
