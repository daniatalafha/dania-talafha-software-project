package com.example.dania;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    TextInputEditText editTextEmail, editTextPassword;
    Button buttonLogin;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    TextView textView;

    @Override
    public void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // Check if the user exists in the Realtime Database
            checkUserInDatabase(currentUser.getUid());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        buttonLogin = findViewById(R.id.btn_login);
        progressBar = findViewById(R.id.progressBar);
        textView = findViewById(R.id.RegesterNow);

        // Set up click listener for registration navigation
        textView.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
            finish();
        });

        // Set up login button click listener
        buttonLogin.setOnClickListener(view -> {
            String email = editTextEmail.getText().toString();
            String password = editTextPassword.getText().toString();

            if (TextUtils.isEmpty(email)) {
                showError("Enter email");
                return;
            }
            if (TextUtils.isEmpty(password)) {
                showError("Enter password");
                return;
            }

            // Start login process
            progressBar.setVisibility(View.VISIBLE);

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_SHORT).show();
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            if (user != null) {
                                checkUserInDatabase(user.getUid());
                            }
                        } else {
                            handleLoginFailure(task);
                        }
                    });
        });
    }

    private void showError(String message) {
        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    private void checkUserInDatabase(String userId) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users").child(userId);

        // Check if the user exists in the database
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // User data exists in the database
                if (dataSnapshot.exists()) {
                    // Retrieve user data (first name, last name, role)
                    String userRole = dataSnapshot.child("role").getValue(String.class);

                    // Check the saved role in SharedPreferences
                    SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
                    sharedPreferences.edit().putString("user_role", userRole).apply();

                    // Navigate to the appropriate activity based on the role
                    navigateToAppropriateActivity();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error if database read failed
                Toast.makeText(LoginActivity.this, "Failed to fetch user data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToAppropriateActivity() {
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String userRole = sharedPreferences.getString("user_role", "user");

        Intent intent;
        if ("user".equals(userRole)) {
            intent = new Intent(getApplicationContext(), MainActivity.class);
        } else if ("provider".equals(userRole)) {
            intent = new Intent(getApplicationContext(), ProviderMainActivity.class);
        } else {
            intent = new Intent(getApplicationContext(), MainActivity.class); // default fallback
        }
        startActivity(intent);
        finish();
    }

    private void handleLoginFailure(Task<AuthResult> task) {
        String errorMessage = "Authentication failed.";
        if (task.getException() != null) {
            errorMessage = task.getException().getMessage();
        }
        Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
    }
}
