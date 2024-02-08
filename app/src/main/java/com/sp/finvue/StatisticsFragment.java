package com.sp.finvue;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

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
    private JSONArray retrievedData;
    BarChart barChart;

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
        barChart = view.findViewById(R.id.stacked_barchart);

        fetchUserData();
        ArrayList<BarEntry> barValue = new ArrayList<>();
        //barValue.add(new BarEntry(0, new float[]{(float) totalCost, (float) goalAmt}));
        barValue.add(new BarEntry(1, new float[]{2,5.5f}));
        Log.d("bar val", String.valueOf(barValue));
        BarDataSet barDataSet = new BarDataSet(barValue, "");
        BarData barData = new BarData(barDataSet);
        barChart.setData(barData);
        return view;
    }

    private void fetchUserData() {
        DocumentReference documentReference = fStore.collection("users").document(fbuserID);
        documentReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                fbuserUUID = task.getResult().getString("useruuid");
            }
            totalCost = getAllUserTransactions(fbuserUUID);
            goalAmt = getGoal(fbuserUUID);
        });
        
    }

    private double getAllUserTransactions(String user_uuid) {
        String useruuidurl = TransactionVolleyHelper.transaction_url + user_uuid;
        RequestQueue queue = Volley.newRequestQueue(getContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, useruuidurl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (volleyResponseStatus == 200) {
                            try {
                                int count = response.getInt("count");
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
        queue.add(jsonObjectRequest);
        return totalCost;
    }
    private double getGoal(String user_uuid) {
        String usertableurl = TransactionVolleyHelper.user_url + user_uuid;
        RequestQueue queue = Volley.newRequestQueue(getContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, usertableurl, null,
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
        queue.add(jsonObjectRequest);
        return goalAmt;
    }
}