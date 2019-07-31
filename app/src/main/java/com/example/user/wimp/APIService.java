package com.example.user.wimp;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface APIService {
    @FormUrlEncoded
    @POST("/wimp/AddCJ.php")
    Call<ResponseBody> cjInsert(@Field("num") String num,
                                @Field("pi_send") String pi_send,
                                @Field("pi_recv") String pi_recv,
                                @Field("pi_info[]") ArrayList<String> pi_info,
                                @Field("p_level[]") ArrayList<String> p_level,
                                @Field("p_date[]") ArrayList<String> p_date,
                                @Field("p_where[]") ArrayList<String> p_where,
                                @Field("company") String company,
                                @Field("p_userId") String p_userId);

    @FormUrlEncoded
    @POST("/wimp/gmarketcartcrawl.php")
    Call<ResponseBody> gmarketInsert(@Field("g_info[]") ArrayList<String> g_info,
                                    @Field("g_cnt[]") ArrayList<String> g_cnt,
                                    @Field("g_price[]") ArrayList<String> g_price,
                                    @Field("g_level") String g_level,
                                    @Field("g_userid") String g_userid);

    @FormUrlEncoded
    @POST("/wimp/searchCJ.php")
    Call<ResponseBody> searchCJ(@Field("userid") String userid);

    @FormUrlEncoded
    @POST("/wimp/postcodify/addresstest.php")
    Call<ResponseBody> sendAddress(@Field("address") String address);

    @FormUrlEncoded
    @POST("/wimp/reserveCUinDB.php")
    Call<ResponseBody> reserveCU(@Field("rc_cuid") String rc_cuid,
                                 @Field("rc_cupw") String rc_cupw,
                                 @Field("rc_cate") String rc_cate,
                                 @Field("rc_money") String rc_money,
                                 @Field("rc_reserve") String rc_reserve,
                                 @Field("rc_sender") String rc_sender,
                                 @Field("rc_phonef") String rc_phonef,
                                 @Field("rc_phones") String rc_phones,
                                 @Field("rc_phonel") String rc_phonel,
                                 @Field("rc_addrc") String rc_addrc,
                                 @Field("rc_addrl") String rc_addrl,
                                 @Field("rc_addrd") String rc_addrd,
                                 @Field("rc_msg") String rc_msg);
}
