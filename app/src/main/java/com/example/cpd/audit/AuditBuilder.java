package com.example.cpd.audit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.SparseBooleanArray;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AuditBuilder extends AppCompatActivity {

    RecyclerView auditList;
    FirebaseFirestore fStore;
    FirestoreRecyclerAdapter<Activity, AuditBuilder.AuditViewHolder> auditAdapter;
    FirebaseUser user;
    FirebaseAuth fAuth;
    TextView professionEditText, cpdNumberEditText, summaryEditText, personalStatementEditText;
    Button viewAuditProgress;

    final List<Integer> activityList = new ArrayList<>();
    final SparseBooleanArray sparseBooleanArray = new SparseBooleanArray();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audit_builder);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //INITIALIZE FIREBASE INSTANCES
        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser();

        auditList = findViewById(R.id.auditList);
        viewAuditProgress = findViewById(R.id.viewAuditProgress);

        //TO PASS INFO FROM ONE ACTIVITY TO ANOTHER
        professionEditText = findViewById(R.id.professionEditText);
        professionEditText.setText(getIntent().getStringExtra("Profession_Text"));
        professionEditText.setVisibility(View.INVISIBLE);

        cpdNumberEditText = findViewById(R.id.cpdNumberEditText);
        cpdNumberEditText.setText(getIntent().getStringExtra("CPD_Number"));
        cpdNumberEditText.setVisibility(View.INVISIBLE);

        summaryEditText = findViewById(R.id.summaryEditText);
        summaryEditText.setText(getIntent().getStringExtra("Summary_Text"));
        summaryEditText.setVisibility(View.INVISIBLE);

        personalStatementEditText = findViewById(R.id.personalStatementEditText);
        personalStatementEditText.setText(getIntent().getStringExtra("Personal_Statement"));
        personalStatementEditText.setVisibility(View.INVISIBLE);

        //BUTTON CLICK EVENT
        viewAuditProgress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ViewAudit.class);
                intent.putExtra("Profession_Text", professionEditText.getText().toString());
                intent.putExtra("CPD_Number", cpdNumberEditText.getText().toString());
                intent.putExtra("Summary_Text", summaryEditText.getText().toString());
                intent.putExtra("Personal_Statement", personalStatementEditText.getText().toString());
                startActivity(intent);
            }
        });

        //QUERY DATABASE TO DISPLAY CPD ACTIVITIES
        Query query = fStore.collection("cpdActivities")
                .document(user.getUid())
                .collection("myCPD")
                .orderBy("Activity_Date", Query.Direction.ASCENDING);


        FirestoreRecyclerOptions<Activity> auditBuilder = new FirestoreRecyclerOptions.Builder<Activity>()
                .setQuery(query, Activity.class)
                .build();

        auditAdapter = new FirestoreRecyclerAdapter<Activity, AuditBuilder.AuditViewHolder>(auditBuilder) {

            @Override
            protected void onBindViewHolder(@NonNull final AuditBuilder.AuditViewHolder auditViewHolder, int i, @NonNull final Activity activity) {

                auditViewHolder.aAuditActivityName.setText(activity.getActivity_Name());
                auditViewHolder.aAuditActivityType.setText(activity.getActivity_Type());
                auditViewHolder.aAuditActivityDate.setText(activity.getActivity_Date());
                auditViewHolder.auditCheckBox.setSelected(activity.isIn_Audit());
                auditViewHolder.bind(i);

                final String docId = auditAdapter.getSnapshots().getSnapshot(i).getId();

                auditViewHolder.auditCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        //TODO: SCROLL BOXES STILL NOT WORKING CORRECTLY
                        int adapterPosition = auditViewHolder.getAdapterPosition();
                        if (isChecked){
                            auditViewHolder.auditCheckBox.setChecked(true);
                            sparseBooleanArray.put(adapterPosition, true);
                            activityList.add(Integer.valueOf(adapterPosition));


                            //TODO: SCROLLING MAKES THIS UPDATE OF DATABASE WORK INCORRECTLY - TICK ONE BOX, AND IT UPDATES TWO IN DATABASE
                            DocumentReference documentReference = fStore.collection("cpdActivities")
                                    .document(user.getUid())
                                    .collection("myCPD")
                                    .document(docId);

                            Map<String, Object> auditTrue = new HashMap<>();

                            auditTrue.put("In_Audit", true);

                            documentReference.update(auditTrue).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(AuditBuilder.this, "Activity added", Toast.LENGTH_SHORT).show();
                                    //Log.d("TAG", "Activity successfully added to audit");
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(AuditBuilder.this, "Error, try again", Toast.LENGTH_SHORT).show();
                                }
                            });


                        } else {
                            auditViewHolder.auditCheckBox.setChecked(false);
                            activityList.remove(Integer.valueOf(adapterPosition));
                            sparseBooleanArray.put(adapterPosition, false);


                            DocumentReference documentReference = fStore.collection("cpdActivities")
                                    .document(user.getUid())
                                    .collection("myCPD")
                                    .document(docId);

                            Map<String, Object> auditFalse = new HashMap<>();

                            auditFalse.put("In_Audit", false);

                            documentReference.update(auditFalse).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(AuditBuilder.this, "Activity removed", Toast.LENGTH_SHORT).show();
                                    //Log.d("TAG", "Activity successfully removed from audit");
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(AuditBuilder.this, "Error, try again", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    }
                });

                //TODO: IF USERS CLICKS ON ACTIVITY AND PRESSES BACK, CHECKBOX STATE IS LOST AGAIN
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
        final TextView aAuditActivityName;
        final TextView aAuditActivityType;
        final TextView aAuditActivityDate;
        final View view;
        final CheckBox auditCheckBox;

        public AuditViewHolder(@NonNull View itemView) {
            super(itemView);
            aAuditActivityName = itemView.findViewById(R.id.auditActivityName);
            aAuditActivityType = itemView.findViewById(R.id.auditActivityType);
            aAuditActivityDate = itemView.findViewById(R.id.auditActivityDate);
            auditCheckBox = itemView.findViewById(R.id.auditCheckBox);
            view = itemView;
        }

        void bind(int position){
            //use sparse boolean array to check
            if (activityList.contains(position)){
                auditCheckBox.setChecked(true);
            }
            else {
                auditCheckBox.setChecked(false);
            }
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