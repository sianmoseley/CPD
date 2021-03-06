package com.example.cpd.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cpd.MainActivity;
import com.example.cpd.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {

    Button loginBtn, createAcc;
    TextView forgetPass;
    FirebaseAuth fAuth;
    ProgressBar spinner;


    TextInputEditText lEmail, lPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        lEmail = findViewById(R.id.email);
        lPassword = findViewById(R.id.lPassword);
        loginBtn = findViewById(R.id.loginBtn);


        spinner = findViewById(R.id.progressBar3);

        forgetPass = findViewById(R.id.forgotPassword);
        createAcc = findViewById(R.id.createAccount);

        //INITIALIZE FIREBASE INSTANCE
        fAuth = FirebaseAuth.getInstance();

        //SET BUTTON CLICK
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //SAVE USER INPUT TO STRING
                String email = lEmail.getText().toString().trim();
                String password = lPassword.getText().toString().trim();

                //CHECK STRINGS AREN'T EMPTY
                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(Login.this, "Fields are required", Toast.LENGTH_SHORT).show();
                    return;
                }

                spinner.setVisibility(View.VISIBLE);

                //USER INPUTS PASSED TO FIREBASE SIGN IN METHOD
                fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(Login.this, "Logged in Successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            finish();
                        } else {
                            Toast.makeText(Login.this, "Error! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            spinner.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });

        //SET BUTTON CLICK
        createAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Register.class));
            }
        });

        //SET LINK CLICK
        forgetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText resetMail = new EditText(v.getContext());
                //BUILD ALERT DIALOG
                AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(v.getContext());
                passwordResetDialog.setTitle("Reset password?");
                passwordResetDialog.setMessage("Please enter your email");
                passwordResetDialog.setView(resetMail);

                passwordResetDialog.setPositiveButton("Send Link", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //extract email and send reset link
                        String mail = resetMail.getText().toString();
                        if (mail.isEmpty()) {
                            Toast.makeText(Login.this, "Please enter valid email address.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        fAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(Login.this, "Reset Link sent to your Email", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Login.this, "Error! Reset Link is not sent" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

                passwordResetDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //close the dialog
                    }
                });

                passwordResetDialog.create().show();

            }
        });


    }
}