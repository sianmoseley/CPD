package com.example.cpd.audit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cpd.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EditSavedAudit extends AppCompatActivity {

    TextInputEditText editProfessionText, editCpdNumber, editSummaryText, editPersonalStatementText;
    TextView psHelper, summaryHelper;

    FirebaseUser user;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    Button saveEditAuditBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_saved_audit);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser();

        psHelper = findViewById(R.id.psHelper);
        summaryHelper = findViewById(R.id.summaryHelper);

        summaryHelper.setText("Your summary should describe your role and the type of work you do such as your main responsibilities, the specialist " +
                "areas you work in and the people you communicate and work with most.");

        psHelper.setText("Your statement should explain how your CPD activities improve the quality of your work and the " +
                "benefits to service users.");

        //DOCUMENT REFERENCE TO FETCH CURRENTLY SAVED AUDIT TEXT
        DocumentReference documentReference = fStore.collection("audits")
                .document(user.getUid())
                .collection("myAuditText")
                .document("myAudit");

        editProfessionText = findViewById(R.id.editProfessionText);
        editCpdNumber = findViewById(R.id.editCpdNumber);
        editSummaryText = findViewById(R.id.editSummaryText);
        editPersonalStatementText = findViewById(R.id.editPersonalStatementText);

        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()){
                        editProfessionText.setText(document.getString("Profession"));
                        editCpdNumber.setText(document.getString("CPD_Number"));
                        editSummaryText.setText(document.getString("Summary_Text"));
                        editPersonalStatementText.setText(document.getString("Personal_Statement"));
                    }
                }
            }
        });

        saveEditAuditBtn = findViewById(R.id.saveEditAuditBtn);
        saveEditAuditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String updatedProfession = editProfessionText.getText().toString();
                String updatedCpdNumber = editCpdNumber.getText().toString();
                String updatedSummary = editSummaryText.getText().toString();
                String updatedPersonalStatement = editPersonalStatementText.getText().toString();

                DocumentReference editedProfileReference = fStore.collection("audits")
                        .document(user.getUid())
                        .collection("myAuditText")
                        .document("myAudit");

                Map<String, Object> editedProfile = new HashMap<>();
                editedProfile.put("Profession", updatedProfession);
                editedProfile.put("CPD_Number", updatedCpdNumber);
                editedProfile.put("Summary_Text", updatedSummary);
                editedProfile.put("Personal_Statement", updatedPersonalStatement);

                editedProfileReference.update(editedProfile).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        startActivity(new Intent(getApplicationContext(), EditSavedAuditActivities.class));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditSavedAudit.this, "Error, try again", Toast.LENGTH_SHORT).show();
                    }
                });





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
