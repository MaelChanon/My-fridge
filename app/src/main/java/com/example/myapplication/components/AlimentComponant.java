package com.example.myapplication.components;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.example.myapplication.R;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.GregorianCalendar;

public class AlimentComponant extends CardView implements Comparable<AlimentComponant> {
    private GregorianCalendar datePeremption;

    public AlimentComponant(@NonNull Context context) {
        super(context);
        init(context,null);
    }

    public AlimentComponant(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    public AlimentComponant(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
    }

    public AlimentComponant(Context applicationContext, String image_url, String name, GregorianCalendar datePeremption,int remaining) {
        super(applicationContext);
        this.datePeremption = datePeremption;
        init(applicationContext,null,image_url,name,datePeremption,remaining);
    }

    private void init(Context context, AttributeSet attrs) {
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.aliment_componant, this);
    }
    private void init(Context context, AttributeSet attrs,String image_url, String name, GregorianCalendar datePeremption,int remaining){
        init(context,attrs);
        long days = (datePeremption.getTimeInMillis() - new GregorianCalendar().getTimeInMillis()) / (24 * 60 * 60 * 1000);

        Handler mainHandler = new Handler(Looper.getMainLooper());
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                Picasso.get().
                        load(image_url)
                        .fit()
                        .centerInside()
                        .into((ImageView) findViewById(R.id.imageAliment));
                ((TextView)findViewById(R.id.aliment_days)).setText(String.format("%d jours restant",days));
                ((TextView)findViewById(R.id.aliment_name)).setText(String.format("%s",name));
                ((TextView)findViewById(R.id.quantite_restante)).setText(String.format("%d exemplaires",remaining));

            }
        });
            }

    @Override
    public int compareTo(AlimentComponant alimentComponant) {
        return this.datePeremption.compareTo(alimentComponant.datePeremption);
    }
}
