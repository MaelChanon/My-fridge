package com.example.myapplication.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;

import com.example.myapplication.Aliment;
import com.example.myapplication.Login;
import com.example.myapplication.R;
import com.example.myapplication.components.AlimentComponant;
import com.example.myapplication.factory.Aliment_Componant_fractory;
import com.example.myapplication.utils.CallBackActivity;
import com.example.myapplication.utils.db.Aliment_DB;
import com.example.myapplication.utils.foodApiCall;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fridge_fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fridge_fragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";




    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private long count_aliments = 0;

    private List<AlimentComponant> alimentComponants;

    private Map<String,JSONObject> data;

    private View currentView;

    private LinearLayout alimentList;

    private boolean toRefresh = false;
    private ProgressBar loadingProgressBar;


    public Fridge_fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fridge_fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Fridge_fragment newInstance(String param1, String param2) {
        Fridge_fragment fragment = new Fridge_fragment();
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
    private void asignArguments(View view){
        data = data == null ? new HashMap<>() : data;
        this.alimentList = view.findViewById(R.id.alimentList);
        loadingProgressBar = view.findViewById(R.id.loadingProgressBar);

        ScrollView scrollView = view.findViewById(R.id.fridgeList);
        scrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View view, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.navigation_view);
                if (scrollY < oldScrollY) {
                    bottomNavigationView.animate().translationY(bottomNavigationView.getHeight()).setDuration(150).withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            bottomNavigationView.setVisibility(View.GONE);
                        }
                    }).start();
                } else if (scrollY > oldScrollY) {
                    bottomNavigationView.setVisibility(View.VISIBLE);
                    bottomNavigationView.animate().translationY(0).setDuration(150).start();
                }
            }
        });
    }
    public void loadAliments(View view){
        count_aliments = 0;
        loadingProgressBar.setVisibility(View.VISIBLE);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null){
            goToLogin();
        }
        String userUUID = currentUser.getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(String.format("%s/aliments",userUUID));
        alimentComponants = new LinkedList<>();
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                alimentList.removeAllViews();
                Map<String,List<Aliment_DB>> alimentss = new HashMap<>();
                long totalSize = dataSnapshot.getChildrenCount();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Aliment_DB aliment_db = snapshot.getValue(Aliment_DB.class);
                    if(alimentss.containsKey(aliment_db.getAliment_code()))
                        alimentss.get(aliment_db.getAliment_code()).add(aliment_db);
                    else{
                        List list = new ArrayList();
                        list.add(aliment_db);
                        alimentss.put(aliment_db.getAliment_code(),list);
                    }

                }

                for(String key : alimentss.keySet()){
                    List<Aliment_DB> aliments = alimentss.get(key);
                    JSONObject jsonData = data.get(key);
                    if(jsonData !=null){
                        try {
                            addAliments(jsonData, aliments);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                        count_aliments += aliments.size();
                        if(count_aliments == totalSize){
                            DisplayAliments();
                        }
                        continue;
                    }
                    else{
                        new foodApiCall(new CallBackActivity<String>() {
                            @Override
                            public void CallBack(String json) {
                                try {
                                    JSONObject jsonObject = new JSONObject(json);

                                    if(jsonObject.getInt("status") !=1)
                                        goToLogin();
                                    if(getActivity() ==null)
                                        return;
                                    if(!data.containsKey(key))
                                        data.put(key,jsonObject);
                                    addAliments(jsonObject, aliments);
                                    count_aliments += aliments.size();
                                    synchronized (Fridge_fragment.this){
                                        if(count_aliments == totalSize){
                                            DisplayAliments();
                                        }
                                    }
                                } catch (JSONException e) {
                                    goToLogin();
                                }
                            }
                        }).doInBackground(key);
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                goToLogin();
            }
        });

    }

    private void DisplayAliments() {
        Collections.sort(alimentComponants);
        Handler mainHandler = new Handler(Looper.getMainLooper());
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                for(AlimentComponant alimentComponant : alimentComponants)
                    alimentList.addView(alimentComponant);
                loadingProgressBar.setVisibility(View.GONE);
            }
        });
    }



    private void addAliments(JSONObject jsonObject, List<Aliment_DB> aliments) throws JSONException {
        String image_url = jsonObject.getJSONObject("product").getString("image_url");
        String name = jsonObject.getJSONObject("product").getString("product_name_fr");

        for(Aliment_DB aliment_db : aliments){
            alimentComponants.add(Aliment_Componant_fractory.getAliment(getActivity(),aliment_db,image_url,name,new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity().getApplicationContext(), Aliment.class);
                    intent.putExtra("json", jsonObject.toString());
                    intent.putExtra("aliment",aliment_db);
                    startActivity(intent);
                    Fridge_fragment.this.toRefresh = true;

                }
            }));

        }
    }
    public void goToLogin(){
        Activity activity = this.getActivity();
        Intent intent = new Intent(activity.getApplicationContext(), Login.class);
        startActivity(intent);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        synchronized(this){
            View view = inflater.inflate(R.layout.fragment_fridge, container, false);
            asignArguments(view);
            loadAliments(view);
            this.currentView = view;
            return view;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        synchronized(this){
            if(currentView !=null && toRefresh)
                loadAliments(currentView);

        }
    }
}