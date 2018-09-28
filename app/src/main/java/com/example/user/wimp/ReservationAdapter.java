package com.example.user.wimp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class ReservationAdapter extends RecyclerView.Adapter<ReservationAdapter.ViewHolder> {

    private LayoutInflater mInflater;
    ArrayList<ReservationItem> mData = new ArrayList<>();

    // data is passed into the constructor
    ReservationAdapter(Context context, ArrayList<ReservationItem> mData) {
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
        View view = mInflater.from(parent.getContext()).inflate(R.layout.reservation_row, parent, false);
        ViewHolder holder=new ViewHolder(view);
        return holder;
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView countingTv,reservationnumTv;

        ViewHolder(final View itemView) {
            super(itemView);
            countingTv = itemView.findViewById(R.id.countingTv);
            reservationnumTv = itemView.findViewById(R.id.reservationnumTv);
        }
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.countingTv.setText(mData.get(position).getNum());
        holder.reservationnumTv.setText(mData.get(position).getReservationNum());
    }
}
