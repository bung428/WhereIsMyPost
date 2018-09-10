package com.example.user.wimp;

import android.app.ProgressDialog;
import android.content.Intent;
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
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class SignUp extends AppCompatActivity {

    EditText signup_id, signup_name, signup_pwd, signup_pwd_check, signup_phone;
    Button signupBtn, signup_idckeckBtn;
    TextView results;

    String id, pwd, pwd_check, phone, name, message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);

        signup_id = findViewById(R.id.signup_id);
        signup_name = findViewById(R.id.signup_name);
        signup_pwd = findViewById(R.id.signup_pwd);
        signup_pwd_check = findViewById(R.id.signup_pwd_check);
        signup_phone = findViewById(R.id.signup_phone);
        signupBtn = findViewById(R.id.signupBtn);
        signup_idckeckBtn = findViewById(R.id.signup_idckeckBtn);

        signup_idckeckBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                id = signup_id.getText().toString();

                GetData task = new GetData();
                task.execute(id);
            }
        });

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                id = signup_id.getText().toString();
                pwd = signup_pwd.getText().toString();
                pwd_check = signup_pwd_check.getText().toString();
                name = signup_name.getText().toString();
                phone = signup_phone.getText().toString();

                if(pwd.equals(pwd_check)){
                    InsertData task = new InsertData();
                    task.execute(id, pwd, phone, name);

                    signup_id.setText("");
                    signup_pwd.setText("");
                    signup_pwd_check.setText("");
                    signup_name.setText("");
                    signup_phone.setText("");

                    Intent i=new Intent(SignUp.this, LoginActivity.class);
                    i.putExtra("success", true);
                    startActivity(i);
                }else{
                    Toast.makeText(SignUp.this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();

                    signup_pwd.setText("");
                    signup_pwd_check.setText("");
                }
            }
        });

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
        View actionbar = inflater.inflate(R.layout.custom_signup, null);

        actionBar.setCustomView(actionbar);

        //액션바 양쪽 공백 없애기
        Toolbar parent = (Toolbar)actionbar.getParent();
        parent.setContentInsetsAbsolute(0,0);

        ImageButton backBtn = findViewById(R.id.backBtn);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SignUp.this, LoginActivity.class);
                finish();
            }
        });

        return true;
    }

    private class GetData extends AsyncTask<String, Void, String>{

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(SignUp.this,
                    "Please Wait", null, true, true);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
//            results.setText(result);
            Log.d("TAG", "response - " + result);

            if (result == null){
                Toast.makeText(SignUp.this, "error", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(SignUp.this, result.toString(), Toast.LENGTH_SHORT).show();
                message = result.toString();

                if(message.equals("이미 사용중인 아이디입니다.")){
                    Toast.makeText(SignUp.this, message, Toast.LENGTH_SHORT).show();

                    signup_id.setText("");
                }else{
                    Toast.makeText(SignUp.this, message, Toast.LENGTH_SHORT).show();
                }
            }
        }

        @Override
        protected String doInBackground(String... params) {

            String searchKeyword = params[0];
            String serverURL = "http://115.71.232.235/wimp/idcheck.php";
            String postParameters = "id=" + searchKeyword;

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

                Log.d("Result Json", sb.toString());

                return sb.toString().trim();

            } catch (Exception e) {

                Log.d("TAG", "InsertData: Error ", e);
                errorString = e.toString();

                return null;
            }
        }

    }

    class InsertData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(SignUp.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
//            results.setText(result);
            Log.d("result php", result);
            Log.d("TAG", "POST response  - " + result);
        }


        @Override
        protected String doInBackground(String... params) {

            String mem_id = (String)params[0];
            String mem_pwd = (String)params[1];
            String mem_phone = (String)params[2];
            String mem_name = (String)params[3];

            Log.d("data" ,"id = " + mem_id + "pwd = " + mem_pwd + "phone = " + mem_phone + "name = " + mem_name);

            String serverURL = "http://115.71.232.235/wimp/test.php";
            String postParameters = "name=" + mem_name + "&phone=" + mem_phone + "&pwd=" + mem_pwd + "&id=" + mem_id;


            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.connect();


                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();


                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d("TAG", "POST response code - " + responseStatusCode);

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

                return new String("Error: " + e.getMessage());
            }

        }
    }

}

