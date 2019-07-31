package com.example.user.wimp;

import android.content.Intent;

import android.graphics.Bitmap;
import android.os.Handler;

import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;

import android.util.Log;
import android.webkit.JavascriptInterface;

import android.webkit.WebChromeClient;

import android.webkit.WebView;

import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;


public class DaumWebViewActivity extends AppCompatActivity {

    String TAG = "다음웹뷰 화면", loadnameKeyword;
    String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.99 Safari/537.36";

    private WebView daum_webView;
    private TextView daum_result;
    private Handler handler;
    private String url = ServerIP.serverIp+"/wimp/addressmain.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daum_web_view);
        daum_result = findViewById(R.id.daum_result);
//        // WebView 초기화
//        init_webView();
//        // 핸들러를 통한 JavaScript 이벤트 반응
//        handler = new Handler();
        loadNameFindService("삼학사로19길 15");
    }

    public void loadNameFindService (String loadnameKeyword) {
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
                            Connection.Response response = Jsoup.connect(ServerIP.serverIp+"/wimp/addressserver.php")
                                    .method(Connection.Method.GET)
                                    .timeout(10000)
                                    .header("User-Agent",USER_AGENT)
                                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3")
                                    .header("Accept-Encoding","gzip, deflate, br")
                                    .header("Accept-Language","ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7")
                                    .header("Upgrade-Insecure-Requests","1")
                                    .header("Connection","keep-alive")
                                    .header("Host",ServerIP.serverIp)
                                    .ignoreContentType(true)
                                    .execute();

                            Map<String, String> loginTryCookie = response.cookies();

                            Map<String, String> data = new HashMap<>();
                            // 여기서 아이디 비번 입력해주어야할듯?
                            data.put("confmKey", "U01TX0FVVEgyMDE5MDYyOTE1MjgyNjEwODg0NDY=");
                            data.put("returnUrl", ServerIP.serverIp+"/wimp/addressserver.php");
                            data.put("resultType", "4");

                            response = Jsoup.connect("http://www.juso.go.kr/addrlink/addrLinkUrl.do")
                                    .method(Connection.Method.POST)
                                    .timeout(10000)
                                    .header("User-Agent",USER_AGENT)
                                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3")
                                    .header("Accept-Encoding","gzip, deflate, br")
                                    .header("Accept-Language","ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7")
                                    .header("Content-Length", "129")
                                    .header("Cache-Control", "max-age=0")
                                    .header("Content-Type","application/x-www-form-urlencoded")
                                    .header("Referer",ServerIP.serverIp+"/wimp/addressserver.php")
                                    .header("Origin",ServerIP.serverIp)
                                    .header("Upgrade-Insecure-Requests","1")
                                    .header("Host","www.juso.go.kr")
                                    .header("Connection","keep-alive")
                                    .ignoreContentType(true)
                                    .cookies(loginTryCookie)
                                    .data(data)
                                    .execute();

                            Map<String, String> sessionCookie = response.cookies();

                            data = new HashMap<>();
                            // 여기서 아이디 비번 입력해주어야할듯?
                            data.put("rtRoadAddr", "");
                            data.put("rtAddrPart1", "");
                            data.put("rtAddrPart2", "");
                            data.put("rtEngAddr","");
                            data.put("rtJibunAddr","");
                            data.put("rtZipNo","");
                            data.put("rtAdmCd","");
                            data.put("rtRnMgtSn","");
                            data.put("rtBdMgtSn","");
                            data.put("rtDetBdNmList","");
                            data.put("rtBdNm","");
                            data.put("rtBdKdcd","");
                            data.put("rtSiNm","");
                            data.put("rtSggNm","");
                            data.put("rtEmdNm","");
                            data.put("rtLiNm","");
                            data.put("rtRn","");
                            data.put("rtUdrtYn","");
                            data.put("rtBuldMnnm","");
                            data.put("rtBuldSlno","");
                            data.put("rtMtYn","");
                            data.put("rtLnbrMnnm","");
                            data.put("rtLnbrSlno","");
                            data.put("rtEmdNo","");
                            data.put("searchType","");
                            data.put("dsgubuntext","");
                            data.put("dscity1text","");
                            data.put("dscounty1text","");
                            data.put("dsemd1text","");
                            data.put("dsri1text","");
                            data.put("dsrd_nm1text","");
                            data.put("dssan1text","");
                            data.put("keyword",loadnameKeyword);
                            data.put("rtAddrDetail","");

                            response = Jsoup.connect("http://www.juso.go.kr/addrlink/addrLinkUrlSearch.do")
                                    .method( Connection.Method.POST)
                                    .timeout(10000)
                                    .header("User-Agent",USER_AGENT)
                                    .header("Cache-Control","max-age=0")
                                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3")
                                    .header("Accept-Encoding","gzip, deflate, br")
                                    .header("Accept-Language","ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7")
                                    .header("Content-Length","421")
                                    .header("Content-Type","application/x-www-form-urlencoded")
                                    .header("Referer","http://www.juso.go.kr/addrlink/addrLinkUrl.do")
                                    .header("Origin","http://www.juso.go.kr/addrlink/addrLinkUrl.do")
                                    .header("Upgrade-Insecure-Requests","1")
                                    .header("Connection","keep-alive")
                                    .header("Host","www.juso.go.kr")
                                    .ignoreContentType(true)
                                    .cookies(sessionCookie)
                                    .data(data)
                                    .execute();


                            Document main = Jsoup.connect("http://www.juso.go.kr/addrlink/addrLinkUrlSearch.do")
                                    .timeout(10000)
                                    .userAgent(USER_AGENT)
                                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3")
                                    .header("Content-Type","application/x-www-form-urlencoded")
                                    .header("Origin","http://www.juso.go.kr")
                                    .header("Upgrade-Insecure-Requests", "Upgrade-Insecure-Requests")
                                    .cookies(sessionCookie)
                                    .get();

                            Log.d(TAG, "doInBackground: 주소 완료!"+main.html());
                            Log.d(TAG, "doInBackground: 주소 완료!"+main.body());

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

    public void init_webView() {
        // WebView 설정
        daum_webView = findViewById(R.id.daum_webview);
        // JavaScript 허용
        daum_webView.getSettings().setJavaScriptEnabled(true);
        // JavaScript의 window.open 허용
        daum_webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        // JavaScript이벤트에 대응할 함수를 정의 한 클래스를 붙여줌
        daum_webView.addJavascriptInterface(new AndroidBridge(), "AndroidTestApp");
        // web client 를 chrome 으로 설정
        daum_webView.setWebChromeClient(new WebChromeClient());
        // webview url load. php 파일 주소
        daum_webView.loadUrl(ServerIP.serverIp+"/wimp/addressserver.php");

    }

    private class AndroidBridge {
        @JavascriptInterface
        public void setAddresss(final String arg1, final String arg2, final String arg3) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    daum_result.setText(String.format("(%s) %s %s", arg1, arg2, arg3));
                    String myaddress = daum_result.getText().toString();
                    // WebView를 초기화 하지않으면 재사용할 수 없음
                    init_webView();

                    if(!myaddress.equals("")){
                        Intent intent = getIntent();

                        if(intent.getStringExtra("activity") != null){
                            switch (intent.getStringExtra("activity")) {
                                case "cureservationsender":
                                    Intent i1 = new Intent(DaumWebViewActivity.this, CUReservationEditSender.class);
                                    i1.putExtra("address", myaddress);
                                    startActivity(i1);
                                    break;

                                case "cureservationreceiver":
                                    Intent i2 = new Intent(DaumWebViewActivity.this, CUReservationEditReceiver.class);
                                    i2.putExtra("address", myaddress);
                                    startActivity(i2);
                                    break;
                            }
                        }
                    }
                }

            });

        }

    }

}

