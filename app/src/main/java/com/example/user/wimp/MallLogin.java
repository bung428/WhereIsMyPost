package com.example.user.wimp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;

import javax.crypto.Cipher;

public class MallLogin extends AppCompatActivity {

    ImageView mall_logo;
    Button mall_btn;
    EditText mall_id,mall_pw;
    CheckBox mall_auto;

    String mall,message,id,pwd;
    String[] mall_data,user;
    Boolean check=false,value;

    ArrayList<String> mall_user=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mall_login);

        mall_btn=findViewById(R.id.mall_btn);
        mall_logo=findViewById(R.id.mall_logo);
        mall_id=findViewById(R.id.mall_id);
        mall_pw=findViewById(R.id.mall_pw);
        mall_auto=findViewById(R.id.mall_auto);

        Intent intent=getIntent();
        if(intent!=null){
            mall=intent.getStringExtra("mall");
            switch (mall){
                case "pang" :
                    mall_logo.setImageResource(R.drawable.pang);
                    break;
                case "gmarket" :
                    mall_logo.setImageResource(R.drawable.gmarket);
                    break;
            }
        }

        mall_auto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mall_auto.isChecked()==true) {
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

        SharedPreferences preferences=getSharedPreferences("mall",MODE_PRIVATE);

        if (preferences.getString(mall,null)==null) {
            mall_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    id = mall_id.getText().toString();
                    pwd = mall_pw.getText().toString();

                    Intent i = new Intent(MallLogin.this, MallCrawl.class);
                    i.putExtra("mall", mall);
                    i.putExtra("id", id);
                    i.putExtra("pwd", pwd);
                    startActivity(i);
                }
            });
        }else {
            Intent i = new Intent(MallLogin.this, MallCrawl.class);
            startActivity(i);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences preferences = getSharedPreferences("mall",MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        switch (mall){
            case "pang":
                if (preferences.getString(mall,null)==null){
                    id=mall_id.getText().toString();
                    pwd=mall_pw.getText().toString();
                    String idpw=id+"##"+pwd;
                    if(!check){
                        Log.d("mall","no auto login");
                    }else{
                        editor.putString(mall,idpw);
                        editor.commit();
                        Log.d("mall","check saving pang id, pw=>"+preferences.getString(mall,null));
                    }
                }
                break;
            case "gmarket":
                if (preferences.getString(mall,null)==null){
                    id=mall_id.getText().toString();
                    pwd=mall_pw.getText().toString();
                    String idpw=id+"##"+pwd;

                    Key publicKey = null;
                    Key privateKey = null;

                    try {
                        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
                        kpg.initialize(1024);
                        KeyPair kp = kpg.genKeyPair();
                        publicKey = kp.getPublic();
                        privateKey = kp.getPrivate();

                        try {
                            FileOutputStream pubkey = new FileOutputStream(Environment.getDataDirectory() +"/data/com.example.user.opencvcmake/files/"+mall+".pub");
                            pubkey.write(publicKey.getEncoded());
                            pubkey.close();

                            FileOutputStream prikey = new FileOutputStream(Environment.getDataDirectory() +"/data/com.example.user.opencvcmake/files/"+mall+".key");
                            prikey.write(privateKey.getEncoded());
                            prikey.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    } catch (Exception e) {
                        Log.e("RSATEST", "RSA key pair error");
                    }

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

                    // Encode the original data with RSA private key
                    byte[] encodedBytes = null;
                    try {
                        Cipher c = Cipher.getInstance("RSA");
                        c.init(Cipher.ENCRYPT_MODE, publicKey1);
                        encodedBytes = c.doFinal(idpw.getBytes());
                    } catch (Exception e) {
                        Log.e("RSATEST", "RSA encryption error");
                    }
//                    TextView tvencoded = (TextView) findViewById(R.id.textView);
//                    tvencoded.setText("[ENCODED]:\n" +
//                            Base64.encodeToString(encodedBytes, Base64.DEFAULT) + "\n");
                    Log.d("암호화? ", Base64.encodeToString(encodedBytes, Base64.DEFAULT));
                    idpw = Base64.encodeToString(encodedBytes, Base64.DEFAULT);
                    // Decode the encoded data with RSA public key
                    byte[] decodedBytes = null;
                    try {
                        Cipher c = Cipher.getInstance("RSA");
                        c.init(Cipher.DECRYPT_MODE, privateKey1);
                        decodedBytes = c.doFinal(encodedBytes);
                    } catch (Exception e) {
                        Log.e("RSATEST", "RSA decryption error");
                    }
                    Log.d("복호화? ", new String(decodedBytes, 0, decodedBytes.length));
//                    TextView tvdecoded = (TextView) findViewById(R.id.textView1);
//                    tvdecoded.setText("[DECODED]:\n" +
//                            new String(decodedBytes) + "\n");

                    if(!check){
                        Log.d("mall","no auto login");
                    }else{
                        editor.putString(mall,idpw);
                        editor.commit();
                        Log.d("mall","check saving gmarket id, pw=>"+preferences.getString(mall,null));
                    }
                }
                break;
        }
    }
}
