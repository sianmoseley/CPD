package com.example.cpd.audit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cpd.R;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.Map;

public class ViewAudit extends AppCompatActivity {

    RecyclerView auditViewList;
    FirebaseFirestore fStore;
    FirestoreRecyclerAdapter<Activity, ViewAudit.ViewAuditViewHolder> viewAuditAdapter;
    FirebaseUser user;
    FirebaseAuth fAuth;
    Button saveProfileBtn;
    TextView professionText, cpdNumberText, summaryText, personalStatementText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_audit);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser();
        auditViewList = findViewById(R.id.auditViewList);



        //CODE TO DISPLAY WHAT USER HAD ENTERED IN CPD PROFILE CLASS
        professionText = findViewById(R.id.professionText);
        professionText.setText(getIntent().getStringExtra("Profession_Text"));

        cpdNumberText = findViewById(R.id.cpdNumberText);
        cpdNumberText.setText(getIntent().getStringExtra("CPD_Number"));

        summaryText = findViewById(R.id.summaryText);
        summaryText.setText(getIntent().getStringExtra("Summary_Text"));

        personalStatementText = findViewById(R.id.personalStatementText);
        personalStatementText.setText(getIntent().getStringExtra("Personal_Statement"));

        //BUTTON CLICK TO SAVE EDIT TEXT FIELDS TO FIREBASE WITH SELECTED ACTIVITIES FOR AUDIT
        saveProfileBtn = findViewById(R.id.saveProfileBtn);

        saveProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DocumentReference documentReference = fStore.collection("audits")
                        .document(user.getUid())
                        .collection("myAuditText")
                        .document("myAudit");

                String profText = professionText.getText().toString();
                String cpdNum = cpdNumberText.getText().toString();
                String sumText = summaryText.getText().toString();
                String psText = personalStatementText.getText().toString();

                Map<String, Object> auditText = new HashMap<>();
                auditText.put("Profession", profText);
                auditText.put("CPD_Number", cpdNum);
                auditText.put("Summary_Text", sumText);
                auditText.put("Personal_Statement", psText);

                documentReference.set(auditText).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(ViewAudit.this, "Audit saved", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(ViewAudit.this, AuditHome.class));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ViewAudit.this, "Error, audit not saved.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


        //QUERY DATABASE TO DISPLAY SELECTED CPD ACTIVITIES FOR AUDIT
        Query query = fStore.collection("cpdActivities")
                .document(user.getUid())
                .collection("myCPD")
                .whereEqualTo("In_Audit", true);

        FirestoreRecyclerOptions<Activity> viewAuditBuilder = new FirestoreRecyclerOptions.Builder<Activity>()
                .setQuery(query, Activity.class)
                .build();

        viewAuditAdapter = new FirestoreRecyclerAdapter<Activity, ViewAuditViewHolder>(viewAuditBuilder) {
            @Override
            protected void onBindViewHolder(@NonNull ViewAuditViewHolder viewAuditViewHolder, final int i, @NonNull final Activity activity) {
                viewAuditViewHolder.vActivityName.setText(activity.getActivity_Name());
                viewAuditViewHolder.vActivityType.setText(activity.getActivity_Description());
                viewAuditViewHolder.vActivityDate.setText(activity.getActivity_Date());
                viewAuditViewHolder.vActivityTime.setText(activity.getActivity_Hours() + " hours " + activity.getActivity_Mins() + " mins");
                final String docId = viewAuditAdapter.getSnapshots().getSnapshot(i).getId();

                viewAuditViewHolder.view.setOnClickListener(new View.OnClickListener() {
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

                ImageView aMenuIcon = viewAuditViewHolder.view.findViewById(R.id.auditViewMenuIcon);
                aMenuIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        final String docID = viewAuditAdapter.getSnapshots().getSnapshot(i).getId();
                        PopupMenu menu = new PopupMenu(v.getContext(), v);
                        menu.getMenu().add("Remove").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                AlertDialog.Builder removeFromAudit = new AlertDialog.Builder(v.getContext());
                                removeFromAudit.setTitle("Are you sure you want to remove this activity?");
                                removeFromAudit.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        DocumentReference documentReference = fStore.collection("cpdActivities")
                                                .document(user.getUid())
                                                .collection("myCPD")
                                                .document(docID);

                                        Map<String, Object> auditFalse = new HashMap<>();

                                        auditFalse.put("In_Audit", false);

                                        documentReference.update(auditFalse).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                //Log.d("TAG", "Activity successfully removed from audit");
                                                notifyDataSetChanged();
                                                //Toast.makeText(SavedAudit.this, "Activity removed from audit", Toast.LENGTH_SHORT).show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(ViewAudit.this, "Error removing activity, try again", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //CLOSE DIALOG
                                    }
                                });
                                removeFromAudit.create().show();
                                return false;
                            }
                        });
                        menu.show();
                    }
                });


            }

            @NonNull
            @Override
            public ViewAuditViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.auditview_view_layout, parent, false);
                return new ViewAuditViewHolder(view);
            }
        };

        auditViewList.setLayoutManager(new LinearLayoutManager(this));
        auditViewList.setAdapter(viewAuditAdapter);

    }

    public class ViewAuditViewHolder extends RecyclerView.ViewHolder {
        TextView vActivityName, vActivityType, vActivityDate, vActivityTime;
        View view;
        public ViewAuditViewHolder(@NonNull View itemView) {
            super(itemView);
            vActivityName = itemView.findViewById(R.id.auditViewActivityName);
            vActivityType = itemView.findViewById(R.id.auditViewActivityType);
            vActivityTime = itemView.findViewById(R.id.auditActivityTime);
            vActivityDate = itemView.findViewById(R.id.auditActivityDate);
            view = itemView;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        viewAuditAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(viewAuditAdapter != null){
            viewAuditAdapter.stopListening();
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