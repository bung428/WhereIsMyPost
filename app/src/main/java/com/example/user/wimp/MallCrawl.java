package com.example.user.wimp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.crypto.Cipher;

public class MallCrawl extends AppCompatActivity {

    String message,mall,id,pwd,mJsonString;
    Boolean crawl=false;

    Button button;
    TextView crawlresult;
    ImageView loadingimage;
    RecyclerView rvMall;

    ServerIP serverIP;

    ArrayList<String> loginUser;
    String loginId, TAG = "몰 크롤 엑티비티 : ";
    String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.169 Safari/537.36";



    private ArrayList<LittleMallItem> mItems = new ArrayList<>();
    LittleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mall_crawl);

        button=findViewById(R.id.button);
        crawlresult=findViewById(R.id.crawlresult);
        loadingimage=findViewById(R.id.loadingimage);
        rvMall=findViewById(R.id.rvMall);

        setData();
        setRecyclerView();

        Intent i=getIntent();
        if(i!=null){
            mall=i.getStringExtra("mall");
            id=i.getStringExtra("id");
            pwd=i.getStringExtra("pwd");
        }

        try {
            SharedPreferences preferences = getSharedPreferences("auto", Context.MODE_PRIVATE);

            Set<String> set = preferences.getStringSet("userinfo", null);
            loginUser = new ArrayList<>(set);

            Log.d("checkbox", loginUser.get(0).toString());
            String[] loginData = loginUser.get(0).split("@@@@");
            loginId = loginData[0];

        }catch (NullPointerException e){
            e.printStackTrace();
        }

        SharedPreferences preferences=getSharedPreferences("mall",MODE_PRIVATE);
        if (preferences.getString(mall,null)==null){
            Log.d("mall","no id pwd");
        }else{
            String idpw=preferences.getString(mall,null);

            Path pathpub = Paths.get(Environment.getDataDirectory() + "/data/com.example.user.opencvcmake/files/"+mall+".pub");
            byte[] bytes;
            X509EncodedKeySpec ks;
            KeyFactory keyFactory;
            PublicKey publicKey1 = null;
            try {
                bytes = Files.readAllBytes(pathpub);

                ks = new X509EncodedKeySpec(bytes);
                keyFactory = KeyFactory.getInstance("RSA");
                publicKey1 = keyFactory.generatePublic(ks);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (InvalidKeySpecException e) {
                e.printStackTrace();
            }

            Path pathpri = Paths.get(Environment.getDataDirectory() + "/data/com.example.user.opencvcmake/files/"+mall+".key");
            byte[] bytess;
            PKCS8EncodedKeySpec kss;
            KeyFactory keyFactorys;
            PrivateKey privateKey1 = null;
            try {
                bytess = Files.readAllBytes(pathpri);

                kss = new PKCS8EncodedKeySpec(bytess);
                keyFactorys = KeyFactory.getInstance("RSA");
                privateKey1 = keyFactorys.generatePrivate(kss);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (InvalidKeySpecException e) {
                e.printStackTrace();
            }

//            // Encode the original data with RSA private key
//            byte[] encodedBytes = null;
//            try {
//                Cipher c = Cipher.getInstance("RSA");
//                c.init(Cipher.ENCRYPT_MODE, publicKey1);
//                encodedBytes = c.doFinal(idpw.getBytes());
//            } catch (Exception e) {
//                Log.e("RSATEST", "RSA encryption error");
//            }
////                    TextView tvencoded = (TextView) findViewById(R.id.textView);
////                    tvencoded.setText("[ENCODED]:\n" +
////                            Base64.encodeToString(encodedBytes, Base64.DEFAULT) + "\n");
//            Log.d("암호화? ", Base64.encodeToString(encodedBytes, Base64.DEFAULT));
//            idpw = Base64.encodeToString(encodedBytes, Base64.DEFAULT);
            // Decode the encoded data with RSA public key
            byte[] decodedBytes = null;
            try {
                Cipher c = Cipher.getInstance("RSA");
                Log.d("암호화? ", idpw);
                c.init(Cipher.DECRYPT_MODE, privateKey1);
                decodedBytes = c.doFinal(Base64.decode(idpw, Base64.DEFAULT));
            } catch (Exception e) {
                Log.e("RSATEST", "RSA decryption error");
            }
            Log.d("복호화? ", new String(decodedBytes));
            idpw = new String(decodedBytes);
            String[] user=idpw.split("##");
            id=user[0];
            pwd=user[1];
            Log.d("mall crawl", "복호화 "+ id + pwd);
        }
        CheckData checkData = new CheckData();
        switch (mall) {
            case "pang":
//                Toast.makeText(MallCrawl.this, mall+id+pwd, Toast.LENGTH_SHORT).show();
                checkData.execute(mall, id, pwd, loginId);
                break;
            case "gmarket":
//                Toast.makeText(MallCrawl.this, mall+id+pwd, Toast.LENGTH_SHORT).show();
                checkData.execute(mall, id, pwd, loginId);
                break;
        }

        GetData getData = new GetData();
        getData.execute(mall, loginId);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(MallCrawl.this,MainActivity.class);
                i.putExtra("mall",mall);
                i.putExtra("crawl",crawl);
                startActivity(i);
            }
        });
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }

    private class GetData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(MallCrawl.this,
                    "Please Wait", null, true, true);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
//            results.setText(result);
//            mItems.clear();
            Log.d("post", "response - " + result);
            if (result == null) {
                Toast.makeText(MallCrawl.this, "already have", Toast.LENGTH_SHORT).show();
            } else {
                mJsonString = result.toString();
                Log.d("post", "json"+mJsonString);
                if(mJsonString.equals("db don't have data")){
                    Log.d("mall","null");
                    crawlresult.setText(mall+"에 신규 등록된 상품이 없습니다.");
                    button.setVisibility(View.VISIBLE);
                }else {
                    crawlresult.setVisibility(View.INVISIBLE);
                    loadingimage.setVisibility(View.INVISIBLE);
                    rvMall.setVisibility(View.VISIBLE);
                    button.setVisibility(View.VISIBLE);
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

                            Log.d("mall", g_info + g_cnt + g_price + g_level);

                            LittleMallItem littleMallItem = new LittleMallItem(g_info);
                            mItems.add(littleMallItem);
                            adapter = new LittleAdapter(getApplicationContext(), mItems);
                            adapter.notifyDataSetChanged();
                        }
                        rvMall.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                        crawl=true;
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
            String id = params[1];
            String serverURL = serverIP.serverIp+"/wimp/mallcrawl.php";
            String postParameters = "mall=" + mallname + "&id=" + id;

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

    private class CheckData extends AsyncTask<String, Void, String> {

//        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

//            progressDialog = ProgressDialog.show(MallCrawl.this,
//                    "Please Wait", null, true, true);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

//            Log.d("TAG", "response - " + result);
            if (result == null){
                Toast.makeText(MallCrawl.this, "error", Toast.LENGTH_SHORT).show();
            }
            else {
//                Toast.makeText(LoginActivity.this, result.toString(), Toast.LENGTH_SHORT).show();
                message = result.toString();
                Log.d("mall", "hi" +  message);
                if(message.equals("db have data")) {
                    Log.d("mall","서버 디비에 이미 저장된 부분");
                    // 크롤링 시작
                }else {
                    Log.d("mall","서버 디비에 저장해야하는 쿼리문 써야할 부분");
                }
            }
//            progressDialog.dismiss();

        }

        @Override
        protected String doInBackground(String... params) {
            String mall_name = params[0];
            String mall_id = params[1];
            String mall_pwd = params[2];
            String id = params[3];
            String serverURL = serverIP.serverIp+"/wimp/mall_login.php";
            String postParameters = "mall=" + mall_name  + "&id=" + mall_id  + "&pwd=" + mall_pwd + "&loginid=" + id;

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

    private void setRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        rvMall.setLayoutManager(layoutManager);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();

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
                Intent i = new Intent(MallCrawl.this, Mall.class);
                startActivity(i);
            }
        });
        return true;
    }
}
