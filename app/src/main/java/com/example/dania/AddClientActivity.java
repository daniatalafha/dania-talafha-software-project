package com.example.dania;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AddClientActivity extends AppCompatActivity {

    private EditText editTextClientEmail;
    private Button btnAddClient;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_client);

        // Initialize views
        editTextClientEmail = findViewById(R.id.editTextClientEmail);
        btnAddClient = findViewById(R.id.btnAddClient);
        progressBar = findViewById(R.id.progressBar);

        // Initialize Firebase components
        mAuth = FirebaseAuth.getInstance();

        // Handle back button click
        CardView backa = findViewById(R.id.cardBacka);
        backa.setOnClickListener(v -> finish());

        // Add client logic
        btnAddClient.setOnClickListener(v -> {
            String clientEmail = editTextClientEmail.getText().toString().trim();

            // Validate input
            if (TextUtils.isEmpty(clientEmail)) {
                Toast.makeText(AddClientActivity.this, "Please enter the client email", Toast.LENGTH_SHORT).show();
                return;
            }

            progressBar.setVisibility(View.VISIBLE);

            // Step 1: Get the logged-in provider email from Firebase Authentication
            String providerEmail = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getEmail() : null;

            if (providerEmail == null) {
                Toast.makeText(AddClientActivity.this, "No logged-in provider found", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                return;
            }

            // Step 2: Validate if the provider email exists and has "provider" role in Realtime Database
            validateProviderInDatabase(providerEmail, clientEmail);
        });
    }

    private void validateProviderInDatabase(String providerEmail, String clientEmail) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference providerRef = database.getReference("users");

        // Step 1: Check if the provider email exists and has "provider" role
        providerRef.orderByChild("email").equalTo(providerEmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Check if the provider has a "provider" role
                    for (DataSnapshot providerSnapshot : dataSnapshot.getChildren()) {
                        String role = providerSnapshot.child("role").getValue(String.class);
                        if ("provider".equals(role)) {
                            // Step 2: Validate if the client email exists and has "user" role
                            validateClientInDatabase(providerEmail, clientEmail);
                        } else {
                            Toast.makeText(AddClientActivity.this, "The logged-in user is not a provider", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                } else {
                    Toast.makeText(AddClientActivity.this, "Provider not found", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(AddClientActivity.this, "Error checking provider", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void validateClientInDatabase(String providerEmail, String clientEmail) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference userRef = database.getReference("users");

        // Step 2: Check if the client email exists and has "user" role
        userRef.orderByChild("email").equalTo(clientEmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Check if the client has a "user" role
                    for (DataSnapshot clientSnapshot : dataSnapshot.getChildren()) {
                        String role = clientSnapshot.child("role").getValue(String.class);
                        if ("user".equals(role)) {
                            // Step 3: Add the client to the provider's client list in Realtime Database
                            addClientToProvider(providerEmail, clientEmail);
                        } else {
                            Toast.makeText(AddClientActivity.this, "The user email is not valid", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                } else {
                    Toast.makeText(AddClientActivity.this, "Client not found", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(AddClientActivity.this, "Error checking client", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addClientToProvider(String providerEmail, String clientEmail) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference providerRef = database.getReference("users");

        // Step 3: Add the client email to the provider's client list in the Realtime Database
        providerRef.orderByChild("email").equalTo(providerEmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Get the provider's reference
                    for (DataSnapshot providerSnapshot : dataSnapshot.getChildren()) {
                        String providerId = providerSnapshot.getKey(); // Use the provider's unique ID
                        DatabaseReference providerClientListRef = providerRef.child(providerId).child("clients");

                        // Add the client email to the provider's "clients" list
                        providerClientListRef.push().setValue(clientEmail).addOnCompleteListener(clientUpdateTask -> {
                            if (clientUpdateTask.isSuccessful()) {
                                Toast.makeText(AddClientActivity.this, "Client added successfully!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(AddClientActivity.this, "Failed to add client", Toast.LENGTH_SHORT).show();
                            }
                            progressBar.setVisibility(View.GONE);
                        });
                    }
                } else {
                    Toast.makeText(AddClientActivity.this, "Provider not found in the database", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(AddClientActivity.this, "Error adding client to provider", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
