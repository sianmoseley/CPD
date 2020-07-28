package com.example.cpd.audit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.cpd.R;

public class cpdProfile extends AppCompatActivity {

    TextView cpdNumHelper, summaryHelper, psHelper, evidenceHelper;
    Button SupportingEvidenceBtn;
    EditText professionText, cpdNumber, summaryText, personalStatementText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cpd_profile);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        cpdNumHelper = findViewById(R.id.cpdNumHelper);
        summaryHelper = findViewById(R.id.summaryHelper);
        psHelper = findViewById(R.id.psHelper);
        evidenceHelper = findViewById(R.id.evidenceHelper);
        SupportingEvidenceBtn = findViewById(R.id.SupportingEvidenceBtn);

        professionText = findViewById(R.id.professionText);
        cpdNumber = findViewById(R.id.cpdNumber);
        summaryText = findViewById(R.id.summaryText);
        personalStatementText = findViewById(R.id.personalStatementText);


        cpdNumHelper.setText("Your CPD number will be on each piece of correspondence relating to the audit " +
                "from HCPC. Please note that this is not the same as your registration number.");

        summaryHelper.setText("Your summary should describe your role and the type of work you do such as your main responsibilities, the specialist " +
                "areas you work in and the people you communicate and work with most.");

        psHelper.setText("Your statement should explain how your CPD activities improve the quality of your work and the " +
                "benefits to service users.");

        evidenceHelper.setText("Next, select your supporting evidence.");

        SupportingEvidenceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), AuditBuilder.class);
                //PASSES EDIT TEXT BOXES INFO TO NEXT ACTIVITY
                intent.putExtra("Profession_Text", professionText.getText().toString());
                intent.putExtra("CPD_Number", cpdNumber.getText().toString());
                intent.putExtra("Summary_Text", summaryText.getText().toString());
                intent.putExtra("Personal_Statement", personalStatementText.getText().toString());
                startActivity(intent);
            }
        });

    }

    //WHEN BACK BUTTON IS CLICKED, SEND THEM BACK TO MAIN ACTIVITY
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);

    }
}