package com.example.myapplication.utils;
import static android.content.ContentValues.TAG;

import android.util.Log;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class HTTPRequest {


    public void makeGetRequest(String requestUrl, Callback callback) {
        Log.d(TAG, "makeGetRequest: "+requestUrl);
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(requestUrl)
                .build();
        client.newCall(request).enqueue(callback);
    }

}
