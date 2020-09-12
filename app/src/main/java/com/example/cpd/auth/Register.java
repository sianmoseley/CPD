package com.example.cpd.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.cpd.MainActivity;
import com.example.cpd.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Register extends AppCompatActivity {

    TextInputEditText rfullName, rUserEmail, rUserPass, rUserConfPass;
    Button registerBtn;
    ProgressBar progressBar;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //ENABLE BACK BUTTON
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        rfullName = findViewById(R.id.fullName);
        rUserEmail = findViewById(R.id.userEmail);
        rUserPass = findViewById(R.id.password);
        rUserConfPass = findViewById(R.id.passwordConfirm);

        registerBtn = findViewById(R.id.createAccount);
        progressBar = findViewById(R.id.progressBar4);

        //INITIALISING FIREBASE INSTANCES
        fAuth = FirebaseAuth.getInstance();

        //CHECKS IF USER IS ALREADY LOGGED IN - IF YES, SENDS THEM TO MAIN ACTIVITY
        if(fAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }

        //SET BUTTON CLICK EVENT
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //SAVES USER INPUT TO STRINGS
                final String ufullName = rfullName.getText().toString();
                final String uUserEmail = rUserEmail.getText().toString();
                String uUserPass = rUserPass.getText().toString();
                String uConfPass = rUserConfPass.getText().toString();

                //CHECKS STRINGS AREN'T EMPTY
                if(uUserEmail.isEmpty()|| ufullName.isEmpty() || uUserPass.isEmpty() || uConfPass.isEmpty()){
                    Toast.makeText(Register.this, "All fields are required", Toast.LENGTH_SHORT).show();
                    return;
                }

                //CHECKS PASSWORDS MATCH
                if(!uUserPass.equals(uConfPass)){
                    rUserConfPass.setError("Passwords Do not Match.");
                }

                progressBar.setVisibility(View.VISIBLE);

                //REGISTER USER IN FIREBASE
                fAuth.createUserWithEmailAndPassword(uUserEmail,uUserPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(Register.this, "Account created", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        } else {
                            Toast.makeText(Register.this, "Error! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });


            }
        });



    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        startActivity(new Intent(this, Login.class));
        return super.onOptionsItemSelected(item);
    }
}