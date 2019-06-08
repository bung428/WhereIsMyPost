package com.example.user.wimp;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Set;

public class ChatActivity extends AppCompatActivity {

    String loginId,loginName,getjson,worker,broad_sender,broad_receiver,broad_roomname,broad_message,broad_date,resultmessage;

    ArrayList<String> loginUser;
    ArrayList<RecyclerChatItem> mItems=new ArrayList<>();
    RecyclerView chatlist;
    ChatRecyclerViewAdapter adapter;

    ServerIP serverIP;

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

    Button chat,chat1;
    ImageButton imageBtnList,imageBtnChart,imageBtnMypage,imageBtnChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat);

        chatlist=findViewById(R.id.chatlist);
        chat=findViewById(R.id.button);
        chat1=findViewById(R.id.button1);
        imageBtnList = findViewById(R.id.imageBtnList);
        imageBtnChart = findViewById(R.id.imageBtnChart);
        imageBtnMypage = findViewById(R.id.imageBtnMypage);
        imageBtnChat = findViewById(R.id.imageBtnChat);

        setData();
        setRecyclerView();
        setStartService();

//        Intent i=getIntent();
//        try {
//            worker=i.getStringExtra("worker");
//            CheckWokerData checkWokerData=new CheckWokerData();
//            checkWokerData.execute(worker);
//        }catch (NullPointerException e){
//
//        }

        try {
            SharedPreferences preferences = getSharedPreferences("auto", MODE_PRIVATE);

            Set<String> set = preferences.getStringSet("userinfo", null);
            loginUser = new ArrayList<>(set);

            String[] loginData = loginUser.get(0).split("@@@@");
            Log.d("chat",loginData[0]+ " " + loginData[1]);
            loginId=loginData[0];

            if(loginId=="worker"){
                CheckWokerData checkWokerData=new CheckWokerData();
                checkWokerData.execute(worker);
            }else{
                CheckData checkData=new CheckData();
                checkData.execute(loginId);
            }

        }catch (NullPointerException e){
            e.printStackTrace();
        }

        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ChatActivity.this,ChatRoom.class);
                if(loginId.equals("goo")) {
                    i.putExtra("chat", "worker");
                }else if(loginId.equals("hong")){
                    i.putExtra("chat", "worker");
                }else if(loginId.equals("worker")){
                    i.putExtra("chat","goo");
                }
                startActivity(i);
            }
        });
        chat1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ChatActivity.this,ChatRoom.class);
                if(loginId.equals("goo")) {
                    i.putExtra("chat", "worker");
                }else if(loginId.equals("hong")){
                    i.putExtra("chat", "worker");
                }else if(loginId.equals("worker")){
                    i.putExtra("chat","hong");
                }
                startActivity(i);
            }
        });

        chatlist.addOnItemTouchListener(new RecyclerItemClickListner(getApplicationContext(), chatlist, new RecyclerItemClickListner.OnItemClickListener() {
            @Override public void onItemClick(View v, int position) {
                Log.d("main", "click");

                String chatting_room = mItems.get(position).getName()+"##"+mItems.get(position).getText();
//                String intentdata = mItems.get(position).getInfo()+"##"+mItems.get(position).getComp();
                mItems.get(position).setImage(R.drawable.msg_read);
                Toast.makeText(getApplication(), chatting_room, Toast.LENGTH_SHORT).show();
                Intent i=new Intent(ChatActivity.this,ChatRoom.class);
                i.putExtra("chattingdata",chatting_room);
                startActivity(i);
            }
            @Override public void onItemLongClick(View v, int position) {
                Log.d("main", "long click");
            }
        }
        ));

        imageBtnList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ChatActivity.this, MainActivity.class);
                startActivity(i);
            }
        });

        imageBtnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ChatActivity.this, ChatActivity.class);
                startActivity(i);
            }
        });

        imageBtnChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ChatActivity.this, Chart.class);
                startActivity(i);
            }
        });

        imageBtnMypage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ChatActivity.this, Mypage.class);
                startActivity(i);
            }
        });

        GetData getData = new GetData();
        getData.execute("");

    }

    private void setRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        chatlist.setLayoutManager(layoutManager);
        adapter = new ChatRecyclerViewAdapter(getApplicationContext(), mItems);
        chatlist.setAdapter(adapter);
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
        startService(new Intent(ChatActivity.this, SocketService.class));
        bindService(new Intent(this, SocketService.class), conn, Context.BIND_AUTO_CREATE);
        isService = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("lasttext"));
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            broad_sender = intent.getStringExtra("sender");
            broad_receiver = intent.getStringExtra("receiver");
            broad_roomname = intent.getStringExtra("roomname");
            broad_message = intent.getStringExtra("message");
            broad_date = intent.getStringExtra("date");

            Log.d("broadcast get? ",broad_sender+broad_receiver+broad_roomname+broad_message+broad_date);

            InsertData insertData = new InsertData();
            if(broad_message.contains(".jpg") || broad_message.contains(".JPG") || broad_message.contains(".png") || broad_message.contains(".PNG")){
                insertData.execute(broad_roomname,broad_sender,broad_receiver,broad_date,"사진");
            }else {
                insertData.execute(broad_roomname,broad_sender,broad_receiver,broad_date,broad_message);
            }

            if(mItems.size() == 0){
                if(loginId.equals(broad_sender)){
                    if(broad_message.contains(".jpg") || broad_message.contains(".JPG") || broad_message.contains(".png") || broad_message.contains(".PNG")){
                        RecyclerChatItem recyclerChatItem = new RecyclerChatItem(broad_receiver,"사진",broad_date);
                        mItems.add(recyclerChatItem);
//                        chatlist.setAdapter(adapter);
                        adapter = new ChatRecyclerViewAdapter(getApplicationContext(), mItems);
                        chatlist.setAdapter(adapter);
                    }else {
                        RecyclerChatItem recyclerChatItem = new RecyclerChatItem(broad_receiver,broad_message,broad_date);
                        mItems.add(recyclerChatItem);
//                        chatlist.setAdapter(adapter);
                        adapter = new ChatRecyclerViewAdapter(getApplicationContext(), mItems);
                        chatlist.setAdapter(adapter);
                    }
//                    adapter.notifyDataSetChanged();
//                        adapter.notifyItemChanged(i);
                }else {
                    if(broad_message.contains(".jpg") || broad_message.contains(".JPG") || broad_message.contains(".png") || broad_message.contains(".PNG")){
                        RecyclerChatItem recyclerChatItem = new RecyclerChatItem(broad_sender,"사진",broad_date);
                        mItems.add(recyclerChatItem);
                        adapter = new ChatRecyclerViewAdapter(getApplicationContext(), mItems);
                        chatlist.setAdapter(adapter);
                    }else {
                        RecyclerChatItem recyclerChatItem = new RecyclerChatItem(broad_sender,broad_message,broad_date);
                        mItems.add(recyclerChatItem);
                        adapter = new ChatRecyclerViewAdapter(getApplicationContext(), mItems);
                        chatlist.setAdapter(adapter);
                    }
//                    adapter.notifyDataSetChanged();
//                        chatlist.setAdapter(adapter);
//                        adapter.notifyDataSetChanged();
                }
            }else if (mItems.size() > 0){
                if (loginId.equals(broad_sender)) {
                    //receiver가 sender가 되어햐나는 부분
                    RecyclerChatItem recyclerChatItem;
                    if(broad_message.contains(".jpg") || broad_message.contains(".JPG") || broad_message.contains(".png") || broad_message.contains(".PNG")){
                        recyclerChatItem = new RecyclerChatItem(broad_receiver, "사진", broad_date);
                    }else {
                        recyclerChatItem = new RecyclerChatItem(broad_receiver, broad_message, broad_date);
                    }
                    for (int i = 0; i < mItems.size(); i++) {
                        Log.d("mm", mItems.get(i).getName());
                        if (mItems.get(i).getName().equals(broad_receiver)) {
                            Log.d("병신", "바뀌기 전 "+mItems.get(i).getText());
                            mItems.set(i, recyclerChatItem);
                            Log.d("병신", "바뀌기 후 "+mItems.get(i).getText());
//                        chatlist.setAdapter(adapter);
                            adapter = new ChatRecyclerViewAdapter(getApplicationContext(), mItems);
                            chatlist.setAdapter(adapter);
//                            adapter.notifyDataSetChanged();
                        }
                    }
                } else {
                    RecyclerChatItem recyclerChatItem;
                    if(broad_message.contains(".jpg") || broad_message.contains(".JPG") || broad_message.contains(".png") || broad_message.contains(".PNG")){
                        recyclerChatItem = new RecyclerChatItem(broad_sender, "사진", broad_date);
                    }else {
                        recyclerChatItem = new RecyclerChatItem(broad_sender, broad_message, broad_date);
                    }
                    Log.d("test", broad_sender + "가" + broad_message + "보냄");
                    for (int i = 0; i < mItems.size(); i++) {
                        Log.d("mm", mItems.get(i).getName());
                        if (mItems.get(i).getName().equals(broad_sender)) {
                            Log.d("병신", "바뀌기 전 "+mItems.get(i).getText());
                            mItems.set(i, recyclerChatItem);
                            Log.d("병신", "바뀌기 후 "+mItems.get(i).getText());
//                        chatlist.setAdapter(adapter);
                            adapter = new ChatRecyclerViewAdapter(getApplicationContext(), mItems);
                            chatlist.setAdapter(adapter);
//                            adapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        }
    };

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onPause();
        for (int i = 0; i < mItems.size(); i++) {
            Log.d("in pause ", mItems.get(i).getName() + mItems.get(i).getText() + mItems.get(i).getDate());
        }
        Log.d("in pause", broad_sender+broad_message+broad_roomname+broad_date);
        //방 저장
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        for (int i = 0; i < mItems.size(); i++) {
            Log.d("in destroy ", mItems.get(i).getName() + mItems.get(i).getText() + mItems.get(i).getDate());
        }
        Log.d("in destroy", broad_sender+broad_message+broad_roomname+broad_date);
        //방 저장
        unbindService(conn);
    }

    private class GetData extends AsyncTask<String, Void, String>{

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(ChatActivity.this,
                    "Please Wait", null, true, true);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
//            results.setText(result);
            Log.d("TAG", "response - " + result);

            if (result == null){
                Toast.makeText(ChatActivity.this, "error", Toast.LENGTH_SHORT).show();
            }
            else {
//                Toast.makeText(ChatActivity.this, result.toString(), Toast.LENGTH_SHORT).show();
                resultmessage = result.toString();

                if(resultmessage.equals("No data")){
                    Log.d("getting data", "No data in db");
                }else{
                    try {
                        JSONArray jsonArray = new JSONArray(resultmessage);

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject item = jsonArray.getJSONObject(i);

                            String r_roomname = item.getString("roomname");
                            String r_sender = item.getString("sender");
                            String r_receiver = item.getString("receiver");
                            String r_date = item.getString("date");
                            String r_lasttext = item.getString("lasttext");

                            Log.d("chatting room", r_roomname + r_sender + r_date + r_lasttext);

                            if(r_roomname.contains(loginId)) {
                                if (loginId.equals(r_sender)) {
                                    RecyclerChatItem recyclerItem = new RecyclerChatItem(r_receiver, r_lasttext, r_date);
                                    mItems.add(recyclerItem);
                                } else {
                                    RecyclerChatItem recyclerItem = new RecyclerChatItem(r_sender, r_lasttext, r_date);
                                    mItems.add(recyclerItem);
                                }
                            }
                        }
                        chatlist.setAdapter(adapter);
//                        adapter.notifyDataSetChanged();
                    }catch (JSONException e){

                    }
                }
            }
        }

        @Override
        protected String doInBackground(String... params) {

            String roomname = params[0];
            String serverURL = serverIP.serverIp+"/wimp/chattingroomlist.php";
            String postParameters = "roomname=" + roomname;

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
                String line = null;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }

                bufferedReader.close();

                return sb.toString();
            } catch (Exception e) {

                Log.d("TAG", "InsertData: Error ", e);
                errorString = e.toString();

                return null;
            }
        }

    }

    class InsertData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(ChatActivity.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
//            results.setText(result);
            Log.d("result php", result);
            Log.d("TAG", "POST response  - " + result);
        }


        @Override
        protected String doInBackground(String... params) {

            String roomname = (String)params[0];
            String sender = (String)params[1];
            String receiver = (String)params[2];
            String date = (String)params[3];
            String lasttext = (String)params[4];

            Log.d("data" ,"roomname = " + roomname + "sender = " + sender + "date = " + date + "lasttext = " + lasttext);

            String serverURL = serverIP.serverIp+"/wimp/chattingroom.php";
            String postParameters = "roomname=" + roomname + "&sender=" + sender + "&receiver=" + receiver + "&date=" + date + "&lasttext=" + lasttext;


            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.connect();

                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();

                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d("TAG", "POST response code - " + responseStatusCode);

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
                String line = null;

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

    private class CheckWokerData extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(ChatActivity.this,
                    "Please Wait", null, true, true);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();

            Log.d("TAG", "response - " + result);
            Log.d("TAG",result.toString());
            if (result == null){
                Toast.makeText(ChatActivity.this, "error", Toast.LENGTH_SHORT).show();
            }
            else {
                getjson = result.toString();
                Log.d("chat",getjson);
                if(getjson.equals("Wrong id")){
                    Log.d("chat","Wrong id");
                }else{
                    try {
                        JSONArray jsonArray = new JSONArray(getjson);

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject item = jsonArray.getJSONObject(i);

                            String workernickname = item.getString("nickname");
                            String wokername = item.getString("name");

                            Log.d("chat","woker's info "+worker+workernickname+wokername);

                            RecyclerChatItem recyclerChatItem = new RecyclerChatItem(wokername,"","");
                            mItems.add(recyclerChatItem);
                            adapter = new ChatRecyclerViewAdapter(getApplicationContext(), mItems);
                            adapter.notifyDataSetChanged();
                        }
                        chatlist.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        Log.d("chat", "showResult : ", e);
                    } catch (NullPointerException e) {

                    }

                }
            }
        }

        @Override
        protected String doInBackground(String... params) {
            String meme_worker = params[0];
            String serverURL = serverIP.serverIp+"/wimp/checkMember.php";
            String postParameters = "worker=" + meme_worker;

            Log.d("chat",meme_worker);

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

    private class CheckData extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(ChatActivity.this,
                    "Please Wait", null, true, true);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();

            Log.d("TAG", "response - " + result);
            Log.d("TAG",result.toString());
            if (result == null){
                Toast.makeText(ChatActivity.this, "error", Toast.LENGTH_SHORT).show();
            }
            else {
                getjson = result.toString();
                Log.d("chat",getjson);
                if(getjson.equals("Wrong id")){
                    Log.d("chat","Wrong id");
                }else{
                    try {
                        JSONArray jsonArray = new JSONArray(getjson);

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject item = jsonArray.getJSONObject(i);

                            String nickname = item.getString("nickname");
                            loginName = item.getString("name");

                            Log.d("chat", "db data get success "+nickname + loginName);
                        }
                    } catch (JSONException e) {
                        Log.d("chat", "showResult : ", e);
                    } catch (NullPointerException e) {

                    }

                }
            }
        }

        @Override
        protected String doInBackground(String... params) {
            String mem_id = params[0];
            String serverURL = serverIP.serverIp+"/wimp/checkMember.php";
            String postParameters = "id=" + mem_id;

            Log.d("chat",mem_id);

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
