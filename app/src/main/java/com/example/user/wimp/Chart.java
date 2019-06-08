package com.example.user.wimp;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Set;

import im.dacer.androidcharts.BarView;

public class Chart extends AppCompatActivity {

    ArrayList<String> loginUser;
    ArrayList<String> list;
    ArrayList<String> strList = new ArrayList<>();
    ArrayList<Integer> dataList = new ArrayList<>();
    ArrayList<RecyclerCategoryItem> mItems = new ArrayList<>();

    CategoryAdapter categoryAdapter;

    ServerIP serverIP;

    TextView tv;
    ImageButton imageBtnList,imageBtnChart,imageBtnMypage,imageBtnChat;
    BarView barView;
    RecyclerView rvCategory;

    String loginId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chart);

        imageBtnList = findViewById(R.id.imageBtnList);
        imageBtnChart = findViewById(R.id.imageBtnChart);
        imageBtnMypage = findViewById(R.id.imageBtnMypage);
        imageBtnChat = findViewById(R.id.imageBtnChat);
        tv = findViewById(R.id.tv);
        barView = findViewById(R.id.bar_view);
        rvCategory = findViewById(R.id.rvCategory);

        setData();
        setRecyclerView();

        Intent i = getIntent();

        try {
            list = (ArrayList<String>) i.getSerializableExtra("listname");
            for(int j = 0; j < list.size(); j++){
                Log.d("블라블라", list.get(j));
            }
        }catch (NullPointerException e){

        }

        try {
            SharedPreferences preferences = getSharedPreferences("auto", Context.MODE_PRIVATE);

            Set<String> set = preferences.getStringSet("userinfo", null);
            loginUser = new ArrayList<>(set);

            Log.d("checkbox", loginUser.get(0).toString());
            String[] loginData = loginUser.get(0).split("@@@@");
            loginId = loginData[0];

            GetData getData = new GetData();
            getData.execute(loginId);

        }catch (NullPointerException e){
            e.printStackTrace();
        }

        imageBtnList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Chart.this, MainActivity.class);
                startActivity(i);
            }
        });

        imageBtnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Chart.this, ChatActivity.class);
                startActivity(i);
            }
        });

        imageBtnChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Chart.this, Chart.class);
                startActivity(i);
            }
        });

        imageBtnMypage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Chart.this, Mypage.class);
                startActivity(i);
            }
        });

        rvCategory.addOnItemTouchListener(new RecyclerItemClickListner(getApplicationContext(), rvCategory, new RecyclerItemClickListner.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Log.d("main", "click");
                String intentdata = mItems.get(position).getCategory();
                Toast.makeText(getApplication(), intentdata, Toast.LENGTH_SHORT).show();
                Intent i = new Intent(Chart.this, DetailCategory.class);
                i.putExtra("intentdata", intentdata);
                startActivity(i);
            }

            @Override
            public void onItemLongClick(View v, int position) {
                Log.d("main", "long click");
            }
        }
        ));

    }

    private void setRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
//        layoutManager.setReverseLayout(true);
//        layoutManager.setStackFromEnd(true);
        rvCategory.setLayoutManager(layoutManager);
//        adapter = new MyRecyclerViewAdapter(getApplicationContext(), mItems);
////        adapter.setClickListener(this);
//        recyclerView.setAdapter(adapter);
    }

    private void setData(){
        mItems.clear();
        // RecyclerView 에 들어갈 데이터를 추가합니다.
//        for(String name : names){
//            mItems.add(new RecyclerItem(name));
//        }
        // 데이터 추가가 완료되었으면 notifyDataSetChanged() 메서드를 호출해 데이터 변경 체크를 실행합니다.
    }

    @Override
    public void onResume() {
        super.onResume();
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
        View actionbar = inflater.inflate(R.layout.custom_actionbar, null);

        actionBar.setCustomView(actionbar);

        //액션바 양쪽 공백 없애기
        Toolbar parent = (Toolbar)actionbar.getParent();
        parent.setContentInsetsAbsolute(0, 0);

        ImageButton functionBtn = findViewById(R.id.functionBtn);

        functionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup= new PopupMenu(getApplicationContext(), v);//v는 클릭된 뷰를 의미

                getMenuInflater().inflate(R.menu.chartmenu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.m1:
                                Toast.makeText(getApplication(),"메뉴1",Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.m2:
                                Toast.makeText(getApplication(),"메뉴2",Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.m3:
                                SharedPreferences preferences = getSharedPreferences("auto", MODE_PRIVATE);
                                SharedPreferences.Editor editor = preferences.edit();

                                Set<String> set = preferences.getStringSet("userinfo", null);
                                loginUser = new ArrayList<>(set);

                                Log.d("shared", "삭제 전"+loginUser.get(0).toString());

                                editor.remove("userinfo");
                                editor.clear();
                                editor.commit();

                                loginUser.clear();
                                if(preferences.getStringSet("userinfo", null)==null){
                                    Log.d("shared", "삭제되었습니다.");
                                }

                                Intent i = new Intent(Chart.this, LoginActivity.class);
                                startActivity(i);
                                break;
                            default:
                                break;
                        }
                        return false;
                    }
                });

                popup.show();//Popup Menu 보이기
            }
        });

        return true;
    }

    private class GetData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(Chart.this,
                    "Please Wait", null, true, true);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
//            results.setText(result);
            Log.d("post", "response - " + result);
            if (result == null) {
                Toast.makeText(Chart.this, "already have", Toast.LENGTH_SHORT).show();
            } else {
                String mJsonString = result.toString();
                if(mJsonString.equals("db don't have data")){
                    Log.d("mall","null");
                }else {
                    try {
                        JSONArray jsonArray = new JSONArray(mJsonString);
                        Log.d("제이슨 어레이사이즈 ", jsonArray.length()+"");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject item = jsonArray.getJSONObject(i);

                            Log.d("아이템 값들 좀 보자", item.toString());

                            String category_count = item.getString("category_count");
                            String category = item.getString("category");
                            Log.d("like값 왔다 임마!!!!!!", category_count + "  " + category);

                            RecyclerCategoryItem recyclerCategoryItem = new RecyclerCategoryItem(category);
                            mItems.add(recyclerCategoryItem);
                            categoryAdapter = new CategoryAdapter(getApplicationContext(), mItems);

                            strList.add(category);
                            dataList.add(Integer.parseInt(category_count));
                        }
                        rvCategory.setAdapter(categoryAdapter);
                        categoryAdapter.notifyDataSetChanged();

                        barView.setBottomTextList(strList);
                        barView.setDataList(dataList,10);
                    } catch (JSONException e) {
                        Log.d("aaa", "showResult : ", e);
                    } catch (NullPointerException e) {

                    }
                }

            }
        }

        @Override
        protected String doInBackground(String... params) {
            String id = params[0];
            String serverURL = serverIP.serverIp+"/wimp/category_classifier.php";
            String postParameters = "id=" + id;

            Log.d("post", postParameters);
            try {
                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();

                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();

                int responseStatusCode = httpURLConnection.getResponseCode();

                Log.d("post", "response code - " + responseStatusCode);

                InputStream inputStream;
                if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                } else {
                    inputStream = httpURLConnection.getErrorStream();
                }

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }

                bufferedReader.close();

                Log.d("post", sb.toString());

                return sb.toString().trim();

            } catch (IOException e) {
                return null;
            }
        }
    }
}
