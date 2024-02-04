package com.sp.finvue;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity {
    private BottomNavigationView navView;
    private HomepageFragment homepageFragment;
    private StatisticsFragment statisticsFragment;
    private AddFragment addFragment;
    private SpendingFragment spendingFragment;
    private NewsFragment newsFragment;
    private FragmentManager fragmentManager = getSupportFragmentManager();
    private String userID;
    private FirebaseAuth mAuth;
    FirebaseFirestore fStore;

    DrawerLayout drawerLayout;
    ImageButton buttonDrawerToggle;
    NavigationView navigationView;

    CircleImageView userImage;
    TextView textUsername;
    TextView textEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        navView = findViewById(R.id.bottom_navview);
        navView.setOnItemSelectedListener(itemSelected);
        homepageFragment = new HomepageFragment();
        statisticsFragment = new StatisticsFragment();
        addFragment = new AddFragment();
        spendingFragment = new SpendingFragment();
        newsFragment = new NewsFragment();

        drawerLayout = findViewById(R.id.drawerLayout);
        buttonDrawerToggle = findViewById(R.id.buttonDrawerToggle);
        navigationView = findViewById(R.id.navigationView);

        // Initialize Firebase Firestore instance
        fStore = FirebaseFirestore.getInstance();

        // Initialize FirebaseAuth instance
        mAuth = FirebaseAuth.getInstance();

        View headerView = navigationView.getHeaderView(0); // Get the header view
        CircleImageView userImage = headerView.findViewById(R.id.user_image); // Find the ImageView in the header
        TextView textUsername = headerView.findViewById(R.id.textUsername);
        TextView textEmail = headerView.findViewById(R.id.textEmail);

        // Get the current user
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            userID = user.getUid();
            // Fetch user data from Firestore
            fetchUserData();
        }

        // Set click listener on the header view
        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, ProfilePage.class);
                startActivity(intent);
            }
        });

        buttonDrawerToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.open();
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.navHome) {
                    fragmentManager.beginTransaction()
                            .replace(R.id.fragmentContainer, homepageFragment)
                            .setReorderingAllowed(true)
                            .addToBackStack(null)
                            .commit();
                    drawerLayout.closeDrawer(GravityCompat.START);
                    return true;
                } else if (id == R.id.navStatistics) {
                    fragmentManager.beginTransaction()
                            .replace(R.id.fragmentContainer, statisticsFragment)
                            .setReorderingAllowed(true)
                            .addToBackStack(null)
                            .commit();
                    drawerLayout.closeDrawer(GravityCompat.START);
                    return true;
                } else if (id == R.id.navSpendings) {
                    fragmentManager.beginTransaction()
                            .replace(R.id.fragmentContainer, spendingFragment)
                            .setReorderingAllowed(true)
                            .addToBackStack(null)
                            .commit();
                    drawerLayout.closeDrawer(GravityCompat.START);
                    return true;
                } else if (id == R.id.navArticles) {
                    fragmentManager.beginTransaction()
                            .replace(R.id.fragmentContainer, newsFragment)
                            .setReorderingAllowed(true)
                            .addToBackStack(null)
                            .commit();
                    drawerLayout.closeDrawer(GravityCompat.START);
                    return true;
                } else if (id == R.id.navSettings) {
                    Intent intent = new Intent(HomeActivity.this, ProfilePage.class);
                    startActivity(intent);
                    drawerLayout.closeDrawer(GravityCompat.START);
                    return true;
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (fragmentManager.getBackStackEntryCount() > 0) {
            // If there are fragments in the back stack, pop the top one
            fragmentManager.popBackStack();
        } else {
            // If the back stack is empty, perform the default back action
            super.onBackPressed();
        }
        updateNavigationBar(); // Call a method to update the navigation bar based on the current fragment
    }

    public void updateNavigationItemSelected(int itemId) {
        navView.setSelectedItemId(itemId);
    }

    private void updateNavigationBar() {
        // Check the current fragment and update the navbar
        int currentItem = R.id.homepage;
        if (fragmentManager.getBackStackEntryCount() > 0) {
            // Get the current fragment from the top of the back stack
            String fragmentTag = fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 1).getName();
            if (fragmentTag != null) {
                switch (fragmentTag) {
                    case "homepage":
                        currentItem = R.id.homepage;
                        break;
                    case "stats":
                        currentItem = R.id.stats;
                        break;
                    case "add":
                        currentItem = R.id.add;
                        break;
                    case "spendings":
                        currentItem = R.id.spendings;
                        break;
                    case "news":
                        currentItem = R.id.news;
                        break;

                }
            }
        }

        // Update the selected item in the navigation bar
        navView.setSelectedItemId(currentItem);
    }


    private void fetchUserData() {
        View headerView = navigationView.getHeaderView(0); // Get the header view
        CircleImageView userImage = headerView.findViewById(R.id.user_image); // Find the ImageView in the header
        TextView textUsername = headerView.findViewById(R.id.textUsername);
        TextView textEmail = headerView.findViewById(R.id.textEmail);

        DocumentReference documentReference = fStore.collection("users").document(userID);
        documentReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Retrieve user data and update UI
                String displayName = task.getResult().getString("name");
                String email = task.getResult().getString("email");

                textUsername.setText(displayName);
                textEmail.setText(email);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        invalidateOptionsMenu();
        if (!fragmentManager.isStateSaved()) {

            fragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, homepageFragment)
                    .setReorderingAllowed(true)
                    .addToBackStack(null)
                    .commit();
        }


    }

    NavigationBarView.OnItemSelectedListener itemSelected = new NavigationBarView.OnItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            int id = item.getItemId();
            if (id == R.id.homepage) {
                fragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, homepageFragment)
                        .setReorderingAllowed(true)
                        .addToBackStack(null)
                        .commit();
                return true;
            } else if (id == R.id.stats) {
                fragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, statisticsFragment)
                        .setReorderingAllowed(true)
                        .addToBackStack(null)
                        .commit();
                return true;
            } else if (id == R.id.add) {
                fragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, addFragment)
                        .setReorderingAllowed(true)
                        .addToBackStack(null)
                        .commit();
                return true;
            } else if (id == R.id.spendings) {
                fragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, spendingFragment)
                        .setReorderingAllowed(true)
                        .addToBackStack(null)
                        .commit();
                return true;
            } else if (id == R.id.news) {
                fragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, newsFragment)
                        .setReorderingAllowed(true)
                        .addToBackStack(null)
                        .commit();
                return true;
            }
            return false;
        }
    };




}