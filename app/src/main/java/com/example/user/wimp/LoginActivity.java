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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class LoginActivity extends AppCompatActivity {

    EditText login_id, login_pwd;
    Button sign_in, sign_up;
    CheckBox auto_sign_in;
    TextView results;

    ServerIP serverIP;

    Boolean result, check;
    String id, pwd, message;

    ArrayList<String> userinfo = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userinfo.clear();

        SharedPreferences preferences = getSharedPreferences("auto", MODE_PRIVATE);

        if(preferences.getStringSet("userinfo", null)==null){
            login_id = findViewById(R.id.login_id);
            login_pwd = findViewById(R.id.login_pwd);
            sign_in = findViewById(R.id.signin);
            sign_up = findViewById(R.id.signup);
            auto_sign_in = findViewById(R.id.auto_signin);

//            results = findViewById(R.id.results);

            sign_up.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(LoginActivity.this, SignUp.class);
                    startActivity(i);
                }
            });

            sign_in.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    id = login_id.getText().toString();
                    pwd = login_pwd.getText().toString();

                    CheckData task = new CheckData();
                    task.execute(id, pwd);
                }
            });

            auto_sign_in.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(auto_sign_in.isChecked()==true) {
                        //자동 로그인 이용
                        check = true;
                        Log.d("checkbox", "true");
                    }else {
                        //자동 로그인 비 이용
                        check = false;
                        Log.d("checkbox", "false");
                    }
                }
            });

            Intent i=getIntent();
            result = i.getBooleanExtra("success", false);

            if(result==true){
                Toast.makeText(LoginActivity.this, "SIgn Up Success", Toast.LENGTH_SHORT).show();
            }
        }else{
            Set<String> set = preferences.getStringSet("userinfo", null);
            userinfo = new ArrayList<>(set);

            Intent i = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(i);
        }

//        if(userinfo.get(0).equals(null)){
//
//        }else{
//            Intent i = new Intent(LoginActivity.this, MainActivity.class);
//            startActivity(i);
//        }
    }

    private class CheckData extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(LoginActivity.this,
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
                Toast.makeText(LoginActivity.this, "error", Toast.LENGTH_SHORT).show();
            }
            else {
//                Toast.makeText(LoginActivity.this, result.toString(), Toast.LENGTH_SHORT).show();
                message = result.toString();
                Log.d("login message", message);
                if(message.equals("Login Success")){
                    //화면 넘어갈때 인탠트 플래그 처리로 이전 화면 볼 수 없게 처리하자!
                    Intent i = new Intent(LoginActivity.this, MainActivity.class);
                    i.putExtra("login message", message);
                    startActivity(i);
                }else{
                    Toast.makeText(LoginActivity.this, result.toString(), Toast.LENGTH_SHORT).show();
                    login_pwd.setText("");
                }
            }
        }

        @Override
        protected String doInBackground(String... params) {

            String mem_id = params[0];
            String mem_pwd = params[1];
            String serverURL = serverIP.serverIp + "/wimp/login.php";
            String postParameters = "id=" + mem_id  + "&pwd=" + mem_pwd;

            Log.d("TAG",mem_id+mem_pwd);
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
    protected void onPause() {
        super.onPause();

        SharedPreferences preferences = getSharedPreferences("auto", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        userinfo.clear();

        if(preferences.getStringSet("userinfo", null)==null){

            id = login_id.getText().toString();
            pwd = login_pwd.getText().toString();

            userinfo.add(id + "@@@@" + pwd);

            try {
                if(check == true){
                    Log.d("checkbox", "true!");

                    Set<String> sets = new HashSet<String>();
                    sets.addAll(userinfo);
                    editor.putStringSet("userinfo", sets);
                    editor.commit();

                    Log.d("checkbox", userinfo.get(0).toString());
                }else {
                    Log.d("checkbox", "false!");
                }
            }catch (NullPointerException e){

            }

        }else {

        }
    }
}
