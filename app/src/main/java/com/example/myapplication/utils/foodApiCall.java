package com.example.myapplication.utils;

import android.os.AsyncTask;

import com.example.myapplication.QrCodeScanner;

public class foodApiCall extends AsyncTask<String,Void, Void>  {
    private static String url = "https://world.openfoodfacts.org/api/v0/product/%s.json?fields=product_name_fr,quantity,nutriments.energy-kcal_100g,nutriments.fat_100g,nutriments.fiber_100g,nutriments.proteins_100g,nutriments.sugars_100g,image_url";
    private CallBackActivity activity;
    public foodApiCall(CallBackActivity activity){
        this.activity = activity;
    }

    @Override
    public Void doInBackground(String... strings) {
        String qrCodeData = strings[0];
        new HTTPRequest().makeGetRequest(String.format(url, qrCodeData), new FoodApiCallback(activity));
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {

    }


}