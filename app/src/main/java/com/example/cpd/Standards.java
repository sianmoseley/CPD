package com.example.cpd;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class Standards extends AppCompatActivity {

    TextView standardsTitle, standardOne, standardTwo, standardThree, standardFour, standardFive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_standards);
        //SETS TOOL BAR AND BACK BUTTON
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        standardsTitle = findViewById(R.id.standardsTitle);
        standardOne = findViewById(R.id.standardOne);
        standardTwo = findViewById(R.id.standardTwo);
        standardThree = findViewById(R.id.standardThree);
        standardFour = findViewById(R.id.standardFour);
        standardFive = findViewById(R.id.standardFive);

        standardsTitle.setText("Registrants must:");
        standardOne.setText("1. Maintain a continuous, up-to-date and accurate record of their CPD activities.");
        standardTwo.setText("2. Demonstrate that their current CPD activities are a mixture of learning activities relevant to current or future practice.");
        standardThree.setText("3. Seek to ensure that their CPD has contributed to the quality of their practice and service delivery.");
        standardFour.setText("4. Seek to ensure that their CPD benefits the service user.");
        standardFive.setText("5. Upon request, present a written profile (which must be their own work and supported by evidence) explaining how they met the Standards for CPD.");

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