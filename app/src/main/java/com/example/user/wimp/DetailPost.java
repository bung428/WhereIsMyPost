package com.example.user.wimp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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

public class DetailPost extends AppCompatActivity {

    TextView post_info,post_Date,companynumber,receiver,sender,whereget;
    RecyclerView rvPost;

    ArrayList<String> postInfo;

    ServerIP serverIP;

    private ArrayList<RecyclerItemPostInfo> mItems = new ArrayList<>();
    PostRecyclerViewAdapter adapter;

    String mJsonString,infor,dates,comp_num,recvs,sends,wheres,TAG="디테일포스트";
    String[] datasep;
    String loginID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detailpost);

        post_info=findViewById(R.id.post_info);
        post_Date=findViewById(R.id.post_Date);
        companynumber=findViewById(R.id.companynumber);
        receiver=findViewById(R.id.receiver);
        sender=findViewById(R.id.sender);
        whereget=findViewById(R.id.whereget);
        rvPost=findViewById(R.id.rvPost);

        try {
            SharedPreferences preferences = getSharedPreferences("auto", Context.MODE_PRIVATE);

            Set<String> set = preferences.getStringSet("userinfo", null);
            ArrayList<String> loginUser = new ArrayList<>(set);

            Log.d("checkbox", loginUser.get(0).toString());
            String[] loginData = loginUser.get(0).split("@@@@");
            loginID = loginData[0];
        }catch (NullPointerException e){
            e.printStackTrace();
        }

        setData();
        setRecyclerView();

        Intent i=getIntent();
        if(i!=null) {
            String data=i.getStringExtra("whatdata");
            Log.d(TAG, "리싸이클러뷰 왓데이타 값 : "+ data);
            datasep=data.split("##");
            switch (datasep[2]){
                case "pang":
                    break;
                case "gmarket":
                    Log.d(TAG,"in detail Activity "+datasep[0]+datasep[1]+" get");
                    GetMallData  getMallData=new GetMallData();
                    getMallData.execute(datasep[1],datasep[0]);
                    break;
                case "CJ대한통운":
                    CJTask cjTask = new CJTask();
                    cjTask.execute(loginID, datasep[0], datasep[2]);
                    // 여기를 고쳐하는구만!
//                    try {
//                        SharedPreferences preferencess = getSharedPreferences("post",MODE_PRIVATE);
//                        Set<String> sets = preferencess.getStringSet("postInfo", null);
//                        postInfo = new ArrayList<>(sets);
//
//                        Log.d("post", "in main"+postInfo.get(0).toString());
//                        String[] Data = postInfo.get(0).split("##");
//                        Log.d("post","in main"+Data[0]+Data[1]);
//                        if(Data[1].equals("CJ대한통운")) {
//                            cjTask = new CJTask();
//                            cjTask.execute(Data[0], Data[1]);
//                        }
//                    }catch (NullPointerException e){
//
//                    }
                    break;
                case "한진택배":
                    break;
            }
        }

        final GestureDetector gestureDetector = new GestureDetector(DetailPost.this,new GestureDetector.SimpleOnGestureListener()
        {
            @Override
            public boolean onSingleTapUp(MotionEvent e)
            {
                return true;
            }
        });

        rvPost.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                Log.d("post", "onInterceptTouchEvent");
                View child = rv.findChildViewUnder(e.getX(), e.getY());
                if (child != null && gestureDetector.onTouchEvent(e)) {
                /*Log.d(TAG, "getChildAdapterPosition=>" + rv.getChildAdapterPosition(child));
                Log.d(TAG,"getChildLayoutPosition=>"+rv.getChildLayoutPosition(child));
                Log.d(TAG,"getChildViewHolder=>" + rv.getChildViewHolder(child));*/
                    //Toast.makeText(getApplication(), count.get(rv.getChildAdapterPosition(child)).toString(), Toast.LENGTH_SHORT).show();
                    Log.d("post", "AdapterPosition=>" + rv.findViewHolderForAdapterPosition(rv.getChildLayoutPosition(child)));
                    Log.d("post", "LayoutPosition=>" + rv.findViewHolderForLayoutPosition(rv.getChildLayoutPosition(child)));
                    Log.d("post", "getChildViewHolder=>" + rv.getChildViewHolder(child).itemView);
                    //TextView tv = (TextView) rv.findViewHolderForAdapterPosition(rv.getChildLayoutPosition(child)).itemView.findViewById(R.id.tv);
                    //TextView tv = (TextView) rv.findViewHolderForLayoutPosition(rv.getChildLayoutPosition(child)).itemView.findViewById(R.id.tv);
                    TextView post_where = (TextView) rv.getChildViewHolder(child).itemView.findViewById(R.id.post_where);
                    TextView post_level = (TextView) rv.getChildViewHolder(child).itemView.findViewById(R.id.post_level);
                    Toast.makeText(getApplication(), post_where.getText().toString()+post_level.getText().toString(), Toast.LENGTH_SHORT).show();
                    Intent i=new Intent(DetailPost.this,MoreDetailPost.class);
                    i.putExtra("where", post_where.getText().toString());
                    i.putExtra("level", post_level.getText().toString());
                    startActivity(i);
                }

                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });
    }

    private class GetMallData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(DetailPost.this,
                    "Please Wait", null, true, true);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
//            results.setText(result);
            Log.d("post", "response - " + result);
            if (result == null) {
                Toast.makeText(DetailPost.this, "already have", Toast.LENGTH_SHORT).show();
            } else {
                mJsonString = result.toString();
                Log.d("post", "json"+mJsonString);
                if(mJsonString.equals("db don't have data")){
                    Log.d("mall","null");
                }else {
                    try {
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

                            if(g_date.equals("null")){
                                g_date="";
                            }
                            infor=g_info;
                            dates=g_date;
                            comp_num = datasep[1]+" / ";
                            recvs="김*";
                            sends="";

                            Log.d("crawl", g_info + g_cnt + g_price + g_level+g_date);
                            RecyclerItemPostInfo recyclerItemPostInfo = new RecyclerItemPostInfo(g_date, "", g_level);
                            mItems.add(recyclerItemPostInfo);
                            adapter = new PostRecyclerViewAdapter(getApplicationContext(), mItems);
                            adapter.notifyDataSetChanged();
                        }
                        Log.d("crawl", "in detail "+infor+dates+comp_num+recvs+sends);

                        if(!infor.equals("")) {
                            post_info.setText(infor);
                            post_Date.setText(dates);
                            companynumber.setText(comp_num);
                            receiver.setText(recvs);
                            sender.setText(sends);
                            whereget.setText("My home");
                        }

                        rvPost.setAdapter(adapter);
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
            String mallpostname = params[1];
            String serverURL = serverIP.serverIp+"/wimp/mallcrawl.php";
            String postParameters = "mall=" + mallname + "&mallpostname=" + mallpostname;

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

            progressDialog = ProgressDialog.show(DetailPost.this,
                    "Please Wait", null, true, true);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
//            results.setText(result);
            Log.d("post", "response - " + result);
            adapter = new PostRecyclerViewAdapter(getApplicationContext(), mItems);
            if (result == null) {
                Toast.makeText(DetailPost.this, "already have", Toast.LENGTH_SHORT).show();
            } else {
                mJsonString = result.toString();
                Log.d(TAG, "서버로부터 응답"+mJsonString);
                try {
                    Gson gsonObject = new Gson();
                    ArrayList<DetailObject> arrayList = gsonObject.fromJson(mJsonString, new TypeToken<ArrayList<DetailObject>>(){}.getType());

                    for (int i=0; i<arrayList.size(); i++) {
                        Log.d(TAG, "arrayList: "+arrayList.get(i).getP_info() + arrayList.get(i).getP_date());
                    }
//
                    infor=arrayList.get(0).getP_info();
                    dates=arrayList.get(0).getP_date();
                    comp_num = arrayList.get(0).getP_comp()+" / "+arrayList.get(0).getP_num();
                    recvs=arrayList.get(0).getP_recv();
                    sends=arrayList.get(0).getP_send();
                    wheres=arrayList.get(0).getP_where();

                    RecyclerItemPostInfo recyclerItemPostInfo;
                    for (int i=0; i<arrayList.size(); i++) {
                        recyclerItemPostInfo = new RecyclerItemPostInfo(arrayList.get(i).getP_date(), arrayList.get(i).getP_where(), arrayList.get(i).getP_level());
                        mItems.add(recyclerItemPostInfo);
                    }
                    adapter = new PostRecyclerViewAdapter(getApplicationContext(), mItems);
                    adapter.notifyDataSetChanged();

                    if(!infor.equals("")) {
                        post_info.setText(infor);
                        post_Date.setText(dates);
                        companynumber.setText(comp_num.toString());
                        receiver.setText(recvs.toString());
                        sender.setText(sends.toString());
                        whereget.setText(wheres.toString());
                    }

                    rvPost.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                } catch (NullPointerException e) {

                }

            }
        }

        @Override
        protected String doInBackground(String... params) {
            String pi_userid = params[0];
            String pi_num = params[1];
            String pi_comp = params[2];
            String serverURL = serverIP.serverIp+"/wimp/cjcrawldetail.php";
            String postParameters = "userid=" + pi_userid + "&num=" + pi_num + "&company=" + pi_comp;

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

    private void setRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        rvPost.setLayoutManager(layoutManager);
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
}