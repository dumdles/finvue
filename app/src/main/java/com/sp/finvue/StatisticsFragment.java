package com.sp.finvue;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class StatisticsFragment extends Fragment {

    private FirebaseFirestore fStore;
    private FirebaseAuth mAuth;
    private String fbuserUUID, fbuserID;
    private int volleyResponseStatus;

    double totalCost = 0.0;
    double goalAmt = 0.0;
    int querycount = 0;
    BarChart barChart;

    double groceryAmt = 0;
    double transportAmt = 0;
    double shoppingAmt = 0;
    double foodAmt = 0;
    double entertainmentAmt = 0;
    double transferAmt = 0;
    double otherAmt = 0;

    private TextView statsfood, statstransport, statsentertainment, statsshopping, statstransfer, statsgroceries, statsother;

    public StatisticsFragment() {
        // Required empty public constructor
    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        // Get current user
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            fbuserID = user.getUid();
            // Fetch data from Firestore
            fetchUserData();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();


        // Get current user
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            fbuserID = user.getUid();
            // Fetch data from Firestore
            fetchUserData();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_statistics, container, false);
        statsfood = view.findViewById(R.id.stats_food);
        statstransport = view.findViewById(R.id.stats_transportation);
        statsentertainment = view.findViewById(R.id.stats_entertainment);
        statsshopping = view.findViewById(R.id.stats_shopping);
        statstransfer = view.findViewById(R.id.stats_transfer);
        statsgroceries = view.findViewById(R.id.stats_groceries);
        statsother = view.findViewById(R.id.stats_others);

        barChart = view.findViewById(R.id.stacked_barchart);

        barChart.setRotation(90);
        barChart.setFitBars(true);
        fetchUserData();
        return view;
    }

    private void fetchUserData() {
        DocumentReference documentReference = fStore.collection("users").document(fbuserID);
        documentReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                fbuserUUID = task.getResult().getString("useruuid");
            }
            getAllStatistics(fbuserUUID);


        });
        
    }

    private void getAllStatistics(String user_uuid) {
        String useruuidurl = TransactionVolleyHelper.transaction_url + user_uuid;
        RequestQueue queue1 = Volley.newRequestQueue(getContext());
        String usertableurl = TransactionVolleyHelper.user_url + user_uuid;
        RequestQueue queue2 = Volley.newRequestQueue(getContext());
        JsonObjectRequest jsonObjectRequest1 = new JsonObjectRequest(Request.Method.GET, useruuidurl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (volleyResponseStatus == 200) {
                            try {
                                int count = response.getInt("count");
                                if (count > 0) {
                                    JSONArray data = response.getJSONArray("data");
                                    double groceryAmt = 0;
                                    double transportAmt = 0;
                                    double shoppingAmt = 0;
                                    double foodAmt = 0;
                                    double entertainmentAmt = 0;
                                    double transferAmt = 0;
                                    double otherAmt = 0;

                                    for (int i = 0; i < data.length(); i++) {
                                        String category = data.getJSONObject(i).getString("category");
                                        Double cost = data.getJSONObject(i).getDouble("cost");
                                        switch (category) {
                                            case "Groceries":
                                                groceryAmt += cost;
                                                break;
                                            case "Transport":
                                                transportAmt += cost;
                                                break;
                                            case "Shopping":
                                                shoppingAmt += cost;
                                                break;
                                            case "Food":
                                                foodAmt += cost;
                                                break;
                                            case "Entertainment":
                                                entertainmentAmt += cost;
                                                break;
                                            case "Transfer to Friend":
                                                transferAmt += cost;
                                                break;
                                            case "Other":
                                                otherAmt += cost;
                                                break;
                                        }
                                    }

                                    statsfood.setText("$ " + String.format("%.2f", foodAmt));
                                    statstransport.setText("$ " + String.format("%.2f", transportAmt));
                                    statsentertainment.setText("$ " + String.format("%.2f", entertainmentAmt));
                                    statsshopping.setText("$ " + String.format("%.2f", shoppingAmt));
                                    statstransfer.setText("$ " + String.format("%.2f", transferAmt));
                                    statsgroceries.setText("$ " + String.format("%.2f", groceryAmt));
                                    statsother.setText("$ " + String.format("%.2f", otherAmt));
                                }
                                if (querycount < count) {
                                    querycount = count;
                                    if (count > 0) {
                                        JSONArray data = response.getJSONArray("data");
                                        List<JSONObject> transactionsInMonth = new ArrayList<>();
                                        LocalDate currentDate = LocalDate.now();
                                        int currentMonth = currentDate.getMonthValue();


                                        for (int i = 0; i < data.length(); i++) {
                                            JSONObject transaction = data.getJSONObject(i);
                                            String date = transaction.getString("date");
                                            int transactionMonth = Integer.parseInt(date.substring(5, 7)); // Extract the month part from the date

                                            if (transactionMonth == currentMonth) {
                                                transactionsInMonth.add(transaction);
                                            }
                                        }
                                        JSONArray transactionsInCurrentMonth = new JSONArray(transactionsInMonth);

                                        // Iterate through each record in the JSON array
                                        for (int i = 0; i < transactionsInCurrentMonth.length(); i++) {
                                            JSONObject transactionCM = transactionsInCurrentMonth.getJSONObject(i);
                                            double cost = transactionCM.getDouble("cost"); // Extract the cost field from the current transaction
                                            totalCost += cost;
                                        }
                                        setBarChartData(totalCost);
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                        Log.e("OnErrorResponse", error.toString());
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() {
                return TransactionVolleyHelper.getHeader();
            }
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                volleyResponseStatus = response.statusCode;
                return super.parseNetworkResponse(response);
            }
        };

        JsonObjectRequest jsonObjectRequest2 = new JsonObjectRequest(Request.Method.GET, usertableurl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (volleyResponseStatus == 200) {
                            try {
                                int count = response.getInt("count");

                                if (count > 0) {
                                    JSONArray data = response.getJSONArray("data");
                                    goalAmt = data.getJSONObject(0).getDouble("goal");
                                }
                                setBarChartData(goalAmt);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                        Log.e("OnErrorResponse", error.toString());
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() {
                return TransactionVolleyHelper.getHeader();
            }
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                volleyResponseStatus = response.statusCode;
                return super.parseNetworkResponse(response);
            }
        };
        queue1.add(jsonObjectRequest1);
        queue2.add(jsonObjectRequest2);

    }
    private void setBarChartData(double barAmt) {
        Log.d("total", String.valueOf(barAmt));
        ArrayList<BarEntry> barValue = new ArrayList<>();
        barValue.add(new BarEntry(0, new float[]{}));
        BarDataSet barDataSet = new BarDataSet(barValue, "");
        BarData barData = new BarData(barDataSet);
        barChart.setData(barData);
    }
}