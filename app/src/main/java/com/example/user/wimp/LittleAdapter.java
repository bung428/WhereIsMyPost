package com.example.user.wimp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class LittleAdapter extends RecyclerView.Adapter<LittleAdapter.ViewHolder> {

    private LayoutInflater mInflater;
    ArrayList<LittleMallItem> mData = new ArrayList<>();

    // data is passed into the constructor
    LittleAdapter(Context context, ArrayList<LittleMallItem> mData) {
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
        View view = mInflater.from(parent.getContext()).inflate(R.layout.littlerecyclerview_row, parent, false);
        ViewHolder holder=new ViewHolder(view);
        return holder;
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView mall_info;

        ViewHolder(final View itemView) {
            super(itemView);
            mall_info = itemView.findViewById(R.id.mall_info);
        }
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mall_info.setText(mData.get(position).getInfo());
    }
}

