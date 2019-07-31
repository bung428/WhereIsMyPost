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

import com.google.gson.Gson;

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

public class CUReservationCheckPage extends AppCompatActivity {

    Button backBtn, nextBtn;
    TextView boxcountTv, postpriceTv, sendernameTv, senderphonenumTv, senderaddressTv, sendermessageTv, receivernameTv, receiverphonenumTv, receiveraddressTv, receiverboxcountTv, receiverpostinfoTv, howtopayTv, contentpriceTv, contentinfoTv, reservationnameTv;

    String senderInfo, receiverInfo, contentInfo;
    String sender, senderphonenum, senderaddr, sendermsg, receiver, receiverphonenum, receiveraddr, howtopaymoney, postprice, postreservationname;
    String[] senderdata;
    String[] receiverdata;
    String[] contentdata;
    String[] senderaddrdata;
    String[] senderphonedata;
    String[] receiveraddrdata;
    String[] receiverphonedata;
    String senderaddrnum, senderaddrmid, receiveraddrnum, receiveraddrmid, TAG = "CU 편의점 예약 전 페이지";
    int postcategory;

    ServerIP serverIP;
    APIService apiService;
    final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.99 Safari/537.36";

    ArrayList<String> num = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cureservationcheckpage);

        backBtn = findViewById(R.id.backBtn);
        nextBtn = findViewById(R.id.nextBtn);
        sendernameTv = findViewById(R.id.sendernameTv);
        senderphonenumTv = findViewById(R.id.senderphonenumTv);
        senderaddressTv = findViewById(R.id.senderaddressTv);
        sendermessageTv = findViewById(R.id.sendermessageTv);
        receivernameTv = findViewById(R.id.receivernameTv);
        receiverphonenumTv = findViewById(R.id.receiverphonenumTv);
        receiveraddressTv = findViewById(R.id.receiveraddressTv);
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
            //[0] : 이름 / [1] : 전번 - 로 split해야함 / [2] : 주소 [3] [4]
            receiverdata = receiverInfo.split("##");
            //[0] 이름 / [1] 번호 - split go / [2] 주소 [3] [4] / [5] 배송요청사항 / [6] 선불
            contentdata = contentInfo.split("##");
            // [0] 카테고리 / [1] 가격 / [2] 예약명

            senderphonedata = senderdata[1].split("-");
            Log.d(TAG, "발신자 이름: "+senderdata[0]);
            Log.d(TAG, "발신자 번호: "+senderphonedata[0] + "-" + senderphonedata[1] + "-" + senderphonedata[2]);
            Log.d(TAG, "발신자 주소: "+senderdata[2] + ", " + senderdata[3] + ", " + senderdata[4]);

            receiverphonedata = receiverdata[1].split("-");
            Log.d(TAG, "수신자 이름: "+receiverdata[0]);
            Log.d(TAG, "수신자 번호: "+receiverphonedata[0] + "-" + receiverphonedata[1] + "-" + receiverphonedata[2]);
            Log.d(TAG, "수신자 주소: "+receiverdata[2] + ", " + receiverdata[3] + ", " + receiverdata[4]);
            Log.d(TAG, "수신자 배송요청사항: "+receiverdata[5]);
            Log.d(TAG, "수신자 선불: "+receiverdata[4]);

            howtopayTv.setText(receiverdata[6]);
            contentpriceTv.setText(contentdata[1] + "만원");
            contentinfoTv.setText(contentdata[0]);
            reservationnameTv.setText(contentdata[2]);

            sendernameTv.setText(senderdata[0]);
            senderphonenum = senderphonedata[0] + "-" + senderphonedata[1] + "-" + senderphonedata[2];
            senderphonenumTv.setText(senderphonenum);
            senderaddr = senderdata[2] + ", " + senderdata[3] + ", " + senderdata[4];
            senderaddressTv.setText(senderaddr);
            sendermessageTv.setText(receiverdata[5]);

            receivernameTv.setText(receiverdata[0]);
            receiverphonenum = receiverphonedata[0] + "-" + receiverphonedata[1] + "-" + receiverphonedata[2];
            receiverphonenumTv.setText(receiverphonenum);
            receiveraddr = receiverdata[2] + ", " + receiverdata[3] + ", " + receiverdata[4];
            receiveraddressTv.setText(receiveraddr);

            sender = senderdata[0];
            sendermsg = receiverdata[5];
            receiver = receiverdata[0];
            receiverphonenum = receiverdata[1];
            howtopaymoney = receiverdata[6];

            switch (contentdata[0]) {
                case "의류" :
                    postcategory = 1;
                    break;
                case "서신/서류" :
                    postcategory = 2;
                    break;
                case "가전제품류" :
                    postcategory = 3;
                    break;
                case "과일류" :
                    postcategory = 4;
                    break;
                case "곡물류" :
                    postcategory = 5;
                    break;
                case "한약류" :
                    postcategory = 6;
                    break;
                case "식품류" :
                    postcategory = 7;
                    break;
                case "잡화/서적" :
                    postcategory = 8;
                    break;
            }
            postprice = contentdata[1];
            postreservationname = contentdata[2];
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
                // 로그인 먼저
                LoginDialog loginDialog = new LoginDialog(CUReservationCheckPage.this);
                loginDialog.setDialogListener(new LoginDialog.LoginDialogListner() {
                    @Override
                    public void onPositiveClicked(String id, String pw) {
                        if (id != "" && pw != "") {
                            Log.d(TAG, "onPositiveClicked: 하위 ! ");
                            reserve(id, pw);
                        }
                    }
                });

                loginDialog.show();
            }
        });
    }

    public void reserve (String id, String pw) {
        Observable.just("")
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.io())
                .map(new Function<String, Boolean>()
                {
                    @Override
                    public Boolean apply(String s) throws Exception
                    {
                        try
                        {
                            apiService = APIClient.getClient1().create(APIService.class);

                            Call<ResponseBody> callServer = apiService.reserveCU(id, pw, postcategory+"", postprice,postreservationname,sender,
                                    receiverphonedata[0],receiverphonedata[1],receiverphonedata[2],receiverdata[2],receiverdata[3],receiverdata[4],receiverdata[5]);

                            callServer.enqueue(new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                    try {
                                        String msg = response.body().string();
                                        Log.d(TAG, "onResponse: 서버로부터 응답 in retrofit "+msg);
                                        Intent i;
                                        switch (msg){
                                            case "success":
                                                i = new Intent(CUReservationCheckPage.this, Reservation.class);
                                                startActivity(i);
                                                finish();
                                                break;
                                            case "fail":
                                                break;
                                            case "nothing" :
                                                i = new Intent(CUReservationCheckPage.this, Reservation.class);
                                                startActivity(i);
                                                finish();
                                                break;
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                    // 콜백 실패
                                    Log.d(TAG, "onFailure: 콜백 실패");
                                    Log.e(TAG, "onFailure: ", t);
                                }
                            });
                        }
                        catch(Exception e) {
                            // 통신 실패
                            Log.d(TAG, "onFailure: 실패2");
                            Log.e(TAG, "apply: ", e);
                        }

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


}
