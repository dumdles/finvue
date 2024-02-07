package com.sp.finvue;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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

import java.util.HashMap;
import java.util.Map;

public class GoalSetting extends AppCompatActivity {

    private EditText goalAmount;

    private FirebaseFirestore fStore;
    private FirebaseAuth mAuth;
    private String fbuserID;
    private String fbuserUUID;
    private int volleyResponseStatus;

    private Button saveGoal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal_setting);

        goalAmount = findViewById(R.id.editTextGoalAmount);
        saveGoal = findViewById(R.id.btnsavegoal);

        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        // Get current user
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            fbuserID = user.getUid();
        }

        saveGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String goalamt = goalAmount.getText().toString();
                fetchUserData(goalamt);
            }
        });
    }

    private void fetchUserData(String goalamt) {
        DocumentReference documentReference = fStore.collection("users").document(fbuserID);
        documentReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                fbuserUUID = task.getResult().getString("useruuid");
                saveGoal(fbuserUUID, goalamt);
                Intent intent = new Intent(GoalSetting.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void saveGoal(String user_uuid, String goalamt) {

        Map<String, String> params = new HashMap<String, String>();
        params.put("goal", goalamt);

        String usertableurl = TransactionVolleyHelper.user_url + user_uuid;
        RequestQueue queue = Volley.newRequestQueue(this);
        JSONObject putdata = new JSONObject(params);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, usertableurl, putdata,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

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
        };
        queue.add(jsonObjectRequest);
    }

}