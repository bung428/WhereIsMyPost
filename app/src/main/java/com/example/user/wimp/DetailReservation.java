package com.example.user.wimp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
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

public class DetailReservation extends AppCompatActivity {

    TextView reservationnumTv, reservationnameTv, categoryTv, contentpriceTv, sendernameTv, senderphonenumTv, senderaddressTv, receivernameTv, receiverphonenumTv, receiveraddressTv, messageTv, howtopayTv;
    String intentdata;
    ArrayList<String> crawldata = new ArrayList<>();
    final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.99 Safari/537.36";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detailreservation);

        reservationnumTv = findViewById(R.id.reservationnumTv);
        reservationnameTv = findViewById(R.id.reservationnameTv);
        categoryTv = findViewById(R.id.categoryTv);
        contentpriceTv = findViewById(R.id.contentpriceTv);
        sendernameTv = findViewById(R.id.sendernameTv);
        senderphonenumTv = findViewById(R.id.senderphonenumTv);
        senderaddressTv = findViewById(R.id.senderaddressTv);
        receivernameTv = findViewById(R.id.receivernameTv);
        receiverphonenumTv = findViewById(R.id.receiverphonenumTv);
        receiveraddressTv = findViewById(R.id.receiveraddressTv);
        messageTv = findViewById(R.id.messageTv);
        howtopayTv = findViewById(R.id.howtopayTv);

        Intent i = getIntent();
        if(i.getStringExtra("whatdata") != null){
            intentdata = i.getStringExtra("whatdata");
            Toast.makeText(DetailReservation.this, intentdata, Toast.LENGTH_SHORT).show();
            LoginPangTask loginPangTask = new LoginPangTask();
            loginPangTask.execute();
        }
    }

    private class LoginPangTask extends AsyncTask<Void, ArrayList<String>, ArrayList<String>> {
        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(DetailReservation.this,
                    "Please Wait", null, true, true);
        }

        @Override
        protected void onPostExecute(ArrayList<String> result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
            for (int i = 0; i < result.size(); i++)
                System.out.println("온 포스트 받았나?"+result.get(i));

            reservationnumTv.setText(result.get(0));
            reservationnameTv.setText(result.get(1));
            categoryTv.setText(result.get(2));
            contentpriceTv.setText(result.get(3));
            sendernameTv.setText(result.get(4));
            senderphonenumTv.setText(result.get(5));
            senderaddressTv.setText(result.get(6));
            receivernameTv.setText(result.get(7));
            receiverphonenumTv.setText(result.get(8));
            receiveraddressTv.setText(result.get(9));
            messageTv.setText(result.get(10));
            howtopayTv.setText(result.get(11));
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
                reservdata.put("page", "");
                reservdata.put("reserved_no", intentdata);

                Document adminPageDocument = Jsoup.connect("https://www.cupost.co.kr/postbox/local/member/reservationView.cupost")
                        .timeout(7000)
                        .userAgent(USER_AGENT)
                        .header("Upgrade-Insecure-Requests","1")
                        .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
                        .header("Accept-Encoding","gzip, deflate, br")
                        .header("Accept-Language","ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7")
                        .header("Referer", "https://www.cupost.co.kr/postbox/local/member/reservationReg.cupost")
                        .header("Connection", "keep-alive")
                        .cookies(loginTryCookie)
                        .data(reservdata)
                        .get();

//                System.out.println("메인떳니???"+adminPageDocument);
                //인덱스아웃오브 떳다
                Element table = adminPageDocument.select("table[class=tableType1]").get(0);
                Element trzero = table.select("tr").get(0);
                Element tdzeronum = trzero.select("td").get(0);
                Element tdzeroname = trzero.select("td").get(1);

                Element trsecond = table.select("tr").get(2);
                Element tdsecondcategory = trsecond.select("td").get(1);

                Element trthird = table.select("tr").get(3);
                Element trthirdprice = trthird.select("td").get(0);
//
                Element table1 = adminPageDocument.select("table[class=tableType1]").get(1);
                Element trzero1 = table.select("tr").get(0);
                Element tdzero1sender = trzero1.select("td").get(0);
                Element tdzero1phone = trzero1.select("td").get(1);

                Element trsecond1 = table1.select("tr").get(1);
                Element trsecond1addr = trsecond1.select("td").get(0);

                Element table2 = adminPageDocument.select("table[class=tableType1]").get(2);
                Element trzero2 = table2.select("tr").get(0);
                Element tdzero2receiver = trzero2.select("td").get(0);
                Element tdzero2phone = trzero2.select("td").get(1);

                Element trsecond2 = table2.select("tr").get(1);
                Element trsecond2addr = trsecond2.select("td").get(0);

                Element trthird2 = table2.select("tr").get(2);
                Element trthird2msg = trthird2.select("td").get(0);

                Element trforth2 = table2.select("tr").get(3);
                Element trforth2pay = trforth2.select("td").get(0);

                String reservationNum = tdzeronum.text();
                String reservationName = tdzeroname.text();
                String category = tdsecondcategory.text();
                String price = trthirdprice.text();
                String sender = tdzero1sender.text();
                String sender_phone = tdzero1phone.text();
                String sender_addr = trsecond1addr.text();
                String receiver = tdzero2receiver.text();
                String receiver_phone = tdzero2phone.text();
                String receiver_addr = trsecond2addr.text();
                String message = trthird2msg.text();
                String howtopay = trforth2pay.text();
//                Log.d("정ㅇ보?", tdzeronum + "/" + tdzeroname + "/" + trthirdcategory + "/" + tdzero1sender + "/" + tdzero1phone + "/" + trsecond1addr + "/" + tdzero2receiver + "/" + tdzero2phone + "/" + trsecond2addr + "/" + trthird2msg + "/" + trforth2pay);
                Log.d("정보", reservationNum + "/////" +reservationName + "/////" +category + "/////" +price + "/////" +sender + "/////" +sender_phone + "/////" +sender_addr + "/////" +receiver + "/////" +receiver_phone + "/////" +receiver_addr + "/////" +message + "/////" +howtopay);
//                crawldata = reservationNum + "/////" +reservationName + "/////" +category + "/////" +price + "/////" +sender + "/////" +sender_phone + "/////" +sender_addr + "/////" +receiver + "/////" +receiver_phone + "/////" +receiver_addr + "/////" +message + "/////" +howtopay;
                crawldata.add(reservationNum);
                crawldata.add(reservationName);
                crawldata.add(category);
                crawldata.add(price);
                crawldata.add(sender);
                crawldata.add(sender_phone);
                crawldata.add(sender_addr);
                crawldata.add(receiver);
                crawldata.add(receiver_phone);
                crawldata.add(receiver_addr);
                crawldata.add(message);
                crawldata.add(howtopay);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return crawldata;
        }
    }
}
