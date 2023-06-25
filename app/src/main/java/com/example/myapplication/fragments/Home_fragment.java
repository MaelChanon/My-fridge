package com.example.myapplication.fragments;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myapplication.Aliment;
import com.example.myapplication.QrCodeScanner;
import com.example.myapplication.R;
import com.example.myapplication.utils.CallBackActivity;
import com.example.myapplication.utils.Empty_activity;
import com.example.myapplication.utils.foodApiCall;
import com.google.zxing.integration.android.IntentIntegrator;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Home_fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Home_fragment extends Fragment implements CallBackActivity<String> {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private Button scanButton;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ViewGroup parentLayout;

    public Home_fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Home_Fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Home_fragment newInstance(String param1, String param2) {
        Home_fragment fragment = new Home_fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ?
                inflater.inflate(R.layout.fragment_home_land, container, false) :
                inflater.inflate(R.layout.fragment_home, container, false);
        parentLayout = container;
        scanButton = view.findViewById(R.id.scanner_btn);
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator intentIntegrator = new IntentIntegrator(getActivity());
                intentIntegrator.setBeepEnabled(false);
                intentIntegrator.setPrompt("Scannez votre code bar");
                intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                intentIntegrator.setCaptureActivity(Empty_activity.class);
                intentIntegrator.initiateScan();
            }
        });
        ((Button)view.findViewById(R.id.search_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new foodApiCall(Home_fragment.this).doInBackground(((EditText) view.findViewById(R.id.editTextNumberDecimal)).getText().toString());
            }
        });
        return view;


    }
    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            // Restore your fragment state data from the bundle
            String value = savedInstanceState.getString("key");
        }
    }
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        View view = getView();
        if (view != null) {
            if(parentLayout == null)
                return;
            int index = parentLayout.indexOfChild(view);
            parentLayout.removeView(view);
            view = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ?
                    getLayoutInflater().inflate(R.layout.fragment_home_land, parentLayout, false) :
                    getLayoutInflater().inflate(R.layout.fragment_home, parentLayout, false);

            parentLayout.addView(view, index);
            scanButton = view.findViewById(R.id.scanner_btn);
            scanButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    IntentIntegrator intentIntegrator = new IntentIntegrator(getActivity());
                    intentIntegrator.setBeepEnabled(false);
                    intentIntegrator.setPrompt("Scannez votre code bar");
                    intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                    intentIntegrator.setCaptureActivity(Empty_activity.class);
                    intentIntegrator.initiateScan();
                }
            });
            View finalView = view;
            ((Button)view.findViewById(R.id.search_button)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new foodApiCall(Home_fragment.this).doInBackground(((EditText) finalView.findViewById(R.id.editTextNumberDecimal)).getText().toString());
                }
            });
        }
    }


    @Override
    public void CallBack(String object) {
        Handler mainHandler = new Handler(Looper.getMainLooper());
        if(!object.contains("\"status\":1")){
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getActivity().getApplicationContext(),"Le code est invalide",Toast.LENGTH_LONG).show();
                }
            });
        }
        else{
            Intent intent = new Intent(getActivity().getApplicationContext(), Aliment.class);
            intent.putExtra("json",object);
            startActivity(intent);
            }
    }

}