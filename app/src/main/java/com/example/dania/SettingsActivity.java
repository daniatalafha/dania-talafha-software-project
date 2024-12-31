package com.example.dania;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);

        // Apply window insets for system bars (navigation, status bars)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Find the back button (CardView) by its ID
        CardView back = findViewById(R.id.cardBack);

        // Set an OnClickListener for the back button
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // You can either finish the current activity (go back to the previous activity)
                finish();

                // Or start a specific activity (e.g., go back to MainActivity or HomeActivity)
                // Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                // startActivity(intent);

                // Optional: You can also show a Toast message for testing
                // Toast.makeText(SettingsActivity.this, "Going back", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
