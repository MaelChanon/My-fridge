package com.example.myapplication;

import static android.content.ContentValues.TAG;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.utils.aliment_db;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class Aliment extends AppCompatActivity {
    private JSONObject jsonObject;
    private TextView quantity;
    private EditText dateEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.aliment);
        try {
            this.jsonObject = new JSONObject(getIntent().getStringExtra("json"));
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
            Picasso.get().
                    load(jsonObject.getJSONObject("product").getString("image_url"))
                    .fit()
                    .centerInside()
                    .into(alimentImage);
            quantity = findViewById(R.id.quantity);
            String calories = jsonObject.getJSONObject("product").getJSONObject("nutriments").getString("energy-kcal_100g") + " calories/100g";
            quantity.setText(calories);
            ((TextView) findViewById(R.id.product_name))
                    .setText(this.jsonObject.getJSONObject("product").getString("product_name_fr"));
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
                            String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                            dateEditText.setText(selectedDate);
                        }, year, month, dayOfMonth);

                datePickerDialog.show();
            }
        });
        EditText count = findViewById(R.id.quantite);
        count.setText("1");
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
                Log.d("TTTT", "test3");
                FirebaseAuth mAuth = FirebaseAuth.getInstance();

                // Get the currently authenticated user
                FirebaseUser currentUser = mAuth.getCurrentUser();
                ;
                if (currentUser != null) {
                    // User is signed in
                    String userUUID = currentUser.getUid();
                    Log.d("TTTT", "User UUID: " + userUUID);
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference();
                    try {
                        myRef.child(userUUID)
                                .child("aliments")
                                .push()
                                .setValue(new aliment_db(Aliment.this.jsonObject.getString("code"),dateEditText.getText().toString()));
                        Intent intent = new Intent(getApplicationContext(),QrCodeScanner.class);
                        startActivity(intent);
                        finish();
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
                int_value = int_value == 1 ? 1 : int_value - 1;
                count.setText(int_value+"");
            }
        });
    }

    private void setProgressBar(com.example.myapplication.components.ProgressBar progressBar, double value) {
        progressBar.setProgress(value);
    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(getApplicationContext(),QrCodeScanner.class);
        startActivity(intent);
        finish();
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

    }
}