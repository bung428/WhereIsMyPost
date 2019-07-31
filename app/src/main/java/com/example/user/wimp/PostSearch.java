package com.example.user.wimp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import io.reactivex.android.schedulers.AndroidSchedulers;


public class PostSearch extends AppCompatActivity {

    EditText postNum;
    TextView tvSelect, results;

    JSONArray jsonArray;
    JSONObject jsonObject;

    ServerIP serverIP;
    APIService apiService;

    String company, goods, num, msg, number, message, mJsonString, TAG = "송장번호 화면",sendrNm,qty,itemNm,rcvrNm;
    Boolean value;
    String[] user;

    ArrayList<String> postInfo = new ArrayList<>();
    ArrayList<String> crgNm = new ArrayList<>();
    ArrayList<String> dTime = new ArrayList<>();
    ArrayList<String> regBranNm = new ArrayList<>();
    ArrayList<String> scanNm = new ArrayList<>();
    ArrayList<String> loginUser = new ArrayList<>();

    String loginId;

    CJPost cjPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.postsearch);

//        postName = findViewById(R.id.postName);
        postNum = findViewById(R.id.postNum);
//        memo = findViewById(R.id.memo);
        tvSelect = findViewById(R.id.tvSelect);
//        results = findViewById(R.id.results);

        try {
            SharedPreferences preferences = getSharedPreferences("auto", MODE_PRIVATE);

            Set<String> set = preferences.getStringSet("userinfo", null);
            loginUser = new ArrayList<>(set);

            Log.d("checkbox", loginUser.get(0).toString());
            String[] loginData = loginUser.get(0).split("@@@@");

            loginId = loginData[0];
        }catch (NullPointerException e){
            e.printStackTrace();
        }

        tvSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(PostSearch.this, PostCompanyList.class);
                startActivity(i);
            }
        });

        //택배사 회사명 intent로 받아오기
        //상품 내용, 송장번호, intent로 받아온 택배회사명, 메시지를 등록버튼을 누르면 저장한다.
        Intent intent = getIntent();
        if (intent != null) {
            company = intent.getStringExtra("postCompany");
            if (company != null) {
                Log.d("post", company);
                tvSelect.setText(company);
                number = postNum.getText().toString();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //조회할 택배에 정보들을 저장해야한다
        //저장전에 해당 택배사 사이트에서 송장번호로 조회가 가능해지는지 테스트한다
        //조회가된다면 파싱해온다
        //파싱해온정보를 log로 찍어본다

    }

    @Override
    protected void onPause() {
        super.onPause();

        num = postNum.getText().toString();
        company = tvSelect.getText().toString();
        Log.d("post", "onpause" + "송장번호 : " + num + "회사 : " + company);

        SharedPreferences preferences = getSharedPreferences("post", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        if (!num.equals("")) {
            if (preferences.getStringSet("postInfo", null) != null) {
                postInfo.clear();

                Set<String> set = preferences.getStringSet("postInfo", null);
                postInfo = new ArrayList<>(set);
                for (int i = 0; i < postInfo.size(); i++) {
                    value = postInfo.get(i).contains(num);
                    if (value == true) {
                        user = postInfo.get(i).split("##");
                    }
                }
                try {
                    if (num.equals(user[0])) {
                        Log.d("post", "저장된 송장번호");
                    } else {
                        Log.d("post", "저장해야할 송장번호!!!");
                        try {
//                Log.d("mall", mall + id + pwd);
                            postInfo.add(num + "##" + company);

                            Set<String> sets = new HashSet<String>();
                            sets.addAll(postInfo);
                            editor.putStringSet("postInfo", sets);
                            editor.commit();

                            Log.d("post", preferences.getStringSet("postInfo", null).toString());
                        } catch (NullPointerException e) {

                        }
                    }
                } catch (NullPointerException e) {

                }
            } else {
                Log.d("post", num + company);
                try {
                    num = postNum.getText().toString();
                    company = tvSelect.getText().toString();

                    postInfo.add(num + "##" + company);

                    Log.d("post", postInfo.get(0));

                    Set<String> sets = new HashSet<String>();
                    sets.addAll(postInfo);
                    editor.putStringSet("postInfo", sets);
                    editor.commit();

                    Log.d("post", preferences.getStringSet("postInfo", null).toString());
                } catch (NullPointerException e) {

                }
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
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View actionbar = inflater.inflate(R.layout.custom_newpost, null);

        actionBar.setCustomView(actionbar);

        //액션바 양쪽 공백 없애기
        Toolbar parent = (Toolbar) actionbar.getParent();
        parent.setContentInsetsAbsolute(0, 0);

        Button backBtn = findViewById(R.id.backBtn);
        Button uploadBtn = findViewById(R.id.uploadBtn);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(PostSearch.this, MainActivity.class);
                startActivity(i);
            }
        });

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //구매한 물품, 택배사, 송장번호를 통해 택배를 조회한다.
                //택배사를 이용해 해당 택배사 송장번호 조회 사이트로 들아간다.
                //송장번호를 이용해 그 값으로 조회사이트에서 조회를 한다.
                //조회된 물품에 대한 정보를 가져온다.
                //크롤링 한 데이터를 디비에 저장하면 메인화면으로 넘어가 뷰로 보여준다.
                num = postNum.getText().toString();

                Intent intent = getIntent();
                if (intent != null) {
                    company = intent.getStringExtra("postCompany");
                    if (company != null) {
                        tvSelect.setText(company);
//                        Intent i = new Intent(PostSearch.this, MainActivity.class);
//                        startActivity(i);
                        switch (company) {
                            case "CJ대한통운":
                                CJTask cjTask = new CJTask();
                                cjTask.execute();
                                break;
                            case "한진택배":
//                                HJTask hjTask = new HJTask();
//                                hjTask.execute();
                                break;
                        }
                    }
                }
            }
        });

        return true;
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
                org.jsoup.Connection.Response response = Jsoup.connect("https://www.cjlogistics.com/ko/tool/parcel/tracking")
                        .method(org.jsoup.Connection.Method.GET)
                        .timeout(10000)
                        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.99 Safari/537.36")
                        .header("Cache-Control", "max-age=0")
                        .header("Content-Type", "application/x-www-form-urlencoded")
                        .header("Connection", "keep-alive")
                        .header("Host", "www.cjlogistics.com")
                        .header("Upgrade-Insecure-Requests", "1")
                        .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
                        .header("Accept-Encoding", "gzip, deflate, br")
                        .header("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7")
                        .execute();

                Map<String, String> loginTryCookie = response.cookies();
                Document csrfdoc = response.parse();

                String _csrf = csrfdoc.select("input[name=_csrf]").val();
                Log.d("jsoup", "_csrf = " + _csrf);

                Map<String, String> data = new HashMap<>();
                data.put("_csrf", _csrf);
                data.put("paramInvcNo",num);

                org.jsoup.Connection.Response detailResponse = Jsoup.connect("https://www.cjlogistics.com/ko/tool/parcel/tracking-detail")
                        .method(org.jsoup.Connection.Method.POST)
                        .timeout(10000)
                        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.99 Safari/537.36")
                        .header("Referer", "https://www.cjlogistics.com/ko/tool/parcel/tracking")
                        .header("Origin", "https://www.cjlogistics.com")
                        .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                        .header("Connection", "keep-alive")
                        .header("Host", "www.cjlogistics.com")
                        .header("Upgrade-Insecure-Requests", "1")
                        .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
                        .header("Accept-Encoding", "gzip, deflate, br")
                        .header("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7")
                        .header("X-Requested-With", "XMLHttpRequest")
                        .ignoreContentType(true)
                        .cookies(loginTryCookie)
                        .data(data)
                        .execute();

                Document doc = detailResponse.parse();

                jsonObject = new JSONObject(doc.text());
                String parcelResultMap = jsonObject.getString("parcelResultMap");
                String parcelDetailResultMap = jsonObject.getString("parcelDetailResultMap");

                jsonObject = new JSONObject(parcelResultMap);
                jsonArray = new JSONArray(jsonObject.getString("resultList"));

                sendrNm = jsonArray.getJSONObject(0).getString("sendrNm");
                qty = jsonArray.getJSONObject(0).getString("qty");
                itemNm = jsonArray.getJSONObject(0).getString("itemNm");
                rcvrNm = jsonArray.getJSONObject(0).getString("rcvrNm");

                jsonObject = new JSONObject(parcelDetailResultMap);
                jsonArray = new JSONArray(jsonObject.getString("resultList"));
                for (int i=0; i<jsonArray.length(); i++) {
                    crgNm.add(jsonArray.getJSONObject(i).getString("crgNm"));
                    dTime.add(jsonArray.getJSONObject(i).getString("dTime"));
                    regBranNm.add(jsonArray.getJSONObject(i).getString("regBranNm"));
                    scanNm.add(jsonArray.getJSONObject(i).getString("scanNm"));
                }

                Log.d(TAG, "doInBackground: 조회결과 "+sendrNm+" "+qty+" "+itemNm+" "+rcvrNm);
                // 샌더 : 회사이름 qty : 갯수 item : 카테고리 rcvr : 받는사람이름
                for (int i=0; i<crgNm.size(); i++) {
                    Log.d(TAG, "doInBackground: 조회상태 "+crgNm.get(i)+" "+dTime.get(i)+" "+regBranNm.get(i)+" "+scanNm.get(i));
                }
                // crg 배송 메시지 regbrand 위치 scan 레벨

                // 위에 뽑아둔 데이터를 디비에 저장하여 메인으로 이동하여 뿌려주도록하자.
                // 레트로핏으로 arraylist 보내기 하자.
                cjPost = new CJPost(num, sendrNm, rcvrNm, crgNm, scanNm, dTime, regBranNm, company, loginId);
                CJInsertDB(cjPost);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    public void CJInsertDB(CJPost cjPost) {

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
                            Log.d(TAG, "시작 바로 전");
                            apiService = APIClient.getClient1().create(APIService.class);
                            Call<ResponseBody> callServer = apiService.cjInsert(cjPost.getNum(), cjPost.getPi_send(), cjPost.getPi_recv(),
                                    cjPost.getPi_info(), cjPost.getP_level(), cjPost.getP_date(), cjPost.getP_where(), cjPost.getCompany(), cjPost.getP_userId());

                            callServer.enqueue(new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                    try {
                                        String msg = response.body().string();
                                        Log.d(TAG, "onResponse: 서버로부터 응답 in retrofit "+msg);

                                        switch (msg) {
                                            case "success" :
                                                Log.d(TAG, "switch success : 디비에 새로운 데이터 저장 성공 -> 뷰에 갱신");

                                                Intent intent = new Intent(PostSearch.this, MainActivity.class);
                                                intent.putExtra("postNum", cjPost.getNum());
                                                intent.putExtra("company", cjPost.getCompany());
                                                startActivity(intent);
                                                break;
                                            case "fail" :
                                                Log.d(TAG, "switch fail: 디비에 데이터 저장 실패");
                                                break;
                                            case "nothing" :
                                                Log.d(TAG, "switch nothing: 저장할 필요 없음 -> 갱신 노필요");
                                                showToast();
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

                            Log.d(TAG, "onFailure: 통신 실패");
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
                        Log.d(TAG, "onSubscribe : "+d);
                    }
                    @Override
                    public void onNext(Boolean s)
                    {
                        Log.d(TAG, "onNext: "+s);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError: "+e);
                    }
                    @Override
                    public void onComplete()
                    {
                    }
                });
    }

    public void showToast() {
        Toast.makeText(this, "추가 할 정보가 없습니다.", Toast.LENGTH_SHORT).show();
    }
}

//private class HJTask extends AsyncTask<Void, String, String> {
//
//    @Override
//    protected void onPostExecute(String result) {
//        super.onPostExecute(result);
//    }
//
//    @Override
//    protected String doInBackground(Void... voids) {
//        try {
//            //한진택배의 경우
//            //다른 결과값이 나와 문제가 있다.
//            //또한 보이지않았던 form과 파라미터들이 나와서 당황을 한 경우이다
//            //그렇다면 로그인을하여 송장번호 검색을 해보는게 어떨지 도전을 해봐야겠다.
//            org.jsoup.Connection.Response response = Jsoup.connect("https://m.hanex.hanjin.co.kr/inquiry/incoming/inquiryWblNumExt")
//                    .method(org.jsoup.Connection.Method.GET)
//                    .timeout(10000)
////                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.99 Safari/537.36")
//                    .header("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.99 Safari/537.36")
//                    .header("Referer","https://www.google.co.kr/")
//                    .header("Origin", "https://www.doortodoor.co.kr")
//                    .header("Connection","keep-alive")
//                    .header("Upgrade-Insecure-Requests","1")
//                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
//                    .header("Accept-Encoding","gzip, deflate, br")
//                    .header("Accept-Language","ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7")
//                    .ignoreContentType(true)
//                    .execute();
//
//            Map<String, String> loginTryCookie = response.cookies();
////                Document loginPageDocument = response.parse();
////                String _csrf = loginPageDocument.select("input._csrf").val();
////                Log.d("jsoup","cookie = "+loginTryCookie.toString());
//
//            Map<String, String> data = new HashMap<>();
//            data.put("div","B");
//            data.put("show","true");
//            data.put("upDiv","");
//            data.put("upQueryDiv","");
//            data.put("queryDiv","");
//            data.put("empId","");
//            data.put("rcvNam","");
//            data.put("rcvAddr","");
//            data.put("wblNum","8331604326");
//
//            org.jsoup.Connection.Response loginResponse = Jsoup.connect("https://m.hanex.hanjin.co.kr/inquiry/incoming/resultWaybill")
//                    .method(org.jsoup.Connection.Method.POST)
//                    .timeout(10000)
////                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.99 Safari/537.36")
//                    .header("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.99 Safari/537.36")
//                    .header("Referer","https://www.google.co.kr/")
//                    .header("Origin", "https://www.doortodoor.co.kr")
//                    .header("Connection","keep-alive")
//                    .header("Upgrade-Insecure-Requests","1")
//                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
//                    .header("Accept-Encoding","gzip, deflate, br")
//                    .header("Accept-Language","ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7")
////                        .header("Cookie","WMONID=DROEtlYpWCi; _ga=GA1.3.1855348225.1531463575; _gid=GA1.3.1068704932.1531463575; JSESSIONID=GvevdjcD434FXLX9a71lb41iUrqf1VCSuj53PNjECrEa21Pe0onKADmJGqL72Nqr.edtdwas3_servlet_engine1")
//                    .ignoreContentType(true)
//                    .cookies(loginTryCookie)
//                    .data(data)
//                    .execute();
//
//            Map<String, String> sessioncookie = loginResponse.cookies();
//
//
//
////              "http://cart.coupang.com/cartView.pang"
//            Document adminPageDocument = Jsoup.connect("https://m.hanex.hanjin.co.kr/inquiry/incoming/resultWaybill;jsessionid=MBmTR3_QODlWc1I0KsIxRickNVJf56Z9iFN57pp2nvMLD_Tc9Iey!-1331892395")
//                    .timeout(10000)
////                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.99 Safari/537.36")
//                    .header("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.99 Safari/537.36")
//                    .header("Origin", "https://m.hanex.hanjin.co.kr")
//                    .header("Content-Type","application/x-www-form-urlencoded")
////                        .header("Upgrade-Insecure-Requests","1")
//                    .header("Connection","keep-alive")
//                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
//                    .header("Accept-Encoding","gzip, deflate, br")
//                    .header("Accept-Language","ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7")
//                    .header("Referer", "https://m.hanex.hanjin.co.kr/inquiry/incoming/inquiryWblNumExt")
////                        .header("Cookie","_ga=GA1.2.106805566.1529564462; JSESSIONID=3EBFD81B64D5E1040A6B9A87EDECE797.front12; cjlogisticsFrontLangCookie=ko; _ceg.s=pbsubc; _ceg.u=pbsubc; _gid=GA1.2.1408639714.1531475833; _gat_gtag_UA_47919938_1=1")
//                    .cookies(sessioncookie)
//                    .get();
//
//            Elements title = adminPageDocument.select("section[class=resulte_context]");
//
//
//            Log.d("jsoup", "after login title? "+title);
//            for(Element option : title) {
//                String marketLogin = option.text();
//                Log.d("jsoup", marketLogin);
//                break;
//            }
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return null;
//    }
//}
