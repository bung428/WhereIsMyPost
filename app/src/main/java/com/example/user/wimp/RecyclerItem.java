package com.example.user.wimp;

public class RecyclerItem {
    private String level;
    private String date;
    private String comp;
    private String info;
    private String day;
//    private int image;

    public RecyclerItem (String level, String date, String comp, String info, String day){
        this.level = level;
        this.date = date;
        this.comp = comp;
        this.info = info;
        this.day = day;
    }

    public String getLevel() {
        return level;
    }

    public String getDate() {
        return date;
    }

    public String getComp() {
        return comp;
    }

    public String getInfo() {
        return info;
    }

    public String getDay() { return day;}

//    public int getImage() {
//        return image;
//    }
//
//    public void setImage(int image) {
//        this.image = image;
//    }
}

