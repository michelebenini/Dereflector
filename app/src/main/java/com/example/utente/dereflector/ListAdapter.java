package com.example.utente.dereflector;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.MyViewHolder> {
    private String[] mDataset;
    private final String TAG = "LIST ADAPTER";
    private Context context;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder{
        View view;
        // each data item is just a string in this case
        public TextView mTextView;
        public ImageView imView;
        public MyViewHolder(View v) {
            super(v);


            mTextView = (TextView) v.findViewById(R.id.textView1);
            imView = (ImageView)v.findViewById(R.id.imageView1);
        }


    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ListAdapter(Context context, String[] myDataset) {
        this.context = context;
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        MyViewHolder holder;
        View v =  LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_item, parent, false);

        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.mTextView.setText(mDataset[position]);
        holder.imView.setImageBitmap(BitmapFactory.decodeFile(mDataset[position]));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = holder.mTextView.getText().toString();
                Log.d(TAG, "onItemClick position: " +name );
                Intent intent = new Intent(context, ResultActivity.class);
                intent.putExtra("name",name);
                context.startActivity(intent);
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.length;
    }


}