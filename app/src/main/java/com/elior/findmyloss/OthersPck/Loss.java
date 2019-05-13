package com.elior.findmyloss.OthersPck;

public class Loss {

    private String mName;
    private String mPhone;
    private String mPlace;
    private double mLat;
    private double mLng;
    private String mDescription;

    public Loss(String mName, String mPhone, String mPlace, double mLat, double mLng, String mDescription) {
        this.mName = mName;
        this.mPhone = mPhone;
        this.mPlace = mPlace;
        this.mLat = mLat;
        this.mLng = mLng;
        this.mDescription = mDescription;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmPhone() {
        return mPhone;
    }

    public void setmPhone(String mPhone) {
        this.mPhone = mPhone;
    }

    public String getmPlace() {
        return mPlace;
    }

    public void setmPlace(String mPlace) {
        this.mPlace = mPlace;
    }

    public double getmLat() {
        return mLat;
    }

    public void setmLat(double mLat) {
        this.mLat = mLat;
    }

    public double getmLng() {
        return mLng;
    }

    public void setmLng(double mLng) {
        this.mLng = mLng;
    }

    public String getmDescription() {
        return mDescription;
    }

    public void setmDescription(String mDescription) {
        this.mDescription = mDescription;
    }

}
