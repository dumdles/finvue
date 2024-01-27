package com.sp.finvue;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;

public class ProfilePage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);

        Button editProfile = findViewById(R.id.edit_profile_btn);
        LinearLayout signOut = findViewById(R.id.sign_out_btn);

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
}