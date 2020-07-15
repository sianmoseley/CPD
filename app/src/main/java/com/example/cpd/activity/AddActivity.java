package com.example.cpd.activity;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.example.cpd.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AddActivity extends AppCompatActivity {

    EditText cpdName, cpdDes, cpdRef1, cpdRef2, cpdRef3, cpdRef4;
    Spinner cpdTypeSpinner, cpdMinuteSpinner, cpdHourSpinner;
    TextView mDate;
    DatePickerDialog.OnDateSetListener mDateSetListener;
    Button addDocBtn, galleryBtn;
    ProgressBar progressBarSave;
    FirebaseFirestore fStore;
    String selectedType, selectedMins, selectedHours, selectedDate;
    FirebaseUser user;
    ImageView selectedImage;
    StorageReference storageReference;
    String currentPhotoPath;
    ImageView imgPreview;
    FloatingActionButton fab;

    public static final int CAMERA_PERMISSION_CODE = 101;
    public static final int CAMERA_REQUEST_CODE = 102;
    private static final int CHOOSE_IMAGE = 1;
    private Uri imgUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //INITIALIZE FIREBASE
        fStore = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference();

        imgPreview = findViewById(R.id.imgPreview);

        progressBarSave = findViewById(R.id.progressBar);

        cpdName = findViewById(R.id.addActivityName);
        cpdDes = findViewById(R.id.addActivityDescription);
        cpdRef1 = findViewById(R.id.addReflection1);
        cpdRef2 = findViewById(R.id.addReflection2);
        cpdRef3 = findViewById(R.id.addReflection3);
        cpdRef4 = findViewById(R.id.addReflection4);

        addDocBtn = findViewById(R.id.addDocBtn);
        galleryBtn = findViewById(R.id.galleryBtn);
        selectedImage = findViewById(R.id.supportingDoc);

        fab = findViewById(R.id.fab);



        //DATE PICKER
        mDate = findViewById(R.id.cpdDate);
        mDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(AddActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                selectedDate = dayOfMonth + "/" + month + "/" + year;
                mDate.setText(selectedDate);
            }
        };

        //CPD HOUR SPINNER
        cpdHourSpinner = findViewById(R.id.cpdHours);
        ArrayAdapter<CharSequence> adapterHours = ArrayAdapter.createFromResource(this,
                R.array.cpdHours, android.R.layout.simple_spinner_item);
        //layout for the list of choices to appear
        adapterHours.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cpdHourSpinner.setAdapter(adapterHours);

        cpdHourSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedHours = parent.getItemAtPosition(position).toString();
                //Toast.makeText(AddActivity.this, selectedHours, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //CPD MINUTE SPINNER
        cpdMinuteSpinner = findViewById(R.id.cpdMins);
        ArrayAdapter<CharSequence> adapterMins = ArrayAdapter.createFromResource(this,
                R.array.cpdMins, android.R.layout.simple_spinner_item);
        //layout for the list of choices to appear
        adapterMins.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cpdMinuteSpinner.setAdapter(adapterMins);

        cpdMinuteSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedMins = parent.getItemAtPosition(position).toString();
                //Toast.makeText(AddActivity.this, selectedMins, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //CPD TYPE SPINNER
        cpdTypeSpinner = findViewById(R.id.cpdType);
        ArrayAdapter<CharSequence> adapterType = ArrayAdapter.createFromResource(this,
                R.array.cpdType, android.R.layout.simple_spinner_item);
        //layout for the list of choices to appear
        adapterType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cpdTypeSpinner.setAdapter(adapterType);

        cpdTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedType = parent.getItemAtPosition(position).toString();
                //Toast.makeText(AddActivity.this, selectedType, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        addDocBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askCameraPermission();
            }
        });

        galleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChoice();
            }
        });


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadToFirebase();
            }
        });
    }

    //ALLOWS USER TO SELECT IMAGE FROM GALLERY
    private void showFileChoice() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction((Intent.ACTION_GET_CONTENT));
        startActivityForResult(intent, CHOOSE_IMAGE);
    }

    //ASKS FOR CAMERA PERMISSION
    private void askCameraPermission() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        } else {
            dispatchTakePictureIntent();
        }
    }

    //CHECKS RESULT OF PERMISSION REQUEST
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == CAMERA_PERMISSION_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                dispatchTakePictureIntent();
            }else {
                Toast.makeText(this, "Camera Permission is Required to Use Camera", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //IF IMAGE SELECTED, DISPLAYS IN IMAGE VIEW
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                File f = new File(currentPhotoPath);
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                imgUrl = Uri.fromFile(f);
                mediaScanIntent.setData(imgUrl);
                this.sendBroadcast(mediaScanIntent);
                Picasso.get().load(imgUrl).into(imgPreview);


            }
        }
        if (requestCode == CHOOSE_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imgUrl = data.getData();

            Picasso.get().load(imgUrl).into(imgPreview);
        }

    }

    //TODO: NOT SURE IF I NEED THIS?
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        //at the moment, doesn't store photo taken in app to gallery due to function being deprecated in API 29
        //can't find the answer to replace it
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    //TODO: THIS IS SUPPOSED TO CREATE THE FILE IN USERS GALLERY IF THEY TAKE A PICTURE BUT IT DOESN'T WORK
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
            }
        }
    }

    private void uploadToFirebase() {
        if (imgUrl != null){
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = timeStamp + ".JPEG";

            final StorageReference image = storageReference.child(user.getUid() + "/cpdDocuments/" + imageFileName);

            UploadTask uploadTask = image.putFile(imgUrl);

            progressBarSave.setVisibility(View.VISIBLE);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String downloadURL = uri.toString();
                            Log.d("TAG", "Download url is: " + downloadURL);

                            String aName = cpdName.getText().toString();
                            String aDesc = cpdDes.getText().toString();
                            String aRef1 = cpdRef1.getText().toString();
                            String aRef2 = cpdRef2.getText().toString();
                            String aRef3 = cpdRef3.getText().toString();
                            String aRef4 = cpdRef4.getText().toString();

                            if (aName.isEmpty() || aDesc.isEmpty() || aRef1.isEmpty() || aRef2.isEmpty()
                                    || aRef3.isEmpty() || aRef4.isEmpty() || selectedDate.isEmpty() || selectedType.isEmpty() || selectedHours.isEmpty() || selectedMins.isEmpty()) {
                                Toast.makeText(AddActivity.this, "Can not save activity with empty fields", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            //IF CHECKS PASS, SAVE ACTIVITY TO FIREBASE
                            DocumentReference documentReference = fStore.collection("cpdActivities")
                                    .document(user.getUid())
                                    .collection("myCPD")
                                    .document();

                            Map<String, Object> activity = new HashMap<>();
                            activity.put("Activity_Name", aName);
                            activity.put("Activity_Date", selectedDate);
                            activity.put("Activity_Hours", selectedHours);
                            activity.put("Activity_Mins", selectedMins);
                            activity.put("Activity_Type", selectedType);
                            activity.put("Activity_Description", aDesc);
                            activity.put("Activity_Ref1", aRef1);
                            activity.put("Activity_Ref2", aRef2);
                            activity.put("Activity_Ref3", aRef3);
                            activity.put("Activity_Ref4", aRef4);
                            activity.put("Image_URL", downloadURL);

                            documentReference.set(activity).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(AddActivity.this, "Activity Added", Toast.LENGTH_SHORT).show();
                                    onBackPressed();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(AddActivity.this, "Error, try again", Toast.LENGTH_SHORT).show();
                                    progressBarSave.setVisibility(View.INVISIBLE);
                                }
                            });
                        }
                    }
                    );
                }
            }
            );
        }
        else {

            String aName = cpdName.getText().toString();
            String aDesc = cpdDes.getText().toString();
            String aRef1 = cpdRef1.getText().toString();
            String aRef2 = cpdRef2.getText().toString();
            String aRef3 = cpdRef3.getText().toString();
            String aRef4 = cpdRef4.getText().toString();

            if (aName.isEmpty() || aDesc.isEmpty() || aRef1.isEmpty() || aRef2.isEmpty()
                    || aRef3.isEmpty() || aRef4.isEmpty() || selectedDate.isEmpty() || selectedType.isEmpty() || selectedHours.isEmpty() || selectedMins.isEmpty()) {
                Toast.makeText(AddActivity.this, "Can not save activity with empty fields", Toast.LENGTH_SHORT).show();
                return;
            }

            DocumentReference documentReference = fStore.collection("cpdActivities")
                    .document(user.getUid())
                    .collection("myCPD")
                    .document();

            Map<String, Object> activity = new HashMap<>();
            activity.put("Activity_Name", aName);
            activity.put("Activity_Date", selectedDate);
            activity.put("Activity_Hours", selectedHours);
            activity.put("Activity_Mins", selectedMins);
            activity.put("Activity_Type", selectedType);
            activity.put("Activity_Description", aDesc);
            activity.put("Activity_Ref1", aRef1);
            activity.put("Activity_Ref2", aRef2);
            activity.put("Activity_Ref3", aRef3);
            activity.put("Activity_Ref4", aRef4);
            activity.put("Image_URL", "No Image Selected");

            documentReference.set(activity).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(AddActivity.this, "Activity Added", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AddActivity.this, "Error, try again", Toast.LENGTH_SHORT).show();
                    progressBarSave.setVisibility(View.INVISIBLE);
                }
            });
        }
    }

    //IMPLEMENTS THE CLOSE BUTTON IN TOP RIGHT HAND CORNER
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.close_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }
    //WHEN CLOSE BUTTON PRESSED, SENDS USER BACK TO PREVIOUS SCREEN
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.close) {
            Toast.makeText(this, "Activity not saved", Toast.LENGTH_SHORT).show();
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

}



