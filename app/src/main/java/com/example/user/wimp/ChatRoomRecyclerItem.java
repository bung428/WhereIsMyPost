package com.example.user.wimp;

import android.net.Uri;

public class ChatRoomRecyclerItem {
    String msg;
    String name;
    String time;
    int type;
    boolean textorimage;
    Uri uri;
    int num;
    int image;

    ChatRoomRecyclerItem(String _msg,String _name,String _time,int _type,boolean _textorimage)
    {
        this.msg = _msg;
        this.name = _name;
        this.time = _time;
        this.type = _type;
        this.textorimage = _textorimage;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
}
