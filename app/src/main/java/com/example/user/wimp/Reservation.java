package com.example.user.wimp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TabHost;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Reservation extends AppCompatActivity {

    ImageButton imageBtnList,imageBtnChart,imageBtnMypage,imageBtnChat, imageBtnReserve;
    Button reserveBtn;
    TabHost tabHost;
    RecyclerView reservationRv;

    ArrayList<ReservationItem> mItems = new ArrayList<>();
    ArrayList<String> numlist = new ArrayList<>();
    ReservationAdapter adapter;

    String sender, receiver, TAG="예약확인페이지", loginId;

    ServerIP serverIP;
    APIService apiService;

    int j = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reservation);

        imageBtnList = findViewById(R.id.imageBtnList);
        imageBtnChart = findViewById(R.id.imageBtnChart);
        imageBtnMypage = findViewById(R.id.imageBtnMypage);
        imageBtnChat = findViewById(R.id.imageBtnChat);
        imageBtnReserve = findViewById(R.id.imageBtnReserve);
        reserveBtn = findViewById(R.id.reserveBtn);
        tabHost = (TabHost) findViewById(R.id.tabhost1);
        reservationRv = findViewById(R.id.reservationRv);
        tabHost.setup();

        setData();
        setRecyclerView();

        TabHost.TabSpec tab1 = tabHost.newTabSpec("1").setContent(R.id.tab1).setIndicator("택배예약");
        TabHost.TabSpec tab2 = tabHost.newTabSpec("2").setContent(R.id.tab2).setIndicator("예약내역");

        tabHost.addTab(tab1);
        tabHost.addTab(tab2);

        try {
            SharedPreferences preferences = getSharedPreferences("auto", MODE_PRIVATE);

            Set<String> set = preferences.getStringSet("userinfo", null);
            ArrayList<String> loginUser = new ArrayList<>(set);

            Log.d("checkbox", loginUser.get(0).toString());
            String[] loginData = loginUser.get(0).split("@@@@");

            loginId = loginData[0];
            //이름을 텍스트 뷰에 뿌려주자 (9/17 7시 아이디가 넣어져있음 -> 수정해야함)
            GetData getData = new GetData();
            getData.execute(loginId);

        }catch (NullPointerException e){
            e.printStackTrace();
        }

        reserveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Reservation.this, CUReservationEditSender.class);
                startActivity(i);
            }
        });

        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String s) {
                switch (s) {
                    case "1":
                        Log.d("tabtest", "tab 1");
                        break;
                    case "2":
                        Log.d("tabtest", "tab 2");
                        CJTask cjTask = new CJTask();
                        cjTask.execute();
                        break;

                }

            }
        });

//        SharedPreferences preferences = getSharedPreferences("reservation", MODE_PRIVATE);
//        if(preferences.getString("senderInfo", null) != null && preferences.getString("receiverInfo", null) != null){
//            String senderInfo = preferences.getString("senderInfo", null);
//            String receiverInfo = preferences.getString("receiverInfo", null);
//            Log.d("확인 페이지", senderInfo + "/" + receiverInfo);
//
//            String[] senderdata = senderInfo.split("##");
//            String[] receiverdata = receiverInfo.split("##");
//
//            sender = senderdata[0];
//            receiver = receiverdata[0];
//
//            Log.d("shared data", sender+receiver);
//
////            GetData getData = new GetData();
////            getData.execute(sender, receiver);
//        }
//
//        Intent intent = getIntent();
//        if(intent.getStringArrayListExtra("numlist") != null){
//            numlist = intent.getStringArrayListExtra("numlist");
//            for(int i = 0; i < numlist.size(); i++) {
//                Log.d("넘어옴?", numlist.get(i));
//                ReservationItem reservationItem = new ReservationItem(j+"" ,numlist.get(i));
//                mItems.add(reservationItem);
//                adapter = new ReservationAdapter(getApplicationContext(), mItems);
//                j++;
//            }
//            reservationRv.setAdapter(adapter);
//            adapter.notifyDataSetChanged();
//
//            SharedPreferences preferencess = getSharedPreferences("reservationNum", MODE_PRIVATE);
//            SharedPreferences.Editor editor = preferencess.edit();
//            Set<String> set = new HashSet<String>();
//            set.addAll(numlist);
//            editor.putStringSet("reservationNum", set);
//            editor.commit();
//        }else{
//            SharedPreferences preferencess = getSharedPreferences("reservationNum", MODE_PRIVATE);
//            if(preferencess.getStringSet("reservationNum", null) != null){
//                Set<String> set = preferencess.getStringSet("reservationNum", null);
//                numlist = new ArrayList<String>(set);
//                for(int i = 0; i < numlist.size(); i++) {
////                Log.d("넘어옴?", numlist.get(i));
//                    ReservationItem reservationItem = new ReservationItem(j+"" ,numlist.get(i));
//                    mItems.add(reservationItem);
//                    adapter = new ReservationAdapter(getApplicationContext(), mItems);
//                    j++;
//                }
//                reservationRv.setAdapter(adapter);
//                adapter.notifyDataSetChanged();
//            }
//        }

        reservationRv.addOnItemTouchListener(new RecyclerItemClickListner(getApplicationContext(), reservationRv, new RecyclerItemClickListner.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Log.d("main", "click");
                String intentdata = mItems.get(position).getReservationNum();
                Toast.makeText(getApplication(), intentdata, Toast.LENGTH_SHORT).show();
                Intent i = new Intent(Reservation.this, DetailReservation.class);
                i.putExtra("whatdata", intentdata);
                startActivity(i);
            }

            @Override
            public void onItemLongClick(View v, int position) {
                Log.d("main", "long click");
            }
        }
        ));

        imageBtnList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Reservation.this, MainActivity.class);
                startActivity(i);
            }
        });

        imageBtnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Reservation.this, ChatActivity.class);
                startActivity(i);
            }
        });

        imageBtnChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Reservation.this, Chart.class);
                startActivity(i);
            }
        });

        imageBtnMypage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Reservation.this, Mypage.class);
                startActivity(i);
            }
        });

        imageBtnReserve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Reservation.this, Reservation.class);
                startActivity(i);
            }
        });

    }

    private void setRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
//        layoutManager.setReverseLayout(true);
//        layoutManager.setStackFromEnd(true);
        reservationRv.setLayoutManager(layoutManager);
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

    private class CJTask extends AsyncTask<Void, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                Connection.Response response = Jsoup.connect("https://www.cupost.co.kr/postbox/common/login.cupost")
                        .method(Connection.Method.GET)
                        .timeout(5000)
                        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.100 Safari/537.36")
                        .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3")
                        .header("Accept-Encoding", "gzip, deflate, br")
                        .header("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7")
                        .header("Referer","https://www.cupost.co.kr/postbox/main.cupost")
                        .header("Upgrade-Insecure-Requests","1")
                        .header("Connection","keep-alive")
                        .header("Host","www.cupost.co.kr")
                        .execute();

                Map<String, String> loginTryCookie = response.cookies();
                Map<String, String> data = new HashMap<>();
                data.put("member_id", "bung428");
                data.put("member_key","");
                data.put("returnUrl","");

                Connection.Response detailResponse = Jsoup.connect("https://www.cupost.co.kr/postbox/common/logon.cupost")
                        .method(Connection.Method.POST)
                        .timeout(5000)
                        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.99 Safari/537.36")
                        .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3")
                        .header("Accept-Encoding", "gzip, deflate, br")
                        .header("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7")
                        .header("Content-Length", "46")
                        .header("Content-Type", "application/x-www-form-urlencoded")
                        .header("Referer","https://www.cupost.co.kr/postbox/common/login.cupost")
                        .header("Origin","https://www.cupost.co.kr")
                        .header("Upgrade-Insecure-Requests", "1")
                        .header("Connection","keep-alive")
                        .header("Host", "www.cupost.co.kr")
                        .ignoreContentType(true)
                        .cookies(loginTryCookie)
                        .data(data)
                        .execute();

                Document doc = detailResponse.parse();

                Log.d(TAG, "sdfasdfasd"+doc.text());
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    public void reserve () {
        Observable.just("")
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.io())
                .map(new Function<String, Boolean>()
                {
                    @Override
                    public Boolean apply(String s) throws Exception
                    {
                        Connection.Response response = Jsoup.connect("https://www.cupost.co.kr/postbox/common/login.cupost")
                                .method(Connection.Method.GET)
                                .timeout(10000)
                                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.99 Safari/537.36")
                                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3")
                                .header("Accept-Encoding", "gzip, deflate, br")
                                .header("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7")
                                .header("Referer","https://www.cupost.co.kr/postbox/main.cupost")
                                .header("Upgrade-Insecure-Requests","1")
                                .header("Connection","keep-alive")
                                .header("Host","www.cupost.co.kr")
                                .execute();

                        Map<String, String> loginTryCookie = response.cookies();
                        Map<String, String> data = new HashMap<>();
                        data.put("member_id", "bung428");
                        data.put("member_key","");
                        data.put("returnUrl","");

                        Connection.Response detailResponse = Jsoup.connect("https://www.cupost.co.kr/postbox/common/logon.cupost")
                                .method(Connection.Method.POST)
                                .timeout(10000)
                                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.99 Safari/537.36")
                                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3")
                                .header("Accept-Encoding", "gzip, deflate, br")
                                .header("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7")
                                .header("Content-Length", "46")
                                .header("Content-Type", "application/x-www-form-urlencoded")
                                .header("Referer","https://www.cupost.co.kr/postbox/common/login.cupost")
                                .header("Origin","https://www.cupost.co.kr")
                                .header("Upgrade-Insecure-Requests", "1")
                                .header("Connection","keep-alive")
                                .header("Host", "www.cupost.co.kr")
                                .ignoreContentType(true)
                                .cookies(loginTryCookie)
                                .data(data)
                                .execute();

                        Document doc = detailResponse.parse();

                        Log.d(TAG, doc.text());

                        return true;
                    }
                }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>()
                {
                    @Override
                    public void onSubscribe(Disposable d)
                    {
                        Log.d(TAG, "onSubscribe 11111"+d);
                    }
                    @Override
                    public void onNext(Boolean s)
                    {
                        Log.d(TAG, "onNext 2222"+s);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError 3333",e);
                    }
                    @Override
                    public void onComplete()
                    {
                        Log.d(TAG, "onComplete: 4444444");
                    }
                });
    }

    private class GetData extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(Reservation.this,
                    "Please Wait", null, true, true);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
//            results.setText(result);
            Log.d("TAG", "response - " + result);
            Log.d("TAG",result.toString());
            if (result == null){
                Toast.makeText(Reservation.this, "error", Toast.LENGTH_SHORT).show();
            }
            else {
                sender = result.toString();
            }
        }

        @Override
        protected String doInBackground(String... params) {

            String mem_id = params[0];
            String serverURL = serverIP.serverIp+"/wimp/getuserdata.php";
            String postParameters = "id=" + mem_id;

            Log.d("TAG",mem_id);
            Log.d("TAG", postParameters);

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

                Log.d("TAG", "response code - " + responseStatusCode);

                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }

                bufferedReader.close();

                return sb.toString();

            } catch (Exception e) {

                Log.d("TAG", "InsertData: Error ", e);

                return new String("Error: " + e.getMessage());
            }
        }

    }
}
