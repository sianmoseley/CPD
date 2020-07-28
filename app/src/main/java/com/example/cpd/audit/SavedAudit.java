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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class SavedAudit extends AppCompatActivity {

    TextView savedProfessionTitle, savedProfessionText, savedCpdTitle, savedCpdNumberText, savedSummaryTitle, savedSummaryText, savedPersonalStatementTitle, savedPersonalStatementText;
    Button exportProfileBtn, editProfileBtn;
    FirebaseUser user;
    FirebaseAuth fAuth;
    RecyclerView savedAuditViewList;
    FirestoreRecyclerAdapter<Activity, SavedAudit.SavedAuditViewHolder> savedAuditAdapter;
    FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_audit);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser();
        savedAuditViewList = findViewById(R.id.savedAuditViewList);

        exportProfileBtn = findViewById(R.id.exportProfileBtn);
        exportProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SavedAudit.this, "Export profile coming soon", Toast.LENGTH_SHORT).show();
            }
        });


        editProfileBtn = findViewById(R.id.editProfileBtn);
        editProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), EditSavedAudit.class));
                finish();
            }
        });

        savedProfessionTitle = findViewById(R.id.savedProfessionTitle);
        savedCpdTitle = findViewById(R.id.savedCpdTitle);
        savedSummaryTitle = findViewById(R.id.savedSummaryTitle);
        savedPersonalStatementTitle = findViewById(R.id.savedPersonalStatementTitle);



        //DOCUMENT REFERENCE TO DISPLAY SAVED AUDIT TEXT
        DocumentReference documentReference = fStore.collection("audits")
                .document(user.getUid())
                .collection("myAuditText")
                .document("myAudit");

        savedProfessionText = findViewById(R.id.savedProfessionText);
        savedCpdNumberText = findViewById(R.id.savedCpdNumberText);
        savedSummaryText = findViewById(R.id.savedSummaryText);
        savedPersonalStatementText = findViewById(R.id.savedPersonalStatementText);

        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()){
                        savedProfessionText.setText(document.getString("Profession"));
                        savedCpdNumberText.setText(document.getString("CPD_Number"));
                        savedSummaryText.setText(document.getString("Summary_Text"));
                        savedPersonalStatementText.setText(document.getString("Personal_Statement"));
                    }
                }
            }
        });



        //QUERY DATABASE TO DISPLAY SELECTED CPD ACTIVITIES FOR AUDIT
        Query query = fStore.collection("audits")
                .document(user.getUid())
                .collection("myAuditActivities")
                .orderBy("Activity_Date", Query.Direction.ASCENDING);

        final FirestoreRecyclerOptions<Activity> savedAuditBuilder = new FirestoreRecyclerOptions.Builder<Activity>()
                .setQuery(query, Activity.class)
                .build();

        savedAuditAdapter = new FirestoreRecyclerAdapter<Activity, SavedAuditViewHolder>(savedAuditBuilder) {
            @Override
            protected void onBindViewHolder(@NonNull final SavedAuditViewHolder savedAuditViewHolder, final int i, @NonNull final Activity activity) {
                savedAuditViewHolder.sActivityName.setText(activity.getActivity_Name());
                savedAuditViewHolder.sActivityType.setText(activity.getActivity_Description());
                savedAuditViewHolder.sActivityDate.setText(activity.getActivity_Date());
                savedAuditViewHolder.sActivityTime.setText(activity.getActivity_Hours() + " hours " + activity.getActivity_Mins() + " mins");
                final String docID = savedAuditBuilder.getSnapshots().getSnapshot(i).getId();

                savedAuditViewHolder.view.setOnClickListener(new View.OnClickListener() {
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
                        i.putExtra("Activity_ID", docID);
                        v.getContext().startActivity(i);
                    }
                });

                ImageView aMenuIcon = savedAuditViewHolder.view.findViewById(R.id.auditViewMenuIcon);
                aMenuIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        final String docID = savedAuditAdapter.getSnapshots().getSnapshot(i).getId();
                        PopupMenu menu = new PopupMenu(v.getContext(), v);
                        menu.getMenu().add("Remove").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                AlertDialog.Builder removeFromAudit = new AlertDialog.Builder(v.getContext());
                                removeFromAudit.setTitle("Are you sure you want to remove this activity?");
                                removeFromAudit.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //TODO: NEED TO ADD ADDITIONAL UPDATE TO IN_AUDIT BOOLEAN IN CPD ACTIVITIES COLLECTION WHEN WORKING CORRECTLY
                                        DocumentReference documentReference = fStore.collection("audits")
                                                .document(user.getUid())
                                                .collection("myAuditActivities")
                                                .document(docID);
                                        documentReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                //ACTIVITY REMOVED FROM AUDIT LIST
                                                //REFRESH THE RECYCLER VIEW WITH THE EDITED DATA SO ARRAY LIST IS UPDATED
                                                notifyDataSetChanged();
                                                Toast.makeText(SavedAudit.this, "Activity removed from audit", Toast.LENGTH_SHORT).show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(SavedAudit.this, "Error removing activity, try again", Toast.LENGTH_SHORT).show();
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
            public SavedAuditViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.auditview_view_layout, parent, false);
                return new SavedAudit.SavedAuditViewHolder(view);
            }
        };

        savedAuditViewList.setLayoutManager(new LinearLayoutManager(this));
        savedAuditViewList.setAdapter(savedAuditAdapter);


    }

    @Override
    protected void onStart() {
        super.onStart();
        savedAuditAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(savedAuditAdapter != null){
            savedAuditAdapter.stopListening();
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

    public class SavedAuditViewHolder extends RecyclerView.ViewHolder{
        TextView sActivityName, sActivityType, sActivityDate, sActivityTime;
        View view;
        public SavedAuditViewHolder(@NonNull View itemView) {
            super(itemView);
            sActivityName = itemView.findViewById(R.id.auditViewActivityName);
            sActivityType = itemView.findViewById(R.id.auditViewActivityType);
            sActivityDate = itemView.findViewById(R.id.auditActivityTime);
            sActivityTime = itemView.findViewById(R.id.auditActivityDate);
            view = itemView;

        }
    }
}