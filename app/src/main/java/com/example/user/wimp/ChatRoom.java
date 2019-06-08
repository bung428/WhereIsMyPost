package com.example.user.wimp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.user.wimp.apprtc.CallActivity;
import com.example.user.wimp.apprtc.CallFragment;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.Set;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static android.content.ContentValues.TAG;

public class ChatRoom extends AppCompatActivity {

    Handler handler;
    final int PICTURE_REQUEST_CODE = 100;

    ServerIP serverIP;

    ClipData clipData;
    ArrayList<String> images = new ArrayList<>();
    private HttpConnection httpConn = HttpConnection.getInstance();

    ArrayList<String> loginUser;
    ArrayList<String> chatdata = new ArrayList<>();
    private ArrayList<ChatRoomRecyclerItem> mItems = new ArrayList<>();
    ChatRoomRecyclerAdapter chatRoomRecyclerAdapter;
    ChatRoomRecyclerItem recyclerItem;

    Button sendBtn,uploadBtn;
    EditText messageEt;
    RecyclerView recyclerChat;
    ListView m_ListView;
    CustomAdapter m_Adapter;

    String loginId,loginName,chatroom_sender,chatroom_receiver,worker="",getjson,chatterinfo,chatter_name,chatter_lasttext;
    String message,sender="",receiver,imagepath,chatter,intentmsg,intentsender,intentimage,chathistory,body;
    Boolean imageortext;

    Messenger mServiceMessenger = null;
    boolean isService=false, intentflasg = false, bundleflag = false;
    ServiceConnection conn = new ServiceConnection() {
        public void onServiceConnected(ComponentName name,
                                       IBinder service) {
// 서비스와 연결되었을 때 호출되는 메서드
// 서비스 객체를 전역변수로 저장
            mServiceMessenger = new Messenger(service);

            String a = "true";
            sendMessageToServiceFlag(a);

            try {
                Message msg = Message.obtain(null, SocketService.MSG_REGISTER_CLIENT);
                msg.replyTo = mMessenger;
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

    private static final int CONNECTION_REQUEST = 1;
    private static boolean commandLineRun = false;

    private SharedPreferences sharedPref;
    private String keyprefVideoCallEnabled;
    private String keyprefScreencapture;
    private String keyprefCamera2;
    private String keyprefResolution;
    private String keyprefFps;
    private String keyprefCaptureQualitySlider;
    private String keyprefVideoBitrateType;
    private String keyprefVideoBitrateValue;
    private String keyprefVideoCodec;
    private String keyprefAudioBitrateType;
    private String keyprefAudioBitrateValue;
    private String keyprefAudioCodec;
    private String keyprefHwCodecAcceleration;
    private String keyprefCaptureToTexture;
    private String keyprefFlexfec;
    private String keyprefNoAudioProcessingPipeline;
    private String keyprefAecDump;
    private String keyprefOpenSLES;
    private String keyprefDisableBuiltInAec;
    private String keyprefDisableBuiltInAgc;
    private String keyprefDisableBuiltInNs;
    private String keyprefEnableLevelControl;
    private String keyprefDisableWebRtcAGCAndHPF;
    private String keyprefDisplayHud;
    private String keyprefTracing;
    private String keyprefRoomServerUrl;
    private String keyprefRoom;
    private String keyprefRoomList;
//    private ArrayList<String> roomList;
//    private ArrayAdapter<String> adapter;
    private String keyprefEnableDataChannel;
    private String keyprefOrdered;
    private String keyprefMaxRetransmitTimeMs;
    private String keyprefMaxRetransmits;
    private String keyprefDataProtocol;
    private String keyprefNegotiated;
    private String keyprefDataId;

    CallActivity callActivity;
    CallFragment callFragment;
    CallFragment.OnCallEvents callEvents;

    String roomname;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chatroom);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        keyprefVideoCallEnabled = getString(R.string.pref_videocall_key);
        keyprefScreencapture = getString(R.string.pref_screencapture_key);
        keyprefCamera2 = getString(R.string.pref_camera2_key);
        keyprefResolution = getString(R.string.pref_resolution_key);
        keyprefFps = getString(R.string.pref_fps_key);
        keyprefCaptureQualitySlider = getString(R.string.pref_capturequalityslider_key);
        keyprefVideoBitrateType = getString(R.string.pref_maxvideobitrate_key);
        keyprefVideoBitrateValue = getString(R.string.pref_maxvideobitratevalue_key);
        keyprefVideoCodec = getString(R.string.pref_videocodec_key);
        keyprefHwCodecAcceleration = getString(R.string.pref_hwcodec_key);
        keyprefCaptureToTexture = getString(R.string.pref_capturetotexture_key);
        keyprefFlexfec = getString(R.string.pref_flexfec_key);
        keyprefAudioBitrateType = getString(R.string.pref_startaudiobitrate_key);
        keyprefAudioBitrateValue = getString(R.string.pref_startaudiobitratevalue_key);
        keyprefAudioCodec = getString(R.string.pref_audiocodec_key);
        keyprefNoAudioProcessingPipeline = getString(R.string.pref_noaudioprocessing_key);
        keyprefAecDump = getString(R.string.pref_aecdump_key);
        keyprefOpenSLES = getString(R.string.pref_opensles_key);
        keyprefDisableBuiltInAec = getString(R.string.pref_disable_built_in_aec_key);
        keyprefDisableBuiltInAgc = getString(R.string.pref_disable_built_in_agc_key);
        keyprefDisableBuiltInNs = getString(R.string.pref_disable_built_in_ns_key);
        keyprefEnableLevelControl = getString(R.string.pref_enable_level_control_key);
        keyprefDisableWebRtcAGCAndHPF = getString(R.string.pref_disable_webrtc_agc_and_hpf_key);
        keyprefDisplayHud = getString(R.string.pref_displayhud_key);
        keyprefTracing = getString(R.string.pref_tracing_key);
        keyprefRoomServerUrl = getString(R.string.pref_room_server_url_key);
        keyprefRoom = getString(R.string.pref_room_key);
        keyprefRoomList = getString(R.string.pref_room_list_key);
        keyprefEnableDataChannel = getString(R.string.pref_enable_datachannel_key);
        keyprefOrdered = getString(R.string.pref_ordered_key);
        keyprefMaxRetransmitTimeMs = getString(R.string.pref_max_retransmit_time_ms_key);
        keyprefMaxRetransmits = getString(R.string.pref_max_retransmits_key);
        keyprefDataProtocol = getString(R.string.pref_data_protocol_key);
        keyprefNegotiated = getString(R.string.pref_negotiated_key);
        keyprefDataId = getString(R.string.pref_data_id_key);

        sendBtn=findViewById(R.id.sendBtn);
        uploadBtn=findViewById(R.id.uploadBtn);
        messageEt=findViewById(R.id.messageEt);
//        m_ListView=findViewById(R.id.listView1);
        recyclerChat=findViewById(R.id.recyclerChat);

        final DBHelper dbHelper = new DBHelper(getApplicationContext(), "ChatSave.db", null, 1);
//        m_Adapter = new CustomAdapter();
        checkVerify();
        setData();
        setRecyclerView();
        setStartService();

        try {
            SharedPreferences preferences = getSharedPreferences("auto", MODE_PRIVATE);

            Set<String> set = preferences.getStringSet("userinfo", null);
            loginUser = new ArrayList<>(set);

            String[] loginData = loginUser.get(0).split("@@@@");
            Log.d("chat",loginData[0]+ " " + loginData[1]);
            loginId=loginData[0];

            CheckData checkData=new CheckData();
            checkData.execute(loginId);


        }catch (NullPointerException e){
            e.printStackTrace();
        }

        Bundle extras = getIntent().getExtras();
        if(extras.getString("sender")!=null && extras.getString("msg")!=null){
            intentmsg = extras.getString("msg");
            intentsender = extras.getString("sender");
            intentimage = extras.getString("image");
            if(intentmsg.contains(".jpg") || intentmsg.contains(".JPG") || intentmsg.contains(".png") || intentmsg.contains(".PNG")){
                intentmsg = serverIP.serverIp+"/wimp/uploadimage/" + intentmsg;
                Log.d("노티다 새기야",intentsender+""+intentmsg);
            }else {
                Log.d("노티다 새기야",intentsender+""+intentmsg);
            }
        }
        NotificationManager nm =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (nm != null) {
            nm.cancelAll();
        }
//        //노티피케이션 제거
//        nm.cancel();

        Intent intent = getIntent();
        if(intent.getStringExtra("chat")!=null) {
            chatter=intent.getStringExtra("chat");
        }else if(intent.getStringExtra("chattingdata")!=null){
            chatterinfo = intent.getStringExtra("chattingdata");

            String[] data = chatterinfo.split("##");
            chatter_name = data[0];
            chatter_lasttext = data[1];
        }

        try {
            chathistory = dbHelper.getResult();
            Log.d("디비헬퍼다 새기야", chathistory);
            String[] data = chathistory.split("####");
            for(int j = 0; j < data.length; j++){
                chatdata.add(data[j]);
            }
            for(int j = 0; j < chatdata.size(); j++){
                Log.d("sssssss",chatdata.get(j));
                ChatRoomRecyclerItem recyclerItem = null;
                if(chatter!=null && chatdata.get(j).contains(chatter) && chatdata.get(j).contains(loginId)){
                    String[] chatting = chatdata.get(j).split("##");
                    if (loginId.equals(chatting[0])) {
                        Log.d("if문 맞아야해", loginId + chatting[1]);
                        if(chatting[4].equals("true")){
                            recyclerItem = new ChatRoomRecyclerItem(chatting[2], chatting[0], chatting[3], 1, true);
                        }else if(chatting[4].equals("false")) {
                            recyclerItem = new ChatRoomRecyclerItem("", chatting[0], chatting[3], 1,false);
                            recyclerItem.setUri(Uri.parse(chatting[2]));
//                            if (chatting[2].contains("uploadimage")){
//                                recyclerItem.setUri(Uri.parse(chatting[2]));
//                            }else{
//                            }
                        }
                    } else {
                        Log.d("if문 틀려야해", loginId + chatting[1]);
                        if(chatting[4].equals("true")){
                            recyclerItem = new ChatRoomRecyclerItem(chatting[2], chatting[0], chatting[3], 0,true);
                        }else if(chatting[4].equals("false")) {
                            Log.d("이미지냐 새기야?", chatting[2]);
                            recyclerItem = new ChatRoomRecyclerItem("", chatting[0], chatting[3], 0,false);
//                            if (chatting[2].contains("uploadimage")){
//                                recyclerItem.setUri(Uri.parse(chatting[2]));
//                            }else{
//                            }
                            recyclerItem.setUri(Uri.parse(chatting[2]));
                        }
                    }
                    mItems.add(recyclerItem);
                }else if(chatter_name != null && chatdata.get(j).contains(chatter_name) && chatdata.get(j).contains(loginId)){
                    String[] chatting = chatdata.get(j).split("##");
                    if (loginId.equals(chatting[0])) {
                        Log.d("if문 맞아야해", loginId + chatting[1]);
                        if(chatting[4].equals("true")){
                            recyclerItem = new ChatRoomRecyclerItem(chatting[2], chatting[0], chatting[3], 1, true);
                        }else if(chatting[4].equals("false")) {
                            recyclerItem = new ChatRoomRecyclerItem("", chatting[0], chatting[3], 1,false);
//                            if (chatting[2].contains("uploadimage")){
//                                recyclerItem.setUri(Uri.parse(chatting[2]));
//                            }else{
//                            }
                            recyclerItem.setUri(Uri.parse(chatting[2]));
                        }
                    } else {
                        Log.d("if문 틀려야해", loginId + chatting[1]);
                        if(chatting[4].equals("true")){
                            recyclerItem = new ChatRoomRecyclerItem(chatting[2], chatting[0], chatting[3], 0, true);
                        }else if(chatting[4].equals("false")) {
                            Log.d("이미지냐 새기야?", chatting[2]);
                            recyclerItem = new ChatRoomRecyclerItem("", chatting[0], chatting[3], 0,false);
//                            if (chatting[2].contains("uploadimage")){
//                                recyclerItem.setUri(Uri.parse(chatting[2]));
//                            }else{
//                            }
                            recyclerItem.setUri(Uri.parse(chatting[2]));
                        }
                    }
                    mItems.add(recyclerItem);
                }else if(intentsender != null && chatdata.get(j).contains(intentsender) && chatdata.get(j).contains(loginId)){
                    String[] chatting = chatdata.get(j).split("##");
                    if (loginId.equals(chatting[0])) {
                        Log.d("if문 맞아야해", loginId + chatting[1]);
                        if(chatting[4].equals("true")){
                            recyclerItem = new ChatRoomRecyclerItem(chatting[2], chatting[0], chatting[3], 1, true);
                        }else if(chatting[4].equals("false")) {
                            recyclerItem = new ChatRoomRecyclerItem("", chatting[0], chatting[3], 1,false);
//                            if (chatting[2].contains("uploadimage")){
//                                recyclerItem.setUri(Uri.parse(chatting[2]));
//                            }else{
//                            }
                            recyclerItem.setUri(Uri.parse(chatting[2]));
                        }
                    } else {
                        Log.d("if문 틀려야해", loginId + chatting[1]);
                        if(chatting[4].equals("true")){
                            recyclerItem = new ChatRoomRecyclerItem(chatting[2], chatting[0], chatting[3], 0, true);
                        }else if(chatting[4].equals("false")) {
                            Log.d("이미지냐 새기야?", chatting[2]);
                            recyclerItem = new ChatRoomRecyclerItem("", chatting[0], chatting[3], 0,false);
//                            if (chatting[2].contains("uploadimage")){
//                                recyclerItem.setUri(Uri.parse(chatting[2]));
//                            }else{
//                            }
                            recyclerItem.setUri(Uri.parse(chatting[2]));
                        }
                    }
                    mItems.add(recyclerItem);
                }
            }
            recyclerChat.setAdapter(chatRoomRecyclerAdapter);
            chatRoomRecyclerAdapter.notifyDataSetChanged();
        }catch (NullPointerException e){
            Log.d("ss", "db is null");
        }catch (ArrayIndexOutOfBoundsException e){

        }

        handler= new Handler() {
            @Override
            public void handleMessage(Message hdmsg) {
                if (hdmsg.what == 1111) {
                    Log.d("chat", hdmsg.obj.toString() + "\n");
                    JSONObject jsonObject;
                    try {
                        jsonObject=new JSONObject(hdmsg.obj.toString());
                        String header = jsonObject.getString("header");
                        ChatRoomRecyclerItem recyclerItem = null;
                        if(header.equals("chatting")){
                            message = jsonObject.getString("message");
                            sender = jsonObject.getString("sender");
                            receiver = jsonObject.getString("receiver");
                            imagepath = serverIP.serverIp+"/wimp/uploadimage/" + message;
                            Log.d("in second",message + sender + receiver + imagepath + Uri.parse(imagepath));

                            long now = System.currentTimeMillis();
                            Date date = new Date(now);
                            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                            String getTime = sdf.format(date);


                            if(chatter != null){
                                Log.d("chatter 값", chatter);

                                if(chatter.equals(sender)){
                                    if(loginId.equals(sender)) {
                                        if(imagepath.contains(".jpg") || imagepath.contains(".JPG") || imagepath.contains(".png") || imagepath.contains(".PNG")){
                                            recyclerItem = new ChatRoomRecyclerItem("",sender,getTime, 1,false);
                                            recyclerItem.setUri(Uri.parse(imagepath));
                                        }else{
                                            if (message.contains("우리 영상통화해요.")){
                                                Log.d("영상통화왔다!!!!", message);

                                                String[] data = message.split("~~");
                                                Log.d("데이터 영상통화",data[0]+data[1]);
                                                roomname = data[1];
                                                Log.d("방번호", roomname);
                                                recyclerItem = new ChatRoomRecyclerItem(data[0], sender, getTime, 1, true);
                                            }else {
                                                recyclerItem = new ChatRoomRecyclerItem(message, sender, getTime, 1, true);
                                            }
                                        }
                                    }else{
                                        if(imagepath.contains(".jpg") || imagepath.contains(".JPG") || imagepath.contains(".png") || imagepath.contains(".PNG")){
                                            recyclerItem = new ChatRoomRecyclerItem("",sender,getTime, 0,false);
                                            recyclerItem.setUri(Uri.parse(imagepath));
                                        }else{
                                            if (message.contains("우리 영상통화해요.")){
                                                Log.d("영상통화왔다!!!!", message);

                                                String[] data = message.split("~~");
                                                Log.d("데이터 영상통화",data[0]+data[1]);
                                                roomname = data[1];
                                                Log.d("방번호", roomname);
                                                recyclerItem = new ChatRoomRecyclerItem(data[0], sender, getTime, 0, true);
                                            }else {
                                                recyclerItem = new ChatRoomRecyclerItem(message, sender, getTime, 0, true);
                                            }
                                        }
                                    }
                                    mItems.add(recyclerItem);
                                    recyclerChat.setAdapter(chatRoomRecyclerAdapter);
                                    chatRoomRecyclerAdapter.notifyDataSetChanged();
                                    //글이 true 이미지 false
                                    if(imagepath.contains(".jpg") || imagepath.contains(".JPG") || imagepath.contains(".png") || imagepath.contains(".PNG")){
                                        imageortext = false;
                                        dbHelper.insert(sender,receiver,imagepath,getTime,imageortext);
                                    }else{
                                        imageortext = true;
                                        if (message.contains("우리 영상통화해요.")){
                                            Log.d("영상통화왔다!!!!", message);

                                            String[] data = message.split("~~");
                                            Log.d("데이터 영상통화",data[0]+data[1]);
                                            dbHelper.insert(sender,receiver,data[0],getTime,imageortext);
                                        }else {
                                            dbHelper.insert(sender, receiver, message, getTime, imageortext);
                                        }
                                    }
                                }
                            }else if(chatter_name != null){
                                if(chatter_name.equals(sender)){
                                    if(loginId.equals(sender)) {
                                        if(imagepath.contains(".jpg") || imagepath.contains(".JPG") || imagepath.contains(".png") || imagepath.contains(".PNG")){
                                            recyclerItem = new ChatRoomRecyclerItem("",sender,getTime, 1,false);
                                            recyclerItem.setUri(Uri.parse(imagepath));
                                        }else{
                                            if (message.contains("우리 영상통화해요.")){
                                                Log.d("영상통화왔다!!!!", message);

                                                String[] data = message.split("~~");
                                                Log.d("데이터 영상통화",data[0]+data[1]);
                                                roomname = data[1];
                                                Log.d("방번호", roomname);
                                                recyclerItem = new ChatRoomRecyclerItem(data[0], sender, getTime, 1, true);
                                            }else {
                                                recyclerItem = new ChatRoomRecyclerItem(message, sender, getTime, 1, true);
                                            }
                                        }
                                    }else{
                                        if(imagepath.contains(".jpg") || imagepath.contains(".JPG") || imagepath.contains(".png") || imagepath.contains(".PNG")){
                                            recyclerItem = new ChatRoomRecyclerItem("",sender,getTime, 0,false);
                                            recyclerItem.setUri(Uri.parse(imagepath));
                                        }else{
                                            if (message.contains("우리 영상통화해요.")){
                                                Log.d("영상통화왔다!!!!", message);

                                                String[] data = message.split("~~");
                                                Log.d("데이터 영상통화",data[0]+data[1]);
                                                roomname = data[1];
                                                Log.d("방번호", roomname);
                                                recyclerItem = new ChatRoomRecyclerItem(data[0], sender, getTime, 0, true);
                                            }else {
                                                recyclerItem = new ChatRoomRecyclerItem(message, sender, getTime, 0, true);
                                            }
                                        }
                                    }
                                    mItems.add(recyclerItem);
                                    recyclerChat.setAdapter(chatRoomRecyclerAdapter);
                                    chatRoomRecyclerAdapter.notifyDataSetChanged();
                                    //글이 true 이미지 false
                                    if(imagepath.contains(".jpg") || imagepath.contains(".JPG") || imagepath.contains(".png") || imagepath.contains(".PNG")){
                                        imageortext = false;
                                        dbHelper.insert(sender,receiver,imagepath,getTime,imageortext);
                                    }else{
                                        imageortext = true;
                                        if (message.contains("우리 영상통화해요.")){
                                            Log.d("영상통화왔다!!!!", message);

                                            String[] data = message.split("~~");
                                            Log.d("데이터 영상통화",data[0]+data[1]);
                                            dbHelper.insert(sender,receiver,data[0],getTime,imageortext);
                                        }else {
                                            dbHelper.insert(sender, receiver, message, getTime, imageortext);
                                        }
                                    }
                                }
                            }else if(intentsender != null){
                                if(intentsender.equals(sender)){
//                                    ChatRoomRecyclerItem recyclerItem;
                                    if(intentsender.equals(sender)) {

                                        if(imagepath.contains(".jpg") || imagepath.contains(".JPG") || imagepath.contains(".png") || imagepath.contains(".PNG")){
                                            recyclerItem = new ChatRoomRecyclerItem("",sender,getTime, 1,false);
                                            recyclerItem.setUri(Uri.parse(imagepath));
                                        }else{
                                            if (message.contains("우리 영상통화해요.")){
                                                Log.d("영상통화왔다!!!!", message);

                                                String[] data = message.split("~~");
                                                Log.d("데이터 영상통화",data[0]+data[1]);
                                                roomname = data[1];
                                                Log.d("방번호", roomname);
                                                recyclerItem = new ChatRoomRecyclerItem(data[0], sender, getTime, 1, true);
                                            }else {
                                                recyclerItem = new ChatRoomRecyclerItem(message, sender, getTime, 1, true);
                                            }
                                        }
                                    }else{
                                        if(imagepath.contains(".jpg") || imagepath.contains(".JPG") || imagepath.contains(".png") || imagepath.contains(".PNG")){
                                            recyclerItem = new ChatRoomRecyclerItem("",sender,getTime, 0,false);
                                            recyclerItem.setUri(Uri.parse(imagepath));
                                        }else{
                                            if (message.contains("우리 영상통화해요.")){
                                                Log.d("영상통화왔다!!!!", message);

                                                String[] data = message.split("~~");
                                                Log.d("데이터 영상통화",data[0]+data[1]);
                                                roomname = data[1];
                                                Log.d("방번호", roomname);
                                                recyclerItem = new ChatRoomRecyclerItem(data[0], sender, getTime, 0, true);
                                            }else {
                                                recyclerItem = new ChatRoomRecyclerItem(message, sender, getTime, 0, true);
                                            }
                                        }
                                    }
                                    mItems.add(recyclerItem);
                                    recyclerChat.setAdapter(chatRoomRecyclerAdapter);
                                    chatRoomRecyclerAdapter.notifyDataSetChanged();
                                    //글이 true 이미지 false
                                    if(imagepath.contains(".jpg") || imagepath.contains(".JPG") || imagepath.contains(".png") || imagepath.contains(".PNG")){
                                        imageortext = false;
                                        dbHelper.insert(sender,receiver,imagepath,getTime,imageortext);
                                    }else{
                                        imageortext = true;
                                        if (message.contains("우리 영상통화해요.")){
                                            Log.d("영상통화왔다!!!!", message);

                                            String[] data = message.split("~~");
                                            Log.d("데이터 영상통화",data[0]+data[1]);
                                            dbHelper.insert(sender,receiver,data[0],getTime,imageortext);
                                        }else {
                                            dbHelper.insert(sender, receiver, message, getTime, imageortext);
                                        }
                                    }
                                }
                            }
//                            m_Adapter.add(message,0);
//                            m_ListView.setAdapter(m_Adapter);
//                            if(!receiver.equals(id)){
//                                m_Adapter.add(message,0);
//                                m_ListView.setAdapter(m_Adapter);
//                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

//                    String[] data = hdmsg.obj.toString().split("  :  ");

//                    long now = System.currentTimeMillis();
//                    Date date = new Date(now);
//                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//                    String getTime = sdf.format(date);

//                    if(hdmsg.obj.toString().contains("접속")){
//                        m_Adapter.add(hdmsg.obj.toString(),2);
//                    }else if(hdmsg.obj.toString().contains(loginId)){
//                        m_Adapter.add(data[1],1);
//                    }else if(!hdmsg.obj.toString().contains(loginId)){
//                        m_Adapter.add(data[1],0);
//                    }
//                    m_ListView.setAdapter(m_Adapter);
                }
            }
        };

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                //SendThread 시작
                if (messageEt.getText().toString() != null) {
                    String text=messageEt.getText().toString();

                    long now = System.currentTimeMillis();
                    Date date = new Date(now);
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                    String getTime = sdf.format(date);

                    recyclerItem = new ChatRoomRecyclerItem(text,loginId,getTime, 1,true);
                    mItems.add(recyclerItem);
                    chatRoomRecyclerAdapter = new ChatRoomRecyclerAdapter(getApplicationContext(),mItems);
                    recyclerChat.setAdapter(chatRoomRecyclerAdapter);
                    chatRoomRecyclerAdapter.notifyDataSetChanged();

                    if(chatter!=null) {
                        dbHelper.insert(loginId, chatter, text, getTime, true);
                    }else if(chatter_name!=null){
                        dbHelper.insert(loginId, chatter_name, text, getTime, true);
                    }else if(intentsender!=null){
                        dbHelper.insert(loginId, intentsender, text, getTime, true);
                    }

//                    m_Adapter.add(text,1);
//                    m_ListView.setAdapter(m_Adapter);

                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("header", "chatting");
                        jsonObject.put("message",text);
                        jsonObject.put("sender",loginId);
                        if(chatter!=null) {
                            jsonObject.put("receiver", chatter);
                        }else if(chatter_name!=null){
                            jsonObject.put("receiver", chatter_name);
                        }else if(intentsender!=null){
                            jsonObject.put("receiver", intentsender);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    sendMessageToService(jsonObject.toString());
                    //시작후 edittext 초기화
                    messageEt.setText("");
                }
            }
        });

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //이미지 올리겠냐는 팝업 띄우기
                //선택지는 앨범과 닫기
                makeDialog();
            }
        });

        recyclerChat.addOnItemTouchListener(new RecyclerItemClickListner(getApplicationContext(), recyclerChat, new RecyclerItemClickListner.OnItemClickListener() {
            @Override public void onItemClick(View v, int position) {
                Log.d("main", mItems.get(position).msg);
                Log.d("방 번호", roomname);
                if(mItems.get(position).msg.equals("우리 영상통화해요.")){
                    if(roomname!=null){
                        Log.d("방 번호", roomname);
                        connectToRoom(roomname, false, false, false, 0);
                    }
                }
            }
            @Override
            public void onItemLongClick(View v, int position) {

            }
        }
        ));
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void checkVerify()
    {

        if (    checkSelfPermission(Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.CHANGE_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.MODIFY_AUDIO_SETTINGS) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED
                )
        {
            // Should we show an explanation?
            if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE))
            {
                ;
            }

            requestPermissions(new String[]{Manifest.permission.INTERNET,Manifest.permission.CAMERA,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.RECORD_AUDIO, Manifest.permission.CHANGE_NETWORK_STATE ,
                    Manifest.permission.MODIFY_AUDIO_SETTINGS, Manifest.permission.BLUETOOTH,}, 1);
        }
        else
        {
            //startApp();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1)
        {
            if (grantResults.length > 0)
            {
                for (int i=0; i<grantResults.length; ++i)
                {
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED)
                    {
                        // 하나라도 거부한다면.
                        new AlertDialog.Builder(this).setTitle("알림").setMessage("권한을 허용해주셔야 앱을 이용할 수 있습니다.")
                                .setPositiveButton("종료", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        finish();
                                    }
                                }).setNegativeButton("권한 설정", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                        .setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
                                getApplicationContext().startActivity(intent);
                            }
                        }).setCancelable(false).show();

                        return;
                    }
                }
                //Toast.makeText(this, "Succeed Read/Write external storage !", Toast.LENGTH_SHORT).show();
                //startApp();
            }
        }
    }

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        startManagingCursor(cursor);
        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(columnIndex);
    }

    private void sendData() {
// 네트워크 통신하는 작업은 무조건 작업스레드를 생성해서 호출 해줄 것!!
        new Thread() {
            public void run() {
// 파라미터 2개와 미리정의해논 콜백함수를 매개변수로 전달하여 호출
//                httpConn.requestWebServer("데이터1","데이터2", callback);
                if(clipData != null){
//                    Toast.makeText(getApplicationContext(),"not null",Toast.LENGTH_LONG).show();
                    ArrayList<String> image = new ArrayList<>();
                    Log.d("ㅅㅄㅄㅄㅄ", clipData.getItemCount()+"");
                    switch (clipData.getItemCount()){
                        case 1:
                            image.add(getPath(clipData.getItemAt(0).getUri()));
                            httpConn.requestWebServer(image, callback);
                            break;
                        case 2:
                            image.add(getPath(clipData.getItemAt(0).getUri()));
                            image.add(getPath(clipData.getItemAt(1).getUri()));
                            httpConn.requestWebServer(image, callback);
                            break;
                        case 3:
                            image.add(getPath(clipData.getItemAt(0).getUri()));
                            image.add(getPath(clipData.getItemAt(1).getUri()));
                            image.add(getPath(clipData.getItemAt(2).getUri()));
                            httpConn.requestWebServer(image, callback);
                            break;
                        case 4:
                            image.add(getPath(clipData.getItemAt(0).getUri()));
                            image.add(getPath(clipData.getItemAt(1).getUri()));
                            image.add(getPath(clipData.getItemAt(2).getUri()));
                            image.add(getPath(clipData.getItemAt(3).getUri()));
                            httpConn.requestWebServer(image, callback);
                            break;
                        case 5:
                            image.add(getPath(clipData.getItemAt(0).getUri()));
                            image.add(getPath(clipData.getItemAt(1).getUri()));
                            image.add(getPath(clipData.getItemAt(2).getUri()));
                            image.add(getPath(clipData.getItemAt(3).getUri()));
                            image.add(getPath(clipData.getItemAt(4).getUri()));
                            httpConn.requestWebServer(image, callback);
                            break;
                    }
                }
            }
        }.start();
    }

    private final Callback callback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            Log.d("tag", "콜백오류:"+e.getMessage());
        }
        @Override
        public void onResponse(Call call, Response response) throws IOException {
            body = response.body().string();
            DBHelper dbHelper = new DBHelper(getApplicationContext(), "ChatSave.db", null, 1);
            Log.d("tag", "서버에서 응답한 Body:"+body);
            if (body.contains("has been uploaded.") || body.contains("file already exists.")) {
                for (int i = 0; i < clipData.getItemCount(); i++){
                    Log.d("이미지다 임마", getPath(clipData.getItemAt(i).getUri()));
                    String[] filename = getPath(clipData.getItemAt(i).getUri()).split("/");

                    long now = System.currentTimeMillis();
                    Date date = new Date(now);
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                    String getTime = sdf.format(date);

                    if(chatter!=null) {
                        dbHelper.insert(loginId, chatter, serverIP.serverIp+"/wimp/uploadimage/" + filename[filename.length - 1], getTime, false);
                    }else if(chatter_name!=null){
                        dbHelper.insert(loginId, chatter_name, serverIP.serverIp+"/wimp/uploadimage/" + filename[filename.length - 1], getTime, false);
                    }else if(intentsender!=null){
                        dbHelper.insert(loginId, intentsender, serverIP.serverIp+"/wimp/uploadimage/" + filename[filename.length - 1], getTime, false);
                    }

                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("header", "chatting");
                        jsonObject.put("message",filename[filename.length - 1]);
                        jsonObject.put("sender",loginId);
                        if(chatter!=null) {
                            jsonObject.put("receiver", chatter);
                        }else if(chatter_name!=null){
                            jsonObject.put("receiver", chatter_name);
                        }else if(intentsender!=null){
                            jsonObject.put("receiver", intentsender);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    sendMessageToService(jsonObject.toString());
                }
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == PICTURE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                //ClipData 또는 Uri를 가져온다
                Uri uri = data.getData();
                clipData = data.getClipData();
                Log.d("tag", "uri "+uri);
                Log.d("tag","data"+data);

                long now = System.currentTimeMillis();
                Date date = new Date(now);
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                String getTime = sdf.format(date);

                //이미지 URI 를 이용하여 이미지뷰에 순서대로 세팅한다.
                if(clipData!=null) {
                    Log.d("몇개냐",clipData.getItemCount()+"");
                    for(int i = 0; i < clipData.getItemCount(); i++) {
                        if(i<clipData.getItemCount()){
                            Uri urione =  clipData.getItemAt(i).getUri();
                            ChatRoomRecyclerItem chatRoomRecyclerItem = new ChatRoomRecyclerItem("",loginId,getTime,1,false);
                            chatRoomRecyclerItem.setNum(clipData.getItemCount());
                            switch (i){
                                case 0:
                                    mItems.add(chatRoomRecyclerItem);
                                    chatRoomRecyclerItem.setUri(urione);
                                    break;
                                case 1:
                                    mItems.add(chatRoomRecyclerItem);
                                    chatRoomRecyclerItem.setUri(urione);
                                    break;
                                case 2:
                                    mItems.add(chatRoomRecyclerItem);
                                    chatRoomRecyclerItem.setUri(urione);
                                    break;
                                case 3:
                                    mItems.add(chatRoomRecyclerItem);
                                    chatRoomRecyclerItem.setUri(urione);
                                    break;
                                case 4:
                                    mItems.add(chatRoomRecyclerItem);
                                    chatRoomRecyclerItem.setUri(urione);
                                    break;
                            }
                            chatRoomRecyclerAdapter = new ChatRoomRecyclerAdapter(getApplicationContext(),mItems);
                            recyclerChat.setAdapter(chatRoomRecyclerAdapter);
                            chatRoomRecyclerAdapter.notifyDataSetChanged();

                            //대화내용에 이미지 저장해야할 부분
//                            if(chatter!=null) {
//                                dbHelper.insert(loginId, chatter, text, getTime, true);
//                            }else if(chatter_name!=null){
//                                dbHelper.insert(loginId, chatter_name, text, getTime, true);
//                            }else if(intentsender!=null){
//                                dbHelper.insert(loginId, intentsender, text, getTime, true);
//                            }
                        }
                    }
                    sendData();
                } else if(uri != null) {
//                    image1.setImageURI(uri);
                }
            }
        }else if (requestCode == CONNECTION_REQUEST && commandLineRun) {
            Log.d("tag", "Return: " + resultCode);
            setResult(resultCode);
            commandLineRun = false;
            finish();
        }
    }

    public void selectAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
        //사진을 여러개 선택할수 있도록 한다
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"),  PICTURE_REQUEST_CODE);
    }

    private void makeDialog(){

        AlertDialog.Builder alt_bld = new AlertDialog.Builder(ChatRoom.this);

        alt_bld.setTitle("사진 업로드").setCancelable(

                false).setPositiveButton("영상통화",

                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {

                        Log.v("알림", "다이얼로그 > 영상통화 선택");
                        //영상통화 버튼을 누르면 방이 생성되게하고 생성된 방 이름을 소켓을 통해 상대방에게 전해야한다.
                        //아래 조건문들로 상대방을 찾았다.
                        //서버를 통해 상대방에게 방이름을 보내보자.
                        //방이름은 난수로 정하고 이를 메시지인 "영상통화"와 함께 묶어 보내서 서버에서 잘 받아지고 나뉘는지 확인하자
                        Random random = new Random();
                        int roomNum = random.nextInt();

                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("header", "chatting");
                            jsonObject.put("message","우리 영상통화해요.~~"+roomNum);
                            jsonObject.put("sender",loginId);
                            if(chatter!=null) {
                                Log.d("영상통화 전 상대방 찾기", "상대방은 => "+chatter);
                                jsonObject.put("receiver", chatter);
                            }else if(chatter_name!=null){
                                Log.d("영상통화 전 상대방 찾기", "상대방은 => "+chatter_name);
                                jsonObject.put("receiver", chatter_name);
                            }else if(intentsender!=null){
                                Log.d("영상통화 전 상대방 찾기", "상대방은 => "+intentsender);
                                jsonObject.put("receiver", intentsender);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        sendMessageToService(jsonObject.toString());

                        long now = System.currentTimeMillis();
                        Date date = new Date(now);
                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                        String getTime = sdf.format(date);

                        ChatRoomRecyclerItem chatRoomRecyclerItem = new ChatRoomRecyclerItem("우리 영상통화해요.",loginId,getTime,1,true);
                        mItems.add(chatRoomRecyclerItem);
                        chatRoomRecyclerAdapter = new ChatRoomRecyclerAdapter(getApplicationContext(),mItems);
                        recyclerChat.setAdapter(chatRoomRecyclerAdapter);
                        chatRoomRecyclerAdapter.notifyDataSetChanged();

                        DBHelper dbHelper = new DBHelper(getApplicationContext(), "ChatSave.db", null, 1);

                        if(chatter!=null) {
                            dbHelper.insert(loginId, chatter, "우리 영상통화해요.", getTime, true);
                        }else if(chatter_name!=null){
                            dbHelper.insert(loginId, chatter_name, "우리 영상통화해요.", getTime, true);
                        }else if(intentsender!=null){
                            dbHelper.insert(loginId, intentsender, "우리 영상통화해요.", getTime, true);
                        }

                        connectToRoom(roomNum+"", false, false, false, 0);

                    }

                }).setNeutralButton("앨범선택",

                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialogInterface, int id) {

                        Log.v("알림", "다이얼로그 > 앨범선택 선택");

                        //앨범에서 선택

                        selectAlbum();

                    }

                }).setNegativeButton("취소   ",

                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {

                        Log.v("알림", "다이얼로그 > 취소 선택");

                        // 취소 클릭. dialog 닫기.

                        dialog.cancel();

                    }

                });

        AlertDialog alert = alt_bld.create();

        alert.show();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        String roomname;
        Log.d("onDestroy", "맨 마지막 메시지" + mItems.get(mItems.size()-1).name+"##"+loginId + mItems.get(mItems.size()-1).name + loginId + mItems.get(mItems.size()-1).time + mItems.get(mItems.size()-1).msg + "사진");
//        if(chatter!=null) {
//            if(mItems.get(mItems.size()-1).name.equals("worker")){
//
//            }
//        }else if(chatter_name!=null){
//
//        }else if(intentsender!=null){
//
//        }

        if(mItems.get(mItems.size()-1).msg.equals("")){
            GetData getData = new GetData();
            getData.execute(mItems.get(mItems.size()-1).name+"##"+loginId,"사진");
        }else{
            GetData getData = new GetData();
            getData.execute(mItems.get(mItems.size()-1).name+"##"+loginId,mItems.get(mItems.size()-1).msg);
        }
        String a = "false";
        sendMessageToServiceFlag(a);
        unbindService(conn);
    }

    private void setRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
//        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerChat.setLayoutManager(layoutManager);
        chatRoomRecyclerAdapter = new ChatRoomRecyclerAdapter(getApplicationContext(), mItems);
        recyclerChat.setAdapter(chatRoomRecyclerAdapter);
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
        startService(new Intent(ChatRoom.this, SocketService.class));
        bindService(new Intent(this, SocketService.class), conn, Context.BIND_AUTO_CREATE);
        isService = true;
    }

    private final Messenger mMessenger = new Messenger(new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            Log.i("test","in MainActivity : what "+msg.what);
            switch (msg.what) {
                case SocketService.MSG_SEND_TO_ACTIVITY:
                    String text = msg.getData().getString("fromService");
                    if (text != null) {
                        Message hdmsg = handler.obtainMessage();
                        hdmsg.what = 1111;
                        hdmsg.obj = text;
                        Log.d("test", "in MainActivity : text " + text);
                        handler.sendMessage(hdmsg);
                    }
                    break;
            }
            return false;
        }
    }));

    private void sendMessageToServiceFlag(String flag) {
        Log.d("flag", "메소드 도착.");
        if (isService) {
            Log.d("flag", "메소드 1단계 도착.");
            if (mServiceMessenger != null) {
                Log.d("flag", "메소드 2단계 도착.");
                try {
                    Log.d("flag", "보낸다.");
                    Message msg = Message.obtain(null, SocketService.MSG_SEND_TO_SERVICE_FLAG, flag);
                    msg.replyTo = mMessenger;
                    mServiceMessenger.send(msg);
                } catch (RemoteException e) {
                }
            }
        }
    }

    private void sendMessageToService(String str) {
        if (isService) {
            if (mServiceMessenger != null) {
                try {
                    Message msg = Message.obtain(null, SocketService.MSG_SEND_TO_SERVICE, str);
                    msg.replyTo = mMessenger;
                    mServiceMessenger.send(msg);
                } catch (RemoteException e) {
                }
            }
        }
    }

    private class CheckData extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(ChatRoom.this,
                    "Please Wait", null, true, true);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();

            Log.d("TAG", "response - " + result);
            Log.d("TAG",result.toString());
            if (result == null){
                Toast.makeText(ChatRoom.this, "error", Toast.LENGTH_SHORT).show();
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
//                        client = new SocketClient(IP,Port);
////                Log.d("chat","ip"+IP+" port"+Port);
//                        threadList.add(client);
//                        client.start();
//                        Intent intent = new Intent(
//                                ChatRoom.this, // 현재 화면
//                                SocketService.class); // 다음넘어갈 컴퍼넌트
//                        startService(intent);
//                        bindService(intent, // intent 객체
//                                conn, // 서비스와 연결에 대한 정의
//                                Context.BIND_AUTO_CREATE);
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

    private class GetData extends AsyncTask<String, Void, String>{

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

//            progressDialog = ProgressDialog.show(ChatRoom.this,
//                    "Please Wait", null, true, true);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

//            progressDialog.dismiss();
//            results.setText(result);
            Log.d("TAG", "response - " + result);

            if (result == null){
                Toast.makeText(ChatRoom.this, "error", Toast.LENGTH_SHORT).show();
            }
            else {

            }
        }

        @Override
        protected String doInBackground(String... params) {

            String roomname = params[0];
            String lasttext = params[1];
            String serverURL = serverIP.serverIp+"/wimp/chattingroomlistupdate.php";
            String postParameters = "roomname=" + roomname + "&lasttext=" + lasttext;

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
        View actionbar = inflater.inflate(R.layout.custom_mallcrawl, null);

        actionBar.setCustomView(actionbar);

        //액션바 양쪽 공백 없애기
        Toolbar parent = (Toolbar)actionbar.getParent();
        parent.setContentInsetsAbsolute(0, 0);

        Button backBtn = findViewById(R.id.backBtn);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ChatRoom.this, ChatActivity.class);
                i.putExtra("worker",worker);
                startActivity(i);
            }
        });
        return true;
    }

    private String sharedPrefGetString(
            int attributeId, String intentName, int defaultId, boolean useFromIntent) {
        String defaultValue = getString(defaultId);
        if (useFromIntent) {
            String value = getIntent().getStringExtra(intentName);
            if (value != null) {
                return value;
            }
            return defaultValue;
        } else {
            String attributeName = getString(attributeId);
            return sharedPref.getString(attributeName, defaultValue);
        }
    }

    private boolean sharedPrefGetBoolean(
            int attributeId, String intentName, int defaultId, boolean useFromIntent) {
        boolean defaultValue = Boolean.valueOf(getString(defaultId));
        if (useFromIntent) {
            return getIntent().getBooleanExtra(intentName, defaultValue);
        } else {
            String attributeName = getString(attributeId);
            return sharedPref.getBoolean(attributeName, defaultValue);
        }
    }

    /**
     * Get a value from the shared preference or from the intent, if it does not
     * exist the default is used.
     */
    private int sharedPrefGetInteger(
            int attributeId, String intentName, int defaultId, boolean useFromIntent) {
        String defaultString = getString(defaultId);
        int defaultValue = Integer.parseInt(defaultString);
        if (useFromIntent) {
            return getIntent().getIntExtra(intentName, defaultValue);
        } else {
            String attributeName = getString(attributeId);
            String value = sharedPref.getString(attributeName, defaultString);
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                Log.e(TAG, "Wrong setting for: " + attributeName + ":" + value);
                return defaultValue;
            }
        }
    }

    private boolean validateUrl(String url) {
        if (URLUtil.isHttpsUrl(url) || URLUtil.isHttpUrl(url)) {
            return true;
        }

        new AlertDialog.Builder(this)
                .setTitle(getText(R.string.invalid_url_title))
                .setMessage(getString(R.string.invalid_url_text, url))
                .setCancelable(false)
                .setNeutralButton(R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        })
                .create()
                .show();
        return false;
    }

    private void connectToRoom(String roomId, boolean commandLineRun, boolean loopback,
                               boolean useValuesFromIntent, int runTimeMs) {
        this.commandLineRun = commandLineRun;

        // roomId is random for loopback.
        if (loopback) {
            roomId = Integer.toString((new Random()).nextInt(100000000));
        }

        String roomUrl = sharedPref.getString(
                keyprefRoomServerUrl, getString(R.string.pref_room_server_url_default));

        // Video call enabled flag.
        boolean videoCallEnabled = sharedPrefGetBoolean(R.string.pref_videocall_key,
                CallActivity.EXTRA_VIDEO_CALL, R.string.pref_videocall_default, useValuesFromIntent);

        // Use screencapture option.
        boolean useScreencapture = sharedPrefGetBoolean(R.string.pref_screencapture_key,
                CallActivity.EXTRA_SCREENCAPTURE, R.string.pref_screencapture_default, useValuesFromIntent);

        // Use Camera2 option.
        boolean useCamera2 = sharedPrefGetBoolean(R.string.pref_camera2_key, CallActivity.EXTRA_CAMERA2,
                R.string.pref_camera2_default, useValuesFromIntent);

        // Get default codecs.
        String videoCodec = sharedPrefGetString(R.string.pref_videocodec_key,
                CallActivity.EXTRA_VIDEOCODEC, R.string.pref_videocodec_default, useValuesFromIntent);
        String audioCodec = sharedPrefGetString(R.string.pref_audiocodec_key,
                CallActivity.EXTRA_AUDIOCODEC, R.string.pref_audiocodec_default, useValuesFromIntent);

        // Check HW codec flag.
        boolean hwCodec = sharedPrefGetBoolean(R.string.pref_hwcodec_key,
                CallActivity.EXTRA_HWCODEC_ENABLED, R.string.pref_hwcodec_default, useValuesFromIntent);

        // Check Capture to texture.
        boolean captureToTexture = sharedPrefGetBoolean(R.string.pref_capturetotexture_key,
                CallActivity.EXTRA_CAPTURETOTEXTURE_ENABLED, R.string.pref_capturetotexture_default,
                useValuesFromIntent);

        // Check FlexFEC.
        boolean flexfecEnabled = sharedPrefGetBoolean(R.string.pref_flexfec_key,
                CallActivity.EXTRA_FLEXFEC_ENABLED, R.string.pref_flexfec_default, useValuesFromIntent);

        // Check Disable Audio Processing flag.
        boolean noAudioProcessing = sharedPrefGetBoolean(R.string.pref_noaudioprocessing_key,
                CallActivity.EXTRA_NOAUDIOPROCESSING_ENABLED, R.string.pref_noaudioprocessing_default,
                useValuesFromIntent);

        // Check Disable Audio Processing flag.
        boolean aecDump = sharedPrefGetBoolean(R.string.pref_aecdump_key,
                CallActivity.EXTRA_AECDUMP_ENABLED, R.string.pref_aecdump_default, useValuesFromIntent);

        // Check OpenSL ES enabled flag.
        boolean useOpenSLES = sharedPrefGetBoolean(R.string.pref_opensles_key,
                CallActivity.EXTRA_OPENSLES_ENABLED, R.string.pref_opensles_default, useValuesFromIntent);

        // Check Disable built-in AEC flag.
        boolean disableBuiltInAEC = sharedPrefGetBoolean(R.string.pref_disable_built_in_aec_key,
                CallActivity.EXTRA_DISABLE_BUILT_IN_AEC, R.string.pref_disable_built_in_aec_default,
                useValuesFromIntent);

        // Check Disable built-in AGC flag.
        boolean disableBuiltInAGC = sharedPrefGetBoolean(R.string.pref_disable_built_in_agc_key,
                CallActivity.EXTRA_DISABLE_BUILT_IN_AGC, R.string.pref_disable_built_in_agc_default,
                useValuesFromIntent);

        // Check Disable built-in NS flag.
        boolean disableBuiltInNS = sharedPrefGetBoolean(R.string.pref_disable_built_in_ns_key,
                CallActivity.EXTRA_DISABLE_BUILT_IN_NS, R.string.pref_disable_built_in_ns_default,
                useValuesFromIntent);

        // Check Enable level control.
        boolean enableLevelControl = sharedPrefGetBoolean(R.string.pref_enable_level_control_key,
                CallActivity.EXTRA_ENABLE_LEVEL_CONTROL, R.string.pref_enable_level_control_key,
                useValuesFromIntent);

        // Check Disable gain control
        boolean disableWebRtcAGCAndHPF = sharedPrefGetBoolean(
                R.string.pref_disable_webrtc_agc_and_hpf_key, CallActivity.EXTRA_DISABLE_WEBRTC_AGC_AND_HPF,
                R.string.pref_disable_webrtc_agc_and_hpf_key, useValuesFromIntent);

        // Get video resolution from settings.
        int videoWidth = 0;
        int videoHeight = 0;
        if (useValuesFromIntent) {
            videoWidth = getIntent().getIntExtra(CallActivity.EXTRA_VIDEO_WIDTH, 0);
            videoHeight = getIntent().getIntExtra(CallActivity.EXTRA_VIDEO_HEIGHT, 0);
        }
        if (videoWidth == 0 && videoHeight == 0) {
            String resolution =
                    sharedPref.getString(keyprefResolution, getString(R.string.pref_resolution_default));
            String[] dimensions = resolution.split("[ x]+");
            if (dimensions.length == 2) {
                try {
                    videoWidth = Integer.parseInt(dimensions[0]);
                    videoHeight = Integer.parseInt(dimensions[1]);
                } catch (NumberFormatException e) {
                    videoWidth = 0;
                    videoHeight = 0;
                    Log.e(TAG, "Wrong video resolution setting: " + resolution);
                }
            }
        }

        // Get camera fps from settings.
        int cameraFps = 0;
        if (useValuesFromIntent) {
            cameraFps = getIntent().getIntExtra(CallActivity.EXTRA_VIDEO_FPS, 0);
        }
        if (cameraFps == 0) {
            String fps = sharedPref.getString(keyprefFps, getString(R.string.pref_fps_default));
            String[] fpsValues = fps.split("[ x]+");
            if (fpsValues.length == 2) {
                try {
                    cameraFps = Integer.parseInt(fpsValues[0]);
                } catch (NumberFormatException e) {
                    cameraFps = 0;
                    Log.e(TAG, "Wrong camera fps setting: " + fps);
                }
            }
        }

        // Check capture quality slider flag.
        boolean captureQualitySlider = sharedPrefGetBoolean(R.string.pref_capturequalityslider_key,
                CallActivity.EXTRA_VIDEO_CAPTUREQUALITYSLIDER_ENABLED,
                R.string.pref_capturequalityslider_default, useValuesFromIntent);

        // Get video and audio start bitrate.
        int videoStartBitrate = 0;
        if (useValuesFromIntent) {
            videoStartBitrate = getIntent().getIntExtra(CallActivity.EXTRA_VIDEO_BITRATE, 0);
        }
        if (videoStartBitrate == 0) {
            String bitrateTypeDefault = getString(R.string.pref_maxvideobitrate_default);
            String bitrateType = sharedPref.getString(keyprefVideoBitrateType, bitrateTypeDefault);
            if (!bitrateType.equals(bitrateTypeDefault)) {
                String bitrateValue = sharedPref.getString(
                        keyprefVideoBitrateValue, getString(R.string.pref_maxvideobitratevalue_default));
                videoStartBitrate = Integer.parseInt(bitrateValue);
            }
        }

        int audioStartBitrate = 0;
        if (useValuesFromIntent) {
            audioStartBitrate = getIntent().getIntExtra(CallActivity.EXTRA_AUDIO_BITRATE, 0);
        }
        if (audioStartBitrate == 0) {
            String bitrateTypeDefault = getString(R.string.pref_startaudiobitrate_default);
            String bitrateType = sharedPref.getString(keyprefAudioBitrateType, bitrateTypeDefault);
            if (!bitrateType.equals(bitrateTypeDefault)) {
                String bitrateValue = sharedPref.getString(
                        keyprefAudioBitrateValue, getString(R.string.pref_startaudiobitratevalue_default));
                audioStartBitrate = Integer.parseInt(bitrateValue);
            }
        }

        // Check statistics display option.
        boolean displayHud = sharedPrefGetBoolean(R.string.pref_displayhud_key,
                CallActivity.EXTRA_DISPLAY_HUD, R.string.pref_displayhud_default, useValuesFromIntent);

        boolean tracing = sharedPrefGetBoolean(R.string.pref_tracing_key, CallActivity.EXTRA_TRACING,
                R.string.pref_tracing_default, useValuesFromIntent);

        // Get datachannel options
        boolean dataChannelEnabled = sharedPrefGetBoolean(R.string.pref_enable_datachannel_key,
                CallActivity.EXTRA_DATA_CHANNEL_ENABLED, R.string.pref_enable_datachannel_default,
                useValuesFromIntent);
        boolean ordered = sharedPrefGetBoolean(R.string.pref_ordered_key, CallActivity.EXTRA_ORDERED,
                R.string.pref_ordered_default, useValuesFromIntent);
        boolean negotiated = sharedPrefGetBoolean(R.string.pref_negotiated_key,
                CallActivity.EXTRA_NEGOTIATED, R.string.pref_negotiated_default, useValuesFromIntent);
        int maxRetrMs = sharedPrefGetInteger(R.string.pref_max_retransmit_time_ms_key,
                CallActivity.EXTRA_MAX_RETRANSMITS_MS, R.string.pref_max_retransmit_time_ms_default,
                useValuesFromIntent);
        int maxRetr =
                sharedPrefGetInteger(R.string.pref_max_retransmits_key, CallActivity.EXTRA_MAX_RETRANSMITS,
                        R.string.pref_max_retransmits_default, useValuesFromIntent);
        int id = sharedPrefGetInteger(R.string.pref_data_id_key, CallActivity.EXTRA_ID,
                R.string.pref_data_id_default, useValuesFromIntent);
        String protocol = sharedPrefGetString(R.string.pref_data_protocol_key,
                CallActivity.EXTRA_PROTOCOL, R.string.pref_data_protocol_default, useValuesFromIntent);

        // Start AppRTCMobile activity.
        Log.d(TAG, "Connecting to room " + roomId + " at URL " + roomUrl);
        if (validateUrl(roomUrl)) {
            Uri uri = Uri.parse(roomUrl);
            Intent intent = new Intent(this, CallActivity.class);
            intent.setData(uri);
            intent.putExtra(CallActivity.EXTRA_ROOMID, roomId);
            intent.putExtra(CallActivity.EXTRA_LOOPBACK, loopback);
            intent.putExtra(CallActivity.EXTRA_VIDEO_CALL, videoCallEnabled);
            intent.putExtra(CallActivity.EXTRA_SCREENCAPTURE, useScreencapture);
            intent.putExtra(CallActivity.EXTRA_CAMERA2, useCamera2);
            intent.putExtra(CallActivity.EXTRA_VIDEO_WIDTH, videoWidth);
            intent.putExtra(CallActivity.EXTRA_VIDEO_HEIGHT, videoHeight);
            intent.putExtra(CallActivity.EXTRA_VIDEO_FPS, cameraFps);
            intent.putExtra(CallActivity.EXTRA_VIDEO_CAPTUREQUALITYSLIDER_ENABLED, captureQualitySlider);
            intent.putExtra(CallActivity.EXTRA_VIDEO_BITRATE, videoStartBitrate);
            intent.putExtra(CallActivity.EXTRA_VIDEOCODEC, videoCodec);
            intent.putExtra(CallActivity.EXTRA_HWCODEC_ENABLED, hwCodec);
            intent.putExtra(CallActivity.EXTRA_CAPTURETOTEXTURE_ENABLED, captureToTexture);
            intent.putExtra(CallActivity.EXTRA_FLEXFEC_ENABLED, flexfecEnabled);
            intent.putExtra(CallActivity.EXTRA_NOAUDIOPROCESSING_ENABLED, noAudioProcessing);
            intent.putExtra(CallActivity.EXTRA_AECDUMP_ENABLED, aecDump);
            intent.putExtra(CallActivity.EXTRA_OPENSLES_ENABLED, useOpenSLES);
            intent.putExtra(CallActivity.EXTRA_DISABLE_BUILT_IN_AEC, disableBuiltInAEC);
            intent.putExtra(CallActivity.EXTRA_DISABLE_BUILT_IN_AGC, disableBuiltInAGC);
            intent.putExtra(CallActivity.EXTRA_DISABLE_BUILT_IN_NS, disableBuiltInNS);
            intent.putExtra(CallActivity.EXTRA_ENABLE_LEVEL_CONTROL, enableLevelControl);
            intent.putExtra(CallActivity.EXTRA_DISABLE_WEBRTC_AGC_AND_HPF, disableWebRtcAGCAndHPF);
            intent.putExtra(CallActivity.EXTRA_AUDIO_BITRATE, audioStartBitrate);
            intent.putExtra(CallActivity.EXTRA_AUDIOCODEC, audioCodec);
            intent.putExtra(CallActivity.EXTRA_DISPLAY_HUD, displayHud);
            intent.putExtra(CallActivity.EXTRA_TRACING, tracing);
            intent.putExtra(CallActivity.EXTRA_CMDLINE, commandLineRun);
            intent.putExtra(CallActivity.EXTRA_RUNTIME, runTimeMs);

            intent.putExtra(CallActivity.EXTRA_DATA_CHANNEL_ENABLED, dataChannelEnabled);

            if (dataChannelEnabled) {
                intent.putExtra(CallActivity.EXTRA_ORDERED, ordered);
                intent.putExtra(CallActivity.EXTRA_MAX_RETRANSMITS_MS, maxRetrMs);
                intent.putExtra(CallActivity.EXTRA_MAX_RETRANSMITS, maxRetr);
                intent.putExtra(CallActivity.EXTRA_PROTOCOL, protocol);
                intent.putExtra(CallActivity.EXTRA_NEGOTIATED, negotiated);
                intent.putExtra(CallActivity.EXTRA_ID, id);
            }

            if (useValuesFromIntent) {
                if (getIntent().hasExtra(CallActivity.EXTRA_VIDEO_FILE_AS_CAMERA)) {
                    String videoFileAsCamera =
                            getIntent().getStringExtra(CallActivity.EXTRA_VIDEO_FILE_AS_CAMERA);
                    intent.putExtra(CallActivity.EXTRA_VIDEO_FILE_AS_CAMERA, videoFileAsCamera);
                }

                if (getIntent().hasExtra(CallActivity.EXTRA_SAVE_REMOTE_VIDEO_TO_FILE)) {
                    String saveRemoteVideoToFile =
                            getIntent().getStringExtra(CallActivity.EXTRA_SAVE_REMOTE_VIDEO_TO_FILE);
                    intent.putExtra(CallActivity.EXTRA_SAVE_REMOTE_VIDEO_TO_FILE, saveRemoteVideoToFile);
                }

                if (getIntent().hasExtra(CallActivity.EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_WIDTH)) {
                    int videoOutWidth =
                            getIntent().getIntExtra(CallActivity.EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_WIDTH, 0);
                    intent.putExtra(CallActivity.EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_WIDTH, videoOutWidth);
                }

                if (getIntent().hasExtra(CallActivity.EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_HEIGHT)) {
                    int videoOutHeight =
                            getIntent().getIntExtra(CallActivity.EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_HEIGHT, 0);
                    intent.putExtra(CallActivity.EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_HEIGHT, videoOutHeight);
                }
            }

            startActivityForResult(intent, CONNECTION_REQUEST);
        }
    }
}
