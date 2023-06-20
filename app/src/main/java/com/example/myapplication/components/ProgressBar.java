package com.example.myapplication.components;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.myapplication.R;

public class ProgressBar extends LinearLayout {
    private TextView progress_bar_name;
    private LinearLayout progress_bar_container;
    private android.widget.ProgressBar progress_bar;
    private TextView progress_bar_text;
    public ProgressBar(@NonNull Context context) {
        super(context);
        init(context,null);
    }
    public ProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ProgressBar(Context context, TextView progress_bar_name) {
        super(context);
        this.progress_bar_name = progress_bar_name;
    }

    public ProgressBar(Context context, @Nullable AttributeSet attrs, TextView progress_bar_name) {
        super(context, attrs);
        this.progress_bar_name = progress_bar_name;
    }

    public ProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr, TextView progress_bar_name) {
        super(context, attrs, defStyleAttr);
        this.progress_bar_name = progress_bar_name;
    }

    public ProgressBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes, TextView progress_bar_name) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.progress_bar_name = progress_bar_name;
    }

    private void init(Context context, AttributeSet attrs) {
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.progress_bar_componant, this);

        progress_bar_name = findViewById(R.id.progress_bar_name);
        progress_bar_container = findViewById(R.id.progress_bar_container);
        progress_bar = findViewById(R.id.progress_bar);
        progress_bar_text = findViewById(R.id.progress_bar_text);
        if(attrs == null)
            return;
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomComponent);
        String text = typedArray.getString(R.styleable.CustomComponent_text_progress_bar);
        // Utilisez la valeur de l'attribut personnalisé comme nécessaire
        int color = typedArray.getColor(R.styleable.CustomComponent_progress_bar_color, Color.CYAN);
        if (text != null) {
            progress_bar_name.setText(text);
        }
        ColorStateList colorStateList = ColorStateList.valueOf(color);
        progress_bar.setProgressTintList(colorStateList);

    }
    public void setProgress(double value){
        this.progress_bar.setProgress((int) value);
        this.progress_bar_text.setText(value + " g");
    }
}
