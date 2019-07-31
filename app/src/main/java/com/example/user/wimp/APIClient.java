package com.example.user.wimp;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIClient {

    private static Retrofit retrofit = null;

    public static Retrofit getClient() {

        // 이 부분은 http상에서 나오는 에러를 로그를 찍어보기 위해서
        //   나중에 추가하면 좋을듯
        //   HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        //   interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        //   OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(100, TimeUnit.SECONDS)
                .readTimeout(100,TimeUnit.SECONDS).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ServerIP.serverIp)  //
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        return retrofit;
    }


    // getClient1로 따로 함수를 만든 이유는
    // getClient1은 받은 응답을 옵저버블 형태로 변환하기 때문에
    // 혹시 기존 소스가 에러가 날 수 있다는 생각이 들어서 이렇게 따로 뺏다.

    public static Retrofit getClient1(){

        if (retrofit == null) {

            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();  // http통신에 대한 로그를 찍어보기 위함
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient okHttpClient = new OkHttpClient.Builder().addInterceptor(interceptor).build();

            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();

            retrofit = new Retrofit.Builder()
                    .baseUrl(ServerIP.serverIp)
                    //.baseUrl("http://www.weather.com.cn/")

                    // 서버에서 json 형식으로 데이터를 보내고 이를 파싱해서 받아옵니다.
                    .addConverterFactory(GsonConverterFactory.create(gson))

                    // 받은 응답을 옵저버블 형태로 변환해 줍니다.
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())

                    // 네트워크 요청 로그를 표시해 줍니다.
                    .client(okHttpClient)
                    .build();

        }
        return retrofit;
    }
}
