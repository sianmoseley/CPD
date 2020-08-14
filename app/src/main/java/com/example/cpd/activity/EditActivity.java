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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cpd.MainActivity;
import com.example.cpd.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
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

    TextInputEditText editActivityName, editActivityDescription, editActivityRef1, editActivityRef2, editActivityRef3, editActivityRef4;

    TextInputLayout editActivityHoursLayout, editActivityMinsLayout, editActivityTypeLayout;
    AutoCompleteTextView editActivityHours, editActivityMins, editActivityType;

    TextView editActivityDate;

    ImageView editImgPrev;
    FirebaseFirestore fStore;
    ProgressBar spinner;
    FirebaseUser user;
    Button editPicBtn, editGalleryBtn, editActivityDateBtn;
    String currentPhotoPath;
    String editedType = "";
    String editedHours = "";
    String editedMins = "";
    StorageReference storageReference;

    public static final int CAMERA_PERMISSION_CODE = 101;
    public static final int CAMERA_REQUEST_CODE = 102;
    private static final int CHOOSE_IMAGE = 1;
    private Uri imgUrl;

    String activityTypeString, activityHoursString, activityMinsString;



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
        String activityRef1 = data.getStringExtra("Activity_Ref1");
        String activityRef2 = data.getStringExtra("Activity_Ref2");
        String activityRef3 = data.getStringExtra("Activity_Ref3");
        String activityRef4 = data.getStringExtra("Activity_Ref4");
        String imgUrl = data.getStringExtra("Image_URL");
        Picasso.get().load(imgUrl).into(editImgPrev);

        //DATE PICKER
        editActivityDateBtn = findViewById(R.id.editActivityDateBtn);
        editActivityDate = findViewById(R.id.editActivityDate);

        //MATERIAL DATE BUILDER
        MaterialDatePicker.Builder builder = MaterialDatePicker.Builder.datePicker();
        builder.setTitleText("Select a date");
        final MaterialDatePicker materialDatePicker = builder.build();


        editActivityDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialDatePicker.show(getSupportFragmentManager(), "DATE_PICKER");
            }
        });

        materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
            @Override
            public void onPositiveButtonClick(Object selection) {
                editActivityDate.setText(materialDatePicker.getHeaderText());
            }
        });

        //CPD EDITED HOURS DROPDOWN MENU
        editActivityHoursLayout = findViewById(R.id.editActivityHoursLayout);
        editActivityHours = findViewById(R.id.editActivityHours);

        String[] hours = new String[]{
                "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"
        };

        final ArrayAdapter<String> editAdapterHours = new ArrayAdapter<>(
                EditActivity.this,
                R.layout.dropdown_item,
                hours
        );

        editActivityHours.setAdapter(editAdapterHours);

        //TO CAPTURE USER SELECTION FROM DROP DOWN LIST
        ((AutoCompleteTextView)editActivityHoursLayout.getEditText()).setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                editAdapterHours.getItem(position);
                editedHours = ((AutoCompleteTextView)editActivityHoursLayout.getEditText()).getText().toString();
                Log.d("TAG", "selected hours is: " + editedHours);
            }
        });

        //CPD EDITED MINS DROPDOWN MENU
        editActivityMinsLayout = findViewById(R.id.editActivityMinsLayout);
        editActivityMins = findViewById(R.id.editActivityMins);

        String[] mins = new String[]{
                "0", "15", "30", "45"
        };

        final ArrayAdapter<String> editAdapterMins = new ArrayAdapter<>(
                EditActivity.this,
                R.layout.dropdown_item,
                mins
        );

        editActivityMins.setAdapter(editAdapterMins);

        //TO CAPTURE USER SELECTION FROM DROP DOWN LIST
        ((AutoCompleteTextView)editActivityMinsLayout.getEditText()).setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                editAdapterMins.getItem(position);
                editedMins = ((AutoCompleteTextView)editActivityMinsLayout.getEditText()).getText().toString();
                Log.d("TAG", "selected mins is: " + editedMins);
            }
        });


        //CPD TYPE DROPDOWN MENU
        editActivityTypeLayout = findViewById(R.id.editActivityTypeLayout);
        editActivityType = findViewById(R.id.editActivityType);

        final String[] type = new String[]{
                "Formal Education Completed", "Other Completed", "Professional Activities", "Self-Directed Learning", "Work-Based Learning"
        };

        final ArrayAdapter<String> editAdapterType = new ArrayAdapter<>(
                EditActivity.this,
                R.layout.dropdown_item,
                type
        );

        editActivityType.setAdapter(editAdapterType);

        //TO CAPTURE USER SELECTION FROM DROP DOWN LIST
        ((AutoCompleteTextView)editActivityTypeLayout.getEditText()).setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                editAdapterType.getItem(position);
                editedType = ((AutoCompleteTextView)editActivityTypeLayout.getEditText()).getText().toString();
                Log.d("TAG", "selected type is: " + editedType);
            }
        });


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
        editActivityDate.setText(activityDate);

        AutoCompleteTextView activityType = (AutoCompleteTextView)editActivityTypeLayout.getEditText();
        AutoCompleteTextView activityHours = (AutoCompleteTextView)editActivityHoursLayout.getEditText();
        AutoCompleteTextView activityMins = (AutoCompleteTextView)editActivityMinsLayout.getEditText();

        activityTypeString = data.getStringExtra("Activity_Type");
        activityHoursString = data.getStringExtra("Activity_Hours");
        activityMinsString = data.getStringExtra("Activity_Mins");

        activityType.setAdapter(editAdapterType);
        activityType.setText(activityTypeString, false);

        activityHours.setAdapter(editAdapterHours);
        activityHours.setText(activityHoursString, false);

        activityMins.setAdapter(editAdapterMins);
        activityMins.setText(activityMinsString, false);

        editActivityDescription.setText(activityDescription);
        editActivityRef1.setText(activityRef1);
        editActivityRef2.setText(activityRef2);
        editActivityRef3.setText(activityRef3);
        editActivityRef4.setText(activityRef4);

        //SAVE BUTTON SENDS CHANGES TO FIREBASE
        FloatingActionButton fab = findViewById(R.id.saveEditBtn);
        fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (editedType.trim().length() != 0 && editedHours.trim().length() != 0 && editedMins.trim().length() !=0){
                        saveChangesToFirebase();
                    }
                    else {
                        if (editedType.trim().length() == 0){
                            editActivityTypeLayout.setError("Please select Activity Type");
                        }
                        if (editedHours.trim().length() == 0){
                            editActivityHoursLayout.setError("Please select Hours");
                        }
                        if (editedMins.trim().length() == 0){
                            editActivityMinsLayout.setError("Please select Mins");
                        }
                    }
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

        if (imgUrl != null){
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

                            String editedActivityName = editActivityName.getText().toString();
                            String editedActivityDate = editActivityDate.getText().toString();
                            String editedActivityDescription = editActivityDescription.getText().toString();
                            String editedRef1 = editActivityRef1.getText().toString();
                            String editedRef2 = editActivityRef2.getText().toString();
                            String editedRef3 = editActivityRef3.getText().toString();
                            String editedRef4 = editActivityRef4.getText().toString();

                            if (editedActivityName.isEmpty() || editedActivityDescription.isEmpty() || editedRef1.isEmpty() || editedRef2.isEmpty() || editedRef3.isEmpty() || editedRef4.isEmpty()){
                                Toast.makeText(EditActivity.this, "Can not save activity with empty fields", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            if (editedHours == null) {
                                editedHours = activityHoursString;
                            }

                            if (editedMins == null) {
                                editedMins = activityMinsString;
                            }

                            if (editedType == null) {
                                editedType = activityTypeString;
                            }



                            //SAVED EDITED NOTE
                            DocumentReference documentReference = fStore.collection("cpdActivities")
                                    .document(user.getUid())
                                    .collection("myCPD")
                                    .document(data.getStringExtra("Activity_ID"));

                            Map<String, Object> editActivity = new HashMap<>();
                            editActivity.put("Activity_Name", editedActivityName);
                            editActivity.put("Activity_Date", editedActivityDate);
                            editActivity.put("Activity_Hours", editedHours);
                            editActivity.put("Activity_Mins", editedMins);
                            editActivity.put("Activity_Type", editedType);
                            editActivity.put("Activity_Description", editedActivityDescription);
                            editActivity.put("Activity_Ref1", editedRef1);
                            editActivity.put("Activity_Ref2", editedRef2);
                            editActivity.put("Activity_Ref3", editedRef3);
                            editActivity.put("Activity_Ref4", editedRef4);
                            editActivity.put("Image_URL", downloadURL);

                            documentReference.update(editActivity).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(EditActivity.this, "Activity Edited", Toast.LENGTH_SHORT).show();
                                    //startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                    onBackPressed();
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
        } else {

            String editedActivityName = editActivityName.getText().toString();
            String editedActivityDate = editActivityDate.getText().toString();
            String editedActivityDescription = editActivityDescription.getText().toString();
            String editedRef1 = editActivityRef1.getText().toString();
            String editedRef2 = editActivityRef2.getText().toString();
            String editedRef3 = editActivityRef3.getText().toString();
            String editedRef4 = editActivityRef4.getText().toString();

            if (editedActivityName.isEmpty() || editedActivityDescription.isEmpty() || editedRef1.isEmpty() || editedRef2.isEmpty() || editedRef3.isEmpty() || editedRef4.isEmpty()){
                Toast.makeText(EditActivity.this, "Can not save activity with empty fields", Toast.LENGTH_SHORT).show();
                return;
            }

           if (editedHours == null) {
               editedHours = activityHoursString;
           }

            if (editedMins == null) {
                editedMins = activityMinsString;
            }

            if (editedType == null) {
                editedType = activityTypeString;
            }

            //SAVED EDITED NOTE
            DocumentReference documentReference = fStore.collection("cpdActivities")
                    .document(user.getUid())
                    .collection("myCPD")
                    .document(data.getStringExtra("Activity_ID"));

            Map<String, Object> editActivity = new HashMap<>();
            editActivity.put("Activity_Name", editedActivityName);
            editActivity.put("Activity_Date", editedActivityDate);
            editActivity.put("Activity_Hours", editedHours);
            editActivity.put("Activity_Mins", editedMins);
            editActivity.put("Activity_Type", editedType);
            editActivity.put("Activity_Description", editedActivityDescription);
            editActivity.put("Activity_Ref1", editedRef1);
            editActivity.put("Activity_Ref2", editedRef2);
            editActivity.put("Activity_Ref3", editedRef3);
            editActivity.put("Activity_Ref4", editedRef4);
            editActivity.put("Image_URL", "No Image Selected");

            documentReference.update(editActivity).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(EditActivity.this, "Activity Edited", Toast.LENGTH_SHORT).show();
                    //startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    onBackPressed();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(EditActivity.this, "Error, try again", Toast.LENGTH_SHORT).show();
                    spinner.setVisibility(View.INVISIBLE);
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.close) {
            Toast.makeText(this, "Activity not saved", Toast.LENGTH_SHORT).show();
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }




}