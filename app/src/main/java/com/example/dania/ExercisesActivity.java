package com.example.dania;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class ExercisesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercises);
        CardView oeCard = findViewById(R.id.cardOtherExcersises);
        oeCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(ExercisesActivity.this, OtherExcersisesActivity.class));
            }});
        CardView wv = findViewById(R.id.cardWatchVideo);

        // Set the click listener to start BreathingExercisesActivity
        wv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start BreathingExercisesActivity when the card is clicked
                startActivity(new Intent(ExercisesActivity.this, WatchVideoActivity.class));
            }});
        CardView rf = findViewById(R.id.cardVRecommendedFoods);

        // Set the click listener to start BreathingExercisesActivity
        rf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start BreathingExercisesActivity when the card is clicked
                startActivity(new Intent(ExercisesActivity.this, RecommendedFoodsActivity.class));
            }});

        // Find the Breathing Exercises CardView
        CardView beCard = findViewById(R.id.cardBreathingExercises);

        // Set the click listener to start BreathingExercisesActivity
        beCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start BreathingExercisesActivity when the card is clicked
                startActivity(new Intent(ExercisesActivity.this, BreathingExercisesActivity.class));
            }


        });
    }
}
