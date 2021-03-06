package com.example.cpd.audit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SavedAudit extends AppCompatActivity {

    TextView savedProfessionText, savedCpdNumberText, savedSummaryText, savedPersonalStatementText;
    Button exportProfileBtn, editProfileBtn;
    FirebaseUser user;
    FirebaseAuth fAuth;
    RecyclerView savedAuditViewList;
    FirestoreRecyclerAdapter<Activity, SavedAudit.SavedAuditViewHolder> savedAuditAdapter;
    FirebaseFirestore fStore;

    String fActivity_Name, fActivity_Date, fActivity_Hours, fActivity_Mins, fActivity_Type, fActivity_Ref1, fActivity_Description, fActivity_Ref2, fActivity_Ref3, fActivity_Ref4, fImage_Url;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_audit);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //INITIALISE FIREBASE INSTANCES
        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser();

        savedAuditViewList = findViewById(R.id.savedAuditViewList);


        editProfileBtn = findViewById(R.id.editProfileBtn);
        editProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), EditSavedAudit.class));
                finish();
            }
        });

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
        Query query = fStore.collection("cpdActivities")
                .document(user.getUid())
                .collection("myCPD")
                .whereEqualTo("In_Audit", true);

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
                return new SavedAuditViewHolder(view);
            }
        };

        savedAuditViewList.setLayoutManager(new LinearLayoutManager(this));
        savedAuditViewList.setAdapter(savedAuditAdapter);


        //TO CREATE SAVED AUDIT PROFILE AS A CSV.FILE THAT IS THEN EMAILED TO THE USER
        final File csv = new File(getExternalFilesDir(null), "/MyCPDAudit.csv");

        exportProfileBtn = findViewById(R.id.exportProfileBtn);
        exportProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    //AUDIT TEXT DATA READ FROM FIREBASE
                    final String fProfession = savedProfessionText.getText().toString();
                    final String fCpd_Number = savedCpdNumberText.getText().toString();
                    final String fSummary_Text = savedSummaryText.getText().toString();
                    final String fPersonal_Statement = savedPersonalStatementText.getText().toString();

                    //QUERY DATABASE TO DISPLAY SELECTED CPD ACTIVITIES FOR AUDIT
                    final Query query = fStore.collection("cpdActivities")
                            .document(user.getUid())
                            .collection("myCPD")
                            .whereEqualTo("In_Audit", true);

                    query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()){
                                //CSVWRITER LIBRARY USED TO CREATED CSV FILE
                                try {
                                    CSVWriter writer = new CSVWriter((new FileWriter(csv)));

                                    List<String[]> data = new ArrayList<>();
                                    data.add(new String[]{"Profession", "CPD_Number", "Summary_Text", "Personal_Statement"});
                                    data.add(new String[]{fProfession, fCpd_Number, fSummary_Text, fPersonal_Statement});
                                    data.add(new String[]{"Activity_Name", "Activity_Date", "Activity_Hours", "Activity_Mins", "Activity_Type", "Activity_Description", "Activity_Ref1", "Activity_Ref2", "Activity_Ref3", "Activity_Ref4", "Image_Url"});

                                    for (QueryDocumentSnapshot document : task.getResult()){

                                        fActivity_Name = document.getString("Activity_Name");
                                        fActivity_Date = document.getString("Activity_Date");
                                        fActivity_Hours = document.getString("Activity_Hours");
                                        fActivity_Mins = document.getString("Activity_Mins");
                                        fActivity_Type = document.getString("Activity_Type");
                                        fActivity_Description = document.getString("Activity_Description");
                                        fActivity_Ref1 = document.getString("Activity_Ref1");
                                        fActivity_Ref2 = document.getString("Activity_Ref2");
                                        fActivity_Ref3 = document.getString("Activity_Ref3");
                                        fActivity_Ref4 = document.getString("Activity_Ref4");
                                        fImage_Url = document.getString("Image_URL");
                                        //Log.d("TAG", document.getId() + " => " + document.getData());
                                        data.add(new String[]{fActivity_Name, fActivity_Date, fActivity_Hours, fActivity_Mins, fActivity_Type, fActivity_Description, fActivity_Ref1, fActivity_Ref2, fActivity_Ref3, fActivity_Ref4, fImage_Url});
                                    }

                                    writer.writeAll(data);
                                    writer.close();

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                        }
                    });


                //INTENT TO SEND USER EMAIL
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setType("text/plain");
                //SETS THE USERS EMAIL AS THE RECIPIENT EMAIL ADDRESS
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{user.getEmail()});
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "My Audit Profile");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "Thank you for using CPD Journal to build your CPD Audit Profile. Please see the attached file for your records.");
                emailIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                //File file = new File(csv);
                Log.d("TAG", "csv = " + csv);

                Uri uri = FileProvider.getUriForFile(SavedAudit.this, "com.example.cpd.provider", csv);
                Log.d("TAG", "uri = " + uri);


                emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
                emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                String packageName = getApplicationContext().getPackageName();
//                grantUriPermission(packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);

                //USER CAN SELECT HOW THEY WANT TO SHARE THE CSV FILE
                startActivity(Intent.createChooser(emailIntent, "Pick an email provider"));


            }
        });

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

    public static class SavedAuditViewHolder extends RecyclerView.ViewHolder{
        final TextView sActivityName;
        final TextView sActivityType;
        final TextView sActivityDate;
        final TextView sActivityTime;
        final View view;
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