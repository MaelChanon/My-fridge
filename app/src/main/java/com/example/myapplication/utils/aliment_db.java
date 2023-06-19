package com.example.myapplication.utils;

public class aliment_db {
    private String aliment_code;
    private String date_String;
    public aliment_db(String property1, String property2) {
        this.aliment_code = property1;
        this.date_String = property2;
    }

    public String getAliment_code() {
        return aliment_code;
    }

    public String getDate_String() {
        return date_String;
    }
}
