package com.example.user.wimp;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> loginUser;
    ArrayList<String> postInfo;
    ArrayList<String> list_name = new ArrayList<>();
    private ArrayList<RecyclerItem> mItems = new ArrayList<>();
    MyRecyclerViewAdapter adapter;

    ImageButton imageBtnList,imageBtnChart,imageBtnMypage,imageBtnChat, imageBtnReserve;
    RecyclerView recyclerView;

    String msg, kakaoname, postLogin,mJsonString,day,getdate,mall,app_mall, loginId;
    String[] appmall={"pang","gmarket"};
    Boolean crawl=false;

    Messenger mServiceMessenger = null;
    boolean isService=false;
    ServiceConnection conn = new ServiceConnection() {
        public void onServiceConnected(ComponentName name,
                                       IBinder service) {
// 서비스와 연결되었을 때 호출되는 메서드
// 서비스 객체를 전역변수로 저장
            mServiceMessenger = new Messenger(service);
            try {
                Message msg = Message.obtain(null, SocketService.MSG_REGISTER_CLIENT);
//                msg.replyTo = mMessenger;
                mServiceMessenger.send(msg);
            }
            catch (RemoteException e) {
            }
        }
        public void onServiceDisconnected(ComponentName name) {
// 서비스와 연결이 끊겼을 때 호출되는 메서드
            isService = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        testTv=findViewById(R.id.test);
        imageBtnList = findViewById(R.id.imageBtnList);
        imageBtnChart = findViewById(R.id.imageBtnChart);
        imageBtnMypage = findViewById(R.id.imageBtnMypage);
        imageBtnChat = findViewById(R.id.imageBtnChat);
        imageBtnReserve = findViewById(R.id.imageBtnReserve);
        recyclerView = findViewById(R.id.rvAnimals);

        setData();
        setRecyclerView();
//        setStartService();

        imageBtnList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, MainActivity.class);
                startActivity(i);
            }
        });

        imageBtnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, ChatActivity.class);
                startActivity(i);
            }
        });

        imageBtnChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, Chart.class);
                if(list_name.size() > 0){
                    i.putExtra("listname",list_name);
                }
                startActivity(i);
            }
        });

        imageBtnMypage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, Mypage.class);
                startActivity(i);
            }
        });

        imageBtnReserve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, Reservation.class);
                startActivity(i);
            }
        });

        try {
            SharedPreferences preferences = getSharedPreferences("auto", MODE_PRIVATE);

            Set<String> set = preferences.getStringSet("userinfo", null);
            loginUser = new ArrayList<>(set);

            Log.d("checkbox", loginUser.get(0).toString());
            String[] loginData = loginUser.get(0).split("@@@@");

            loginId = loginData[0];
        }catch (NullPointerException e){
            e.printStackTrace();
        }

        if(!loginId.equals("worker")) {
            recyclerView.addOnItemTouchListener(new RecyclerItemClickListner(getApplicationContext(), recyclerView, new RecyclerItemClickListner.OnItemClickListener() {
                @Override
                public void onItemClick(View v, int position) {
                    Log.d("main", "click");
                    String intentdata = mItems.get(position).getInfo() + "##" + mItems.get(position).getComp();
                    Toast.makeText(getApplication(), intentdata, Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(MainActivity.this, DetailPost.class);
                    i.putExtra("whatdata", intentdata);
                    startActivity(i);
                }

                @Override
                public void onItemLongClick(View v, int position) {
                    Log.d("main", "long click");
                }
            }
            ));

            try {
                SharedPreferences preferencess = getSharedPreferences("post", MODE_PRIVATE);
                Set<String> sets = preferencess.getStringSet("postInfo", null);
                postInfo = new ArrayList<>(sets);

                Log.d("post", "in mainin main" + postInfo.get(0).toString());
                String[] Data = postInfo.get(0).split("##");
                Log.d("post", "in main" + Data[0] + Data[1]);
                if (Data[1].equals("CJ대한통운")) {
                    CJTask cjTask = new CJTask();
                    cjTask.execute(Data[0], Data[1]);
                }
            } catch (NullPointerException e) {

            }

            SharedPreferences preferencesss = getSharedPreferences("mall", MODE_PRIVATE);
            for (int i = 0; i < appmall.length; i++) {
                if (preferencesss.getString(appmall[i], null) != null) {
                    app_mall = appmall[i];
                    Log.d("메인이다!!", appmall[i]);
                    GetData getData = new GetData();
                    getData.execute(appmall[i], loginId);
                }
            }

            try {
                Intent i = getIntent();
                msg = i.getStringExtra("login message");
                kakaoname = i.getStringExtra("username");

                mall = i.getStringExtra("mall");
                crawl = i.getBooleanExtra("crawl", false);
                Log.d("crawl", mall + crawl);
//            if(!mall.equals("")&&crawl!=false){
//                GetData getData=new GetData();
//                getData.execute(mall);
//            }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            Intent intent = getIntent();

            if (intent != null) {
                String goods = intent.getStringExtra("goods");
                String num = intent.getStringExtra("num");
                String msg = intent.getStringExtra("msg");
                String company = intent.getStringExtra("company");

//            String json=intent.getStringExtra("json");
////            Log.d("aaa","main"+json);
//            try {
//                JSONArray jsonArray = new JSONArray(json);
//
//                for(int i=0;i<jsonArray.length();i++){
//                    JSONObject item = jsonArray.getJSONObject(i);
//
//                    String pi_num = item.getString("num");
//                    String pi_send = item.getString("send");
//                    String pi_recv = item.getString("recv");
//                    String pi_info = item.getString("info");
//
//                    RecyclerItem recyclerItem = new RecyclerItem(pi_num,pi_send,pi_recv,pi_info);
//
//                    mItems.add(recyclerItem);
//                    adapter = new MyRecyclerViewAdapter(getApplicationContext(), mItems);
//                }
//                recyclerView.setAdapter(adapter);
//            } catch (JSONException e) {
//                Log.d("aaa", "showResult : ", e);
//            } catch (NullPointerException e){
//
//            }
//            Log.d("upload","in main 상품명"+goods+"송장번호"+num+"메시지"+msg+"회사명"+company);
                //조회할 택배에 정보들을 저장해야한다
                //저장전에 해당 택배사 사이트에서 송장번호로 조회가 가능해지는지 테스트한다
                //조회가된다면 파싱해온다
                //파싱해온정보를 log로 찍어본다
                //async로 송장번호를 파라미터로 하여 보내자
                //현재는 시연용으로 cj꺼만 넘기는걸로 처리해논상태다!
            }
        }else if(loginId.equals("worker")){
            Toast.makeText(MainActivity.this, "워커 로그인", Toast.LENGTH_SHORT).show();
            //워커 로그인일땐 메인화면 목록에 회원 정보들 볼수있게 처리하자
            //그래야 수령장소 확인이 쉽다.
        }
    }

    private void setRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
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

    private void setStartService() {
        startService(new Intent(MainActivity.this, SocketService.class));
        bindService(new Intent(this, SocketService.class), conn, Context.BIND_AUTO_CREATE);
        isService = true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        setStartService();

        try {
            SharedPreferences preferences = getSharedPreferences("auto", MODE_PRIVATE);

            Set<String> set = preferences.getStringSet("userinfo", null);
            loginUser = new ArrayList<>(set);

            Log.d("checkbox", loginUser.get(0).toString());
            String[] loginData = loginUser.get(0).split("@@@@");
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(conn);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        unbindService(conn);
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
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

                getMenuInflater().inflate(R.menu.postmenu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.m1:
//                                Toast.makeText(getApplication(),"쇼핑몰 추가",Toast.LENGTH_SHORT).show();
                                Intent intent=new Intent(MainActivity.this,Mall.class);
                                startActivity(intent);
                                break;
                            case R.id.m2:
//                                Toast.makeText(getApplication(),"운송장번호 검색",Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(MainActivity.this, PostSearch.class);
                                startActivity(i);
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

                                Intent intent1 = new Intent(MainActivity.this, LoginActivity.class);
                                startActivity(intent1);
                                break;
                            case R.id.m4:
//                                Toast.makeText(getApplication(),"쇼핑몰 추가",Toast.LENGTH_SHORT).show();
                                Intent intents=new Intent(MainActivity.this,UnityPlayerActivity.class);
                                startActivity(intents);
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

            progressDialog = ProgressDialog.show(MainActivity.this,
                    "Please Wait", null, true, true);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
//            results.setText(result);
//            mItems.clear();
            Log.d("post", "response - " + result);
            if (result == null) {
                Toast.makeText(MainActivity.this, "already have", Toast.LENGTH_SHORT).show();
            } else {
                mJsonString = result.toString();
                Log.d("post", "json"+mJsonString);
                if(mJsonString.equals("db don't have data")){
                    Log.d("mall","null");
                }else {
                    try {
                        Log.d("메인 겟데이타!!!","");
                        JSONArray jsonArray = new JSONArray(mJsonString);
//                    String[] pi_date = new String[jsonArray.length()];
//                    String[] pi_level = new String[jsonArray.length()];
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject item = jsonArray.getJSONObject(i);

                            String g_info = item.getString("info");
                            String g_cnt = item.getString("cnt");
                            String g_price = item.getString("price");
                            String g_level = item.getString("level");
                            String g_date = item.getString("date");

                            list_name.add(g_info);

                            if(g_date.equals("null")){
                                g_date="";
                                day="";
                            }else{
                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                                Date date = null;
                                try {
                                    date = dateFormat.parse(g_date);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                Calendar calendar = Calendar.getInstance();
                                calendar.setTime(date);
                                int dayNum = calendar.get(Calendar.DAY_OF_WEEK);
                                switch (dayNum) {
                                    case 1:
                                        day = "Sun";
                                        break;
                                    case 2:
                                        day = "Mon";
                                        break;
                                    case 3:
                                        day = "Tue";
                                        break;
                                    case 4:
                                        day = "Wed";
                                        break;
                                    case 5:
                                        day = "Thu";
                                        break;
                                    case 6:
                                        day = "Fri";
                                        break;
                                    case 7:
                                        day = "Sat";
                                        break;

                                }
                                Log.d("crawl", "day:" + day);
                            }
                            Log.d("몰 크롤링 데이터", g_info + g_cnt + g_price + g_level+g_date);
                            RecyclerItem recyclerItem = new RecyclerItem(g_level,g_date,app_mall,g_info,day);
                            mItems.add(recyclerItem);
                            adapter = new MyRecyclerViewAdapter(getApplicationContext(), mItems);
                            adapter.notifyDataSetChanged();
                        }
                        recyclerView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        Log.d("aaa", "showResult : ", e);
                    } catch (NullPointerException e) {

                    }
                }

            }
        }

        @Override
        protected String doInBackground(String... params) {
            String mallname = params[0];
            String id = params[1];
            String serverURL = "http://115.71.232.235/wimp/mallcrawl.php";
            String postParameters = "mall=" + mallname + "&id=" + id;

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

    private class CJTask extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(MainActivity.this,
                    "Please Wait", null, true, true);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
//            results.setText(result);
            Log.d("post", "response - " + result);
            adapter = new MyRecyclerViewAdapter(getApplicationContext(), mItems);
            if (result == null) {
                Toast.makeText(MainActivity.this, "already have", Toast.LENGTH_SHORT).show();
            } else {
                mJsonString = result.toString();
                Log.d("post", "json"+mJsonString);

                try {
                    JSONArray jsonArray = new JSONArray(mJsonString);
//                    String[] pi_date = new String[jsonArray.length()];
//                    String[] pi_level = new String[jsonArray.length()];
                    for (int i = jsonArray.length()-1; i < jsonArray.length(); i++) {
                        JSONObject item = jsonArray.getJSONObject(i);

                        String pi_num = item.getString("num");
                        String pi_send = item.getString("send");
                        String pi_recv = item.getString("recv");
                        String pi_info = item.getString("info");
                        String pi_level = item.getString("level");
                        String pi_date = item.getString("date");
                        String pi_where = item.getString("where");
                        String pi_comp = item.getString("comp");

                        list_name.add(pi_info);

                        getdate = pi_date;
                        String[] splitdate = getdate.split(" ");
                        Log.d("post", "splitdate : " + splitdate[0]);
                        if (!getdate.equals("")) {
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                            Date date = null;
                            try {
                                date = dateFormat.parse(splitdate[0]);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(date);
                            int dayNum = calendar.get(Calendar.DAY_OF_WEEK);
                            switch (dayNum) {
                                case 1:
                                    day = "Sun";
                                    break;
                                case 2:
                                    day = "Mon";
                                    break;
                                case 3:
                                    day = "Tue";
                                    break;
                                case 4:
                                    day = "Wed";
                                    break;
                                case 5:
                                    day = "Thu";
                                    break;
                                case 6:
                                    day = "Fri";
                                    break;
                                case 7:
                                    day = "Sat";
                                    break;

                            }
                            Log.d("post", "day:" + day);
                            Log.d("송장번호를 통해 얻음", pi_level+pi_date+pi_comp+pi_info);
                            RecyclerItem recyclerItem = new RecyclerItem(pi_level, pi_date, pi_comp, pi_info, day);
                            mItems.add(recyclerItem);
                            adapter = new MyRecyclerViewAdapter(getApplicationContext(), mItems);
                            adapter.notifyDataSetChanged();
                        }
                        recyclerView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    Log.d("aaa", "showResult : ", e);
                } catch (NullPointerException e) {

                }

            }
        }

        @Override
        protected String doInBackground(String... params) {
            String pi_num = params[0];
            String pi_comp = params[1];
            String serverURL = "http://115.71.232.235/wimp/cjcrawl.php";
            String postParameters = "num=" + pi_num + "&company=" + pi_comp;

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
