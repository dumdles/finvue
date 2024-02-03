package com.sp.finvue;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONObject;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NewTransaction extends AppCompatActivity {

    private EditText transaction_Amount;
    private TextInputEditText transaction_Name;
    private TextInputEditText transaction_Category;
    private TextInputEditText transaction_Location;
    private TextInputEditText transaction_Mode;
    private TextInputEditText transaction_Date;
    private TextInputEditText transaction_Remarks;

    private static final String TAG = "newtransaction";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_transaction);

        transaction_Amount = findViewById(R.id.transactionAmount);
        transaction_Name = findViewById(R.id.transactionName);
        transaction_Category = findViewById(R.id.transactionCategory);
        transaction_Location = findViewById(R.id.transactionLocation);
        transaction_Mode = findViewById(R.id.transactionMode);
        transaction_Date = findViewById(R.id.transactionDate);
        transaction_Remarks = findViewById(R.id.transactionRemarks);

        // Assuming you have references to your toggle group and input layouts
        MaterialButtonToggleGroup toggleGroup = findViewById(R.id.toggleGroup);
        LinearLayout informationInputs = findViewById(R.id.informationInputs);
        LinearLayout remarksInputs = findViewById(R.id.remarksInputs);

        MaterialButton infoTabButton = findViewById(R.id.infoTab);
        infoTabButton.setChecked(true);
        informationInputs.setVisibility(View.VISIBLE);
        remarksInputs.setVisibility(View.GONE);

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
        TextView savebtn = findViewById(R.id.saveButton);

        // Set click listener for the X button
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close the activity and return to the previous fragment
                finish();
            }
        });

        savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String amountString = transaction_Amount.getText().toString();
                String amountstr = amountString.replaceAll("[^\\d.]", "");  // Remove $ sign

                String transactionid = UUID.randomUUID().toString();
                String namestr = transaction_Name.getText().toString();
                String categorystr = transaction_Category.getText().toString();
                String locationstr = transaction_Location.getText().toString();
                String modestr = transaction_Mode.getText().toString();
                String datestr = transaction_Date.getText().toString();
                String remarkstr = transaction_Remarks.getText().toString();

//                Log.d(TAG, transactionid + categorystr + amountstr + datestr + locationstr + modestr + namestr + remarkstr);

                if (transactionid != null) {
                    insertTransactionVolley(transactionid, categorystr, amountstr, datestr, locationstr, modestr, namestr, remarkstr);
                }
                startActivity(new Intent(NewTransaction.this, HomeActivity.class));
                finish();
            }
        });

    }
    // insert new transaction
    private void insertTransactionVolley(String transactionUUID, String category, String cost, String date, String location, String paymentmethod, String name, String remarks) {
        // Create a JSON object from the parameters
        Map<String, String> params = new HashMap<String, String>();

        LocalTime current_time = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        String formatted_time = current_time.format(formatter);

        params.put("transaction_id", transactionUUID);
        params.put("category", category);
        params.put("cost", cost);
        params.put("date", date);
        params.put("location", location);
        params.put("mop", paymentmethod);
        params.put("name", name);
        params.put("remarks", remarks);
        params.put("submission_time", formatted_time);

        JSONObject postdata = new JSONObject(params);
        RequestQueue queue = Volley.newRequestQueue(this);
        String transactionurl = TransactionVolleyHelper.transaction_url;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, transactionurl, postdata,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Handle successful response
                        Log.d("POST Request", "Success: " + response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                        Log.e("POST Request", "Error: " + error.toString());
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() {
                return TransactionVolleyHelper.getHeader();
            }
        };
        // add JsonObjectRequest to the RequestQueue
        queue.add(jsonObjectRequest);
    }

    private void insertUserTransactionVolley() {

    }

}