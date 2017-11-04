package com.example.fiyinfolu.alccryptocompare;

/**
 * Created by fiyinfolu on 11/4/17.
 */

public class ArrayFormat {
    String title, currency;

    public ArrayFormat(String title, String currency) {
        this.title = title;
        this.currency = currency;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getTitle() {
        return title;
    }

    public String getCurrency() {
        return currency;
    }
}
