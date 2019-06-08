package com.example.user.wimp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Set;

public class CUReservationEditSender extends AppCompatActivity {

    TextView username, address;
    Spinner firstPhoneNum, choiceCategorySp;
    EditText midPhoneNum, lastPhoneNum, detailAddress, contentPriceEd, reservationNameEd;
    Button backBtn, nextBtn, addressBtn;

    ServerIP serverIP;

    ArrayList<String> loginUser;
    String loginId, firstphoneNum, addressFeatAPI, fullAddress, loginName, fullPhoneNum, category, contentPrice, reservationName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cureservationsender);

        username = findViewById(R.id.username);
        address = findViewById(R.id.address);
        firstPhoneNum = findViewById(R.id.firstPhoneNum);
        midPhoneNum = findViewById(R.id.midPhoneNum);
        lastPhoneNum = findViewById(R.id.lastPhoneNum);
        detailAddress = findViewById(R.id.detailAddress);
        backBtn = findViewById(R.id.backBtn);
        nextBtn = findViewById(R.id.nextBtn);
        addressBtn = findViewById(R.id.addressBtn);
        choiceCategorySp = findViewById(R.id.choiceCategorySp);
        contentPriceEd = findViewById(R.id.contentPriceEd);
        reservationNameEd = findViewById(R.id.reservationNameEd);

        ArrayAdapter adapter = ArrayAdapter.createFromResource(this,R.array.phone,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        firstPhoneNum.setAdapter(adapter);

        //스피너 이벤트 발생
        firstPhoneNum.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //각 항목 클릭시 포지션값을 토스트에 띄운다.
//                Toast.makeText(getApplicationContext(), parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
                firstphoneNum = parent.getItemAtPosition(position).toString();
//                if(!midPhoneNum.getText().equals("") && !lastPhoneNum.getText().equals("")){
//                    phoneNum = parent.getItemAtPosition(position).toString() + midPhoneNum.getText() + lastPhoneNum.getText();
//
//                    Log.d("phone num", phoneNum);
//                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter categoryadapter = ArrayAdapter.createFromResource(this,R.array.category,android.R.layout.simple_spinner_item);
        categoryadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        choiceCategorySp.setAdapter(categoryadapter);
        choiceCategorySp.setSelection(0);

        //스피너 이벤트 발생
        choiceCategorySp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //각 항목 클릭시 포지션값을 토스트에 띄운다.
//                Toast.makeText(getApplicationContext(), parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
                category = parent.getItemAtPosition(position).toString();
                Log.d("카테고리는? ", category);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        try {
            SharedPreferences preferences = getSharedPreferences("auto", MODE_PRIVATE);

            Set<String> set = preferences.getStringSet("userinfo", null);
            loginUser = new ArrayList<>(set);

            Log.d("checkbox", loginUser.get(0).toString());
            String[] loginData = loginUser.get(0).split("@@@@");

            loginId = loginData[0];
            //이름을 텍스트 뷰에 뿌려주자 (9/17 7시 아이디가 넣어져있음 -> 수정해야함)
            GetData getData = new GetData();
            getData.execute(loginId);

        }catch (NullPointerException e){
            e.printStackTrace();
        }

        Intent i = getIntent();
        if(i.getStringExtra("address") != null){
//            Log.d("주소왔다", i.getStringExtra("address"));
            //주소 api를 받아와서 텍스트 뷰에 넣어놨다.
            addressFeatAPI = i.getStringExtra("address");
            address.setText(addressFeatAPI);

        }

//        if(!detailAddress.getText().toString().equals("")){
//            //상세 주소까지 입력한 경우
//            //주소 합쳐서 변수로 가지고 있자
//            fullAddress = addressFeatAPI + detailAddress.getText().toString();
//            Log.d("주소 합쳐졌나? sender", fullAddress);
//        }

        addressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(CUReservationEditSender.this, AddressAct.class);
                i.putExtra("activity", "cureservationsender");
                startActivity(i);
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(CUReservationEditSender.this, Reservation.class);
                finish();
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //보내는 사람 정보 (유저이름, 유저 폰 번호, 유저 주소) 저장해서 가지고 있어야함
                Intent i = new Intent(CUReservationEditSender.this, CUReservationEditReceiver.class);
//                i.putExtra("senderInfo", senderInfo);
                startActivity(i);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();

        if(!detailAddress.getText().toString().equals("")){
            //상세 주소까지 입력한 경우
            //주소 합쳐서 변수로 가지고 있자
            fullAddress = addressFeatAPI + detailAddress.getText().toString();
            Log.d("주소 합쳐졌나? sender", fullAddress);
        }

        if(!midPhoneNum.getText().toString().equals("") && !lastPhoneNum.getText().toString().equals(""))
            fullPhoneNum = firstphoneNum + midPhoneNum.getText().toString() + lastPhoneNum.getText().toString();

        String senderInfo = loginName + "##" + fullPhoneNum + "##" +fullAddress;
        Log.d("senderpage info", senderInfo);

        if(!contentPriceEd.getText().toString().equals(""))
            contentPrice = contentPriceEd.getText().toString();

        if(!reservationNameEd.getText().toString().equals(""))
            reservationName = reservationNameEd.getText().toString();

        //물품정보 내역 모아서 저장해보자
        String contentInfo = category + "##" + contentPrice + "##" + reservationName;
        Log.d("contentInfo info", contentInfo);

        SharedPreferences preferences = getSharedPreferences("reservation", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString("senderInfo",senderInfo);
        editor.putString("contentInfo",contentInfo);
        editor.commit();
    }

    private class GetData extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(CUReservationEditSender.this,
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
                Toast.makeText(CUReservationEditSender.this, "error", Toast.LENGTH_SHORT).show();
            }
            else {
                username.setText(result.toString());
                loginName = username.getText().toString();
            }
        }

        @Override
        protected String doInBackground(String... params) {

            String mem_id = params[0];
            String serverURL = serverIP.serverIp+"/wimp/getuserdata.php";
            String postParameters = "id=" + mem_id;

            Log.d("TAG",mem_id);
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
}
