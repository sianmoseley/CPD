package com.example.cpd.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.cpd.MainActivity;
import com.example.cpd.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EditActivity extends AppCompatActivity {

    Intent data;
    EditText editActivityName, editActivityDescription, editActivityType, editActivityDate, editActivityHours,
    editActivityMins, editActivityRef1, editActivityRef2, editActivityRef3, editActivityRef4;
    FirebaseFirestore fStore;
    ProgressBar spinner;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        data = getIntent();

        fStore = fStore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        editActivityName = findViewById(R.id.editActivityName);
        editActivityDescription = findViewById(R.id.editActivityDescription);
        editActivityType = findViewById(R.id.editActivityType);
        editActivityDate = findViewById(R.id.editActivityDate);
        editActivityHours = findViewById(R.id.editActivityHours);
        editActivityMins = findViewById(R.id.editActivityMins);
        editActivityRef1 = findViewById(R.id.editActivityRef1);
        editActivityRef2 = findViewById(R.id.editActivityRef2);
        editActivityRef3 = findViewById(R.id.editActivityRef3);
        editActivityRef4 = findViewById(R.id.editActivityRef4);

        spinner = findViewById(R.id.progressBar2);

        String activityName = data.getStringExtra("Activity_Name");
        String activityDescription = data.getStringExtra("Activity_Description");
        String activityDate = data.getStringExtra("Activity_Date");
        String activityHours = data.getStringExtra("Activity_Hours");
        String activityType = data.getStringExtra("Activity_Type");
        String activityMins = data.getStringExtra("Activity_Mins");
        String activityRef1 = data.getStringExtra("Activity_Ref1");
        String activityRef2 = data.getStringExtra("Activity_Ref2");
        String activityRef3 = data.getStringExtra("Activity_Ref3");
        String activityRef4 = data.getStringExtra("Activity_Ref4");

        editActivityName.setText(activityName);
        editActivityDescription.setText(activityDescription);
        editActivityType.setText(activityType);
        editActivityDate.setText(activityDate);
        editActivityHours.setText(activityHours);
        editActivityMins.setText(activityMins);
        editActivityRef1.setText(activityRef1);
        editActivityRef2.setText(activityRef2);
        editActivityRef3.setText(activityRef3);
        editActivityRef4.setText(activityRef4);

        FloatingActionButton fab = findViewById(R.id.saveEditBtn);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String eName = editActivityName.getText().toString();
                String eDesc = editActivityDescription.getText().toString();
                String eRef1 = editActivityRef1.getText().toString();
                String eRef2 = editActivityRef2.getText().toString();
                String eRef3 = editActivityRef3.getText().toString();
                String eRef4 = editActivityRef4.getText().toString();

                //TODO: NEED TO BE CHANGED INTO SPINNERS/PICKER BUT EDIT TEXTS FOR NOW

                String eType = editActivityType.getText().toString();
                String eHours = editActivityHours.getText().toString();
                String eMins = editActivityMins.getText().toString();
                String eDate = editActivityDate.getText().toString();


                if (eName.isEmpty() || eDesc.isEmpty() || eRef1.isEmpty() || eRef2.isEmpty()
                        || eRef3.isEmpty() || eRef4.isEmpty() || eType.isEmpty() || eHours.isEmpty()
                || eMins.isEmpty() || eDate.isEmpty()) {
                    Toast.makeText(EditActivity.this, "Can not save activity with empty fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                spinner.setVisibility(View.VISIBLE);

                //SAVE EDITED NOTE
                DocumentReference documentReference = fStore.collection("cpdActivities")
                        .document(user.getUid())
                        .collection("myCPD")
                        .document(data.getStringExtra("Activity_ID"));

                Map<String, Object> activity = new HashMap<>();
                activity.put("Activity_Name", eName);
                activity.put("Activity_Date", eDate);
                activity.put("Activity_Hours", eHours);
                activity.put("Activity_Mins", eMins);
                activity.put("Activity_Type", eType);
                activity.put("Activity_Description", eDesc);
                activity.put("Activity_Ref1", eRef1);
                activity.put("Activity_Ref2", eRef2);
                activity.put("Activity_Ref3", eRef3);
                activity.put("Activity_Ref4", eRef4);

                documentReference.update(activity).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(EditActivity.this, "Activity Edited", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditActivity.this, "Error, try again", Toast.LENGTH_SHORT).show();
                        spinner.setVisibility(View.INVISIBLE);
                    }
                });
            }
        }
        );
    }

    //IMPLEMENTS THE CLOSE BUTTON IN TOP RIGHT HAND CORNER
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.close_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.close) {
            Toast.makeText(this, "Activity not saved", Toast.LENGTH_SHORT).show();
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

}