package com.example.cpd.account;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.cpd.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EditAccount extends AppCompatActivity {


    EditText editName, editEmail, editPassword;
    Button saveChangesBtn;
    ProgressBar progressBar4;

    FirebaseFirestore fStore;
    FirebaseUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_account);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        editName = findViewById(R.id.editName);
        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        saveChangesBtn = findViewById(R.id.saveChangesBtn);
        progressBar4 = findViewById(R.id.progressBar4);

        fStore = fStore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        final DocumentReference documentReference = fStore.collection("users").document(user.getUid());

        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()){
                        editName.setText(document.getString("fName"));
                        editEmail.setText(document.getString("email"));
                    }
                }
            }
        });

        saveChangesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String eName = editName.getText().toString();
                String eEmail = editEmail.getText().toString();

                if (eName.isEmpty() || eEmail.isEmpty()){
                    Toast.makeText(EditAccount.this, "Can not save with empty fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar4.setVisibility(View.VISIBLE);

                Map<String, Object> updateUser = new HashMap<>();
                updateUser.put("fName", eName);
                updateUser.put("email", eEmail);

                documentReference.update(updateUser).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(EditAccount.this, "User details updated in Firestore", Toast.LENGTH_SHORT).show();
                        progressBar4.setVisibility(View.GONE);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditAccount.this, "Error, try again" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        progressBar4.setVisibility(View.GONE);
                    }
                });



                user.updateEmail(eEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Log.d("TAG", "User email address updated");
                        } else {
                            Log.d("TAG","Changes not saved");
                        }
                    }
                });

            }
        });












    }

    //WHEN BACK BUTTON IS CLICKED, SEND THEM BACK TO PREVIOUS PAGE
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);

    }
}