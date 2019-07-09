package com.elior.findmyloss.OthersPck;

public class LossModel {

    private String mName;
    private String mPhone;
    private String mPlace;
    private String mDate;
    private double mLat;
    private double mLng;
    private String mDescription;
    private String mImage;

    public LossModel() {

    }

    public LossModel(String mName, String mPhone, String mPlace, String mDate, double mLat, double mLng, String mDescription, String mImage) {
        this.mName = mName;
        this.mPhone = mPhone;
        this.mPlace = mPlace;
        this.mDate = mDate;
        this.mLat = mLat;
        this.mLng = mLng;
        this.mDescription = mDescription;
        this.mImage = mImage;
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

    public String getmDate() {
        return mDate;
    }

    public void setmDate(String mDate) {
        this.mDate = mDate;
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

    public String getmImage() {
        return mImage;
    }

    public void setmImage(String mImage) {
        this.mImage = mImage;
    }

}
