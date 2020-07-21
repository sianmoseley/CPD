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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.api.Distribution;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

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

        saveProfileBtn = findViewById(R.id.saveProfileBtn);

        professionText = findViewById(R.id.professionText);
        professionText.setText(getIntent().getStringExtra("Profession_Text"));

        cpdNumberText = findViewById(R.id.cpdNumberText);
        cpdNumberText.setText(getIntent().getStringExtra("CPD_Number"));

        summaryText = findViewById(R.id.summaryText);
        summaryText.setText(getIntent().getStringExtra("Summary_Text"));

        personalStatementText = findViewById(R.id.personalStatementText);
        personalStatementText.setText(getIntent().getStringExtra("Personal_Statement"));



        //QUERY DATABASE TO DISPLAY CPD ACTIVITIES
        Query query = fStore.collection("audits")
                .document(user.getUid())
                .collection("myAudit")
                .orderBy("Activity_Date", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<Activity> viewAuditBuilder = new FirestoreRecyclerOptions.Builder<Activity>()
                .setQuery(query, Activity.class)
                .build();

        viewAuditAdapter = new FirestoreRecyclerAdapter<Activity, ViewAuditViewHolder>(viewAuditBuilder) {
            @Override
            protected void onBindViewHolder(@NonNull ViewAuditViewHolder viewAuditViewHolder, final int i, @NonNull final Activity activity) {
                viewAuditViewHolder.vActivityName.setText(activity.getActivity_Name());
                viewAuditViewHolder.vActivityDescription.setText(activity.getActivity_Description());
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
                                        DocumentReference documentReference = fStore.collection("audits")
                                                .document(user.getUid())
                                                .collection("myAudit")
                                                .document(docID);
                                        documentReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                //ACTIVITY REMOVED FROM AUDIT LIST
                                                //REFRESH THE RECYCLER VIEW WITH THE EDITED DATA SO ARRAY LIST IS UPDATED
                                                notifyDataSetChanged();
                                                Toast.makeText(ViewAudit.this, "Activity removed from audit", Toast.LENGTH_SHORT).show();
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
        TextView vActivityName, vActivityDescription;
        View view;
        public ViewAuditViewHolder(@NonNull View itemView) {
            super(itemView);
            vActivityName = itemView.findViewById(R.id.auditViewActivityName);
            vActivityDescription = itemView.findViewById(R.id.auditViewActivityDescription);
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