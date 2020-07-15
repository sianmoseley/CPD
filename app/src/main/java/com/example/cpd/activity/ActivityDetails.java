package com.example.cpd.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.cpd.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ActivityDetails extends AppCompatActivity {

    Intent data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details2);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        data = getIntent();


        //LINKS ATTRIBUTES TO EACH TEXT VIEW
        TextView aDetailsName = findViewById(R.id.activityDetailsName);
        TextView aDetailsDescription = findViewById(R.id.activityDetailsDescription);
        TextView aDetailsType = findViewById(R.id.activityDetailsType);
        TextView aDetailsHours = findViewById(R.id.activityDetailsHours);
        TextView aDetailsMins = findViewById(R.id.activityDetailsMins);
        TextView aDetailsDate = findViewById(R.id.activityDetailsDate);
        TextView aDetailsRef1 = findViewById(R.id.activityDetailsRef1);
        TextView aDetailsRef2 = findViewById(R.id.activityDetailsRef2);
        TextView aDetailsRef3 = findViewById(R.id.activityDetailsRef3);
        TextView aDetailsRef4= findViewById(R.id.activityDetailsRef4);
        ImageView aImagePrev = findViewById(R.id.activityImgPreview);

        //SETS SCROLLING IN MULTILINE
        aDetailsDescription.setMovementMethod(new ScrollingMovementMethod());

        //DISPLAYS ALL THE ATTRIBUTES OF THE ACTIVITY
        aDetailsDescription.setText(data.getStringExtra("Activity_Description"));
        aDetailsName.setText(data.getStringExtra("Activity_Name"));
        aDetailsType.setText(data.getStringExtra("Activity_Type"));
        aDetailsHours.setText(data.getStringExtra("Activity_Hours"));
        aDetailsMins.setText(data.getStringExtra("Activity_Mins"));
        aDetailsDate.setText(data.getStringExtra("Activity_Date"));
        aDetailsRef1.setText(data.getStringExtra("Activity_Ref1"));
        aDetailsRef2.setText(data.getStringExtra("Activity_Ref2"));
        aDetailsRef3.setText(data.getStringExtra("Activity_Ref3"));
        aDetailsRef4.setText(data.getStringExtra("Activity_Ref4"));


        FloatingActionButton fab = findViewById(R.id.editBtn);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(view.getContext(),EditActivity.class);
                i.putExtra("Activity_Name", data.getStringExtra("Activity_Name"));
                i.putExtra("Activity_Description", data.getStringExtra("Activity_Description"));
                i.putExtra("Activity_Type", data.getStringExtra("Activity_Type"));
                i.putExtra("Activity_Hours", data.getStringExtra("Activity_Hours"));
                i.putExtra("Activity_Mins", data.getStringExtra("Activity_Mins"));
                i.putExtra("Activity_Date", data.getStringExtra("Activity_Date"));
                i.putExtra("Activity_Ref1", data.getStringExtra("Activity_Ref1"));
                i.putExtra("Activity_Ref2", data.getStringExtra("Activity_Ref2"));
                i.putExtra("Activity_Ref3", data.getStringExtra("Activity_Ref3"));
                i.putExtra("Activity_Ref4", data.getStringExtra("Activity_Ref4"));
                i.putExtra("Activity_ID", data.getStringExtra("Activity_ID"));
                startActivity(i);


            }
        });
    }

    //whenever back button clicked, send them back to previous page
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);

    }
}