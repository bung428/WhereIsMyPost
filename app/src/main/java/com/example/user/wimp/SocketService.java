package com.example.user.wimp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.Set;

public class SocketService extends Service {

    final DBHelper dbHelper = new DBHelper(this, "ChatSave.db", null, 1);

    String ip="115.71.232.235",port="9999",test,id;
    boolean inChatRoom = false;
    ArrayList<String> loginUser;

    IBinder iBinder = new SocketServiceBinder();

    Socket socket;
    ReceiveThread receive;
    SendThread send;
    SocketClient client;
    LinkedList<SocketClient> threadList;
    DataOutputStream output = null;

    public static final int MSG_REGISTER_CLIENT = 1;
    public static final int MSG_SEND_TO_SERVICE_FLAG = 2;
    public static final int MSG_SEND_TO_SERVICE = 3;
    public static final int MSG_SEND_TO_ACTIVITY = 4;
    public static final int MSG_SEND_TO_SERVICE_CALL_FLAG = 5;

    private Messenger mClient = null;

    class SocketServiceBinder extends Binder {
        SocketService getService() { // 서비스 객체를 리턴
            return SocketService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }

    private final Messenger mMessenger = new Messenger(new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            Log.w("test","Myservice - message what : "+msg.what +" , msg.obj "+ msg.obj);
            switch (msg.what) {
                case MSG_REGISTER_CLIENT:
                    mClient = msg.replyTo;  // activity로부터 가져온
                    break;
                case MSG_SEND_TO_SERVICE:
                    mClient = msg.replyTo;  // activity로부터 가져온
                    test=msg.obj.toString();
                    send = new SendThread(socket,test);
                    send.start();
                    break;
                case MSG_SEND_TO_SERVICE_FLAG:
                    mClient = msg.replyTo;
                    Log.d("flag", "message what? "+msg.obj.toString());
                    if(msg.obj.toString().equals("true")){
                        inChatRoom = true;
                    }else if (msg.obj.toString().equals("false")){
                        inChatRoom = false;
                    }
                    break;
                case MSG_SEND_TO_SERVICE_CALL_FLAG:
                    mClient = msg.replyTo;
                    Log.d("flag", "message what? in flag "+msg.obj.toString());
                    test=msg.obj.toString();
                    JSONObject jsonObject;
                    try {
                        jsonObject = new JSONObject(test);
                        String message = jsonObject.getString("message");
                        if(message.contains("false")){
                            send = new SendThread(socket,test);
                            send.start();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    break;
            }
            return false;
        }
    }));

    private void sendMsgToActivity(String sendValue) {
        try {
            Bundle bundle = new Bundle();
            bundle.putString("fromService", sendValue);
            Message msg = Message.obtain(null, MSG_SEND_TO_ACTIVITY);
            msg.setData(bundle);
            mClient.send(msg);      // msg 보내기
        } catch (RemoteException e) {
        } catch (NullPointerException e){
        }
    }

    private void sendMessage(String _sender, String _receiver, String _roomname, String _message, String _date) {
        Intent intent = new Intent("lasttext");
        intent.putExtra("sender", _sender);
        intent.putExtra("receiver", _receiver);
        intent.putExtra("roomname", _roomname);
        intent.putExtra("message", _message);
        intent.putExtra("date", _date);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        threadList = new LinkedList<SocketClient>();

        SharedPreferences preferences = getSharedPreferences("auto", MODE_PRIVATE);

        Set<String> set = preferences.getStringSet("userinfo", null);
        loginUser = new ArrayList<>(set);

        Log.d("checkbox", loginUser.get(0).toString());
        String[] loginData = loginUser.get(0).split("@@@@");
        id = loginData[0];
        //쉐어드로 로그인된 사람 정보 얻어내야함
        client = new SocketClient(ip,port);
        threadList.add(client);
        client.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        client = new SocketClient(ip,port);
//        threadList.add(client);
//        client.start();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    class SocketClient extends Thread {
        boolean threadAlive;
        String ip;
        String port;

        //InputStream inputStream = null;
        OutputStream outputStream = null;
        BufferedReader br = null;

        private DataOutputStream output = null;

        public SocketClient(String ip, String port) {
            threadAlive = true;
            this.ip = ip;
            this.port = port;
        }

        @Override
        public void run() {

            try {
                // 연결후 바로 ReceiveThread 시작
                socket = new Socket(ip, Integer.parseInt(port));
                //inputStream = socket.getInputStream();
                output = new DataOutputStream(socket.getOutputStream());
                receive = new ReceiveThread(socket);
                receive.start();

                Log.d("in my service",id);
                JSONObject jsonObject=new JSONObject();
                jsonObject.put("header", "userInfo");
                jsonObject.put("sender",id);

                //mac 전송
                output.writeUTF(jsonObject.toString());

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    class ReceiveThread extends Thread {
        private Socket socket = null;
        DataInputStream input;

        public ReceiveThread(Socket socket) {
            this.socket = socket;
            try{
                input = new DataInputStream(socket.getInputStream());
            }catch(Exception e){
            }
        }
        // 메세지 수신후 Handler로 전달
        public void run() {
            try {
                while (input != null) {

                    String msg = input.readUTF();
                    if (msg != null) {
                        Log.d(ACTIVITY_SERVICE, "test");
                        Log.d("in myservice ",msg);

                        sendMsgToActivity(msg);
                        JSONObject jsonObject=new JSONObject(msg);
                        String receiver = jsonObject.getString("receiver");
                        String sender = jsonObject.getString("sender");
                        String message = jsonObject.getString("message");
                        String roomname = sender+"##"+receiver;

//                        if(message.contains("영상통화")){
//                            Intent i = new Intent(SocketService.this, ConnectActivity.class);
//                            i.putExtra("facecall", message);
//                            i.putExtra("facecall-sender", sender);
//                            startActivity(i);
//                        }

                        String channelId = "channel";
                        String channelName = "Channel Name";

                        long now = System.currentTimeMillis();
                        Date date = new Date(now);
                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                        String getTime = sdf.format(date);

                        sendMessage(sender,receiver,roomname,message,getTime);

                        if(message.contains(".jpg") || message.contains(".JPG") || message.contains(".png") || message.contains(".PNG")){
                            message = "http://115.71.232.235/wimp/uploadimage/"+message;
                            dbHelper.insert(sender,id,message,getTime,false);
                        } else if(message.contains("우리 영상통화해요.")) {
                            String[] data = message.split("~~");
                            dbHelper.insert(sender,id,data[0],getTime,true);
                        } else {
                            dbHelper.insert(sender,id,message,getTime,true);
                        }

                        Intent intent = new Intent(SocketService.this, ChatRoom.class);
                        intent.putExtra("msg", message); //전달할 값
                        intent.putExtra("sender", sender); //전달할 값
//                        if(!images.equals("")) {
//                            intent.putExtra("image", images); //전달할 값
//                        }

                        PendingIntent contentIntent = PendingIntent.getActivity(SocketService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//
                        if(!inChatRoom) {
                            if (id.equals(receiver)) {
                                String senders = jsonObject.getString("sender");
                                String messages = jsonObject.getString("message");
                                Log.d("in my service", "i'm receiver");
                                Log.d("in my service", messages);

                                NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                int importance = NotificationManager.IMPORTANCE_HIGH;

                                NotificationChannel mChannel = null;
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    mChannel = new NotificationChannel(
                                            channelId, channelName, importance);
                                }

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    nm.createNotificationChannel(mChannel);
                                }
                                if(message.contains(".jpg") || message.contains(".JPG") || message.contains(".png") || message.contains(".PNG")){
                                    NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), channelId)
                                            .setContentTitle("상태바 드래그시 보이는 타이틀")
                                            .setContentTitle(senders)
                                            .setContentText("사진")
                                            .setSmallIcon(R.drawable.nine)
                                            .setTicker("메시지가 도착하였습니다.")
                                            .setAutoCancel(true)
                                            .setContentIntent(contentIntent);

                                    nm.notify(0, builder.build());
                                }else{
                                    NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), channelId)
                                            .setContentTitle("상태바 드래그시 보이는 타이틀")
                                            .setContentTitle(senders)
                                            .setContentText(messages)
                                            .setSmallIcon(R.drawable.nine)
                                            .setTicker("메시지가 도착하였습니다.")
                                            .setAutoCancel(true)
                                            .setContentIntent(contentIntent);

                                    nm.notify(0, builder.build());
                                }
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    class SendThread extends Thread {
        private Socket socket;
        String sendmsg;
        DataOutputStream output;

        public SendThread(Socket socket, String text) {
            this.socket = socket;
            this.sendmsg = text;
            try {
                output = new DataOutputStream(socket.getOutputStream());
            } catch (Exception e) {
            }
        }

        public void run() {

            try {
                // 메세지 전송부 (누군지 식별하기위한 방법으로 mac를 사용)
                Log.d(ACTIVITY_SERVICE, "11111");

                if (output != null) {
                    if (sendmsg != null) {
                        Log.d("send쓰레드다!!!", sendmsg);
                        output.writeUTF(sendmsg);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NullPointerException npe) {
                npe.printStackTrace();

            }
        }
    }
}
