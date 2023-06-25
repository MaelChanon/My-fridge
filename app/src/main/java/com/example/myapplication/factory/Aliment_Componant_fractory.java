package com.example.myapplication.factory;

import android.app.Activity;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.LinearLayout;

import com.example.myapplication.Aliment;
import com.example.myapplication.components.AlimentComponant;
import com.example.myapplication.fragments.Fridge_fragment;
import com.example.myapplication.utils.db.Aliment_DB;

public class Aliment_Componant_fractory {
    public static AlimentComponant getAliment(Activity activity, Aliment_DB aliment_db, String url, String name, View.OnClickListener callback) {
        DisplayMetrics metrics = activity.getResources().getDisplayMetrics();
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(0, 0, 0, (int) (13 * (metrics.densityDpi / 160f)));
        AlimentComponant aliment = new AlimentComponant(activity, url, name, aliment_db.DatePeremption(), aliment_db.getNb_aliments());
        aliment.setLayoutParams(layoutParams);
        aliment.setRadius(10f * (metrics.densityDpi / 160f));
        aliment.setOnClickListener(callback);
        return aliment;

    }
}
