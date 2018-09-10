package com.example.user.wimp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class PostCompanyList extends AppCompatActivity implements View.OnClickListener{

    Button tvCj,tvHanjin,tvLotte,tvPost,tvLozen,tvDream,tvKGB,tvIlyang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.postcompany);

        tvCj=findViewById(R.id.tvCj);
        tvHanjin=findViewById(R.id.tvHanjin);
//        tvLotte=findViewById(R.id.tvLotte);
//        tvPost=findViewById(R.id.tvPost);
//        tvLozen=findViewById(R.id.tvLozen);
//        tvDream=findViewById(R.id.tvDream);
//        tvKGB=findViewById(R.id.tvKGB);
//        tvIlyang=findViewById(R.id.tvIlyang);

        tvCj.setOnClickListener(this);
        tvHanjin.setOnClickListener(this);
//        tvLotte.setOnClickListener(this);
//        tvPost.setOnClickListener(this);
//        tvLozen.setOnClickListener(this);
//        tvDream.setOnClickListener(this);
//        tvKGB.setOnClickListener(this);
//        tvIlyang.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tvCj :
                Intent i = new Intent(PostCompanyList.this, PostSearch.class);
                i.putExtra("postCompany","CJ대한통운");
                startActivity(i);
                break;
            case R.id.tvHanjin :
                Intent intent = new Intent(PostCompanyList.this, PostSearch.class);
                intent.putExtra("postCompany","한진택배");
                startActivity(intent);
                break;
//            case R.id.tvLotte :
//                Intent i2 = new Intent(PostCompanyList.this, PostSearch.class);
//                i2.putExtra("postCompany","롯데택배");
//                startActivity(i2);
//                break;
//            case R.id.tvPost :
//                Intent i3 = new Intent(PostCompanyList.this, PostSearch.class);
//                i3.putExtra("postCompany","우체국택배");
//                startActivity(i3);
//                break;
//            case R.id.tvLozen :
//                Intent i4 = new Intent(PostCompanyList.this, PostSearch.class);
//                i4.putExtra("postCompany","로젠택배");
//                startActivity(i4);
//                break;
//            case R.id.tvDream :
//                Intent i5 = new Intent(PostCompanyList.this, PostSearch.class);
//                i5.putExtra("postCompany","드림택배");
//                startActivity(i5);
//                break;
//            case R.id.tvKGB :
//                Intent i6 = new Intent(PostCompanyList.this, PostSearch.class);
//                i6.putExtra("postCompany","KGB택배");
//                startActivity(i6);
//                break;
//            case R.id.tvIlyang :
//                Intent i7 = new Intent(PostCompanyList.this, PostSearch.class);
//                i7.putExtra("postCompany","일양택배");
//                startActivity(i7);
//                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        ActionBar actionBar = getSupportActionBar();

        // Custom Actionbar를 사용하기 위해 CustomEnabled을 true 시키고 필요 없는 것은 false 시킨다
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false);            //액션바 아이콘을 업 네비게이션 형태로 표시합니다.
        actionBar.setDisplayShowTitleEnabled(false);        //액션바에 표시되는 제목의 표시유무를 설정합니다.
        actionBar.setDisplayShowHomeEnabled(false);            //홈 아이콘을 숨김처리합니다.


        //layout을 가지고 와서 actionbar에 포팅을 시킵니다.
        LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        View actionbar = inflater.inflate(R.layout.custom_postcompany, null);

        actionBar.setCustomView(actionbar);

        //액션바 양쪽 공백 없애기
        Toolbar parent = (Toolbar)actionbar.getParent();
        parent.setContentInsetsAbsolute(0, 0);

        ImageButton backBtn = findViewById(R.id.backBtn);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(PostCompanyList.this,PostSearch.class);
                finish();
            }
        });

        return true;
    }
}
