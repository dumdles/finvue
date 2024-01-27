package com.sp.finvue;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class EditProfile extends AppCompatActivity {

    private EditText displayName;
    private EditText newPassword;
    private TextInputLayout textInputLayout;

    private FirebaseAuth mAuth;
    private FirebaseFirestore fStore;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        displayName = findViewById(R.id.editTextDisplayName);
        newPassword = findViewById(R.id.editTextNewPassword);

        // Set initial display name value
        setInitialDisplayName();

        Button saveChangesBtn = findViewById(R.id.btnSaveChanges);
        saveChangesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveChanges();
            }
        });
    }

    private void setInitialDisplayName() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            userID = user.getUid();
            DocumentReference documentReference = fStore.collection("users").document(userID);
            documentReference.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    String userDisplayName = task.getResult().getString("name");
                    if (userDisplayName != null) {
                        displayName.setText(userDisplayName);
                    }
                }
            });
        }
    }

    private void saveChanges() {
        String newDisplayName = displayName.getText().toString().trim();
        String newPasswordValue = newPassword.getText().toString().trim();

        // Update display name and birthday in Firestore
        updateFirestore(newDisplayName);

        // Update password if a new one is provided
        if (!newPasswordValue.isEmpty()) {
            updatePassword(newPasswordValue);
        }

        Snackbar.make(findViewById(android.R.id.content), "Changes saved successfully", Snackbar.LENGTH_SHORT).show();
        Intent intent = new Intent(EditProfile.this, ProfilePage.class);
        startActivity(intent);
        finish(); // Close the current activity
    }

    private void updateFirestore(String newDisplayName) {
        DocumentReference documentReference = fStore.collection("users").document(userID);
        Map<String, Object> updates = new HashMap<>();

        if (!newDisplayName.isEmpty()) {
            updates.put("name", newDisplayName);
        }

        documentReference.update(updates);
    }

    private void updatePassword(String newPasswordValue) {

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            user.updatePassword(newPasswordValue)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(EditProfile.this, "Password updated successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(EditProfile.this, "Failed to update password", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

}