package com.example.cpd.account;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.cpd.R;
import com.example.cpd.Splash;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DeleteAccount extends AppCompatActivity {

    Button deleteAccountButton;
    EditText userEmail, userPassword;
    FirebaseAuth fAuth;
    FirebaseUser user;
    ProgressBar progressBar5;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_account);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser();

        deleteAccountButton = findViewById(R.id.deleteAccountButton);
        userEmail = findViewById(R.id.userEmail);
        userPassword = findViewById(R.id.userPassword);
        progressBar5 = findViewById(R.id.progressBar5);

        deleteAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dEmail = userEmail.getText().toString();
                String dPassword = userPassword.getText().toString();

                if (dEmail.isEmpty() || dPassword.isEmpty()){
                    Toast.makeText(DeleteAccount.this, "All fields required", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar5.setVisibility(View.VISIBLE);

                //RE-AUTHENTICATE USER
                AuthCredential credential = EmailAuthProvider
                        .getCredential(dEmail, dPassword);

                user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d("TAG", "User re-authenticated.");
                    }
                });

                user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Log.d("TAG", "User account deleted");
                            startActivity(new Intent(getApplicationContext(), Splash.class));
                            finish();
                        }
                    }
                });


            }
        });


    }

    //WHEN BACK BUTTON IS CLICKED, SEND THEM BACK TO PREVIOUS PAGE
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);

    }
}