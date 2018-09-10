package com.example.user.wimp;

public class RecyclerChatItem {
    private String name;
    private String text;
    private String date;
    private int image;

    public RecyclerChatItem (String name, String text, String date){
        this.name = name;
        this.text = text;
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public String getText() {
        return text;
    }

    public String getDate() {
        return date;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
}
