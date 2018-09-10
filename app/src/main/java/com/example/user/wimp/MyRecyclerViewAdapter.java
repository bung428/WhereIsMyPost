package com.example.user.wimp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {

    private LayoutInflater mInflater;
    ArrayList<RecyclerItem> mData = new ArrayList<>();

    // data is passed into the constructor
    MyRecyclerViewAdapter(Context context, ArrayList<RecyclerItem> mData) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = mData;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.from(parent.getContext()).inflate(R.layout.recyclerview_row, parent, false);
        ViewHolder holder=new ViewHolder(view);
        return holder;
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView post_level,post_date,post_comp,post_info,post_day;
        ImageView post_image;

        ViewHolder(final View itemView) {
            super(itemView);
            post_level = itemView.findViewById(R.id.post_level);
            post_date = itemView.findViewById(R.id.post_date);
            post_comp = itemView.findViewById(R.id.post_comp);
            post_info = itemView.findViewById(R.id.post_info);
            post_day = itemView.findViewById(R.id.post_day);
//            post_image = itemView.findViewById(R.id.post_image);

//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Toast.makeText(itemView.getContext(), "click " + mData.get(getPosition()), Toast.LENGTH_SHORT).show();
//                }
//            });
        }
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.post_level.setText(mData.get(position).getLevel());
        holder.post_date.setText(mData.get(position).getDate());
        holder.post_comp.setText(mData.get(position).getComp());
        holder.post_info.setText(mData.get(position).getInfo());
        holder.post_day.setText(mData.get(position).getDay());
//        if(mData.get(position).getComp().equals("CJ대한통운")){
//            Log.d("post","in adapter : 회사명받기 성공");
//            holder.post_image.setImageResource(R.drawable.cj);
//        }

    }
}
