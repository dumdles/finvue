package com.sp.finvue;

import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class HomepageFragment extends Fragment {

    private FirebaseFirestore fStore;
    private FirebaseAuth mAuth;
    private String fbuserID;
    private String fbuserUUID, fbusername;

    private TextView welcomeUser, amtSpentWk, amtLeftWk;
    private ImageView homeProfile;
    private CardView spending, addTrans, statistic, readArticle;

    double totalCost = 0.0;
    int querycount = 0;
    private int volleyResponseStatus;

    public HomepageFragment() {
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
        View view = inflater.inflate(R.layout.fragment_homepage, container, false);
        welcomeUser = view.findViewById(R.id.welcomeuser);
        amtSpentWk = view.findViewById(R.id.homeamtspent);
        amtLeftWk = view.findViewById(R.id.homeamtleft);

        homeProfile = view.findViewById(R.id.homeprofile);
        spending = view.findViewById(R.id.spentthisweek);
        addTrans = view.findViewById(R.id.homeaddtransaction);
        statistic = view.findViewById(R.id.knowyourtrends);
        readArticle = view.findViewById(R.id.homereadarticles);

        homeProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ProfilePage.class);
                startActivity(intent);
            }
        });

        spending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an instance of the SpendingFragment
                SpendingFragment spendingFragment = new SpendingFragment();

                // Replace the current fragment with the SpendingFragment
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainer, spendingFragment)
                        .addToBackStack(null)  // Add to the back stack for back navigation
                        .commit();
                if (getActivity() instanceof HomeActivity) {
                    ((HomeActivity) getActivity()).updateNavigationItemSelected(R.id.spendings);
                }
            }
        });

        addTrans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NewTransaction.class);
                startActivity(intent);
            }
        });

        statistic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StatisticsFragment statisticsFragment = new StatisticsFragment();


                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainer, statisticsFragment)
                        .addToBackStack(null)
                        .commit();
                if (getActivity() instanceof HomeActivity) {
                    ((HomeActivity) getActivity()).updateNavigationItemSelected(R.id.stats);
                }
            }
        });

        readArticle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewsFragment newsFragment = new NewsFragment();

                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainer, newsFragment)
                        .addToBackStack(null)
                        .commit();
                if (getActivity() instanceof HomeActivity) {
                    ((HomeActivity) getActivity()).updateNavigationItemSelected(R.id.news);
                }
            }
        });
        return view;
    }

    private void fetchUserData() {
        DocumentReference documentReference = fStore.collection("users").document(fbuserID);
        documentReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                fbuserUUID = task.getResult().getString("useruuid");
                fbusername = task.getResult().getString("name");
                welcomeUser.setText("Welcome,\n" + fbusername);
                getByUserUUID(fbuserUUID);
                getGoal(fbuserUUID);
            }
        });
    }

    private void getByUserUUID(String user_uuid) {
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

                                        amtSpentWk.setText("$ " + String.format("%.2f", totalCost));
                                        Log.d("querydata", String.valueOf(data));
                                    }
                                } else {
                                    amtSpentWk.setText("$ " + String.format("%.2f", totalCost));
                                    //Log.d("querydata", String.valueOf(data));
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

    }

    private void getGoal(String user_uuid) {
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
                                    if (data.getJSONObject(0).isNull("goal")) {
                                        amtLeftWk.setText("Please set your goal now!");
                                    } else {
                                        double goal = data.getJSONObject(0).getDouble("goal");
                                        double remaining = goal - totalCost;
                                        if (remaining > 0) {
                                            amtLeftWk.setText("$" + String.format("%.2f", remaining) + " left until spending limit!");
                                        } else if (remaining < 0) {
                                            amtLeftWk.setText("You have exceeded your spending limit by $" + String.format("%.2f", Math.abs(remaining)) + "!");
                                        } else {
                                            amtLeftWk.setText("You have reached your spending limit!");
                                        }
                                    }
                                    Log.d("querygoal", String.valueOf(data));
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

    }
}