package com.example.user.wimp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.wimp.arcore.helloar.HelloArActivity;

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
import java.util.ArrayList;
import java.util.Set;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class Mypage extends AppCompatActivity {

    private String TAG = "마이페이지";

    private HttpConnectionARimage httpConn = HttpConnectionARimage.getInstance();

    ArrayList<String> loginUser;

    ImageButton imageBtnList,imageBtnChart,imageBtnMypage, imageBtnChat;
    ImageView mypage_profile, mypage_arimage;
    TextView tv, mypage_name, mypage_ar_position;

    String loginID,imagename,uploading,profile;

    ServerIP serverIP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mypage);

        imageBtnList = findViewById(R.id.imageBtnList);
        imageBtnChart = findViewById(R.id.imageBtnChart);
        imageBtnChat = findViewById(R.id.imageBtnChat);
        imageBtnMypage = findViewById(R.id.imageBtnMypage);
        mypage_profile = findViewById(R.id.mypage_profile);
        mypage_name = findViewById(R.id.mypage_name);
        mypage_arimage = findViewById(R.id.mypage_arimage);
        mypage_ar_position = findViewById(R.id.mypage_ar_position);

        try {
            SharedPreferences preferences = getSharedPreferences("auto", Context.MODE_PRIVATE);

            Set<String> set = preferences.getStringSet("userinfo", null);
            loginUser = new ArrayList<>(set);

            Log.d("checkbox", loginUser.get(0).toString());
            String[] loginData = loginUser.get(0).split("@@@@");
            loginID = loginData[0];

            mypage_name.setText(loginID);

            //유저아이디를 통해 마이페이지를 들어왔을때 유저의 정보를 다 세팅하기위해 서버에서 데이터를 가져와야한다.
            GetData getData = new GetData();
            getData.execute(loginID);
        }catch (NullPointerException e){
            e.printStackTrace();
        }

        Intent intent = getIntent();
        if (intent.getStringExtra("arimage") != null){
            imagename = intent.getStringExtra("arimage");
            Log.d("이미지 이름 ", imagename);

            Intent i = new Intent(Mypage.this, Mypage.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            mypage_arimage.setImageURI(Uri.parse(imagename));

            //ar이미지를 업로딩하는 부분
//            UpdateImage updateImage = new UpdateImage();
//            updateImage.execute(imagename);

            //업로딩된 이미지를 유저 테이블에 저장해줘야한다.
            //이미지에 경우 경로를 다주면 에러가 났었기때문에 파일이름 즉, 경로에서 맨마지막부분만 잘라서 넣어주자
            UpdateUserInfo updateUserInfo = new UpdateUserInfo();
            updateUserInfo.execute(loginID,imagename);
        }else if (intent.getStringExtra("profile") != null) {
            profile = intent.getStringExtra("profile");
            Log.d("프로필이미지의 행방", profile + " 이건데 왔나???");
            String[] data = profile.split("://");
            Log.d("프로필 이미지", data[1]);
            mypage_profile.setImageURI(Uri.parse(data[1]));
//            Intent i = new Intent(Mypage.this, Mypage.class);
//            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            startActivity(i);

        }

        mypage_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Mypage.this, FaceDetect.class);
                startActivity(i);
            }
        });

        mypage_ar_position.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Mypage.this, HelloArActivity.class);
                startActivity(i);
            }
        });

        imageBtnList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Mypage.this, MainActivity.class);
                startActivity(i);
            }
        });

        imageBtnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Mypage.this, ChatActivity.class);
                startActivity(i);
            }
        });

        imageBtnChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Mypage.this, Chart.class);
                startActivity(i);
            }
        });

        imageBtnMypage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Mypage.this, Mypage.class);
                startActivity(i);
            }
        });

    }

    private final Callback callback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            Log.d("tag", "콜백오류:"+e.getMessage());
            uploading = "false";
        }
        @Override
        public void onResponse(Call call, Response response) throws IOException {
            String body = response.body().string();
            Log.d("tag", "마이페이지인데!! 서버에서 응답한 Body:"+body);
            uploading = "true";
        }
    };

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
                Log.i(TAG, "onPostExecute: getData error");
//                Toast.makeText(Mypage.this, "error", Toast.LENGTH_SHORT).show();
            }
            else {
                String getjson = result.toString();
                try {
                    JSONArray jsonArray = new JSONArray(getjson);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject item = jsonArray.getJSONObject(i);

                        String arimage = item.getString("mem_arimage");

                        Log.d("가져왔냐 이미지 주소? ", "db data get success "+arimage);
                        mypage_arimage.setImageURI(Uri.parse(arimage));
                    }
                } catch (JSONException e) {
                    Log.d("chat", "showResult : ", e);
                } catch (NullPointerException e) {

                }
            }
        }

        @Override
        protected String doInBackground(String... params) {

            String mem_id = params[0];
            String serverURL = serverIP.serverIp+"/wimp/member_page.php";
            String postParameters = "id=" + mem_id;

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

    private class UpdateImage extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(Mypage.this,
                    "Please Wait", null, true, true);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

//            progressDialog.dismiss();
//            results.setText(result);
            Log.d("TAG", "response - " + result);

            if (result == null){
                Log.i(TAG, "onPostExecute: updateImage error");
//                Toast.makeText(Mypage.this, "error", Toast.LENGTH_SHORT).show();
            }
            else {
                if(result.equals("true")) {
                    progressDialog.dismiss();
                }else if (result.equals("false")){
                    Toast.makeText(Mypage.this, "업로딩 실패", Toast.LENGTH_SHORT).show();
                }
            }
        }

        @Override
        protected String doInBackground(String... params) {
            String arimage = params[0];
            httpConn.requestWebServer(arimage, callback);

            return uploading;
        }

    }

    private class UpdateUserInfo extends AsyncTask<String, Void, String> {

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
            String message = result.toString();
            Log.d("업데이트 인포", message);
            if (result == null){
                Log.i(TAG, "onPostExecute: updateInfo error");
//                Toast.makeText(Mypage.this, "error", Toast.LENGTH_SHORT).show();
            }
            else {
                if (message.equals("success")){
                    Log.d("db에 저장 성공", "나이쓰~");
                }else if (message.equals("null")){
                    Log.d("db에 저장 실패", "젠장할");
                }
            }
        }

        @Override
        protected String doInBackground(String... params) {
            String mem_id = params[0];
            String mem_arimage = params[1];
            String serverURL = serverIP.serverIp+"/wimp/member_page_update.php";
            String postParameters = "id=" + mem_id + "&arimage=" + mem_arimage;

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
    public void onResume() {
        super.onResume();

    }


}
