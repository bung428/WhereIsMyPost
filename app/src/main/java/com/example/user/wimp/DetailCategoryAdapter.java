package com.example.user.wimp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class DetailCategoryAdapter extends RecyclerView.Adapter<DetailCategoryAdapter.ViewHolder> {

    private LayoutInflater mInflater;
    ArrayList<DetailCategoryItem> mData = new ArrayList<>();

    // data is passed into the constructor
    DetailCategoryAdapter(Context context, ArrayList<DetailCategoryItem> mData) {
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
        View view = mInflater.from(parent.getContext()).inflate(R.layout.recyclerview_detailcategory, parent, false);
        ViewHolder holder=new ViewHolder(view);
        return holder;
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView goods;

        ViewHolder(final View itemView) {
            super(itemView);
            goods = itemView.findViewById(R.id.goods);
        }
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.goods.setText(mData.get(position).getGoods());
    }
}

