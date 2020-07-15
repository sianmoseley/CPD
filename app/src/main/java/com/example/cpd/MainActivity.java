package com.example.cpd;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.cpd.account.MyAccount;
import com.example.cpd.activity.AddActivity;
import com.example.cpd.activity.MyActivities;
import com.example.cpd.calendar.Calendar;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements  NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    NavigationView nav_view;
    FirebaseFirestore fStore;
    FirebaseUser user;
    FirebaseAuth fAuth;
    float totalHours;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);

        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser();

        drawerLayout = findViewById(R.id.drawer);
        nav_view = findViewById(R.id.nav_view);
        nav_view.setNavigationItemSelectedListener(this);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);

        //enables hamburger icon in toolbar
        toggle.setDrawerIndicatorEnabled(true);
        //informs the action bar toggle that the nav drawer is open or closed
        toggle.syncState();

        View headerView = nav_view.getHeaderView(0);
        final TextView auditYear = headerView.findViewById(R.id.auditYearRef);

        final DocumentReference documentReference = fStore.collection("auditYear").document("xItBROZmfDnOzOpqLWSh");

        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        auditYear.setText(document.getString("year"));
                        }
                    }
                }
            }
        );





        final PieChart pieChart = findViewById(R.id.pieChart);

        //STATIC PIE DATA
        final ArrayList<PieEntry> hours = new ArrayList<>();

        //TODO: ONLY COUNTS HOURS, NOT MINUTES YET
        //TODO: PIE CHART DOES NOT UPDATE WITH NEW DATA CHANGES
        Query cpdHoursSelfDirectedLearning = fStore.collection("cpdActivities")
                .document(user.getUid())
                .collection("myCPD")
                .whereEqualTo("Activity_Type", "Self-Directed Learning");

        cpdHoursSelfDirectedLearning.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    hours.add(
                            new PieEntry(calcTime(task.getResult()), "Self-Directed Learning")
                    );
                    pieChart.notifyDataSetChanged();
                }
            }
        });

        Query cpdHoursFormalEducation = fStore.collection("cpdActivities")
                .document(user.getUid())
                .collection("myCPD")
                .whereEqualTo("Activity_Type", "Formal Education Completed");


        cpdHoursFormalEducation.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    hours.add(
                            new PieEntry(calcTime(task.getResult()), "Self-Directed Learning")
                    );
                    pieChart.notifyDataSetChanged();
                }
            }
        });



        PieDataSet pieDataSet = new PieDataSet(hours, "CPD Health Check");
        pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        pieDataSet.setValueTextColor(Color.BLACK);
        pieDataSet.setValueTextSize(16f);

        PieData pieData = new PieData(pieDataSet);

        pieChart.setData(pieData);
        pieChart.getDescription().setEnabled(false);
        //pieChart.setCenterText("You have X hours left to complete ");
        pieChart.animate();

        Legend legend = pieChart.getLegend();
        legend.setTextSize(13);
        legend.setDrawInside(false);
        legend.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        legend.setWordWrapEnabled(true);

    }

    private float calcTime(QuerySnapshot result) {
       totalHours = 0;
        for (QueryDocumentSnapshot document: result){
            String sHour = document.getString("Activity_Hours");
            float xHours = Float.parseFloat(sHour);
            totalHours += xHours;
        }
        return totalHours;

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        //CLOSES THE DRAWER WHEN ITEM IS CLICKED
        drawerLayout.closeDrawer(GravityCompat.START);
        //CHECKS WHICH ITEM IS CLICKED
        switch (item.getItemId()) {
            case R.id.addActivity:
                startActivity(new Intent(this, AddActivity.class));
                break;
            case R.id.logout:
                //TODO: COULD ADD AN ALERT TO CHECK USER IS SURE THEY WANT TO LOG OUT?
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(), Splash.class));
                finish();
                break;
            case R.id.cpdStandards:
                startActivity(new Intent(this, Standards.class));
                break;
            case R.id.activities:
                startActivity(new Intent(this, MyActivities.class));
                break;
            case R.id.myAccount:
                startActivity(new Intent(this, MyAccount.class));
                break;
            case R.id.calendar:
                startActivity(new Intent(this, Calendar.class));
                break;

            default:
                Toast.makeText(this, "Coming soon", Toast.LENGTH_SHORT).show();
        }
        return false;
    }


}