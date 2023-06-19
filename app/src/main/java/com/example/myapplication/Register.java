    package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

    public class Register extends AppCompatActivity {
    private TextInputEditText editMail,editPassword,editPasswordConfirm,editPrenom;
    private Button buttonReg;
    private FirebaseAuth mAuth;

    private TextView click_to_login;
    private DatabaseReference databaseReference;





    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        editMail = findViewById(R.id.register_email);
        editPassword = findViewById(R.id.register_password);
        buttonReg = findViewById(R.id.register_button);
        click_to_login = findViewById(R.id.login_link);
        editPrenom = findViewById(R.id.register_name);
        editPasswordConfirm = findViewById(R.id.register_comfirm_password);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        click_to_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),Login.class);
                startActivity(intent);
                finish();
            }
        });
        buttonReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TESt","click surp button");
                final String email, password, confirmPassword,nom, error_msg;
                email = editMail.getText().toString();
                password = editPassword.getText().toString();
                confirmPassword = editPasswordConfirm.getText().toString();
                nom = editPrenom.getText().toString();
                error_msg = verif_Register(nom,email,confirmPassword,password);
                if(error_msg != null){
                    Toast.makeText(Register.this, error_msg, Toast.LENGTH_LONG).show();
                    return;
                }

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    String userId = user.getUid();
                                    DatabaseReference currentUserRef = databaseReference.child("Users").child(userId);
                                    currentUserRef.child("email").setValue(email);
                                    currentUserRef.child("pseudo").setValue(nom);
                                    Toast.makeText(Register.this, "Compte crée avec succès ",
                                            Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getApplicationContext(),Login.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(Register.this, "Erreur dans la création de votre compte.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
        private String verif_Register(String nom,String email ,String confirmPassword, String password){
            if(TextUtils.isEmpty(nom)){
                return "veuillez rentrer votre nom";
            }
            if (TextUtils.isEmpty(email)) {
                return "Rentrez un email";
            }
            if (TextUtils.isEmpty(email)) {
                return "Rentrez un mot de passe";
            }
            if( TextUtils.isEmpty(confirmPassword)|| !confirmPassword.equals(password)){
                return "mot de passe différent";
            }
            return null;
        }

        @Override
        public void onStart() {
            super.onStart();
            // Check if user is signed in (non-null) and update UI accordingly.
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if(currentUser != null){
                Intent intent = new Intent(getApplicationContext(),QrCodeScanner.class);
                startActivity(intent);
                finish();
            }
        }

}