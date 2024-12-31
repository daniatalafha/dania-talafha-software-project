package com.example.dania;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.android.material.textfield.TextInputEditText;

public class RegisterActivity extends AppCompatActivity {

    TextInputEditText editTextEmail, editTextPassword, editTextFirstName, editTextLastName;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    TextView textView;
    CheckBox checkboxUserRegistration, checkboxProviderRegistration;
    Button btnProceedRegister;
    android.widget.DatePicker datePicker;
    FirebaseDatabase database;
    DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        // Initialize views
        checkboxUserRegistration = findViewById(R.id.checkboxUserRegistration);
        checkboxProviderRegistration = findViewById(R.id.checkboxProviderRegistration);
        editTextFirstName = findViewById(R.id.firstName);
        editTextLastName = findViewById(R.id.lastName);
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        datePicker = findViewById(R.id.datePicker);
        btnProceedRegister = findViewById(R.id.btnProceedRegister);
        progressBar = findViewById(R.id.progressBar);
        textView = findViewById(R.id.LoginNow);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        usersRef = database.getReference("users"); // Reference to "users" node

        // Setup Login redirection
        textView.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        });

        // Handle registration button click
        btnProceedRegister.setOnClickListener(view -> {
            // Validate registration type selection
            if (checkboxUserRegistration.isChecked() && checkboxProviderRegistration.isChecked()) {
                Toast.makeText(RegisterActivity.this, "Please select only one registration type", Toast.LENGTH_SHORT).show();
                return;
            }

            // Get user inputs
            String firstName = editTextFirstName.getText().toString();
            String lastName = editTextLastName.getText().toString();
            String email = editTextEmail.getText().toString();
            String password = editTextPassword.getText().toString();

            // Get the selected date from DatePicker
            int day = datePicker.getDayOfMonth();
            int month = datePicker.getMonth();
            int year = datePicker.getYear();
            String dob = year + "-" + (month + 1) + "-" + day; // Format as YYYY-MM-DD

            // Validate input fields
            if (TextUtils.isEmpty(firstName) || TextUtils.isEmpty(lastName)) {
                Toast.makeText(RegisterActivity.this, "Enter first and last name", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(RegisterActivity.this, "Enter email", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(password)) {
                Toast.makeText(RegisterActivity.this, "Enter password", Toast.LENGTH_SHORT).show();
                return;
            }

            // Show progress bar
            progressBar.setVisibility(View.VISIBLE);

            // Register the user in Firebase Authentication
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();

                            // Create a new user object
                            User user = new User(firstName, lastName, dob, email,
                                    checkboxUserRegistration.isChecked() ? "user" : "provider");

                            // Save user data to Firebase Realtime Database
                            usersRef.child(firebaseUser.getUid()).setValue(user)
                                    .addOnCompleteListener(databaseTask -> {
                                        if (databaseTask.isSuccessful()) {
                                            Toast.makeText(RegisterActivity.this, "User Registered Successfully!", Toast.LENGTH_SHORT).show();

                                            // Navigate to the appropriate activity based on the role
                                            Intent intent;
                                            if (checkboxUserRegistration.isChecked()) {
                                                intent = new Intent(RegisterActivity.this, MainActivity.class);
                                            } else {
                                                intent = new Intent(RegisterActivity.this, ProviderMainActivity.class);
                                            }
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            Toast.makeText(RegisterActivity.this, "Failed to save user data", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            Toast.makeText(RegisterActivity.this, "Registration failed.", Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }
}
