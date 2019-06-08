package com.example.user.wimp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
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

public class MypageARImage extends Activity {

    private HttpConnectionARimage httpConn = HttpConnectionARimage.getInstance();

    ImageView arImage;

    ServerIP serverIP;

    ArrayList<String> loginUser;
    String loginID;
    String imagename, uploading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.mypagearimage);

        //UI 객체생성
        arImage = findViewById(R.id.arImage);

        try {
            SharedPreferences preferences = getSharedPreferences("auto", Context.MODE_PRIVATE);

            Set<String> set = preferences.getStringSet("userinfo", null);
            loginUser = new ArrayList<>(set);

            Log.d("checkbox", loginUser.get(0).toString());
            String[] loginData = loginUser.get(0).split("@@@@");
            loginID = loginData[0];


        }catch (NullPointerException e){
            e.printStackTrace();
        }

        Intent intent = getIntent();
        if (intent.getStringExtra("arimage") != null){
            imagename = intent.getStringExtra("arimage");
            Log.d("이미지 이름 ", imagename);
            arImage.setImageURI(Uri.parse(imagename));

            //ar이미지를 업로딩하는 부분
            UpdateImage updateImage = new UpdateImage();
            updateImage.execute(imagename);

            //업로딩된 이미지를 유저 테이블에 저장해줘야한다.

            UpdateUserInfo updateUserInfo = new UpdateUserInfo();
            updateUserInfo.execute(loginID,imagename);
        }

        arImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MypageARImage.this, HelloArActivity.class);
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
            Log.d("tag", "서버에서 응답한 Body:"+body);
            uploading = "true";
        }
    };

    private class UpdateImage extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(MypageARImage.this,
                    "Please Wait", null, true, true);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

//            progressDialog.dismiss();
//            results.setText(result);
            Log.d("TAG", "response - " + result);

            if (result == null){
                Toast.makeText(MypageARImage.this, "error", Toast.LENGTH_SHORT).show();
            }
            else {
                if(result.equals("true")) {
                    progressDialog.dismiss();
                }else if (result.equals("false")){
                    Toast.makeText(MypageARImage.this, "업로딩 실패", Toast.LENGTH_SHORT).show();
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

            if (result == null){
                Toast.makeText(MypageARImage.this, "error", Toast.LENGTH_SHORT).show();
            }
            else {
                String message = result.toString();
                if(message.equals("null")){
                    Toast.makeText(MypageARImage.this, "null", Toast.LENGTH_SHORT).show();
                }else{
                    try {
                        JSONArray jsonArray = new JSONArray(message);

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject item = jsonArray.getJSONObject(i);

                            String arimage = item.getString("arimage");

                            Log.d("chat", "db data get success "+arimage);
                        }
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

    //확인 버튼 클릭
    public void mOnClose(View v){
        //데이터 전달하기
//        Intent intent = new Intent();
//        intent.putExtra("result", "Close Popup");
//        setResult(RESULT_OK, intent);

        //액티비티(팝업) 닫기
        finish();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //바깥레이어 클릭시 안닫히게
        if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        //안드로이드 백버튼 막기
        return;
    }
}
