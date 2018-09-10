package com.example.user.wimp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

public class MoreDetailPost extends Activity {

    TextView worker,where,level;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.moredetailpost);

        //UI 객체생성
        worker = findViewById(R.id.worker);
        where = findViewById(R.id.where);
        level = findViewById(R.id.level);

        worker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(MoreDetailPost.this,ChatRoom.class);
                i.putExtra("chat","worker");
                startActivity(i);
            }
        });

        //데이터 가져오기
        Intent intent = getIntent();
        String postwhere = intent.getStringExtra("where");
        String postlevel = intent.getStringExtra("level");
        where.setText(postwhere);
        level.setText(postlevel);
    }

    //확인 버튼 클릭
    public void mOnClose(View v){
        //데이터 전달하기
//        Intent intent = new Intent();
//        intent.putExtra("result", "Close Popup");
//        setResult(RESULT_OK, intent);

        //액티비티(팝업) 닫기
        finish();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //바깥레이어 클릭시 안닫히게
        if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        //안드로이드 백버튼 막기
        return;
    }
}
