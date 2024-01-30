package com.sp.finvue;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class HomeActivity extends AppCompatActivity {
    private BottomNavigationView navView;
    private HomepageFragment homepageFragment;
    private StatisticsFragment statisticsFragment;
    private AddFragment addFragment;
    private SpendingFragment spendingFragment;
    private NewsFragment newsFragment;
    private FragmentManager fragmentManager = getSupportFragmentManager();

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
    }

    @Override
    protected void onStart() {
        invalidateOptionsMenu();
        fragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, homepageFragment)
                .setReorderingAllowed(true)
                .addToBackStack(null)
                .commit();
        super.onStart();
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
}