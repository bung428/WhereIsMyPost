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
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class Mall extends AppCompatActivity {

    final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.99 Safari/537.36";
    //    final String USER_AGENT = "Mozilla/5.0 (Linux; Android 4.2.2; ko-kr; SAMSUNG SHV-E300K/KKUAME7 Build/JDQ39) AppleWebkit/535.19(KHTML, like Gecko) Version/1.0 Chrome/18.0.1025.308 Mobile Safari/535.19";
    final String LOGIN_FORM_URL = "https://login.coupang.com/login/login.pang?rtnUrl=http%3A%2F%2Fwww.coupang.com%2Fnp%2Fpost%2Flogin%3Fr%3D";
    final String USERNAME = "goo428@naver.com";
    final String PASSWORD = "qodrn326";
    String[] first = new String[3];
    String[] second = new String[3];
    String[] panguser;
    String[] gmarketuser;
    String msg;

    ArrayList<String> mallConnect;
    ArrayList<String> mallDelete;
    Button pangBtn,gmarketBtn;
    TextView mallPang,mallGmarket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mall);

        pangBtn=findViewById(R.id.pangBtn);
        gmarketBtn=findViewById(R.id.gmarketBtn);
        mallPang=findViewById(R.id.mallPang);
        mallGmarket=findViewById(R.id.mallGmarket);

        final SharedPreferences preferences = getSharedPreferences("mall",MODE_PRIVATE);
        final SharedPreferences.Editor editor=preferences.edit();
        if(preferences.getString("pang",null)==null){
            pangBtn.setText("연동");
        }else{
            pangBtn.setText("취소");
        }
        if(preferences.getString("gmarket",null)==null){
            gmarketBtn.setText("연동");
        }else{
            gmarketBtn.setText("취소");
        }

        pangBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = pangBtn.getText().toString();
                switch (text){
                    case "연동":
                        Intent intent = new Intent(Mall.this,MallLogin.class);
                        intent.putExtra("mall","pang");
                        startActivity(intent);
                        break;
                    case "취소":
                        String idpw=preferences.getString("pang",null);
                        String[] user=idpw.split("##");
                        String id=user[0];
                        String pwd=user[1];

                        DelData delData=new DelData();
                        delData.execute("pang",id,pwd);

                        editor.remove("pang");
                        editor.commit();

                        Intent intent1=new Intent(Mall.this,Mall.class);
                        startActivity(intent1);
                        break;
                }
            }
        });
        gmarketBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = gmarketBtn.getText().toString();
                switch (text){
                    case "연동":
                        Intent intent = new Intent(Mall.this,MallLogin.class);
                        intent.putExtra("mall","gmarket");
                        startActivity(intent);
                        break;
                    case "취소":
                        String idpw=preferences.getString("gmarket",null);
                        String[] user=idpw.split("##");
                        String id=user[0];
                        String pwd=user[1];

                        DelData delData=new DelData();
                        delData.execute("gmarket",id,pwd);

                        editor.remove("gmarket");
                        editor.commit();

                        Intent intent1=new Intent(Mall.this,Mall.class);
                        startActivity(intent1);
                        break;
                }
            }
        });
        if(pangBtn.getText().toString().equals("취소")) {
            mallPang.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i=new Intent(Mall.this,MallCrawl.class);
                    i.putExtra("mall","pang");
                    if(preferences.getString("pang",null)!=null) {
                        String pang=preferences.getString("pang",null);
                        String[] user=pang.split("##");
                        i.putExtra("id",user[0]);
                        i.putExtra("pwd",user[1]);
                    }
                    startActivity(i);
                }
            });
        }
        if(gmarketBtn.getText().toString().equals("취소")) {
            mallGmarket.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i=new Intent(Mall.this,MallCrawl.class);
                    i.putExtra("mall","gmarket");
                    if(preferences.getString("gmarket",null)!=null) {
                        String pang=preferences.getString("gmarket",null);
                        String[] user=pang.split("##");
                        i.putExtra("id",user[0]);
                        i.putExtra("pwd",user[1]);
                    }
                    startActivity(i);
                }
            });
        }

    }

    private class DelData extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(Mall.this,
                    "Please Wait", null, true, true);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            String message;
            progressDialog.dismiss();
//            results.setText(result);
            Log.d("TAG", "response - " + result);
            Log.d("TAG",result.toString());
            if (result == null){
                Toast.makeText(Mall.this, "error", Toast.LENGTH_SHORT).show();
            }
            else {
//                Toast.makeText(LoginActivity.this, result.toString(), Toast.LENGTH_SHORT).show();
                message = result.toString();
                Log.d("mall", message);
                if(message.equals("success to delete sql")) {
                    Log.d("mall","success to delete sql");
                }else {
                    Log.d("mall","fail to delete sql");
                }
            }
        }

        @Override
        protected String doInBackground(String... params) {
            String mall_name = params[0];
            String mall_id = params[1];
            String mall_pwd = params[2];
            String serverURL = "http://115.71.232.235/wimp/mall_delete.php";
            String postParameters = "mall=" + mall_name  + "&id=" + mall_id  + "&pwd=" + mall_pwd;
            Log.d("mall","parameters "+mall_name+mall_id+mall_pwd);

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
                Intent i = new Intent(Mall.this, MainActivity.class);
                startActivity(i);
            }
        });
        return true;
    }

}
//
//private class LoginPangTask extends AsyncTask<Void, String, String> {
//
//    @Override
//    protected void onPostExecute(String result) {
//        super.onPostExecute(result);
//
//        String[] data = result.split("@@");
//
//        text.setText(data[0]);
//        text1.setText(data[1]);
//        text2.setText(data[2]);
//    }
//
//    @Override
//    protected String doInBackground(Void... voids) {
//        try {
//            org.jsoup.Connection.Response response = Jsoup.connect("https://login.coupang.com/login/login.pang?rtnUrl=http%3A%2F%2Fwww.coupang.com%2Fnp%2Fpost%2Flogin%3Fr%3Dhttp%253A%252F%252Fwww.coupang.com%252F")
//                    .method(org.jsoup.Connection.Method.GET)
//                    .timeout(5000)
//                    .header("User-Agent",USER_AGENT)
//                    .header("Referer","http://www.coupang.com/")
//                    .header("Origin", "https://login.coupang.com")
//                    .header("Upgrade-Insecure-Requests","1")
//                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
//                    .header("Accept-Encoding","gzip, deflate, br")
//                    .header("Accept-Language","ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7")
//                    .ignoreContentType(true)
//                    .execute();
//
//            Map<String, String> loginTryCookie = response.cookies();
//            Log.d("jsoup",loginTryCookie.toString());
//
//            Map<String, String> data = new HashMap<>();
//            data.put("adult", "false");
//            data.put("rtnUrl", "http%3A%2F%2Fwww.coupang.com%2Fnp%2Fpost%2Flogin%3Fr%3Dhttp%253A%252F%252Fwww.coupang.com%252F");
//            data.put("token", "");
//            data.put("email", USERNAME);
//            data.put("password",PASSWORD );
//
//            org.jsoup.Connection.Response loginResponse = Jsoup.connect("https://login.coupang.com/login/loginProcess.pang/")
//                    .method( org.jsoup.Connection.Method.POST)
//                    .timeout(7000)
//                    .header("User-Agent",USER_AGENT)
//                    .header("Referer","http://www.coupang.com/")
//                    .header("Origin", "https://login.coupang.com")
//                    .header("Upgrade-Insecure-Requests","1")
//                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
//                    .header("Accept-Encoding","gzip, deflate, br")
//                    .header("Accept-Language","ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7")
//                    .ignoreContentType(true)
//                    .cookies(loginTryCookie)
//                    .data(data)
//                    .execute();
//
//            Map<String, String> sessioncookie = loginResponse.cookies();
////                Log.d("jsoup",sessioncookie.toString());
////              "http://cart.coupang.com/cartView.pang"
//            Document adminPageDocument = Jsoup.connect("http://cart.coupang.com/cartView.pang")
//                    .timeout(7000)
//                    .userAgent(USER_AGENT)
//                    .header("Upgrade-Insecure-Requests","1")
//                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
//                    .header("Accept-Encoding","gzip, deflate")
//                    .header("Accept-Language","ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7")
//                    .header("Referer", "http://www.coupang.com/")
//                    .cookies(sessioncookie)
//                    .get();
//
//            Elements title = adminPageDocument.select("div[class=product-name-part]");
//            Elements title1 = adminPageDocument.select("span[class=unit-cost]");
////                Elements title2 = adminPageDocument.select("tbody[id=cartTable-sku]");
//
//            Log.d("jsoup", "after login title? "+title);
//            int i=0;
//            for(Element option : title) {
//                first[i] = option.text();
//                Log.d("jsoup", "after login marketLogin? "+first[i]);
//                i++;
//            }
//            i=0;
//            for(Element option : title1) {
//                second[i] = option.text();
//                Log.d("jsoup", "after login marketLogin? "+second[i]);
//                i++;
//            }
//            msg=first[0]+"@@"+second[0]+first[1]+"@@"+second[1]+first[2]+"@@"+second[2];
//            Log.d("jsoup",msg);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return msg;
//    }
//}
//
//private class LoginGmarketTask extends AsyncTask<Void, String, String> {
//
//    @Override
//    protected void onPostExecute(String result) {
//        super.onPostExecute(result);
//
////            String[] data = result.split("@@");
////
////            text.setText(data[0]);
////            text1.setText(data[1]);
////            text2.setText(data[2]);
//    }
//
//    @Override
//    protected String doInBackground(Void... voids) {
//        try {
//            org.jsoup.Connection.Response response = Jsoup.connect("https://signinssl.gmarket.co.kr/login/login")
//                    .method(org.jsoup.Connection.Method.GET)
//                    .timeout(10000)
//                    .header("User-Agent",USER_AGENT)
//                    .header("Referer","http://www.gmarket.co.kr/")
////                        .header("Origin", "")
//                    .header("Upgrade-Insecure-Requests","1")
//                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
//                    .header("Accept-Encoding","gzip, deflate, br")
//                    .header("Accept-Language","ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7")
//                    .ignoreContentType(true)
//                    .execute();
//
//            Map<String, String> loginTryCookie = response.cookies();
////                Log.d("jsoup","cookie = "+loginTryCookie.toString());
//
//            Map<String, String> data = new HashMap<>();
//            data.put("command", "login");
//            data.put("valid_url","");
//            data.put("valid_key","");
//            data.put("member_type", "MEM");
//            data.put("untrustCheck","");
//            data.put("FailCheck", "0");
//            data.put("url","http%3a%2f%2fwww.gmarket.co.kr%2f%3fredirect%3d1");
//            data.put("PrmtDisp","Y");
//            data.put("PrmtreferURL","http://www.gmarket.co.kr/?redirect=1");
//            data.put("FromWhere","G");
//            data.put("member_yn","Y");
//            data.put("id","goo428");
//            data.put("pwd","qodrn326");
//            data.put("saveid","");
//
//            org.jsoup.Connection.Response loginResponse = Jsoup.connect("https://signinssl.gmarket.co.kr/LogIn/LogInProc")
//                    .method( org.jsoup.Connection.Method.POST)
//                    .timeout(10000)
//                    .header("User-Agent",USER_AGENT)
//                    .header("Referer","http://www.gmarket.co.kr/")
////                        .header("Origin", "")
//                    .header("Upgrade-Insecure-Requests","1")
//                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
//                    .header("Accept-Encoding","gzip, deflate, br")
//                    .header("Accept-Language","ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7")
////                        .header("Cookie", "pguid=21509902905926004802010000; PCUID=15099029147898807219039; cguid=11408441208298002652000000; cto_lwid=63ebc832-5554-4324-acf3-9353638c1cb5; WMONID=Q2LWbN6QsUL; charset=enUS; shipnation=KR; sguid=31531476343841002082267000; WingFlag=R; ssguid=315314763438410020822670001; RPM=BT%3DL1531476343586; user%5Finfo=isNego=N; BASKET%5FCALLBACK%5FSTAT=F; Sif=fbcbb620c74ec330ad91053f5ccd5448; gmktloadingtimecheck=N; Pif=67D4D3B7BEF59864807172B536401566D790B3149F07E2CEC1710E9DC5E64C010884C2F7A1B9C505EABCC70DAD88B047; cc=CHM1A007")
//                    .ignoreContentType(true)
//                    .cookies(loginTryCookie)
//                    .data(data)
//                    .execute();
//
//            Map<String, String> sessioncookie = loginResponse.cookies();
////                Log.d("jsoup","login cookie = "+sessioncookie.toString());
////              "http://cart.coupang.com/cartView.pang"
//            Document doc = Jsoup.connect("http://escrow.gmarket.co.kr/ko/cart")
//                    .timeout(15000)
//                    .userAgent(USER_AGENT)
//                    .header("Upgrade-Insecure-Requests","1")
//                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
//                    .header("Accept-Encoding","gzip, deflate")
//                    .header("Accept-Language","ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7")
//                    .header("Cache-Control","max-age=0")
//                    .header("Connection","keep-alive")
//                    .header("Host","escrow.gmarket.co.kr")
//                    .header("Referer","http://www.gmarket.co.kr/")
//                    .header("Upgrade-Insecure-Requests","1")
////                        .header("Cookie","pguid=21509902905926004802010000; PCUID=15099029147898807219039; cguid=11408441208298002652000000; cto_lwid=63ebc832-5554-4324-acf3-9353638c1cb5; WMONID=Q2LWbN6QsUL; charset=enUS; shipnation=KR; sguid=31531476343841002082267000; WingFlag=R; ssguid=315314763438410020822670001; RPM=BT%3DL1531476343586; user%5Finfo=isNego=N; BASKET%5FCALLBACK%5FSTAT=F; Sif=fbcbb620c74ec330ad91053f5ccd5448; gmktloadingtimecheck=N; Pif=67D4D3B7BEF59864807172B536401566D790B3149F07E2CEC1710E9DC5E64C010884C2F7A1B9C505EABCC70DAD88B047; cc=CHM1A007")
//                    .cookies(sessioncookie)
//                    .method( org.jsoup.Connection.Method.GET)
//                    .get();
//
//            Element element=doc.select("script").eq(3).first();
//
////                JsonParser jsonParser = new JsonParser();
////                JsonArray jsonArray = (JsonArray) jsonParser.parse(element.text());
////
////                for(int i=0;i<jsonArray.size();i++){
////                    JsonObject jsonObject = (JsonObject) jsonArray.get(i);
////                    Log.d("jsoup","itemName"+jsonObject.get("itemName"));
////                }
//
//            Log.d("jsoup","element "+element.data());
//
//
////                JSONObject jsonObject=new JSONObject(element.text());
////                JSONArray jsonArray=(JSONArray)((JSONArray)jsonObject.get("itemName")).get(0);
//
////                Log.d("jsoup","test"+element.text());
////                for(Element little : element) {
////                    String aaa = little.html();
////                    Log.d("jsoup", "i want? "+aaa);
////                }
////                for(int i=0;i<jsonArray.length();i++){
////                    String item=(String)((JSONArray)jsonArray.get(i)).get(0);
////                    Log.d("jsoup","item"+item);
////                }
////                Log.d("jsoup","html "+doc.outerHtml());
//
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
////            catch (JSONException e) {
////                e.printStackTrace();
////            }
//
//        return null;
//    }
//}
