package com.example.dania;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class BreathingExercisesActivity extends AppCompatActivity {

    private ImageView imageBreathingExercise;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_breathing_exercises);  // Make sure this layout exists

        // Initialize the ImageView
        imageBreathingExercise = findViewById(R.id.imageBreathingExercise);

        // Set your breathing exercise image here
        imageBreathingExercise.setImageResource(R.drawable.be); // Ensure this image exists in the drawable folder
    }
}
