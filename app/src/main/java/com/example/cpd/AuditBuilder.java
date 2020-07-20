package com.example.cpd;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cpd.activity.ActivityDetails;
import com.example.cpd.model.Activity;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.api.Distribution;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.Map;

public class AuditBuilder extends AppCompatActivity {

    RecyclerView auditList;
    FirebaseFirestore fStore;
    FirestoreRecyclerAdapter<Activity, AuditBuilder.AuditViewHolder> auditAdapter;
    FirebaseUser user;
    FirebaseAuth fAuth;
    String auditDocId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audit_builder);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser();
        auditList = findViewById(R.id.auditList);

        //QUERY DATABASE TO DISPLAY CPD ACTIVITIES
        Query query = fStore.collection("cpdActivities")
                .document(user.getUid())
                .collection("myCPD")
                .orderBy("Activity_Date", Query.Direction.ASCENDING);

        //TODO: READ UP ON WHAT THIS IS ABOUT IN MORE DETAIL SO I CAN EXPLAIN
        FirestoreRecyclerOptions<Activity> auditBuilder = new FirestoreRecyclerOptions.Builder<Activity>()
                .setQuery(query, Activity.class)
                .build();

        auditAdapter = new FirestoreRecyclerAdapter<Activity, AuditBuilder.AuditViewHolder>(auditBuilder) {
            @Override
            protected void onBindViewHolder(@NonNull AuditBuilder.AuditViewHolder auditViewHolder, int i, @NonNull final Activity activity) {
                auditViewHolder.aAuditActivityName.setText(activity.getActivity_Name());
                auditViewHolder.aAuditActivityDescription.setText(activity.getActivity_Description());
                final String docId = auditAdapter.getSnapshots().getSnapshot(i).getId();

                auditViewHolder.auditCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked){

                            //ADD DOCUMENT ID TO DATABASE
                            DocumentReference documentReference = fStore.collection("audits")
                                    .document(user.getUid())
                                    .collection("myAudit")
                                    .document();

                            auditDocId = documentReference.getId();


                            Map<String, Object> audit = new HashMap<>();
                            //TODO: MIGHT NEED TO PULL ALL ACTIVITY DETAILS HERE INSTEAD OF JUST ID TO LIST IN VIEW AUDIT SCREEN
                            audit.put("Activity_ID", docId);

                            documentReference.set(audit).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(AuditBuilder.this, "Activity added to Audit", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(AuditBuilder.this, "Error, try again", Toast.LENGTH_SHORT).show();
                                }
                            });

                        } else {
                            DocumentReference docRef = fStore.collection("audits")
                                    .document(user.getUid())
                                    .collection("myAudit")
                                    .document(auditDocId);
                            docRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(AuditBuilder.this, "Activity removed from Audit", Toast.LENGTH_SHORT).show();
                                    notifyDataSetChanged();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });

                        }
                    }
                });


                auditViewHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(v.getContext(), ActivityDetails.class);
                        //DISPLAYS ALL THE ATTRIBUTES OF THE ACTIVITY
                        i.putExtra("Activity_Name", activity.getActivity_Name());
                        i.putExtra("Activity_Description", activity.getActivity_Description());
                        i.putExtra("Activity_Type", activity.getActivity_Type());
                        i.putExtra("Activity_Date", activity.getActivity_Date());
                        i.putExtra("Activity_Hours", activity.getActivity_Hours());
                        i.putExtra("Activity_Mins", activity.getActivity_Mins());
                        i.putExtra("Activity_Ref1", activity.getActivity_Ref1());
                        i.putExtra("Activity_Ref2", activity.getActivity_Ref2());
                        i.putExtra("Activity_Ref3", activity.getActivity_Ref3());
                        i.putExtra("Activity_Ref4", activity.getActivity_Ref4());
                        i.putExtra("Image_URL", activity.getImage_URL());
                        i.putExtra("Activity_ID", docId);
                        v.getContext().startActivity(i);
                    }
                });



            }

            @NonNull
            @Override
            public AuditBuilder.AuditViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.auditbuilder_view_layout, parent,false);
                return new AuditViewHolder(view);
            }
        };

        auditList.setLayoutManager(new LinearLayoutManager(this));
        auditList.setAdapter(auditAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        auditAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (auditAdapter != null){
            auditAdapter.stopListening();
        }
    }

    public class AuditViewHolder extends RecyclerView.ViewHolder {
        TextView aAuditActivityName, aAuditActivityDescription;
        View view;
        CheckBox auditCheckBox;

        public AuditViewHolder(@NonNull View itemView) {
            super(itemView);
            aAuditActivityName = itemView.findViewById(R.id.auditActivityName);
            aAuditActivityDescription = itemView.findViewById(R.id.auditActivityDescription);
            auditCheckBox = itemView.findViewById(R.id.auditCheckBox);
            view = itemView;
        }
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