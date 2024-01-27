package com.sp.finvue;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthWebException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ProfilePage extends AppCompatActivity {

    private FirebaseFirestore fStore;
    private FirebaseAuth mAuth;
    private String userID;

    private TextView displayNameTextView;
    private TextView emailTextView;
    private TextView joinDateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);

        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        displayNameTextView = findViewById(R.id.display_name);
        emailTextView = findViewById(R.id.user_email);
        joinDateTextView = findViewById(R.id.join_date);

        Button editProfile = findViewById(R.id.edit_profile_btn);
        LinearLayout signOut = findViewById(R.id.sign_out_btn);

        // Get the current user
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            userID = user.getUid();
            // Fetch user data from Firestore
            fetchUserData();
        }

        // Open the Edit Profile activity when button is selected
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfilePage.this, EditProfile.class);
                startActivity(intent);
            }
        });

        // Sign-out functionality
        signOut.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                // Redirect to the login page
                Intent intent = new Intent(ProfilePage.this, LoginActivity.class);
                startActivity(intent);
                finish(); // Close the current activity
            }
        });
    }

    private void fetchUserData() {
        DocumentReference documentReference = fStore.collection("users").document(userID);
        documentReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Retrieve user data and update UI
                String displayName = task.getResult().getString("name");
                String email = task.getResult().getString("email");
                String joinDate = task.getResult().getString("joinDate");

                displayNameTextView.setText(displayName);
                emailTextView.setText(email);

                if (joinDate != null) {
                    // Format the joinDate using SimpleDateFormat
                    try {
                        SimpleDateFormat inputFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                        Date date = inputFormat.parse(joinDate);

                        SimpleDateFormat outputFormat = new SimpleDateFormat("d MMMM yyyy", Locale.getDefault());
                        joinDateTextView.setText("Joined " + outputFormat.format(date));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}