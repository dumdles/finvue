package com.sp.finvue;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class SignupActivity extends AppCompatActivity {

    private static final String TAG = "SignUpActivity";
    private static final int RC_SIGN_IN = 9001; // What is this
    private FirebaseAuth mAuth;
    FirebaseFirestore fStore;
    String userID;
    String name;
    String email;
    String password;
    String cfmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialise Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        TextView textViewLogin = findViewById(R.id.textViewLogin);
        LinearLayout btnGoogleSignUp = findViewById(R.id.btnSignUpWithGoogle);
        Button btnSignUp = findViewById(R.id.sign_up_btn);

        textViewLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Open the Login activity when the text is clicked
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        btnGoogleSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle Google Sign up
                signInWithGoogle();
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                EditText editTextName = findViewById(R.id.editTextName);
                EditText editTextEmail = findViewById(R.id.editTextEmail);
                EditText editTextPassword = findViewById(R.id.editTextPassword);
                EditText editTextCfmPassword = findViewById(R.id.editTextConfirmPassword);

                // Get email and password from EditText fields
                name = editTextName.getText().toString();
                email = editTextEmail.getText().toString();
                password = editTextPassword.getText().toString();
                cfmPassword = editTextCfmPassword.getText().toString();

                if (TextUtils.isEmpty(name)) {
                    editTextName.setError("Name is required");
                } else if (TextUtils.isEmpty(email)) {
                    editTextEmail.setError("Email is required");
                } else if (TextUtils.isEmpty(password)) {
                    editTextPassword.setError("Password is required");
                } else if (!password.equals(cfmPassword)) {
                    editTextCfmPassword.setError("Passwords do not match");
                } else {
                    // Call createAccount method with email and password
                    createAccount(email, password);
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            reload();
        }
    }

    private void createAccount(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(SignupActivity.this, "Account created!", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "createUserWithEmail:success");

                            // Get user ID
                            userID = mAuth.getCurrentUser().getUid();

                            // Get current date
                            String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

                            // Insert data into Firestore database
                            DocumentReference documentReference = fStore.collection("users").document(userID);
                            Map<String, Object> user = new HashMap<>();
                            user.put("name", name);
                            user.put("email", email);
                            user.put("joinDate", currentDate);
                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Log.d(TAG, "onSuccess: user profile created for " + userID);
                                }
                            });
                            // updateUI(user);
                        } else {
                            // If Sign in fails, display a message to the user
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());

                            // Log the email that failed to sign up
                            Log.e(TAG, "Failed email: " + email);

                            // Log the specific exception message for further troubleshooting
                            Log.e(TAG, "Exception message: " + task.getException().getMessage());

                            Toast.makeText(SignupActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            // updateUI(null);
                        }
                    }
                });
    }


    private void reload() {

    }

    private void updateUI(FirebaseUser user) {

    }

    private void signInWithGoogle() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            try {
                GoogleSignInAccount account = GoogleSignIn.getSignedInAccountFromIntent(data).getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Log the exception details for troubleshooting
                Log.e(TAG, "Google Sign-in failed with error code: " + e.getStatusCode(), e);

                // Google Sign-in failed, update UI appropriately
                Toast.makeText(this, "Google Sign-in failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign-in success
                            Log.d(TAG, "createUserWithEmail.success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            // Update or create user document in Firestore
                            updateUserDocument(user);

                            Intent intent = new Intent(SignupActivity.this, HomeActivity.class);
                            startActivity(intent);

                        } else {
                            Toast.makeText(SignupActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void updateUserDocument(FirebaseUser user) {
        if (user != null) {
            String userId = user.getUid();
            String userEmail = user.getEmail();
            String userName = user.getDisplayName();

            // Check if the user document already exists
            DocumentReference userRef = fStore.collection("users").document(userId);

            userRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Update the existing document
                        userRef.update("email", userEmail);
                        userRef.update("name", userName);
                    } else {
                        // Create a new user document
                        Map<String, Object> newUser = new HashMap<>();
                        newUser.put("email", userEmail);
                        newUser.put("name", userName);
                        newUser.put("joinDate", getCurrentDate());

                        userRef.set(newUser).addOnSuccessListener(unused -> {
                            Log.d(TAG, "New user document created for " + userId);
                        });
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            });
        }
    }

    private String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        return dateFormat.format(new Date());
    }
}

