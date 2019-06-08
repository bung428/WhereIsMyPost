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
import java.util.HashSet;
import java.util.Set;

public class Reservation extends AppCompatActivity {

    ImageButton imageBtnList,imageBtnChart,imageBtnMypage,imageBtnChat, imageBtnReserve;
    Button reserveBtn;
    TabHost tabHost;
    RecyclerView reservationRv;

    ArrayList<ReservationItem> mItems = new ArrayList<>();
    ArrayList<String> numlist = new ArrayList<>();
    ReservationAdapter adapter;

    String sender, receiver;

    ServerIP serverIP;

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
                        break;

                }

            }
        });

        SharedPreferences preferences = getSharedPreferences("reservation", MODE_PRIVATE);
        if(preferences.getString("senderInfo", null) != null && preferences.getString("receiverInfo", null) != null){
            String senderInfo = preferences.getString("senderInfo", null);
            String receiverInfo = preferences.getString("receiverInfo", null);
            Log.d("확인 페이지", senderInfo + "/" + receiverInfo);

            String[] senderdata = senderInfo.split("##");
            String[] receiverdata = receiverInfo.split("##");

            sender = senderdata[0];
            receiver = receiverdata[0];

            Log.d("shared data", sender+receiver);

//            GetData getData = new GetData();
//            getData.execute(sender, receiver);
        }

        Intent intent = getIntent();
        if(intent.getStringArrayListExtra("numlist") != null){
            numlist = intent.getStringArrayListExtra("numlist");
            for(int i = 0; i < numlist.size(); i++) {
//                Log.d("넘어옴?", numlist.get(i));
                ReservationItem reservationItem = new ReservationItem(j+"" ,numlist.get(i));
                mItems.add(reservationItem);
                adapter = new ReservationAdapter(getApplicationContext(), mItems);
                j++;
            }
            reservationRv.setAdapter(adapter);
            adapter.notifyDataSetChanged();

            SharedPreferences preferencess = getSharedPreferences("reservationNum", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferencess.edit();
            Set<String> set = new HashSet<String>();
            set.addAll(numlist);
            editor.putStringSet("reservationNum", set);
            editor.commit();
        }else{
            SharedPreferences preferencess = getSharedPreferences("reservationNum", MODE_PRIVATE);
            if(preferencess.getStringSet("reservationNum", null) != null){
                Set<String> set = preferencess.getStringSet("reservationNum", null);
                numlist = new ArrayList<String>(set);
                for(int i = 0; i < numlist.size(); i++) {
//                Log.d("넘어옴?", numlist.get(i));
                    ReservationItem reservationItem = new ReservationItem(j+"" ,numlist.get(i));
                    mItems.add(reservationItem);
                    adapter = new ReservationAdapter(getApplicationContext(), mItems);
                    j++;
                }
                reservationRv.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        }

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
            Log.d("post", "response - " + result);
            if (result == null) {
                Toast.makeText(Reservation.this, "already have", Toast.LENGTH_SHORT).show();
            } else {
                String mJsonString = result.toString();
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

                            String r_num = item.getString("reser_id");
                            String r_reservnum = item.getString("reser_reservation");

                            ReservationItem reservationItem = new ReservationItem(r_num,r_reservnum);
                            mItems.add(reservationItem);
                            adapter = new ReservationAdapter(getApplicationContext(), mItems);
                            adapter.notifyDataSetChanged();
                        }
                        reservationRv.setAdapter(adapter);
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
            String sender = params[0];
            String receiver = params[1];

            String serverURL = serverIP.serverIp+"/wimp/getreservation.php";
            String postParameters = "sender=" + sender + "&receiver=" + receiver;

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
