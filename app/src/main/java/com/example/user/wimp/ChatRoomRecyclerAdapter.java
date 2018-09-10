package com.example.user.wimp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ChatRoomRecyclerAdapter extends RecyclerView.Adapter<ChatRoomRecyclerAdapter.ViewHolder> {

    private LayoutInflater mInflater;
    ArrayList<ChatRoomRecyclerItem> mData = new ArrayList<>();

    // data is passed into the constructor
    ChatRoomRecyclerAdapter(Context context, ArrayList<ChatRoomRecyclerItem> mData) {
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
        View view = mInflater.from(parent.getContext()).inflate(R.layout.chatitem, parent, false);
        ViewHolder holder=new ViewHolder(view);
        return holder;
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder{
        LinearLayout layout;
        View viewRight;
        View viewLeft;
        TextView text,name,lefttime,righttime;
        ImageView image1,image2,image3,image4,image5;

        ViewHolder(final View itemView) {
            super(itemView);
            layout    = (LinearLayout) itemView.findViewById(R.id.layout);
            text    = (TextView) itemView.findViewById(R.id.text);
            name    = (TextView) itemView.findViewById(R.id.name);
            lefttime    = (TextView) itemView.findViewById(R.id.lefttime);
            righttime    = (TextView) itemView.findViewById(R.id.righttime);
            viewRight    = (View) itemView.findViewById(R.id.imageViewright);
            viewLeft    = (View) itemView.findViewById(R.id.imageViewleft);
            image1    = itemView.findViewById(R.id.image1);
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
        holder.text.setText(mData.get(position).msg);
        holder.name.setText(mData.get(position).name);
        holder.lefttime.setText(mData.get(position).time);
        holder.righttime.setText(mData.get(position).time);
        Picasso.with(holder.image1.getContext())
                .load(mData.get(position).uri)
                .into(holder.image1);

        if( mData.get(position).type == 0 ) {
            if(mData.get(position).textorimage==true){
                //true면 글
                holder.text.setBackgroundResource(R.drawable.inbox2);
                holder.layout.setGravity(Gravity.LEFT);
                holder.lefttime.setVisibility(View.GONE);
                holder.righttime.setVisibility(View.VISIBLE);
                holder.viewRight.setVisibility(View.GONE);
                holder.viewLeft.setVisibility(View.GONE);
                holder.image1.setVisibility(View.GONE);
            }else if(mData.get(position).textorimage==false){
                //false면 이미지
                holder.text.setVisibility(View.GONE);
                holder.layout.setGravity(Gravity.LEFT);
                holder.lefttime.setVisibility(View.GONE);
                holder.righttime.setVisibility(View.VISIBLE);
                holder.viewRight.setVisibility(View.GONE);
                holder.viewLeft.setVisibility(View.GONE);
                Log.d("어댑터인데 몇개냐", mData.get(position).getNum()+"");
                holder.image1.setVisibility(View.VISIBLE);
            }
        }else if(mData.get(position).type == 1){
            if(mData.get(position).textorimage==true){
                holder.text.setBackgroundResource(R.drawable.outbox2);
                holder.layout.setGravity(Gravity.RIGHT);
                holder.lefttime.setVisibility(View.VISIBLE);
                holder.righttime.setVisibility(View.GONE);
                holder.viewRight.setVisibility(View.GONE);
                holder.viewLeft.setVisibility(View.GONE);
                holder.image1.setVisibility(View.GONE);
            }else if(mData.get(position).textorimage==false){
                holder.text.setVisibility(View.GONE);
                holder.layout.setGravity(Gravity.RIGHT);
                holder.lefttime.setVisibility(View.GONE);
                holder.righttime.setVisibility(View.VISIBLE);
                holder.viewRight.setVisibility(View.GONE);
                holder.viewLeft.setVisibility(View.GONE);
                holder.image1.setVisibility(View.VISIBLE);
            }
        }else if(mData.get(position).type == 2){
            holder.text.setBackgroundResource(R.drawable.datebg);
            holder.layout.setGravity(Gravity.CENTER);
            holder.viewRight.setVisibility(View.VISIBLE);
            holder.viewLeft.setVisibility(View.VISIBLE);
        }

//        holder.viewRight.setText(mData.get(position).getText());
//        holder.viewLeft.setText(mData.get(position).getDate());
//        if(mData.get(position).getComp().equals("CJ대한통운")){
//            Log.d("post","in adapter : 회사명받기 성공");
//            holder.post_image.setImageResource(R.drawable.cj);
//        }
    }
}
