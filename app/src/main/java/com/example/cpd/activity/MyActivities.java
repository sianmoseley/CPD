package com.example.cpd.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;


import com.example.cpd.MainActivity;
import com.example.cpd.R;
import com.example.cpd.Splash;
import com.example.cpd.Standards;
import com.example.cpd.model.Activity;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class MyActivities extends AppCompatActivity {

    RecyclerView activityList;
    FirebaseFirestore fStore;
    FirestoreRecyclerAdapter<Activity, MyActivities.ActivityViewHolder> activityAdapter;
    FirebaseUser user;
    FirebaseAuth fAuth;
    FloatingActionButton floatBtnAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_activities);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser();
        activityList = findViewById(R.id.activityList2);


        //QUERY DATABASE TO DISPLAY CPD ACTIVITIES
        Query query = fStore.collection("cpdActivities")
                .document(user.getUid())
                .collection("myCPD")
                .orderBy("Activity_Date", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<Activity> allActivities = new FirestoreRecyclerOptions.Builder<Activity>()
                .setQuery(query, Activity.class)
                .build();

        activityAdapter = new FirestoreRecyclerAdapter<Activity, MyActivities.ActivityViewHolder>(allActivities) {
            @Override
            protected void onBindViewHolder(@NonNull MyActivities.ActivityViewHolder activityViewHolder, final int i, @NonNull final Activity activity) {
                activityViewHolder.mActivityName.setText(activity.getActivity_Name());
                activityViewHolder.mActivityDescription.setText(activity.getActivity_Description());
                final String docId = activityAdapter.getSnapshots().getSnapshot(i).getId();


                activityViewHolder.view.setOnClickListener(new View.OnClickListener() {
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

                //POP UP MENU (THREE DOTS) ON ACTIVITY VIEW LAYOUT
                ImageView menuIcon = activityViewHolder.view.findViewById(R.id.menuIcon);
                menuIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        final String docID = activityAdapter.getSnapshots().getSnapshot(i).getId();
                        PopupMenu menu = new PopupMenu(v.getContext(),v);
                        menu.getMenu().add("Edit").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                Intent i = new Intent(v.getContext(), EditActivity.class);
                                i.putExtra("Activity_Name", activity.getActivity_Name());
                                i.putExtra("Activity_Description", activity.getActivity_Description());
                                i.putExtra("Activity_Type",activity.getActivity_Type());
                                i.putExtra("Activity_Hours", activity.getActivity_Hours() );
                                i.putExtra("Activity_Mins", activity.getActivity_Mins());
                                i.putExtra("Activity_Date", activity.getActivity_Date());
                                i.putExtra("Activity_Ref1", activity.getActivity_Ref1());
                                i.putExtra("Activity_Ref2", activity.getActivity_Ref2());
                                i.putExtra("Activity_Ref3",activity.getActivity_Ref3() );
                                i.putExtra("Activity_Ref4",activity.getActivity_Ref4() );
                                i.putExtra("Image_URL", activity.getImage_URL());
                                i.putExtra("Activity_ID", docID);
                                startActivity(i);
                                return false;
                            }
                        });

                        menu.getMenu().add("Delete").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                AlertDialog.Builder deleteActivity = new AlertDialog.Builder(v.getContext());
                                deleteActivity.setTitle("Are you sure you want to delete this activity?");
                                deleteActivity.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        DocumentReference docRef = fStore.collection("cpdActivities")
                                                .document(user.getUid())
                                                .collection("myCPD")
                                                .document(docId);
                                        docRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                //ACTIVITY DELETED
                                                //REFRESH THE RECYCLER VIEW WITH THE EDITED DATA SO ARRAY LIST IS UPDATED
                                                notifyDataSetChanged();
                                                Toast.makeText(MyActivities.this, "Activity Deleted", Toast.LENGTH_SHORT).show();

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(MyActivities.this, "Error in deleting activity", Toast.LENGTH_SHORT).show();

                                            }
                                        });
                                    }
                                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //close the dialog
                                    }
                                });
                                deleteActivity.create().show();
                                return false;
                            }
                        });

                        menu.show();
                    }
                });
            }

            @NonNull
            @Override
            public MyActivities.ActivityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cpdactivity_view_layout, parent, false);
                return new ActivityViewHolder(view);
            }
        };

        activityList.setLayoutManager(new LinearLayoutManager(this));
        activityList.setAdapter(activityAdapter);


        floatBtnAdd = findViewById(R.id.floatBtnAdd);

        floatBtnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), AddActivity.class));
            }
        });
    }

    public class ActivityViewHolder extends RecyclerView.ViewHolder {
        TextView mActivityName, mActivityDescription;
        View view;
        public ActivityViewHolder(@NonNull View itemView) {
            super(itemView);
            mActivityName = itemView.findViewById(R.id.activityName);
            mActivityDescription = itemView.findViewById(R.id.activityDescription);
            view = itemView;

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        activityAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(activityAdapter != null){
            activityAdapter.stopListening();
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, MainActivity.class));
    }
}




