package com.example.fiyinfolu.alccryptocompare;

/**
 * Created by fiyinfolu on 11/4/17.
 */

public class Currency {
    private String mTitle;
    private double mBTCValue;
    private double mETHValue;

    public Currency() {}

    public Currency(String title, double BTCValue, double ETHValue) {
        this.mTitle = title;
        this.mBTCValue = BTCValue;
        this.mETHValue = ETHValue;
    }

    public String getTitle() {
        return mTitle;
    }

    public double getBTCValue() {
        return mBTCValue;
    }

    public double getETHValue() {
        return mETHValue;
    }

    public void setBTCValue(double BTCValue) {
        mBTCValue = BTCValue;
    }

    public void setETHValue(double ETHValue) {
        mETHValue = ETHValue;
    }

    public void setTitle(String title) {

        mTitle = title;
    }
}
