package com.example.user.wimp;

import android.util.Log;

import java.io.File;
import java.util.ArrayList;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class HttpConnection {
    private OkHttpClient client;
    private static HttpConnection instance = new HttpConnection();
    private static final MediaType MEDIA_TYPE = MediaType.parse("image/*");
    public static HttpConnection getInstance() {
        return instance;
    }

    private HttpConnection(){ this.client = new OkHttpClient(); }


    /** 웹 서버로 요청을 한다. */
    public void requestWebServer(ArrayList<String> parameter, Callback callback) {
        String [] imagefile, imagefile1, imagefile2, imagefile3, imagefile4;
        File file, file1, file2, file3, file4;
        switch (parameter.size()){
            case 1:
                imagefile = parameter.get(0).split("/");
                Log.d("ㅅㅂ", imagefile[imagefile.length-1]);
                file = new File(parameter.get(0));

                if(file!=null) {
                    RequestBody body = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("fileToUpload", imagefile[imagefile.length-1], RequestBody.create(MEDIA_TYPE, file))
                            .build();
                    Request request = new Request.Builder()
                            .url("http://115.71.232.235/wimp/multipart_test.php")
                            .post(body)
                            .build();
                    Log.d("아니 시바","뭔데");
                    client.newCall(request).enqueue(callback);
                    Log.d("아니 시바","뭔데!!!");
                }
                break;
            case 2:
                imagefile = parameter.get(0).split("/");
                Log.d("ㅅㅂ", imagefile[imagefile.length-1]);
                file = new File(parameter.get(0));

                imagefile1 = parameter.get(1).split("/");
                Log.d("ㅅㅂ", imagefile1[imagefile1.length-1]);
                file1 = new File(parameter.get(1));

                if(file!=null && file1!=null) {
                    RequestBody bodys = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("fileToUpload", imagefile[imagefile.length-1], RequestBody.create(MEDIA_TYPE, file))
                            .addFormDataPart("fileToUpload1", imagefile1[imagefile1.length-1], RequestBody.create(MEDIA_TYPE, file1))
                            .build();
                    Request request = new Request.Builder()
                            .url("http://115.71.232.235/wimp/multipart_test.php")
                            .post(bodys)
                            .build();
                    client.newCall(request).enqueue(callback);
                }
                break;
            case 3:
                imagefile = parameter.get(0).split("/");
                Log.d("ㅅㅂ", imagefile[imagefile.length-1]);
                file = new File(parameter.get(0));

                imagefile1 = parameter.get(1).split("/");
                Log.d("ㅅㅂ", imagefile1[imagefile1.length-1]);
                file1 = new File(parameter.get(1));

                imagefile2 = parameter.get(2).split("/");
                Log.d("ㅅㅂ", imagefile2[imagefile2.length-1]);
                file2 = new File(parameter.get(2));

                if(file!=null && file1!=null && file2!=null) {
                    RequestBody bodys = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("fileToUpload", imagefile[imagefile.length-1], RequestBody.create(MEDIA_TYPE, file))
                            .addFormDataPart("fileToUpload1", imagefile1[imagefile1.length-1], RequestBody.create(MEDIA_TYPE, file1))
                            .addFormDataPart("fileToUpload2", imagefile2[imagefile2.length-1], RequestBody.create(MEDIA_TYPE, file2))
                            .build();
                    Request request = new Request.Builder()
                            .url("http://115.71.232.235/wimp/multipart_test.php")
                            .post(bodys)
                            .build();
                    client.newCall(request).enqueue(callback);
                }
                break;
            case 4:
                imagefile = parameter.get(0).split("/");
                Log.d("ㅅㅂ", imagefile[imagefile.length-1]);
                file = new File(parameter.get(0));

                imagefile1 = parameter.get(1).split("/");
                Log.d("ㅅㅂ", imagefile1[imagefile1.length-1]);
                file1 = new File(parameter.get(1));

                imagefile2 = parameter.get(2).split("/");
                Log.d("ㅅㅂ", imagefile2[imagefile2.length-1]);
                file2 = new File(parameter.get(2));

                imagefile3 = parameter.get(3).split("/");
                Log.d("ㅅㅂ", imagefile3[imagefile3.length-1]);
                file3 = new File(parameter.get(3));

                if(file!=null) {
                    RequestBody bodys = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("fileToUpload", imagefile[imagefile.length-1], RequestBody.create(MEDIA_TYPE, file))
                            .addFormDataPart("fileToUpload1", imagefile1[imagefile1.length-1], RequestBody.create(MEDIA_TYPE, file1))
                            .addFormDataPart("fileToUpload2", imagefile2[imagefile2.length-1], RequestBody.create(MEDIA_TYPE, file2))
                            .addFormDataPart("fileToUpload3", imagefile3[imagefile3.length-1], RequestBody.create(MEDIA_TYPE, file3))
                            .build();
                    Request request = new Request.Builder()
                            .url("http://115.71.232.235/wimp/multipart_test.php")
                            .post(bodys)
                            .build();
                    client.newCall(request).enqueue(callback);
                }
                break;
            case 5:
                imagefile = parameter.get(0).split("/");
                Log.d("ㅅㅂ", imagefile[imagefile.length-1]);
                file = new File(parameter.get(0));

                imagefile1 = parameter.get(1).split("/");
                Log.d("ㅅㅂ", imagefile1[imagefile1.length-1]);
                file1 = new File(parameter.get(1));

                imagefile2 = parameter.get(2).split("/");
                Log.d("ㅅㅂ", imagefile2[imagefile2.length-1]);
                file2 = new File(parameter.get(2));

                imagefile3 = parameter.get(3).split("/");
                Log.d("ㅅㅂ", imagefile3[imagefile3.length-1]);
                file3 = new File(parameter.get(3));

                imagefile4 = parameter.get(4).split("/");
                Log.d("ㅅㅂ", imagefile4[imagefile4.length-1]);
                file4 = new File(parameter.get(4));
                if(file!=null && file1!=null && file2!=null && file3!=null && file4!=null) {
                    RequestBody bodys = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("fileToUpload", imagefile[imagefile.length-1], RequestBody.create(MEDIA_TYPE, file))
                            .addFormDataPart("fileToUpload1", imagefile1[imagefile1.length-1], RequestBody.create(MEDIA_TYPE, file1))
                            .addFormDataPart("fileToUpload2", imagefile2[imagefile2.length-1], RequestBody.create(MEDIA_TYPE, file2))
                            .addFormDataPart("fileToUpload3", imagefile3[imagefile3.length-1], RequestBody.create(MEDIA_TYPE, file3))
                            .addFormDataPart("fileToUpload4", imagefile4[imagefile4.length-1], RequestBody.create(MEDIA_TYPE, file4))
                            .build();
                    Request request = new Request.Builder()
                            .url("http://115.71.232.235/wimp/multipart_test.php")
                            .post(bodys)
                            .build();
                    client.newCall(request).enqueue(callback);
                }
                break;
        }
    }

}
