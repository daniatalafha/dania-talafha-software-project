package com.example.dania;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity {

    private String selectedColor = "";  // Variable to store selected color
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize Firebase Auth and Database
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Initialize the Spinner
        Spinner colorSpinner = findViewById(R.id.colorSpinner);

        // Create an array of colors (strings)
        String[] colors = {"White", "Blue", "Red", "Orange"};

        // Create an ArrayAdapter using the colors array and a default spinner layout
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, colors);

        // Set the layout style for the dropdown items
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Set the adapter to the spinner
        colorSpinner.setAdapter(adapter);

        // Set an item selected listener for the spinner
        colorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Get the selected item
                selectedColor = parentView.getItemAtPosition(position).toString();
                // Show a toast with the selected color
                Toast.makeText(HomeActivity.this, "Selected Color: " + selectedColor, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Handle case when no item is selected (optional)
            }
        });

        // Back button functionality
        CardView back = findViewById(R.id.cardBack);
        back.setOnClickListener(v -> finish()); // Close the current activity and return to the previous one

        // Submit button functionality
        CardView submit = findViewById(R.id.cardSubmit);
        submit.setOnClickListener(view -> {
            if (selectedColor.isEmpty()) {
                Toast.makeText(HomeActivity.this, "Please select a color", Toast.LENGTH_SHORT).show();
                return;
            }

            // Save the selected color to Firebase Realtime Database with a timestamp
            saveColorHistory(selectedColor);

            // Optionally, pass selected color to the next activity
            Intent intent = new Intent(HomeActivity.this, ExercisesActivity.class);
            intent.putExtra("SELECTED_COLOR", selectedColor);  // Pass selected color to the next activity
            startActivity(intent);
        });
    }

    private void saveColorHistory(String color) {
        // Get current user ID
        String userId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null;

        if (userId == null) {
            Toast.makeText(HomeActivity.this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create timestamp for the current operation
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        // Create a new color history object
        ColorHistory colorHistory = new ColorHistory(color, timestamp);

        // Save the color and timestamp to the user's color history in the database
        mDatabase.child("users").child(userId).child("colorHistory").child(timestamp).setValue(colorHistory)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(HomeActivity.this, "Color history saved successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(HomeActivity.this, "Failed to save color history", Toast.LENGTH_SHORT).show();
                });
    }

    // Define the ColorHistory model class
    public static class ColorHistory {
        private String color;
        private String time;

        public ColorHistory(String color, String time) {
            this.color = color;
            this.time = time;
        }

        public String getColor() {
            return color;
        }

        public String getTime() {
            return time;
        }
    }
}
