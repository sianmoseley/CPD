package com.example.cpd.audit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cpd.R;
import com.example.cpd.activity.ActivityDetails;
import com.example.cpd.model.Activity;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.Map;

public class EditSavedAuditActivities extends AppCompatActivity {

    RecyclerView editAuditList;
    FirebaseFirestore fStore;
    FirestoreRecyclerAdapter<Activity, EditSavedAuditActivities.EditAuditViewHolder> editAuditAdapter;
    FirebaseUser user;
    FirebaseAuth fAuth;
    Button saveAuditEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_saved_audit_activities);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser();

        saveAuditEdit = findViewById(R.id.saveAuditEdit);
        saveAuditEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), AuditHome.class));
                finish();
            }
        });

        editAuditList = findViewById(R.id.editAuditList);



        //QUERY DATABASE TO DISPLAY CPD ACTIVITIES
        Query query = fStore.collection("cpdActivities")
                .document(user.getUid())
                .collection("myCPD")
                .orderBy("Activity_Date", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<Activity> editAuditBuilder = new FirestoreRecyclerOptions.Builder<Activity>()
                .setQuery(query, Activity.class)
                .build();

        editAuditAdapter = new FirestoreRecyclerAdapter<Activity, EditAuditViewHolder>(editAuditBuilder) {
            @Override
            protected void onBindViewHolder(@NonNull EditAuditViewHolder editAuditViewHolder, int i, @NonNull final Activity activity) {
                editAuditViewHolder.eAuditActivityName.setText(activity.getActivity_Name());
                editAuditViewHolder.eAuditActivityType.setText(activity.getActivity_Type());
                editAuditViewHolder.eAuditActivityDate.setText(activity.getActivity_Date());
                //TODO: IF TRUE, CHECK BOX SHOULD BE TICKED (NOT WORKING YET)
                editAuditViewHolder.editAuditCheckBox.setChecked(activity.isIn_Audit());

                final String docId = editAuditAdapter.getSnapshots().getSnapshot(i).getId();

                //TO DISPLAY FULL ACTIVITY DETAILS WHEN CLICKED
                editAuditViewHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(v.getContext(), ActivityDetails.class);
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

                editAuditViewHolder.editAuditCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked){

                            //ADD DOCUMENT ID IN DATABASE
                            DocumentReference documentReference = fStore.collection("audits")
                                    .document(user.getUid())
                                    .collection("myAuditActivities")
                                    .document(docId);

                            Map<String, Object> editAudit = new HashMap<>();

                            editAudit.put("Activity_ID", docId);
                            editAudit.put("Activity_Name", activity.getActivity_Name());
                            editAudit.put("Activity_Description", activity.getActivity_Description());
                            editAudit.put("Activity_Type", activity.getActivity_Type());
                            editAudit.put("Activity_Date", activity.getActivity_Date());
                            editAudit.put("Activity_Hours", activity.getActivity_Hours());
                            editAudit.put("Activity_Mins", activity.getActivity_Mins());
                            editAudit.put("Activity_Ref1", activity.getActivity_Ref1());
                            editAudit.put("Activity_Ref2", activity.getActivity_Ref2());
                            editAudit.put("Activity_Ref3", activity.getActivity_Ref3());
                            editAudit.put("Activity_Ref4", activity.getActivity_Ref4());
                            editAudit.put("Image_URL", activity.getImage_URL());
                            editAudit.put("In_Audit", true);

                            documentReference.set(editAudit).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(EditSavedAuditActivities.this, "Activity added to Audit", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(EditSavedAuditActivities.this, "Error, try again", Toast.LENGTH_SHORT).show();
                                }
                            });

//                            DocumentReference updateAuditBooleanTrue = fStore.collection("cpdActivities")
//                                    .document(user.getUid())
//                                    .collection("myCPD")
//                                    .document(docId);
//
//                            Map<String, Object> updateBooleanTrue = new HashMap<>();
//                            updateBooleanTrue.put("In_Audit", true);
//
//                            updateAuditBooleanTrue.update(updateBooleanTrue).addOnSuccessListener(new OnSuccessListener<Void>() {
//                                @Override
//                                public void onSuccess(Void aVoid) {
//                                    Log.d("TAG", docId + ": In_Audit changed to true");
//                                }
//                            }).addOnFailureListener(new OnFailureListener() {
//                                @Override
//                                public void onFailure(@NonNull Exception e) {
//                                    Log.d("TAG", "Error updating In_Audit");
//                                }
//                            });

                        } else {
                            //CHECKBOX UNCHECKED, DELETES FROM AUDIT TABLE
                            DocumentReference docRef = fStore.collection("audits")
                                    .document(user.getUid())
                                    .collection("myAuditActivities")
                                    .document(docId);
                            docRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(EditSavedAuditActivities.this, "Activity removed from Audit", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(EditSavedAuditActivities.this, "Error, try again", Toast.LENGTH_SHORT).show();
                                }
                            });

//                            DocumentReference updateAuditBoolean = fStore.collection("cpdActivities")
//                                    .document(user.getUid())
//                                    .collection("myCPD")
//                                    .document(docId);
//
//                            Map<String, Object> updateBoolean = new HashMap<>();
//                            updateBoolean.put("In_Audit", false);
//
//                            updateAuditBoolean.update(updateBoolean).addOnSuccessListener(new OnSuccessListener<Void>() {
//                                @Override
//                                public void onSuccess(Void aVoid) {
//                                    Log.d("TAG", docId + ": In_Audit changed to false");
//                                }
//                            }).addOnFailureListener(new OnFailureListener() {
//                                @Override
//                                public void onFailure(@NonNull Exception e) {
//                                    Log.d("TAG", "Error updating In_Audit");
//                                }
//                            });
                        }
                    }
                });

            }

            @NonNull
            @Override
            public EditAuditViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.auditbuilder_view_layout, parent, false);
                return new EditAuditViewHolder(view);
            }
        };

        editAuditList.setLayoutManager(new LinearLayoutManager(this));
        editAuditList.setAdapter(editAuditAdapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        editAuditAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (editAuditAdapter != null){
            editAuditAdapter.stopListening();
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

    public class EditAuditViewHolder extends RecyclerView.ViewHolder {
        TextView eAuditActivityName, eAuditActivityType, eAuditActivityDate;
        View view;
        CheckBox editAuditCheckBox;

        public EditAuditViewHolder(@NonNull View itemView) {
            super(itemView);
            eAuditActivityName = itemView.findViewById(R.id.auditActivityName);
            eAuditActivityType = itemView.findViewById(R.id.auditActivityType);
            eAuditActivityDate = itemView.findViewById(R.id.auditActivityDate);
            editAuditCheckBox = itemView.findViewById(R.id.auditCheckBox);
            view = itemView;
        }
    }
}