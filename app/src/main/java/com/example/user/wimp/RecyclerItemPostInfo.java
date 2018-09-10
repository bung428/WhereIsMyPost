package com.example.user.wimp;

public class RecyclerItemPostInfo {
    private String date;
    private String where;
    private String level;
    private int detail;

    public RecyclerItemPostInfo (String date, String where, String level){
        this.date = date;
        this.where = where;
        this.level = level;
    }

    public String getDate() {
        return date;
    }

    public String getWhere() {
        return where;
    }

    public String getLevel() {
        return level;
    }

    public int getDetail() {
        return detail;
    }

    public void setDetail(int detail) {
        this.detail = detail;
    }
}
