package com.sp.finvue;

import androidx.appcompat.app.AppCompatActivity;

import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.textfield.TextInputEditText;

import java.security.MessageDigestSpi;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NewTransaction extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_transaction);

        // Assuming you have references to your toggle group and input layouts
        MaterialButtonToggleGroup toggleGroup = findViewById(R.id.toggleGroup);
        LinearLayout informationInputs = findViewById(R.id.informationInputs);
        LinearLayout remarksInputs = findViewById(R.id.remarksInputs);

        toggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                // Check which button is selected
                if (checkedId == R.id.infoTab) {
                    // Show information inputs
                    informationInputs.setVisibility(View.VISIBLE);
                    remarksInputs.setVisibility(View.GONE);
                } else if (checkedId == R.id.remarksTab) {
                    // Show remarks inputs
                    informationInputs.setVisibility(View.GONE);
                    remarksInputs.setVisibility(View.VISIBLE);
                }
            }
        });

        // Close button functionality
        ImageButton closeButton = findViewById(R.id.closeButton);

        // Set click listener for the X button
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close the activity and return to the previous fragment
                finish();
            }
        });

        // Date picker functionality
        TextInputEditText datePicker = findViewById(R.id.transactionDate);

        datePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaterialDatePicker<Long>materialDatePicker =
                        MaterialDatePicker.Builder.datePicker()
                                .setTitleText("Select transaction date")
                                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                                .build();
                materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
                    @Override
                    public void onPositiveButtonClick(Long selection) {
                        String date = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date(selection));
                        datePicker.setText(MessageFormat.format("{0}", date));
                    }
                });
                materialDatePicker.show(getSupportFragmentManager(), "tag");
            }
        });

    }
}