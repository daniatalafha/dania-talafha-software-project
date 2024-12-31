package com.example.dania;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class RecommendedFoodsActivity extends AppCompatActivity {
    private ImageView rfa;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_recommended_foods);
        // Initialize the ImageView
        rfa= findViewById(R.id.cardVRecommendedFoods);

        // Set your breathing exercise image here
        rfa.setImageResource(R.drawable.rf); // Ensure this image exists in the drawable folder

    };
}

