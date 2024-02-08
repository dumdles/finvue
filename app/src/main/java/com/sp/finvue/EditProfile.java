package com.sp.finvue;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
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
    private ImageView profileImageView;

    private FirebaseAuth mAuth;
    private FirebaseFirestore fStore;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private String userID;

    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        displayName = findViewById(R.id.editTextDisplayName);
        profileImageView = findViewById(R.id.profile_image);
        newPassword = findViewById(R.id.editTextNewPassword);

        // Set initial display name value
        setInitialDisplayName();

        // Set the user ID
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            userID = user.getUid();
        }

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Button saveChangesBtn = findViewById(R.id.btnSaveChanges);
        saveChangesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveChanges();
            }
        });

        // Image to change profile picture
        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
            }
        });
    }

    private void openFileChooser() {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Ensure that there is a camera activity to handle the intent
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            // Create a file to save the image
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the file
                ex.printStackTrace();
            }

            // Continue only if the file was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            }
        }

        Intent chooserIntent = Intent.createChooser(galleryIntent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{cameraIntent});
        startActivityForResult(chooserIntent, PICK_IMAGE_REQUEST);
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        String currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();

            // Set the selected image to the profile image view
            profileImageView.setImageURI(imageUri);

            // Upload the image to Firebase Storage
            uploadImage(imageUri);
        }
    }


    private void uploadImage(Uri imageUri) {
        if (imageUri != null) {
            StorageReference fileReference = storageReference.child("profile_images/" + userID);
            fileReference.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        // Get the download URL of the uploaded image
                        fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                            // Update the user's profile picture URL in Firestore
                            DocumentReference documentReference = fStore.collection("users").document(userID);
                            documentReference.update("profile_image_url", uri.toString())
                                    .addOnSuccessListener(aVoid -> {
                                        // Image URL saved successfully
                                        Snackbar.make(findViewById(android.R.id.content), "Profile picture updated", Snackbar.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        // Handle any errors
                                        Snackbar.make(findViewById(android.R.id.content), "Failed to update profile picture", Snackbar.LENGTH_SHORT).show();
                                    });
                        });
                    })
                    .addOnFailureListener(e -> {
                        // Handle any errors
                        Snackbar.make(findViewById(android.R.id.content), "Upload failed: " + e.getMessage(), Snackbar.LENGTH_SHORT).show();
                    });
        }
    }

    private void setInitialDisplayName() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            userID = user.getUid();
            DocumentReference documentReference = fStore.collection("users").document(userID);
            documentReference.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String userDisplayName = document.getString("name");
                        if (userDisplayName != null) {
                            displayName.setText(userDisplayName);
                        }

                        String profileImageUrl = document.getString("profile_image_url");
                        if (profileImageUrl != null) {
                            // Load the profile picture using the URL
                            Glide.with(this)
                                    .load(profileImageUrl)
                                    .into(profileImageView);
                        }
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