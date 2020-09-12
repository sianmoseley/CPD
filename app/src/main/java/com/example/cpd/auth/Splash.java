package com.example.cpd.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.example.cpd.MainActivity;
import com.example.cpd.R;
import com.example.cpd.auth.Login;
import com.google.firebase.auth.FirebaseAuth;

public class Splash extends AppCompatActivity {

    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //INITIALISE FIREBASE INSTANCE
        fAuth = FirebaseAuth.getInstance();

        Handler handler = new Handler();

        //DELAYS ACTIVITY FOR 2 SECONDS
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //CHECKS IF USER IS LOGGED IN
                if(fAuth.getCurrentUser() != null){
                    Log.d("TAG", "User is authenticated.");
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                } else {
                    Log.d("TAG", "User not logged in.");
                    startActivity(new Intent(getApplicationContext(), Login.class));
                    finish();
                }
            }
        },2000);

    }
}