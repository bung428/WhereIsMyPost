package com.example.user.wimp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class PostRecyclerViewAdapter extends RecyclerView.Adapter<PostRecyclerViewAdapter.ViewHolder> {

    private LayoutInflater mInflater;
    ArrayList<RecyclerItemPostInfo> mData = new ArrayList<>();

    // data is passed into the constructor
    PostRecyclerViewAdapter(Context context, ArrayList<RecyclerItemPostInfo> mData) {
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
        View view = mInflater.from(parent.getContext()).inflate(R.layout.postrecyclerview_row, parent, false);
        ViewHolder holder=new ViewHolder(view);
        return holder;
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView post_level,post_date,post_where;
        ImageView post_image,post_detail;

        ViewHolder(final View itemView) {
            super(itemView);
            post_level = itemView.findViewById(R.id.post_level);
            post_date = itemView.findViewById(R.id.post_date);
            post_where = itemView.findViewById(R.id.post_where);
            post_image = itemView.findViewById(R.id.post_image);
            post_detail = itemView.findViewById(R.id.post_detail);

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
        holder.post_where.setText(mData.get(position).getWhere());
        holder.post_detail.setImageResource(R.drawable.right);
//        if(mData.get(position).getComp().equals("CJ대한통운")){
//            Log.d("post","in adapter : 회사명받기 성공");
//            holder.post_image.setImageResource(R.drawable.cj);
//        }

    }
}
