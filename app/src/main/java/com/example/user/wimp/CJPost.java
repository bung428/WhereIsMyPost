package com.example.user.wimp;

import java.io.Serializable;
import java.util.ArrayList;

public class CJPost {

    String num; // 송장번호
    String pi_send; // 보내는 사람(혹은 회사)
    String pi_recv; // 받는 사람
    ArrayList<String> pi_info; // 배송 메시지
    ArrayList<String> p_level; // 배송 레벨
    ArrayList<String> p_date; // 배송 시간
    ArrayList<String> p_where; // 배송 위치
    String company; // 회사
    String p_userId; // 유저 아이디

    public CJPost(String num, String pi_send, String pi_recv, ArrayList<String> pi_info, ArrayList<String> p_level,
                  ArrayList<String> p_date, ArrayList<String> p_where, String company, String p_userId) {
        this.num = num;
        this.pi_send = pi_send;
        this.pi_recv = pi_recv;
        this.pi_info = pi_info;
        this.p_level = p_level;
        this.p_date = p_date;
        this.p_where = p_where;
        this.company = company;
        this.p_userId = p_userId;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getPi_send() {
        return pi_send;
    }

    public void setPi_send(String pi_send) {
        this.pi_send = pi_send;
    }

    public String getPi_recv() {
        return pi_recv;
    }

    public void setPi_recv(String pi_recv) {
        this.pi_recv = pi_recv;
    }

    public ArrayList<String> getPi_info() {
        return pi_info;
    }

    public void setPi_info(ArrayList<String> pi_info) {
        this.pi_info = pi_info;
    }

    public ArrayList<String> getP_level() {
        return p_level;
    }

    public void setP_level(ArrayList<String> p_level) {
        this.p_level = p_level;
    }

    public ArrayList<String> getP_date() {
        return p_date;
    }

    public void setP_date(ArrayList<String> p_date) {
        this.p_date = p_date;
    }

    public ArrayList<String> getP_where() {
        return p_where;
    }

    public void setP_where(ArrayList<String> p_where) {
        this.p_where = p_where;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getP_userId() {
        return p_userId;
    }

    public void setP_userId(String p_userId) {
        this.p_userId = p_userId;
    }
}
