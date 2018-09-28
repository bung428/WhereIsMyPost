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

    EditText editReceiver, midPhoneNum, lastPhoneNum, detailAddress, postprice, wantMessageEdit;
    Spinner firstPhoneNum, postcategory, wantMessage, howtopaySp;
    TextView address, smallboxcountTv, bigboxcountTv;
    Button addressBtn, smallboxminusBtn, smallboxplusBtn, bigboxminusBtn, bigboxplusBtn, backBtn, nextBtn;
    CheckBox smallboxCheck, bigboxCheck;

    String firstphoneNum, addressFeatAPI, fullAddress, category, message, senderInfo, fullPhoneNum, fullPostInfo, postBox, writemessage, receiverInfo, boxPrice, howtopay;
    int smallboxcounting = 0, bigboxcounting = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cureservationreceiver);

        editReceiver = findViewById(R.id.editReceiver);
        midPhoneNum = findViewById(R.id.midPhoneNum);
        lastPhoneNum = findViewById(R.id.lastPhoneNum);
        detailAddress = findViewById(R.id.detailAddress);
//        postprice = findViewById(R.id.postprice);
        firstPhoneNum = findViewById(R.id.firstPhoneNum);
//        postcategory = findViewById(R.id.postcategory);
        wantMessage = findViewById(R.id.wantMessage);
        address = findViewById(R.id.address);
//        smallboxcountTv = findViewById(R.id.smallboxcountTv);
//        bigboxcountTv = findViewById(R.id.bigboxcountTv);
        addressBtn = findViewById(R.id.addressBtn);
//        smallboxminusBtn = findViewById(R.id.smallboxminusBtn);
//        smallboxplusBtn = findViewById(R.id.smallboxplusBtn);
//        bigboxminusBtn = findViewById(R.id.bigboxminusBtn);
//        bigboxplusBtn = findViewById(R.id.bigboxplusBtn);
//        smallboxCheck = findViewById(R.id.smallboxCheck);
//        bigboxCheck = findViewById(R.id.bigboxCheck);
        backBtn = findViewById(R.id.backBtn);
        nextBtn = findViewById(R.id.nextBtn);
        wantMessageEdit = findViewById(R.id.wantMessageEdit);
        howtopaySp = findViewById(R.id.howtopaySp);

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

        ArrayAdapter payadapter = ArrayAdapter.createFromResource(this,R.array.howtopay,android.R.layout.simple_spinner_item);
        payadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        howtopaySp.setAdapter(payadapter);

        //스피너 이벤트 발생
        howtopaySp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //각 항목 클릭시 포지션값을 토스트에 띄운다.
//                Toast.makeText(getApplicationContext(), parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
                howtopay = parent.getItemAtPosition(position).toString();
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

//        ArrayAdapter categoryadapter = ArrayAdapter.createFromResource(this,R.array.category,android.R.layout.simple_spinner_item);
//        categoryadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        postcategory.setAdapter(categoryadapter);
//        postcategory.setSelection(0);
//
//        //스피너 이벤트 발생
//        postcategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                //각 항목 클릭시 포지션값을 토스트에 띄운다.
////                Toast.makeText(getApplicationContext(), parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
//                category = parent.getItemAtPosition(position).toString();
//                Log.d("카테고리는? ", category);
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });

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
//            Log.d("주소왔다", i.getStringExtra("address"));
            //주소 api를 받아와서 텍스트 뷰에 넣어놨다.
            addressFeatAPI = i.getStringExtra("address");
            address.setText(addressFeatAPI);

        }else if (i.getStringExtra("senderInfo") != null){
            senderInfo = i.getStringExtra("senderInfo");
            Log.d("보낸 이 정보", senderInfo);
        }

        addressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(CUReservationEditReceiver.this, AddressAct.class);
                i.putExtra("activity", "cureservationreceiver");
                startActivity(i);
            }
        });

//        smallboxCheck.setChecked(false);
//        bigboxCheck.setChecked(false);

//        smallboxCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                if(smallboxCheck.isChecked()){
//                    //극소형 박스를 선택하였을 때 -> 극소형박스 카운트 올려주기
//                    smallboxcountTv.setText("1");
//                }else{
//                    //극소형 박스 카운트 0으로 바꾸기
//                    smallboxcountTv.setText("0");
//                }
//            }
//        });
//
//        bigboxCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                if(bigboxCheck.isChecked()){
//                    //중형 박스를 선택하였을 때 -> 극소형박스 카운트 올려주기
//                    bigboxcountTv.setText("1");
//                }else{
//                    //중형 박스 카운트 0으로 바꾸기
//                    bigboxcountTv.setText("0");
//                }
//            }
//        });
//
//        smallboxminusBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //극소형 카운트가 0이면 더 이상 변화없게
//                smallboxcounting = Integer.parseInt(smallboxcountTv.getText().toString());
//                Log.d("극소형 박스 갯수", smallboxcounting+"");
//                smallboxcounting = smallboxcounting - 1;
//                if(smallboxcounting <= 0 ){
//                    smallboxcountTv.setText("0");
//                    smallboxCheck.setChecked(false);
//                }else{
//                    smallboxcountTv.setText(smallboxcounting+"");
//                    if(smallboxcounting <= 0 ){
//                        smallboxCheck.setChecked(false);
//                    }
//                }
//            }
//        });
//
//        smallboxplusBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //극소형 카운트가 0보다 크게되면 극소형 체크박스 = true되게
//                smallboxcounting = Integer.parseInt(smallboxcountTv.getText().toString());
//                smallboxcounting = smallboxcounting + 1;
//                if(smallboxcounting > 0){
//                    smallboxCheck.setChecked(true);
//                    smallboxcountTv.setText(smallboxcounting+"");
//                }
//            }
//        });
//
//        bigboxminusBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //중형 카운트가 0이면 더 이상 변화없게
//                bigboxcounting = Integer.parseInt(bigboxcountTv.getText().toString());
//                bigboxcounting = bigboxcounting - 1;
//                if(bigboxcounting < 0){
//                    bigboxcountTv.setText("0");
//                    bigboxCheck.setChecked(false);
//                }else{
//                    bigboxcountTv.setText(bigboxcounting+"");
//                    if(bigboxcounting <= 0 ){
//                        bigboxCheck.setChecked(false);
//                    }
//                }
//            }
//        });
//
//        bigboxplusBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //중형 카운트가 0보다 크게되면 중형 체크박스 = true되게
//                bigboxcounting = Integer.parseInt(bigboxcountTv.getText().toString());
//                bigboxcounting = bigboxcounting + 1;
//                if(bigboxcounting > 0){
//                    bigboxCheck.setChecked(true);
//                    bigboxcountTv.setText(bigboxcounting+"");
//                }
//            }
//        });

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

        if(!detailAddress.getText().toString().equals("")){
            //상세 주소까지 입력한 경우
            //주소 합쳐서 변수로 가지고 있자
            fullAddress = addressFeatAPI + detailAddress.getText().toString();
            Log.d("주소 합쳐졌나? receiver", fullAddress);
        }

        if(!midPhoneNum.getText().toString().equals("") && !lastPhoneNum.getText().toString().equals(""))
            fullPhoneNum = firstphoneNum + midPhoneNum.getText().toString() + lastPhoneNum.getText().toString();

//        if(!postprice.equals("")){
//            fullPostInfo = category + "!!!!" + postprice.getText().toString();
//        }

//        if(Integer.parseInt(smallboxcountTv.getText().toString()) > 0) {
//            postBox = "극소형 " + Integer.parseInt(smallboxcountTv.getText().toString());
//            boxPrice = "3,300";
//        }
//
//        if(Integer.parseInt(bigboxcountTv.getText().toString()) > 0){
//            postBox = "중형 " + Integer.parseInt(bigboxcountTv.getText().toString());
//            boxPrice = "3,800";
//        }

        if(!wantMessageEdit.getText().toString().equals("")) {
            writemessage = wantMessageEdit.getText().toString();
        }

        if(message.equals("")){
            receiverInfo = editReceiver.getText().toString() + "##" + fullPhoneNum + "##" + fullAddress + "##" + writemessage + "##" + howtopay;
            Log.d("받는 이 정보", receiverInfo);
        }else{
            receiverInfo = editReceiver.getText().toString() + "##" + fullPhoneNum + "##" + fullAddress + "##" + message + "##" + howtopay;
            Log.d("받는 이 정보", receiverInfo);
        }

        SharedPreferences preferences = getSharedPreferences("reservation", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString("receiverInfo",receiverInfo);
        editor.commit();
    }
}
