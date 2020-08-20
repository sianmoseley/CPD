package com.example.cpd.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.cpd.R;

public class Standards extends AppCompatActivity {

    TextView standardOne, standardTwo, standardThree, standardFour, standardFive, oneExtra, twoExtra,
            threeExtra, fourExtra, fiveExtra;
    ImageButton imgPlus, imgPlus2, imgPlus3, imgPlus4, imgPlus5;
    int i = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_standards);
        //SETS TOOL BAR AND BACK BUTTON
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        standardOne = findViewById(R.id.standardOne);
        standardTwo = findViewById(R.id.standardTwo);
        standardThree = findViewById(R.id.standardThree);
        standardFour = findViewById(R.id.standardFour);
        standardFive = findViewById(R.id.standardFive);
        oneExtra = findViewById(R.id.oneExtra);
        twoExtra = findViewById(R.id.twoExtra);
        threeExtra = findViewById(R.id.threeExtra);
        fourExtra = findViewById(R.id.fourExtra);
        fiveExtra = findViewById(R.id.fiveExtra);


        imgPlus = findViewById(R.id.imgPlus);
        imgPlus2 = findViewById(R.id.imgPlus2);
        imgPlus3 = findViewById(R.id.imgPlus3);
        imgPlus4 = findViewById(R.id.imgPlus4);
        imgPlus5 = findViewById(R.id.imgPlus5);


        //SET VISIBILITY TO GONE INITIALLY SO THAT WHITE SPACE IS NOT THERE WHEN ACTIVITY FIRST LOADS
        oneExtra.setVisibility(View.GONE);
        twoExtra.setVisibility(View.GONE);
        threeExtra.setVisibility(View.GONE);
        fourExtra.setVisibility(View.GONE);
        fiveExtra.setVisibility(View.GONE);

        imgPlus.setBackgroundResource(R.drawable.ic_baseline_add_24_accent);
        imgPlus2.setBackgroundResource(R.drawable.ic_baseline_add_24_accent);
        imgPlus3.setBackgroundResource(R.drawable.ic_baseline_add_24_accent);
        imgPlus4.setBackgroundResource(R.drawable.ic_baseline_add_24_accent);
        imgPlus5.setBackgroundResource(R.drawable.ic_baseline_add_24_accent);

        imgPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //I IS NUMBER OF CLICKS TO MAKE IT ACT LIKE A TOGGLE BUTTON (BUT WITH CUSTOM IMAGES)
                if (i == 0){
                    imgPlus.setBackgroundResource(R.drawable.ic_baseline_remove_24_accent);
                    oneExtra.setVisibility(View.VISIBLE);
                    i++;
                } else if (i == 1){
                    imgPlus.setBackgroundResource(R.drawable.ic_baseline_add_24_accent);
                    oneExtra.setVisibility(View.GONE);
                    i = 0;
                }
            }
        });

        imgPlus2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (i == 0){
                    imgPlus2.setBackgroundResource(R.drawable.ic_baseline_remove_24_accent);
                    twoExtra.setVisibility(View.VISIBLE);
                    i++;
                } else if (i == 1){
                    imgPlus2.setBackgroundResource(R.drawable.ic_baseline_add_24_accent);
                    twoExtra.setVisibility(View.GONE);
                    i = 0;
                }
            }
        });

        imgPlus3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (i == 0){
                    imgPlus3.setBackgroundResource(R.drawable.ic_baseline_remove_24_accent);
                    threeExtra.setVisibility(View.VISIBLE);
                    i++;
                } else if (i == 1){
                    imgPlus3.setBackgroundResource(R.drawable.ic_baseline_add_24_accent);
                    threeExtra.setVisibility(View.GONE);
                    i = 0;
                }
            }
        });

        imgPlus4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (i == 0){
                    imgPlus4.setBackgroundResource(R.drawable.ic_baseline_remove_24_accent);
                    fourExtra.setVisibility(View.VISIBLE);
                    i++;
                } else if (i == 1){
                    imgPlus4.setBackgroundResource(R.drawable.ic_baseline_add_24_accent);
                    fourExtra.setVisibility(View.GONE);
                    i = 0;
                }
            }
        });

        imgPlus5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (i == 0){
                    imgPlus5.setBackgroundResource(R.drawable.ic_baseline_remove_24_accent);
                    fiveExtra.setVisibility(View.VISIBLE);
                    i++;
                } else if (i == 1){
                    imgPlus5.setBackgroundResource(R.drawable.ic_baseline_add_24_accent);
                    fiveExtra.setVisibility(View.GONE);
                    i = 0;
                }
            }
        });


        standardOne.setText("Maintain a continuous, up-to-date and accurate record of their CPD activities.");
        standardTwo.setText("Demonstrate that their current CPD activities are a mixture of learning activities relevant to current or future practice.");
        standardThree.setText("Seek to ensure that their CPD has contributed to the quality of their practice and service delivery.");
        standardFour.setText("Seek to ensure that their CPD benefits the service user.");
        standardFive.setText("Upon request, present a written profile (which must be their own work and supported by evidence) explaining how they met the Standards for CPD.");

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