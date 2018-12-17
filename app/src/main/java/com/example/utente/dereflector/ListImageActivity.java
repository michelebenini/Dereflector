package com.example.utente.dereflector;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;


public class ListImageActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private final String TAG="LIST";
    private String ls;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        ls = intent.getStringExtra("dataset");
        ls = ls.substring(1,ls.length()-1);
        Log.v(TAG,ls);
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
        ArrayList<String> ds = new ArrayList<>();
        Scanner scan = new Scanner(ls).useDelimiter(", ");
        while(scan.hasNext()){
            ds.add(scan.next());
        }
        int n = ds.size();
        String[] dataset = new String[n];
        for (int i = 0; i < n; i++){
            String str = ds.get(i);
            Log.v(TAG,str);
            if(checkfile(str)){
                Log.v(TAG,"Add to dataset : "+str);
                dataset[i] = str;
            }

        }
        return  dataset;
    }
    private boolean checkfile(String s){
        boolean done1 = false;
        boolean done2 = false;
        File dirImg = new File(Environment.getExternalStorageDirectory(), "Dereflection/Images/");
        File dirRes = new File(Environment.getExternalStorageDirectory(), "Dereflection/Result/");

        //Get the text file
        File fileI = new File(dirImg,s);
        File fileR = new File(dirRes,s);
        String res = new String();
        if(!fileI.exists()){
            Log.v(TAG,"IMAGE does not exist!");
            Client myClient = new Client(3,s, "");
            try {
                res = myClient.execute().get();
                done1 = true;
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }else{
            done1 = true;
        }

        if(!fileR.exists()){
            Log.v(TAG,"RESULT does not exist!");
            Client myClient = new Client(4,s, "");
            try {
                res = myClient.execute().get();
                done2 = true;
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }else {
            done2 = true;
        }

        Log.v(TAG,"done 1 : "+done1+" done 2 "+done2);
        return done1&done2;
    }

}
