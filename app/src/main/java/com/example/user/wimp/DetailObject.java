package com.example.user.wimp;

public class DetailObject {

    private String p_num;
    private String p_send;
    private String p_recv;
    private String p_info;
    private String p_level;
    private String p_date;
    private String p_where;
    private String p_comp;

    public DetailObject(String p_num, String p_send, String p_recv, String piinfo, String p_level, String p_date, String p_where, String p_comp) {
        this.p_num = p_num;
        this.p_send = p_send;
        this.p_recv = p_recv;
        this.p_info = p_info;
        this.p_level = p_level;
        this.p_date = p_date;
        this.p_where = p_where;
        this.p_comp = p_comp;
    }

    public String getP_num() {
        return p_num;
    }

    public void setP_num(String i_num) {
        this.p_num = p_num;
    }

    public String getP_send() {
        return p_send;
    }

    public void seti_send(String p_send) {
        this.p_send = p_send;
    }

    public String getP_recv() {
        return p_recv;
    }

    public void setP_recv(String p_recv) {
        this.p_recv = p_recv;
    }

    public String getP_info() {
        return p_info;
    }

    public void setP_info(String p_info) {
        this.p_info = p_info;
    }

    public String getP_level() {
        return p_level;
    }

    public void setP_level(String p_level) {
        this.p_level = p_level;
    }

    public String getP_date() {
        return p_date;
    }

    public void setP_date(String p_date) {
        this.p_date = p_date;
    }

    public String getP_where() {
        return p_where;
    }

    public void setP_where(String p_where) {
        this.p_where = p_where;
    }

    public String getP_comp() {
        return p_comp;
    }

    public void setP_comp(String p_comp) {
        this.p_comp = p_comp;
    }
}
