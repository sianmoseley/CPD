package com.example.cpd.account;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.cpd.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePassword extends AppCompatActivity {

    EditText currentEmail, currentPassword, newPassword, confirmNewPassword;
    Button saveChangesBtn;
    ProgressBar progressBar4;
    FirebaseAuth fAuth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        currentEmail = findViewById(R.id.currentEmail);
        currentPassword = findViewById(R.id.currentPassword);
        newPassword = findViewById(R.id.newPassword);
        confirmNewPassword = findViewById(R.id.confirmNewPassword);
        saveChangesBtn = findViewById(R.id.saveChangesBtn);
        progressBar4 = findViewById(R.id.progressBar4);

        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser();

        saveChangesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String cEmail = currentEmail.getText().toString();
                String cPassword = currentPassword.getText().toString();
                String nPassword = newPassword.getText().toString();
                String checkPassword = confirmNewPassword.getText().toString();

                if (cEmail.isEmpty() || cPassword.isEmpty() || nPassword.isEmpty() || checkPassword.isEmpty()){
                    Toast.makeText(ChangePassword.this, "All fields are required", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!nPassword.equals(checkPassword)){
                    confirmNewPassword.setError("Passwords Do not Match.");
                    progressBar4.setVisibility(View.GONE);
                }

                progressBar4.setVisibility(View.VISIBLE);

                //RE-AUTHENTICATE USER
                AuthCredential credential = EmailAuthProvider
                        .getCredential(cEmail, cPassword);

                user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d("TAG", "User re-authenticated.");
                    }
                });

                user.updatePassword(nPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(ChangePassword.this, "Password updated.", Toast.LENGTH_SHORT).show();
                            Log.d("TAG", "User password updated");
                            progressBar4.setVisibility(View.GONE);
                        }
                    }
                });

            }
        });




    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);

    }
}