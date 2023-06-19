package com.example.myapplication;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

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
import com.example.myapplication.utils.FoodApiCallback;
import com.example.myapplication.utils.HTTPRequest;
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
        replaceFragment(new Home_fragment());
        binding.navigationView.setOnItemSelectedListener(item -> {
            if(item.getItemId() == R.id.my_food){
                Log.d("TAG", "my food ");
                replaceFragment(new Food_fragment());
            }
            else if (item.getItemId() == R.id.scanner) {
                Log.d("TAG", "scanner ");
                replaceFragment(new Home_fragment());
            }
            else if (item.getItemId() == R.id.my_fridge) {
                Log.d("TAG", "my fridge ");
                replaceFragment(new Fridge_fragment());

            }
            return true;
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.frameLayouts);
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
        fragmentTransaction.commit();
    }

    @Override
    public void CallBack(String object) {
        Intent intent = new Intent(getApplicationContext(), Aliment.class);
        intent.putExtra("json",object);
        startActivity(intent);
        finish();
    }


}
