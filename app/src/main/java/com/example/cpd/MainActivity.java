package com.example.cpd;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.cpd.account.MyAccount;
import com.example.cpd.activity.AddActivity;
import com.example.cpd.activity.MyActivities;
import com.example.cpd.audit.AuditHome;
import com.example.cpd.calendar.Calendar;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.datatype.Duration;

public class MainActivity extends AppCompatActivity implements  NavigationView.OnNavigationItemSelectedListener {

    protected DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    NavigationView nav_view;
    FirebaseFirestore fStore;
    FirebaseUser user;
    FirebaseAuth fAuth;
    float totalHours;
    float totalMins;
    FloatingActionButton addNewBtn;
    PieChart pieChart;
    ArrayList<PieEntry> hours;
    TextView welcomeText;
    float formalEducationCompletedHours, otherCompletedHours, professionalActivitiesHours, selfDirectedLearningHours, workBasedLearningHours;

    int totalHoursCount;


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

        createPieChart();

        addNewBtn = findViewById(R.id.addNewActivityBtn);
        addNewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), AddActivity.class));
                finish();
            }
        });

        //TODO: NEED NEW QUERY/WAY TO ADD UP TOTAL HOURS/MINS TO DISPLAY HERE
        welcomeText = findViewById(R.id.welcomeText);

        countTotalCPDTime();

    }

    private void countTotalCPDTime() {

        final CollectionReference totalHours = fStore.collection("cpdActivities").document(user.getUid()).collection("myCPD");

        totalHours.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    totalHoursCount = 0;
                    for (QueryDocumentSnapshot document : task.getResult()){
                        String hoursTotalString = document.getString("Activity_Hours");
                        int hoursTotal = Integer.parseInt(hoursTotalString);
                        totalHoursCount += hoursTotal;
                    }
                    Log.d("TAG", "Total hours of CPD activity is: " + totalHoursCount);

                }
            }
        });

        CollectionReference totalMins = fStore.collection("cpdActivities").document(user.getUid()).collection("myCPD");

        totalMins.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    int totalMins = 0;
                    for (QueryDocumentSnapshot document : task.getResult()){
                        String totalMinsString = document.getString("Activity_Mins");
                        int minsTotal = Integer.parseInt(totalMinsString);
                        totalMins += minsTotal;
                    }
                    Log.d("TAG", "Total mins of CPD activity is: " + totalMins);

                    int hoursToMins = totalHoursCount * 60;
                    int totalTimeInMins = totalMins + hoursToMins;
                    Log.d("TAG", "TIME CALCUATED TO ALL MINUTES:" + totalTimeInMins);


                    String totalTimeString = String.valueOf(totalTimeInMins);




                    SimpleDateFormat sdf = new SimpleDateFormat("mm");
                    try {
                        Date dt = sdf.parse(totalTimeString);
                        sdf = new SimpleDateFormat("HH:mm");
                        Log.d("TAG", sdf.format(dt));
                        welcomeText.setText("You have recorded " + sdf.format(dt) + " minutes of CPD activities.");
                    } catch (ParseException e){
                        e.printStackTrace();
                    }



//                    if (totalMins > 60) {
//                        newMins = totalMins - 60;
//                        newHoursCount = totalHoursCount + 1;
//                        if (newMins >= 60){
//                            updatedMins = newMins - 60;
//                            Log.d("TAG", "new mins is: " + updatedMins);
//                            updatedHours = newHoursCount + 1;
//                            Log.d("TAG", "new hours is: " + updatedHours);
//                        }
//                        );
//                    }


//                    if (totalMins == 60) {
//                        newMins = totalMins - 60;
//                        Log.d("TAG", "new mins is: " + newMins);
//                        newHoursCount = totalHoursCount + 1;
//                        Log.d("TAG", "new hours is: " + newHoursCount);
//                    }
//                    welcomeText.setText("You have recorded " + newHoursCount + " hours and " + newMins + " minutes of CPD.");
                }
            }
        });

    }


    private void createPieChart() {

        pieChart = findViewById(R.id.pieChart);
        pieChart.invalidate();


        //STATIC PIE DATA
        hours = new ArrayList<>();

        //TODO: ONLY COUNTS HOURS, NOT MINUTES YET - NOT QUITE THERE YET, COME BACK TO THIS
        Query cpdHoursFormalEducation = fStore.collection("cpdActivities")
                .document(user.getUid())
                .collection("myCPD")
                .whereEqualTo("Activity_Type", "Formal Education Completed");

        cpdHoursFormalEducation.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    formalEducationCompletedHours = calcHours(task.getResult());
                  if(calcHours(task.getResult()) != 0.0){
                      hours.add(
                              new PieEntry(formalEducationCompletedHours, "Formal Education Completed")
                      );
                      Log.d("TAG", "Formal Education Completed: " + formalEducationCompletedHours);
                      pieChart.notifyDataSetChanged();
                  }
                }
            }
        });

        Query cpdHoursOtherCompleted = fStore.collection("cpdActivities")
                .document(user.getUid())
                .collection("myCPD")
                .whereEqualTo("Activity_Type", "Other Completed");


        cpdHoursOtherCompleted.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    otherCompletedHours = calcHours(task.getResult());
                    if(calcHours(task.getResult()) != 0.0){
                        hours.add(
                                new PieEntry(otherCompletedHours, "Other Completed")
                        );
                        Log.d("TAG", "Other Completed: " + otherCompletedHours);
                        pieChart.notifyDataSetChanged();
                    }
                }
            }
        });

        Query cpdHoursProfessionalActivities = fStore.collection("cpdActivities")
                .document(user.getUid())
                .collection("myCPD")
                .whereEqualTo("Activity_Type", "Professional Activities");


        cpdHoursProfessionalActivities.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    professionalActivitiesHours = calcHours(task.getResult());
                    if(calcHours(task.getResult()) != 0.0){
                        hours.add(
                                new PieEntry(professionalActivitiesHours, "Professional Activities")
                        );
                        Log.d("TAG", "Professional Activities: " + professionalActivitiesHours);
                        pieChart.notifyDataSetChanged();
                    }
                }
            }
        });

        Query cpdHoursSelfDirectedLearning = fStore.collection("cpdActivities")
                .document(user.getUid())
                .collection("myCPD")
                .whereEqualTo("Activity_Type", "Self-Directed Learning");

        cpdHoursSelfDirectedLearning.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    selfDirectedLearningHours = calcHours(task.getResult());
                    if(calcHours(task.getResult()) != 0.0){
                        hours.add(
                                new PieEntry(selfDirectedLearningHours, "Self-Directed Learning")
                        );
                        Log.d("TAG", "Self-Directed Learning: " + selfDirectedLearningHours);
                        pieChart.notifyDataSetChanged();
                    }
                }
            }
        });

        Query cpdHoursWorkBasedLearning = fStore.collection("cpdActivities")
                .document(user.getUid())
                .collection("myCPD")
                .whereEqualTo("Activity_Type", "Work-Based Learning");

        cpdHoursWorkBasedLearning.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    workBasedLearningHours = calcHours(task.getResult());
                    if(calcHours(task.getResult()) != 0.0){
                        hours.add(
                                new PieEntry(workBasedLearningHours, "Work-Based Learning")
                        );
                        Log.d("TAG", "Worked-Based Learning: " + workBasedLearningHours);
                        pieChart.notifyDataSetChanged();
                    }
                }
            }
        });


        //TODO: FIX PIE CHART LAYOUT STUFF
        PieDataSet pieDataSet = new PieDataSet(hours, "");
        pieDataSet.setColors(new int[] {R.color.pie_blue, R.color.pie_green, R.color.pie_yellow, R.color.pie_red, R.color.pie_purple}, this);
        pieDataSet.setValueTextColor(Color.WHITE);
        pieDataSet.setValueTextSize(16f);
        PieData pieData = new PieData(pieDataSet);


        pieChart.setData(pieData);
        pieChart.getDescription().setEnabled(false);
        pieChart.setEntryLabelColor(Color.parseColor("#2F334F"));
        pieChart.setCenterText("My CPD");
        pieChart.setCenterTextSize(16f);

        pieChart.setCenterTextColor(Color.parseColor("#2F334F"));
        pieChart.animate();

        //MAKES PIE CHART SHOW TOTAL COUNT WITH DECIMAL
        //pieData.setValueFormatter(new MyDecimalValueFormatter());

        //MAKES PIE CHART SHOW PERCENTAGES
        pieData.setValueFormatter(new PercentFormatter(pieChart));
        pieChart.setUsePercentValues(true);

        Legend legend = pieChart.getLegend();
        legend.setTextSize(16);

        legend.setTextColor(Color.parseColor("#2F334F"));
        legend.setDrawInside(true);
        legend.setWordWrapEnabled(true);
        //legend.setOrientation(Legend.LegendOrientation.VERTICAL);



    }


    //CLASS ALLOWS VALUES OF PIE CHART AS DECIMALS
    public class MyDecimalValueFormatter extends ValueFormatter{

        @Override
        public String getFormattedValue(float value) {
            return super.getFormattedValue(value);
        }

    }


    private float calcHours(QuerySnapshot result) {
       totalHours = 0;
        for (QueryDocumentSnapshot document: result){
            String sHour = document.getString("Activity_Hours");
            float xHours = Float.parseFloat(sHour);
            totalHours += xHours;
        }
        return totalHours;

    }


    //TODO: NEEDS WORK TO ADD MINUTES PROPERLY
    private float calcMins(QuerySnapshot result) {
        totalMins = 0;
        for (QueryDocumentSnapshot document: result){
            String sMins = document.getString("Activity_Mins");
            float xHours = Float.parseFloat(sMins);
            totalMins += xHours;
        }
        return totalMins;
    }




    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        //CLOSES THE DRAWER WHEN ITEM IS CLICKED
        drawerLayout.closeDrawer(GravityCompat.START);
        //CHECKS WHICH ITEM IS CLICKED
        switch (item.getItemId()) {
            case R.id.addActivity:
                startActivity(new Intent(this, AddActivity.class));
                finish();
                break;
            case R.id.logout:
                AlertDialog.Builder logOutDialog = new AlertDialog.Builder(this);
                logOutDialog.setTitle("Are you sure you want to logout?");
                logOutDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(getApplicationContext(), Splash.class));
                        finish();
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //close the dialog
                    }
                });
                logOutDialog.create().show();
                break;
            case R.id.cpdStandards:
                startActivity(new Intent(this, Standards.class));
                break;
            case R.id.activities:
                startActivity(new Intent(this, MyActivities.class));
                finish();
                break;
            case R.id.myAccount:
                startActivity(new Intent(this, MyAccount.class));
                break;
            case R.id.calendar:
                startActivity(new Intent(this, Calendar.class));
                break;
            case R.id.auditBuilder:
                startActivity(new Intent(this, AuditHome.class));
                break;

            default:
                Toast.makeText(this, "Coming soon", Toast.LENGTH_SHORT).show();
        }
        return false;
    }


}