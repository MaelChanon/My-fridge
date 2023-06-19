package com.example.myapplication.utils;

import androidx.annotation.NonNull;
import com.example.myapplication.QrCodeScanner;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class FoodApiCallback implements Callback {
    CallBackActivity<String> task;
    public FoodApiCallback(CallBackActivity<String> task){
        this.task = task;
    }

    @Override
    public void onFailure(@NonNull Call call, @NonNull IOException e) {
    }

    @Override
    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
        if(response.isSuccessful())
            this.task.CallBack(response.body().string());
    }
}
