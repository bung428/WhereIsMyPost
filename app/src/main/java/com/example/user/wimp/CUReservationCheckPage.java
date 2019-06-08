package com.example.user.wimp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CUReservationCheckPage extends AppCompatActivity {

    Button backBtn, nextBtn;
    TextView boxcountTv, postpriceTv, sendernameTv, senderphonenumTv, senderaddressTv, sendermessageTv, receivernameTv, receiverphonenumTv, receiveraddressTv, receiverboxcountTv, receiverpostinfoTv, howtopayTv, contentpriceTv, contentinfoTv, reservationnameTv;

    String senderInfo, receiverInfo, contentInfo;
    String sender, senderphonenum, senderaddr, sendermsg, receiver, receiverphonenum, receiveraddr, howtopaymoney, postcategory, postprice, postreservationname, reservewant;
    String[] senderdata;
    String[] receiverdata;
    String[] contentdata;
    String[] senderaddrdata;
    String[] receiveraddrdata;
    String senderaddrnum, senderaddrmid, receiveraddrnum, receiveraddrmid;

    ServerIP serverIP;
    final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.99 Safari/537.36";

    ArrayList<String> num = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cureservationcheckpage);

        backBtn = findViewById(R.id.backBtn);
        nextBtn = findViewById(R.id.nextBtn);
//        boxcountTv = findViewById(R.id.boxcountTv);
//        postpriceTv = findViewById(R.id.postpriceTv);
        sendernameTv = findViewById(R.id.sendernameTv);
        senderphonenumTv = findViewById(R.id.senderphonenumTv);
        senderaddressTv = findViewById(R.id.senderaddressTv);
        sendermessageTv = findViewById(R.id.sendermessageTv);
        receivernameTv = findViewById(R.id.receivernameTv);
        receiverphonenumTv = findViewById(R.id.receiverphonenumTv);
        receiveraddressTv = findViewById(R.id.receiveraddressTv);
//        receiverboxcountTv = findViewById(R.id.receiverboxcountTv);
//        receiverpostinfoTv = findViewById(R.id.receiverpostinfoTv);
        howtopayTv = findViewById(R.id.howtopayTv);
        contentpriceTv = findViewById(R.id.contentpriceTv);
        contentinfoTv = findViewById(R.id.contentinfoTv);
        reservationnameTv = findViewById(R.id.reservationnameTv);

        SharedPreferences preferences = getSharedPreferences("reservation", MODE_PRIVATE);
        if(preferences.getString("senderInfo", null) != null && preferences.getString("receiverInfo", null) != null && preferences.getString("contentInfo", null) != null){
            senderInfo = preferences.getString("senderInfo", null);
            receiverInfo = preferences.getString("receiverInfo", null);
            contentInfo = preferences.getString("contentInfo", null);
            Log.d("확인 페이지", senderInfo + "/" + receiverInfo + "/" + contentInfo);

            senderdata = senderInfo.split("##");
            receiverdata = receiverInfo.split("##");
            contentdata = contentInfo.split("##");

            senderaddrdata = senderdata[2].split("\\s");
            senderaddrnum = senderaddrdata[0].substring(1,6);
            Log.d("보낸이 주소 ", "우편번호? " + senderaddrnum);
            senderaddrmid = senderaddrdata[1] + " " + senderaddrdata[2] + " " + senderaddrdata[3] + " " + senderaddrdata[4];
            Log.d("보낸이 주소 ", "중간? " + senderaddrmid);
            Log.d("보낸이 주소 ", "마지막? " + senderaddrdata[5]);

            receiveraddrdata = receiverdata[2].split("\\s");
            receiveraddrnum = receiveraddrdata[0].substring(1,6);
            Log.d("받는이 주소 ", "우편번호? " + receiveraddrnum);
            receiveraddrmid = receiveraddrdata[1] + " " + receiveraddrdata[2] + " " + receiveraddrdata[3] + " " + receiveraddrdata[4];
            Log.d("받는이 주소 ", "중간? " + receiveraddrmid);
            Log.d("받는이 주소 ", "마지막? " + receiveraddrdata[5]);

            howtopayTv.setText(receiverdata[4]);
            contentpriceTv.setText(contentdata[1] + "만원");
            contentinfoTv.setText(contentdata[0]);
            reservationnameTv.setText(contentdata[2]);

            sendernameTv.setText(senderdata[0]);
            senderphonenumTv.setText(senderdata[1]);
            senderaddressTv.setText(senderdata[2]);
            sendermessageTv.setText(receiverdata[3]);

            receivernameTv.setText(receiverdata[0]);
            receiverphonenumTv.setText(receiverdata[1]);
            receiveraddressTv.setText(receiverdata[2]);
//            receiverboxcountTv.setText(receiverdata[4]);
//            receiverpostinfoTv.setText(info[0]+"( " + info[1] + "원 )");
            sender = senderdata[0];
            senderphonenum = senderdata[1];
            senderaddr = senderdata[2];
            sendermsg = receiverdata[3];

            receiver = receiverdata[0];
            receiverphonenum = receiverdata[1];
            receiveraddr = receiverdata[2];
            howtopaymoney = receiverdata[4];

            postcategory = contentdata[0];
            postprice = contentdata[1];
            postreservationname = contentdata[2];
            reservewant = "true";
        }

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(CUReservationCheckPage.this, CUReservationEditReceiver.class);
                finish();
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //예약
                LoginPangTask loginPangTask = new LoginPangTask();
                loginPangTask.execute();
//                SaveData saveData = new SaveData();
//                saveData.execute(sender,senderphonenum,senderaddr,postcategory,postprice,postreservationname,receiver,receiverphonenum,receiveraddr,sendermsg,howtopaymoney);
            }
        });
    }

    private class LoginPangTask extends AsyncTask<Void, ArrayList<String>, ArrayList<String>> {

        @Override
        protected void onPostExecute(ArrayList<String> result) {
            super.onPostExecute(result);
//            for (int i = 0; i < result.size(); i++)
//                System.out.println("온 포스트"+result.get(i));

            Intent intent = new Intent(CUReservationCheckPage.this, Reservation.class);
            intent.putStringArrayListExtra("numlist", result);
            startActivity(intent);
        }

        @Override
        protected ArrayList<String> doInBackground(Void... voids) {
            try {
                org.jsoup.Connection.Response response = Jsoup.connect("https://www.cupost.co.kr/postbox/common/login.cupost")
                        .method(org.jsoup.Connection.Method.GET)
                        .timeout(5000)
                        .header("User-Agent",USER_AGENT)
                        .header("Referer","https://www.cupost.co.kr/postbox/main.cupost")
                        .header("Origin", "https://login.coupang.com")
                        .header("Upgrade-Insecure-Requests","1")
                        .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
                        .header("Accept-Encoding","gzip, deflate, br")
                        .header("Accept-Language","ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7")
                        .header("Connection","keep-alive")
                        .ignoreContentType(true)
                        .execute();

                Map<String, String> loginTryCookie = response.cookies();
                Log.d("jsoup",loginTryCookie.toString());

                Map<String, String> data = new HashMap<>();
                data.put("returnUrl", "");

                org.jsoup.Connection.Response loginResponse = Jsoup.connect("https://www.cupost.co.kr/postbox/common/logon.cupost")
                        .method( org.jsoup.Connection.Method.POST)
                        .timeout(7000)
                        .header("User-Agent",USER_AGENT)
                        .header("Referer","https://www.cupost.co.kr/postbox/local/member/reservation.cupost")
                        .header("Connection","keep-alive")
                        .header("Origin","https://www.cupost.co.kr")
                        .header("Content-Type","application/x-www-form-urlencoded")
//                        .header("Content-Length","738")
                        .header("Upgrade-Insecure-Requests","1")
                        .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
                        .header("Accept-Encoding","gzip, deflate, br")
                        .header("Accept-Language","ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7")
                        .ignoreContentType(true)
                        .cookies(loginTryCookie)
                        .data(data)
                        .execute();

                Document main = Jsoup.connect("https://www.cupost.co.kr/postbox/main.cupost")
                        .timeout(7000)
                        .userAgent(USER_AGENT)
                        .header("Upgrade-Insecure-Requests","1")
                        .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
                        .header("Accept-Encoding","gzip, deflate, br")
                        .header("Accept-Language","ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7")
                        .header("Referer", "https://www.cupost.co.kr/postbox/common/login.cupost")
                        .header("Connection", "keep-alive")
                        .cookies(loginTryCookie)
                        .get();
//                System.out.println("메인떳니???"+main);

                Map<String, String> sessioncookie = loginResponse.cookies();

                Map<String, String> reservdata = new HashMap<>();
                if(contentdata[0].equals("의류"))
                    reservdata.put("goods_kind", "01");//카테고리 01=의류 02=....
                else if(contentdata[0].equals("서신/서류"))
                    reservdata.put("goods_kind", "02");
                else if(contentdata[0].equals("가전제품류"))
                    reservdata.put("goods_kind", "03");
                else if(contentdata[0].equals("과일류"))
                    reservdata.put("goods_kind", "04");
                else if(contentdata[0].equals("곡물류"))
                    reservdata.put("goods_kind", "05");
                else if(contentdata[0].equals("한약류"))
                    reservdata.put("goods_kind", "06");
                else if(contentdata[0].equals("식품류"))
                    reservdata.put("goods_kind", "07");
                else if(contentdata[0].equals("잡화/서적"))
                    reservdata.put("goods_kind", "08");
                else if(contentdata[0].equals("편의점행사상품"))
                    reservdata.put("goods_kind", "09");
                reservdata.put("trans_code", "1");
                reservdata.put("reserved_counter", "1");
                reservdata.put("real_sender_telno", senderdata[1]);//보낼이 번호
                reservdata.put("exemption_agree", "Y");
                reservdata.put("max_counter", "1");
                reservdata.put("goods_price", contentdata[1]);//물품가격
                reservdata.put("reserved_comments", contentdata[2]);//예약명
                reservdata.put("addSel", "on");
                reservdata.put("real_sender_name", senderdata[0]);//보낼이 이름
                reservdata.put("real_sender_post_no", senderaddrnum);//우편번호
                reservdata.put("real_sender_addr", senderaddrmid);//주소중간내용
                reservdata.put("real_sender_detaddr", senderaddrdata[5]);//상세주소
                reservdata.put("receiver_telno_0", receiverdata[1]);//받을분번호
                reservdata.put("receiver_telno2_0", "");
                reservdata.put("receiver_name_0", receiverdata[0]);//받는이 이름
                reservdata.put("receiver_postno_0", receiveraddrnum);//받을분 우편번호
                reservdata.put("receiver_addr_0", receiveraddrmid);//받을분 주소 중간내용
                reservdata.put("receiver_detail_addr_0", receiveraddrdata[5]);//받을분 상세주소
                reservdata.put("special_contents_0", receiverdata[3]);//메시지
                if(receiverdata[4].equals("선불"))
                    reservdata.put("pay_flag_0", "1");//지불방법 1=선불 2= 착불
                else if(receiverdata[4].equals("착불"))
                    reservdata.put("pay_flag_0", "2");

//                SharedPreferences preferences = getSharedPreferences("reservationstate", MODE_PRIVATE);
//                SharedPreferences.Editor editor = preferences.edit();
//
//                editor.putString("reservName",contentdata[2]);
//                editor.putString("category",contentdata[0]);
//                editor.putString("contentPrice",contentdata[1]);
//                editor.putString("sender",senderdata[0]);
//                editor.putString("senderPhone",senderdata[1]);
//                editor.putString("senderAddr",senderdata[2]);
//                editor.putString("receiver",receiverdata[0]);
//                editor.putString("receiverPhone",receiverdata[1]);
//                editor.putString("receiverAddr",receiverdata[2]);
//                editor.putString("message",receiverdata[3]);
//                editor.putString("howtopay",receiverdata[4]);
//                editor.commit();

                org.jsoup.Connection.Response reservationResponse = Jsoup.connect("https://www.cupost.co.kr/postbox/local/member/reservationReg.cupost")
                        .method( org.jsoup.Connection.Method.POST)
                        .timeout(7000)
                        .header("User-Agent",USER_AGENT)
                        .header("Referer","https://www.cupost.co.kr/postbox/main.cupost")
                        .header("Connection","keep-alive")
                        .header("Upgrade-Insecure-Requests","1")
                        .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
                        .header("Accept-Encoding","gzip, deflate, br")
                        .header("Accept-Language","ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7")
                        .ignoreContentType(true)
                        .cookies(loginTryCookie)
                        .data(reservdata)
                        .execute();

                Map<String, String> reservcookie = reservationResponse.cookies();

                Document adminPageDocument = Jsoup.connect("https://www.cupost.co.kr/postbox/local/member/reservationList.cupost")
                        .timeout(7000)
                        .userAgent(USER_AGENT)
                        .header("Upgrade-Insecure-Requests","1")
                        .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
                        .header("Accept-Encoding","gzip, deflate, br")
                        .header("Accept-Language","ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7")
                        .header("Referer", "https://www.cupost.co.kr/postbox/local/member/reservationReg.cupost")
                        .header("Connection", "keep-alive")
                        .cookies(loginTryCookie)
                        .get();

                Elements table = adminPageDocument.select("table[class=tableType2 mt10]");
                Elements rows = table.select("tr");
                Elements a;
                for (int i = 1; i < rows.size(); i++) { //first row is the col names so skip it.
                    Element row = rows.get(i);
                    Elements cols = row.select("td");
                    a = cols.select("a");

//                    System.out.println("a만이니????"+a.get(0).text());
                    num.add(a.get(0).text());
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            return num;
        }
    }

    private class SaveData2 extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(CUReservationCheckPage.this,
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
                Toast.makeText(CUReservationCheckPage.this, "error", Toast.LENGTH_SHORT).show();
            }
            else {
                if(result.toString().equals("success to insert sql")) {
                    Toast.makeText(CUReservationCheckPage.this, "save", Toast.LENGTH_SHORT).show();

                    SaveData2 saveData2 = new SaveData2();
                    saveData2.execute(sender,receiver);
                }
                else if(result.toString().equals("fail to insert sql"))
                    Toast.makeText(CUReservationCheckPage.this, "fail", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected String doInBackground(String... params) {

            String sender = params[0];
            String receiver = params[1];

            String serverURL = serverIP.serverIp+"/wimp/savereserv.php";
            String postParameters = "sender=" + sender + "&receiver=" + receiver;

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

    private class SaveData extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(CUReservationCheckPage.this,
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
                Toast.makeText(CUReservationCheckPage.this, "error", Toast.LENGTH_SHORT).show();
            }
            else {
                if(result.toString().equals("success to insert sql"))
                    Toast.makeText(CUReservationCheckPage.this, "save", Toast.LENGTH_SHORT).show();
                else if(result.toString().equals("fail to insert sql"))
                    Toast.makeText(CUReservationCheckPage.this, "fail", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected String doInBackground(String... params) {

            String sender = params[0];
            String senderphone = params[1];
            String senderaddr = params[2];
            String postcategory = params[3];
            String postprice = params[4];
            String postreservname = params[5];
            String receiver = params[6];
            String receiverphone = params[7];
            String receiveraddr = params[8];
            String message = params[9];
            String howtopay = params[10];


            String serverURL = serverIP.serverIp+"/wimp/savereservation.php";
            String postParameters = "sender=" + sender + "&senderphone=" + senderphone + "&senderaddr=" + senderaddr + "&postcategory=" + postcategory + "&postprice=" + postprice +
                    "&postreservname=" + postreservname + "&receiver=" + receiver + "&receiverphone=" + receiverphone + "&receiveraddr=" + receiveraddr + "&message=" + message + "&howtopay=" + howtopay;

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
