package com.example.cpd;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.example.cpd.auth.Login;
import com.google.firebase.auth.FirebaseAuth;

public class Splash extends AppCompatActivity {

    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        fAuth = FirebaseAuth.getInstance();

        Handler handler = new Handler();

        //DELAYS ACTIVITY FOR 2 SECONDS
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //CHECKS IF USER IS LOGGED IN
                if(fAuth.getCurrentUser() != null){
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                } else {
                    startActivity(new Intent(getApplicationContext(), Login.class));
                    finish();
                }
            }
        },2000);

    }
}