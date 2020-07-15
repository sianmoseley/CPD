package com.example.cpd.account;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.cpd.R;

public class MyAccount extends AppCompatActivity {

    Button detailsBtn, notificationBtn, deleteBtn, changePasswordBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        detailsBtn = findViewById(R.id.detailsBtn);
        notificationBtn = findViewById(R.id.notificationBtn);
        deleteBtn = findViewById(R.id.deleteBtn);
        changePasswordBtn = findViewById(R.id.changePasswordBtn);

        detailsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyAccount.this, EditAccount.class);
                startActivity(intent);
            }
        });

        changePasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyAccount.this, ChangePassword.class);
                startActivity(intent);
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