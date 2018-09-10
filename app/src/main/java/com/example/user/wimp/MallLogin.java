package com.example.user.wimp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.ArrayList;

public class MallLogin extends AppCompatActivity {

    ImageView mall_logo;
    Button mall_btn;
    EditText mall_id,mall_pw;
    CheckBox mall_auto;

    String mall,message,id,pwd;
    String[] mall_data,user;
    Boolean check=false,value;

    ArrayList<String> mall_user=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mall_login);

        mall_btn=findViewById(R.id.mall_btn);
        mall_logo=findViewById(R.id.mall_logo);
        mall_id=findViewById(R.id.mall_id);
        mall_pw=findViewById(R.id.mall_pw);
        mall_auto=findViewById(R.id.mall_auto);

        Intent intent=getIntent();
        if(intent!=null){
            mall=intent.getStringExtra("mall");
            switch (mall){
                case "pang" :
                    mall_logo.setImageResource(R.drawable.pang);
                    break;
                case "gmarket" :
                    mall_logo.setImageResource(R.drawable.gmarket);
                    break;
            }
        }

        mall_auto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mall_auto.isChecked()==true) {
                    //자동 로그인 이용
                    check = true;
                    Log.d("checkbox", "true");
                }else {
                    //자동 로그인 비 이용
                    check = false;
                    Log.d("checkbox", "false");
                }
            }
        });

        SharedPreferences preferences=getSharedPreferences("mall",MODE_PRIVATE);

        if (preferences.getString(mall,null)==null) {
            mall_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    id = mall_id.getText().toString();
                    pwd = mall_pw.getText().toString();

                    Intent i = new Intent(MallLogin.this, MallCrawl.class);
                    i.putExtra("mall", mall);
                    i.putExtra("id", id);
                    i.putExtra("pwd", pwd);
                    startActivity(i);
                }
            });
        }else {
            Intent i = new Intent(MallLogin.this, MallCrawl.class);
            startActivity(i);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences preferences = getSharedPreferences("mall",MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        switch (mall){
            case "pang":
                if (preferences.getString(mall,null)==null){
                    id=mall_id.getText().toString();
                    pwd=mall_pw.getText().toString();
                    String idpw=id+"##"+pwd;
                    if(!check){
                        Log.d("mall","no auto login");
                    }else{
                        editor.putString(mall,idpw);
                        editor.commit();
                        Log.d("mall","check saving pang id, pw=>"+preferences.getString(mall,null));
                    }
                }
                break;
            case "gmarket":
                if (preferences.getString(mall,null)==null){
                    id=mall_id.getText().toString();
                    pwd=mall_pw.getText().toString();
                    String idpw=id+"##"+pwd;
                    if(!check){
                        Log.d("mall","no auto login");
                    }else{
                        editor.putString(mall,idpw);
                        editor.commit();
                        Log.d("mall","check saving gmarket id, pw=>"+preferences.getString(mall,null));
                    }
                }
                break;
        }
    }
}
