package com.example.cpd.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.cpd.MainActivity;
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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class EditActivity extends AppCompatActivity {

    Intent data;
    EditText editActivityName, editActivityDescription, editActivityType, editActivityDate, editActivityHours,
    editActivityMins, editActivityRef1, editActivityRef2, editActivityRef3, editActivityRef4;
    ImageView editImgPrev;
    FirebaseFirestore fStore;
    ProgressBar spinner;
    FirebaseUser user;
    Button editPicBtn, editGalleryBtn;
    String currentPhotoPath;
    StorageReference storageReference;

    public static final int CAMERA_PERMISSION_CODE = 101;
    public static final int CAMERA_REQUEST_CODE = 102;
    private static final int CHOOSE_IMAGE = 1;
    private Uri imgUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        data = getIntent();

        //INITIALIZE FIREBASE
        fStore = fStore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference();

        editActivityName = findViewById(R.id.editActivityName);
        editActivityDescription = findViewById(R.id.editActivityDescription);
        editActivityType = findViewById(R.id.editActivityType);
        editActivityDate = findViewById(R.id.editActivityDate);
        editActivityHours = findViewById(R.id.editActivityHours);
        editActivityMins = findViewById(R.id.editActivityMins);
        editActivityRef1 = findViewById(R.id.editActivityRef1);
        editActivityRef2 = findViewById(R.id.editActivityRef2);
        editActivityRef3 = findViewById(R.id.editActivityRef3);
        editActivityRef4 = findViewById(R.id.editActivityRef4);
        editImgPrev = findViewById(R.id.editImgPreview);

        editPicBtn = findViewById(R.id.editPicBtn);
        editGalleryBtn = findViewById(R.id.editGalleryBtn);

        spinner = findViewById(R.id.progressBar2);

        //DISPLAYS ALL CURRENT ACTIVITY INFORMATION ON SCREEN
        String activityName = data.getStringExtra("Activity_Name");
        String activityDescription = data.getStringExtra("Activity_Description");
        String activityDate = data.getStringExtra("Activity_Date");
        String activityHours = data.getStringExtra("Activity_Hours");
        String activityType = data.getStringExtra("Activity_Type");
        String activityMins = data.getStringExtra("Activity_Mins");
        String activityRef1 = data.getStringExtra("Activity_Ref1");
        String activityRef2 = data.getStringExtra("Activity_Ref2");
        String activityRef3 = data.getStringExtra("Activity_Ref3");
        String activityRef4 = data.getStringExtra("Activity_Ref4");
        String imgUrl = data.getStringExtra("Image_URL");
        Picasso.get().load(imgUrl).into(editImgPrev);

        editPicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askCameraPermission();
            }
        });

        editGalleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChoice();
            }
        });

        //SETS NEW INFORMATION TO WHAT IS ENTERED BY USER
        editActivityName.setText(activityName);
        editActivityDescription.setText(activityDescription);
        editActivityType.setText(activityType);
        editActivityDate.setText(activityDate);
        editActivityHours.setText(activityHours);
        editActivityMins.setText(activityMins);
        editActivityRef1.setText(activityRef1);
        editActivityRef2.setText(activityRef2);
        editActivityRef3.setText(activityRef3);
        editActivityRef4.setText(activityRef4);

        //SAVE BUTTON SENDS CHANGES TO FIREBASE
        FloatingActionButton fab = findViewById(R.id.saveEditBtn);
        fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    saveChangesToFirebase();
                }
            }
        );
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
                Picasso.get().load(imgUrl).into(editImgPrev);


            }
        }
        if (requestCode == CHOOSE_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imgUrl = data.getData();

            Picasso.get().load(imgUrl).into(editImgPrev);
        }

    }

    //METHODS FOR IF USERS SELECTED GALLERY BUTTON
    private void showFileChoice() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction((Intent.ACTION_GET_CONTENT));
        startActivityForResult(intent, CHOOSE_IMAGE);
    }

    //METHODS FOR IF USER WANTS TO TAKE NEW PICTURE
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

    //TODO: THIS IS SUPPOSED TO CREATE THE FILE IN USERS GALLERY IF THEY TAKE A PICTURE BUT IT DOESN'T WORK - REPEAT FROM ADD ACTIVITY
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

    //TODO: NOT SURE IF I NEED THIS? - REPEAT FROM ADD ACTIVITY
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

    private void saveChangesToFirebase() {
        //CREATE FILE NAME FOR IMAGE TO SAVE TO FIRE STORE
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = timeStamp + ".JPEG";

        final StorageReference image = storageReference.child(user.getUid() + "/cpdDocuments/" + imageFileName);

        UploadTask uploadTask = image.putFile(imgUrl);

        spinner.setVisibility(View.VISIBLE);

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String downloadURL = uri.toString();
                        Log.d("TAG", "Download url is: " + downloadURL);

                        String eName = editActivityName.getText().toString();
                        String eDesc = editActivityDescription.getText().toString();
                        String eRef1 = editActivityRef1.getText().toString();
                        String eRef2 = editActivityRef2.getText().toString();
                        String eRef3 = editActivityRef3.getText().toString();
                        String eRef4 = editActivityRef4.getText().toString();

                        //TODO: NEED TO BE CHANGED INTO SPINNERS/PICKER BUT EDIT TEXTS FOR NOW

                        String eType = editActivityType.getText().toString();
                        String eHours = editActivityHours.getText().toString();
                        String eMins = editActivityMins.getText().toString();
                        String eDate = editActivityDate.getText().toString();

                        if (eName.isEmpty() || eDesc.isEmpty() || eRef1.isEmpty() || eRef2.isEmpty()
                                || eRef3.isEmpty() || eRef4.isEmpty() || eType.isEmpty() || eHours.isEmpty()
                                || eMins.isEmpty() || eDate.isEmpty()) {
                            Toast.makeText(EditActivity.this, "Can not save activity with empty fields", Toast.LENGTH_SHORT).show();
                            return;
                        }


                        //SAVE EDITED NOTE
                        DocumentReference documentReference = fStore.collection("cpdActivities")
                                .document(user.getUid())
                                .collection("myCPD")
                                .document(data.getStringExtra("Activity_ID"));

                        Map<String, Object> activity = new HashMap<>();
                        activity.put("Activity_Name", eName);
                        activity.put("Activity_Date", eDate);
                        activity.put("Activity_Hours", eHours);
                        activity.put("Activity_Mins", eMins);
                        activity.put("Activity_Type", eType);
                        activity.put("Activity_Description", eDesc);
                        activity.put("Activity_Ref1", eRef1);
                        activity.put("Activity_Ref2", eRef2);
                        activity.put("Activity_Ref3", eRef3);
                        activity.put("Activity_Ref4", eRef4);
                        activity.put("Image_URL", downloadURL);

                        documentReference.update(activity).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(EditActivity.this, "Activity Edited", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(EditActivity.this, "Error, try again", Toast.LENGTH_SHORT).show();
                                spinner.setVisibility(View.INVISIBLE);
                            }
                        });
                    }
                });
            }
        });
    }


    //IMPLEMENTS THE CLOSE BUTTON IN TOP RIGHT HAND CORNER
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.close_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.close) {
            Toast.makeText(this, "Activity not saved", Toast.LENGTH_SHORT).show();
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }




}