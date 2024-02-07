package com.sp.finvue;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

import java.util.Map;


public class StatisticsFragment extends Fragment {

    private FirebaseFirestore fStore;
    private FirebaseAuth mAuth;
    private String fbuserUUID, fbuserID;
    private int volleyResponseStatus;

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
        return inflater.inflate(R.layout.fragment_statistics, container, false);
    }

    private void fetchUserData() {
        DocumentReference documentReference = fStore.collection("users").document(fbuserID);
        documentReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                fbuserUUID = task.getResult().getString("useruuid");
            }
            getAllUserTransactions(fbuserUUID);
        });
    }

    private void getAllUserTransactions(String user_uuid) {
        String useruuidurl = TransactionVolleyHelper.transaction_url + user_uuid;
        RequestQueue queue = Volley.newRequestQueue(getContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, useruuidurl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (volleyResponseStatus == 200) {
                            try {
                                int count = response.getInt("count");
                                    if (count > 0) {
                                        JSONArray data = response.getJSONArray("data");
                                        Log.d("query", String.valueOf(data));
                                        // Iterate through each record in the JSON array
                                        for (int i = 0; i < data.length(); i++) {
                                            JSONObject transaction = data.getJSONObject(i);
                                            double cost = transaction.getDouble("cost"); // Extract the cost field from the current transaction
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

    }

}