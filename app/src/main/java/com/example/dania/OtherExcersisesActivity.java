package com.example.dania;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class OtherExcersisesActivity extends AppCompatActivity {
    private ImageView imageOtherExercise;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_other_excersises);
        // Initialize the ImageView
        imageOtherExercise = findViewById(R.id.imageOtherExercise);

        // Set your breathing exercise image here
        imageOtherExercise.setImageResource(R.drawable.oe); // Ensure this image exists in the drawable folder

    };
    }
