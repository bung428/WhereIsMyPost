package com.example.user.wimp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class ChatRecyclerViewAdapter extends RecyclerView.Adapter<ChatRecyclerViewAdapter.ViewHolder> {

    private LayoutInflater mInflater;
    ArrayList<RecyclerChatItem> mData = new ArrayList<>();

    // data is passed into the constructor
    ChatRecyclerViewAdapter(Context context, ArrayList<RecyclerChatItem> mData) {
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
        View view = mInflater.from(parent.getContext()).inflate(R.layout.recyclerview_chat, parent, false);
        ViewHolder holder=new ViewHolder(view);
        return holder;
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView chat_name,chat_text,chat_date;
        ImageView chat_read_flag;

        ViewHolder(final View itemView) {
            super(itemView);
            chat_name = itemView.findViewById(R.id.chat_name);
            chat_text = itemView.findViewById(R.id.chat_text);
            chat_date = itemView.findViewById(R.id.chat_date);
            chat_read_flag = itemView.findViewById(R.id.chat_read_flag);
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
        holder.chat_name.setText(mData.get(position).getName());
        holder.chat_text.setText(mData.get(position).getText());
        holder.chat_date.setText(mData.get(position).getDate());
        holder.chat_read_flag.setImageResource(R.drawable.msg_read);
//        if(mData.get(position).getComp().equals("CJ대한통운")){
//            Log.d("post","in adapter : 회사명받기 성공");
//            holder.post_image.setImageResource(R.drawable.cj);
//        }
    }
}
