package com.example.user.wimp;

import java.util.ArrayList;

public class GmarketCart {

    private ArrayList<String> g_info;
    private ArrayList<String> g_cnt;
    private ArrayList<String> g_price;
    private String g_level;
    private String g_userid;

    public GmarketCart (ArrayList<String> g_info, ArrayList<String> g_cnt, ArrayList<String> g_price, String g_level, String g_userid) {
        this.g_info = g_info;
        this.g_cnt = g_cnt;
        this.g_price = g_price;
        this.g_level = g_level;
        this.g_userid = g_userid;
    }

    public ArrayList<String> getG_info() {
        return g_info;
    }

    public void setG_info(ArrayList<String> g_info) {
        this.g_info = g_info;
    }

    public ArrayList<String> getG_cnt() {
        return g_cnt;
    }

    public void setG_cnt(ArrayList<String> g_cnt) {
        this.g_cnt = g_cnt;
    }

    public ArrayList<String> getG_price() {
        return g_price;
    }

    public void setG_price(ArrayList<String> g_price) {
        this.g_price = g_price;
    }

    public String getG_level() {
        return g_level;
    }

    public void setG_level(String g_level) {
        this.g_level = g_level;
    }

    public String getG_userid() {
        return g_userid;
    }

    public void setG_userid(String g_userid) {
        this.g_userid = g_userid;
    }
}
