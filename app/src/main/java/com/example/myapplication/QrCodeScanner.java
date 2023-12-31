package com.example.myapplication;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.myapplication.databinding.BottomNavBinding;

import com.example.myapplication.fragments.Food_fragment;
import com.example.myapplication.fragments.Fridge_fragment;
import com.example.myapplication.fragments.Home_fragment;

import com.example.myapplication.utils.CallBackActivity;

import com.example.myapplication.utils.foodApiCall;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;


@SuppressLint("MissingInflatedId")

public class QrCodeScanner extends AppCompatActivity implements CallBackActivity<String> {
    private static final String url = "https://world.openfoodfacts.org/api/v0/product/%s.json?fields=product_name_fr,quantity,nutriments.energy-kcal_100g,nutriments.fat_100g,nutriments.fiber_100g,nutriments.proteins_100g,nutriments.sugars_100g,image_url";
    BottomNavBinding binding;
    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        getSupportActionBar().hide();
        binding = BottomNavBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        FragmentManager fragmentManager = getSupportFragmentManager();
        boolean isBackStackEmpty = fragmentManager.popBackStackImmediate();
        Fragment previousFragment = null;
        if (!isBackStackEmpty) {
            // A fragment was popped from the back stack
            previousFragment = fragmentManager.findFragmentById(R.id.frameLayouts);
        }
        if(previousFragment==null)
            replaceFragment(new Home_fragment());
        else{
            replaceFragment(previousFragment);
        }
        binding.navigationView.setOnItemSelectedListener(item -> {
            if(item.getItemId() == R.id.my_food){
                replaceFragment(new Food_fragment());
            }
            else if (item.getItemId() == R.id.scanner) {
                replaceFragment(new Home_fragment());
            }
            else if (item.getItemId() == R.id.my_fridge) {
                replaceFragment(new Fridge_fragment());

            }
            return true;
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.frameLayouts);
        ProgressBar progressBar = findViewById(R.id.homeProgress);
        if(progressBar != null)
            progressBar.setVisibility(View.GONE);
        if (fragment instanceof Home_fragment) {

            IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
            if(intentResult != null){
                String contents = intentResult.getContents();
                new foodApiCall(this).doInBackground(contents);
            }
            else{
                super.onActivityResult(requestCode, resultCode, data);

            }
        }


    }
    public void DisplayAliment(String json){
        Intent intent = new Intent(getApplicationContext(), Aliment.class);
        intent.putExtra("json",json);
        startActivity(intent);
        finish();
    }
    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayouts,fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void CallBack(String object) {
        if(!object.contains("\"status\":1")){
            Handler mainHandler = new Handler(Looper.getMainLooper());
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(),"Le code est invalide",Toast.LENGTH_LONG).show();
                }
            });
            return;
        }
        Intent intent = new Intent(getApplicationContext(), Aliment.class);
        intent.putExtra("json",object);
        startActivity(intent);
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.frameLayouts);

        if (currentFragment != null) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frameLayouts, currentFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
    }
}
