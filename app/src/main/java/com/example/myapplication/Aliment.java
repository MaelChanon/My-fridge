package com.example.myapplication;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.myapplication.components.ProgressBarComponant;
import com.example.myapplication.fragments.Fridge_fragment;
import com.example.myapplication.utils.db.Aliment_DB;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class Aliment extends AppCompatActivity {
    private JSONObject jsonObject;
    private TextView quantity;
    private EditText dateEditText;
    private Aliment_DB current_Aliment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.aliment);
        try {
            this.jsonObject = new JSONObject(getIntent().getStringExtra("json"));
            this.current_Aliment = (Aliment_DB) getIntent().getSerializableExtra("aliment");
        } catch (JSONException e) {
            Intent intent = new Intent(getApplicationContext(), QrCodeScanner.class);
            startActivity(intent);
            finish();
        }
        loadData();
    }


    private void loadData() {
        ImageView alimentImage = findViewById(R.id.imageAliment);
        try {
            if(jsonObject.getJSONObject("product").has("image_url"))
                Picasso.get().
                        load(jsonObject.getJSONObject("product").getString("image_url"))
                        .fit()
                        .centerInside()
                        .into(alimentImage);
            quantity = findViewById(R.id.quantity);

            String calories = jsonObject.getJSONObject("product").getJSONObject("nutriments").has("energy-kcal_100g") ?
                    jsonObject.getJSONObject("product").getJSONObject("nutriments").getString("energy-kcal_100g") + " calories/100g":
                    "calories inconues";
            quantity.setText(calories);
            ((TextView) findViewById(R.id.product_name))
                    .setText(this.jsonObject.getJSONObject("product").has("product_name_fr") ? this.jsonObject.getJSONObject("product").getString("product_name_fr") : "super produit");
            String quantity = this.jsonObject.getJSONObject("product").has("quantity") ?
                    this.jsonObject.getJSONObject("product").getString("quantity") : "unknow";
            ((TextView) findViewById(R.id.poid))
                    .setText(quantity);
            JSONObject nutriments = jsonObject.getJSONObject("product").getJSONObject("nutriments");


            double value = nutriments.has("proteins_100g") ? nutriments.getDouble("proteins_100g") : 0;
            setProgressBar(findViewById(R.id.prots),value);

            value = nutriments.has("fat_100g") ? nutriments.getDouble("fat_100g") : 0;
            setProgressBar(findViewById(R.id.fat),value);

            value = nutriments.has("sugars_100g") ? nutriments.getDouble("sugars_100g") : 0;
            setProgressBar(findViewById(R.id.sugar),value);

            value = nutriments.has("fiber_100g") ? nutriments.getDouble("fiber_100g") : 0;
            setProgressBar(findViewById(R.id.fiber),value);


        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        dateEditText = findViewById(R.id.editTextDate);
        EditText count = findViewById(R.id.quantite);
        if(current_Aliment != null){
            dateEditText.setText(current_Aliment.getDate_peremption());
            count.setText(current_Aliment.getNb_aliments()+"");
        }
        else{
            count.setText("1");

        }

        dateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(Aliment.this,
                        (DatePickerDialog.OnDateSetListener) (datePicker, selectedYear, selectedMonth, selectedDay) -> {
                            // Update the EditText with the selected date
                            String selectedDate = selectedDay + "/" + (selectedMonth+1) + "/" + selectedYear;
                            dateEditText.setText(selectedDate);
                        }, year, month, dayOfMonth);

                datePickerDialog.show();
            }
        });

        ((Button)findViewById(R.id.buttonPlus)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String value = count.getText().toString();
                int int_value = Integer.parseInt(value) +1;
                count.setText(int_value+"");
            }
        });

        ((Button)findViewById(R.id.validerAliment)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dateEditText.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(),"Veuillez selectionner une date de péremption", Toast.LENGTH_SHORT).show();
                    return;
                }
                FirebaseAuth mAuth = FirebaseAuth.getInstance();

                // Get the currently authenticated user
                FirebaseUser currentUser = mAuth.getCurrentUser();
                ;
                if (currentUser != null) {
                    // User is signed in
                    try {
                        Aliment_DB aliment_db = new Aliment_DB(Aliment.this.jsonObject.getString("code"),
                                dateEditText.getText().toString(),
                                Integer.parseInt(((EditText)findViewById(R.id.quantite)).getText().toString()));
                        String userUUID = currentUser.getUid();
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference myRef = database.getReference(String.format("%s/aliments",userUUID));
                        Query query = myRef.orderByChild("aliment_code").equalTo(Aliment.this.jsonObject.getString("code"));
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                boolean dataupdated = false;

                                if (dataSnapshot.exists()) {
                                    boolean current_aliment_not_null = Aliment.this.current_Aliment != null;
                                    boolean current_equal_new =current_aliment_not_null && current_Aliment.getDate_peremption().equals(aliment_db.getDate_peremption());
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        // Access the specific child data
                                        Aliment_DB aliment = snapshot.getValue(Aliment_DB.class);
                                        // Process the child data
                                        if(current_aliment_not_null && !current_equal_new && aliment.getDate_peremption().equals(aliment_db.getDate_peremption())){
                                            snapshot.getRef().child("nb_aliments").getRef().setValue((Long) snapshot.child("nb_aliments").getValue() + (aliment_db.getNb_aliments()));
                                            for (DataSnapshot snapshotToDelete : dataSnapshot.getChildren()){
                                                Aliment_DB aliment_toDelete = snapshotToDelete.getValue(Aliment_DB.class);
                                                if(aliment_toDelete.getDate_peremption().equals(current_Aliment.getDate_peremption())){
                                                    snapshotToDelete.getRef().removeValue();
                                                    dataupdated = true;
                                                }
                                            }
                                        }
                                        else if(current_aliment_not_null && current_equal_new){
                                            dataupdated = true;
                                            long result = (long) aliment.getNb_aliments() + (aliment_db.getNb_aliments()-Aliment.this.current_Aliment.getNb_aliments());
                                            if(result != 0){
                                                snapshot.getRef().child("nb_aliments").getRef().setValue(result);
                                            }
                                            else{
                                                snapshot.getRef().removeValue();
                                            }
                                        }
                                        else if(aliment.getDate_peremption().equals(dateEditText.getText().toString())){
                                            dataupdated = true;
                                            snapshot.getRef().child("nb_aliments").getRef().setValue((Long) snapshot.child("nb_aliments").getValue() + aliment_db.getNb_aliments());

                                        }
                                        if(dataupdated)
                                            break;
                                    }

                                }
                                if(!dataupdated){
                                    myRef.push().setValue(aliment_db);
                                }
                                Intent intent = new Intent(getApplicationContext(),QrCodeScanner.class);
                                startActivity(intent);
                                finish();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });



                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                } else {
                    // No user is signed in
                    Log.d("TTTT", "No user is signed in.");
                }
            }
        });
        ((Button)findViewById(R.id.buttonMoins)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String value = count.getText().toString();
                int int_value = Integer.parseInt(value);
                if(current_Aliment == null)
                    int_value = int_value == 1 ? 1 : int_value - 1;
                else{
                    int_value = int_value == 0 ? 0 : int_value - 1;
                }
                count.setText(int_value+"");
            }
        });
    }

    private void setProgressBar(ProgressBarComponant progressBar, double value) {
        progressBar.setProgress(value);
    }

    @Override
    public void onBackPressed(){
        goBack();
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

    }
    private void goBack(){
        finish();
    }
}