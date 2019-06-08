package com.example.user.wimp;

import android.util.Log;

import java.io.File;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class HttpConnectionARimage {
    private OkHttpClient client;
    private static HttpConnectionARimage instance = new HttpConnectionARimage();
    private static final MediaType MEDIA_TYPE = MediaType.parse("image/*");
    public static HttpConnectionARimage getInstance() {
        return instance;
    }
    ServerIP serverIP;

    private HttpConnectionARimage(){ this.client = new OkHttpClient(); }


    /** 웹 서버로 요청을 한다. */
    public void requestWebServer(String parameter, Callback callback) {
        String[] imagefile;
        File file;

        imagefile = parameter.split("/");
        Log.d("ㅅㅂ", imagefile[imagefile.length-1]);
        file = new File(parameter);

        if(file!=null) {
            RequestBody body = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("fileToUpload", imagefile[imagefile.length-1], RequestBody.create(MEDIA_TYPE, file))
                    .build();
            Request request = new Request.Builder()
                    .url(serverIP.serverIp+"/wimp/imageupload.php")
                    .post(body)
                    .build();
            client.newCall(request).enqueue(callback);
        }
    }
}
