package com.example.dania;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProviderMainActivity extends AppCompatActivity {
    FirebaseAuth auth;
    Button button;
    TextView textView;
    FirebaseUser provider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_provider_main);

        // Retrieve user details from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String firstName = sharedPreferences.getString("first_name", "");
        String lastName = sharedPreferences.getString("last_name", "");
        String dob = sharedPreferences.getString("dob", "");

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();
        provider = auth.getCurrentUser();

        // Handle user authentication
        textView = findViewById(R.id.Provider_details);
        if (provider == null) {
            // If provider is not logged in, navigate to login page
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            // Display provider's email (or other details like name)
            textView.setText("Welcome, " + firstName + " " + lastName); // Show full name or email
        }

        // Set up the logout button
        button = findViewById(R.id.Logout);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Card for adding a client
        CardView addClientCard = findViewById(R.id.AddClient);
        addClientCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProviderMainActivity.this, AddClientActivity.class));
            }
        });

        // Card for viewing clients
        CardView viewClientCard = findViewById(R.id.cardViewClients);
        viewClientCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Navigate to View Clients Activity
                Intent intent = new Intent(ProviderMainActivity.this, ViewClientsActivity.class);
                startActivity(intent);
            }
        });

        // Card for removing a client
        CardView removeClientCard = findViewById(R.id.cardRemoveClient);
        removeClientCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Navigate to Remove Client Activity
                Intent intent = new Intent(ProviderMainActivity.this, RemoveClientActivity.class);
                startActivity(intent);
            }
        });

        // Handle window insets for edge-to-edge UI design
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
