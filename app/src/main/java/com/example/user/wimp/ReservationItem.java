package com.example.user.wimp;

public class ReservationItem {
    String num;
    String reservationNum;

    public ReservationItem (String num, String reservationNum){
        this.num = num;
        this.reservationNum = reservationNum;
    }

    public String getNum() {
        return num;
    }

    public String getReservationNum() {
        return reservationNum;
    }
}
