package com.example.user.wimp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class CUReservationEditReceiver extends AppCompatActivity {

    EditText editReceiver, midPhoneNum, lastPhoneNum, detailAddress, postprice, wantMessageEdit, et_address;
    Spinner firstPhoneNum, postcategory, wantMessage, howtopaySp, sp_selectCity;
    TextView smallboxcountTv, bigboxcountTv;
    Button addressBtn, smallboxminusBtn, smallboxplusBtn, bigboxminusBtn, bigboxplusBtn, backBtn, nextBtn;
    CheckBox smallboxCheck, bigboxCheck;

    String city,firstphoneNum, addressFeatAPI, fullAddress, category, message, senderInfo, fullPhoneNum, fullPostInfo, postBox, writemessage, receiverInfo, boxPrice, howtopay;
    int smallboxcounting = 0, bigboxcounting = 0;

    String TAG = "수신자 정보 엑티비티";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cureservationreceiver);

        editReceiver = findViewById(R.id.editReceiver);
        midPhoneNum = findViewById(R.id.midPhoneNum);
        lastPhoneNum = findViewById(R.id.lastPhoneNum);
        detailAddress = findViewById(R.id.detailAddress);
        firstPhoneNum = findViewById(R.id.firstPhoneNum);
        wantMessage = findViewById(R.id.wantMessage);
        addressBtn = findViewById(R.id.addressBtn);
        backBtn = findViewById(R.id.backBtn);
        nextBtn = findViewById(R.id.nextBtn);
        wantMessageEdit = findViewById(R.id.wantMessageEdit);
        howtopaySp = findViewById(R.id.howtopaySp);
        sp_selectCity = findViewById(R.id.selectCIty);
        et_address = findViewById(R.id.address);

        ArrayAdapter cityAdapter = ArrayAdapter.createFromResource(this,R.array.city,android.R.layout.simple_spinner_item);
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_selectCity.setAdapter(cityAdapter);

        sp_selectCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                city = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        ArrayAdapter adapter = ArrayAdapter.createFromResource(this,R.array.phone,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        firstPhoneNum.setAdapter(adapter);

        //스피너 이벤트 발생
        firstPhoneNum.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //각 항목 클릭시 포지션값을 토스트에 띄운다.
                firstphoneNum = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter payadapter = ArrayAdapter.createFromResource(this,R.array.howtopay,android.R.layout.simple_spinner_item);
        payadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        howtopaySp.setAdapter(payadapter);

        //스피너 이벤트 발생
        howtopaySp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //각 항목 클릭시 포지션값을 토스트에 띄운다.
                howtopay = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter messageadapter = ArrayAdapter.createFromResource(this,R.array.messgae,android.R.layout.simple_spinner_item);
        messageadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        wantMessage.setAdapter(messageadapter);
        wantMessage.setSelection(0);

        //스피너 이벤트 발생
        wantMessage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //각 항목 클릭시 포지션값을 토스트에 띄운다.
//                Toast.makeText(getApplicationContext(), parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
                message = "";
                Log.d("요청사항 메시지는? ", message);
                if(parent.getItemAtPosition(position).toString().equals("직접 입력")){
                    wantMessageEdit.setVisibility(View.VISIBLE);
                    writemessage = wantMessageEdit.getText().toString();
                    Log.d("요청사항 메시지는? ", writemessage);
                }else{
                    wantMessageEdit.setVisibility(View.INVISIBLE);
                    message = parent.getItemAtPosition(position).toString();
                    Log.d("요청사항 메시지는? ", message);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Intent i = getIntent();
        if(i.getStringExtra("address") != null){

        }else if (i.getStringExtra("senderInfo") != null){
            senderInfo = i.getStringExtra("senderInfo");
            Log.d("보낸 이 정보", senderInfo);
        }

        addressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(CUReservationEditReceiver.this, DaumWebViewActivity.class);
                i.putExtra("activity", "cureservationreceiver");
                startActivity(i);
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(CUReservationEditReceiver.this, CUReservationEditSender.class);
                finish();
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //보낸 이 정보와 받는 이 정보를 예약내역 페이지로 같이 넘겨주자
                //받는 사람의 정보 (받는사람 이름, 받는사람 폰 번호, 받는사람 주소, 택배물건 정보, 포장박스 정보, 요청메시지 내용) 가지고 있어야함

                Intent i = new Intent(CUReservationEditReceiver.this, CUReservationCheckPage.class);
//                i.putExtra("senderInfo", senderInfo);
//                i.putExtra("receiverInfo", receiverInfo);
                startActivity(i);
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();

        String address = "";
        if(!et_address.getText().toString().equals("") && !city.equals("") && !detailAddress.getText().toString().equals("")){
            // 주소방식 바꿨다. 스피너로 시를 받고 + 도로명주소 + 상세주소를 합쳐야할지 나눌지 정하자.
            Log.d(TAG,city+", "+et_address.getText().toString()+", "+detailAddress.getText().toString());
            address = city+"##"+et_address.getText().toString()+"##"+detailAddress.getText().toString();
        }

        if(!midPhoneNum.getText().toString().equals("") && !lastPhoneNum.getText().toString().equals("")) {
            Log.d(TAG, firstphoneNum + "-" + midPhoneNum.getText().toString() + "-" + lastPhoneNum.getText().toString());
            fullPhoneNum = firstphoneNum + "-" + midPhoneNum.getText().toString() + "-" + lastPhoneNum.getText().toString();
        }

        if(!wantMessageEdit.getText().toString().equals("")) {
            writemessage = wantMessageEdit.getText().toString();
        }

        if(message.equals("")){
            receiverInfo = editReceiver.getText().toString() + "##" + fullPhoneNum + "##" + address + "##" + writemessage + "##" + howtopay;
            Log.d(TAG, receiverInfo);
        }else{
            receiverInfo = editReceiver.getText().toString() + "##" + fullPhoneNum + "##" + address + "##" + message + "##" + howtopay;
            Log.d(TAG, receiverInfo);
        }

        SharedPreferences preferences = getSharedPreferences("reservation", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString("receiverInfo",receiverInfo);
        editor.commit();
    }
}
