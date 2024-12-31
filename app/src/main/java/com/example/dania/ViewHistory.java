package com.example.dania;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ViewHistory extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ColorHistoryAdapter colorHistoryAdapter;
    private ArrayList<ColorHistory> colorHistoryList;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_history);  // Single layout for Activity

        // Initialize RecyclerView and list
        recyclerView = findViewById(R.id.recyclerViewColorHistory);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        colorHistoryList = new ArrayList<>();
        colorHistoryAdapter = new ColorHistoryAdapter(colorHistoryList);
        recyclerView.setAdapter(colorHistoryAdapter);

        mAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressBar);

        // Fetch color history from Firebase
        fetchColorHistory();
    }

    private void fetchColorHistory() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference userRef = database.getReference("users");

        // Get the logged-in user's ID
        String userId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null;

        if (userId == null) {
            Toast.makeText(ViewHistory.this, "No logged-in user found", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            return;
        }

        // Show progress bar while fetching data
        progressBar.setVisibility(View.VISIBLE);

        // Query the database for the colorHistory of the logged-in user
        userRef.child(userId).child("colorHistory").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Loop through the color history records
                    for (DataSnapshot historySnapshot : dataSnapshot.getChildren()) {
                        String color = historySnapshot.child("color").getValue(String.class);
                        String time = historySnapshot.child("time").getValue(String.class);
                        String dob = historySnapshot.child("dob").getValue(String.class);
                        String email = historySnapshot.child("email").getValue(String.class);
                        String firstName = historySnapshot.child("firstName").getValue(String.class);
                        String lastName = historySnapshot.child("lastName").getValue(String.class);
                        String role = historySnapshot.child("role").getValue(String.class);

                        // Add the color history record to the list
                        colorHistoryList.add(new ColorHistory(color, time, dob, email, firstName, lastName, role));
                    }
                    colorHistoryAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(ViewHistory.this, "No color history found", Toast.LENGTH_SHORT).show();
                }

                // Hide progress bar once data is fetched
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ViewHistory.this, "Error fetching color history", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    // Define a ColorHistory model class
    public static class ColorHistory {
        private String color;
        private String time;
        private String dob;
        private String email;
        private String firstName;
        private String lastName;
        private String role;

        public ColorHistory(String color, String time, String dob, String email, String firstName, String lastName, String role) {
            this.color = color;
            this.time = time;
            this.dob = dob;
            this.email = email;
            this.firstName = firstName;
            this.lastName = lastName;
            this.role = role;
        }

        public String getColor() {
            return color;
        }

        public String getTime() {
            return time;
        }

        public String getDob() {
            return dob;
        }

        public String getEmail() {
            return email;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public String getRole() {
            return role;
        }
    }

    // Define the RecyclerView Adapter for Color History
    public class ColorHistoryAdapter extends RecyclerView.Adapter<ColorHistoryAdapter.ColorHistoryViewHolder> {

        private ArrayList<ColorHistory> colorHistoryList;

        public ColorHistoryAdapter(ArrayList<ColorHistory> colorHistoryList) {
            this.colorHistoryList = colorHistoryList;
        }

        @Override
        public ColorHistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // Inflate the item layout directly in the Adapter
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_color_history, parent, false);
            return new ColorHistoryViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ColorHistoryViewHolder holder, int position) {
            ColorHistory colorHistory = colorHistoryList.get(position);

            // Bind data to the views
            holder.textViewColor.setText("Color: " + colorHistory.getColor());
            holder.textViewTime.setText("Time: " + colorHistory.getTime());
            holder.textViewDob.setText("DOB: " + colorHistory.getDob());
            holder.textViewEmail.setText("Email: " + colorHistory.getEmail());
            holder.textViewFirstName.setText("First Name: " + colorHistory.getFirstName());
            holder.textViewLastName.setText("Last Name: " + colorHistory.getLastName());
            holder.textViewRole.setText("Role: " + colorHistory.getRole());
        }

        @Override
        public int getItemCount() {
            return colorHistoryList.size();
        }

        public class ColorHistoryViewHolder extends RecyclerView.ViewHolder {
            private TextView textViewColor, textViewTime, textViewDob, textViewEmail, textViewFirstName, textViewLastName, textViewRole;

            public ColorHistoryViewHolder(View itemView) {
                super(itemView);
                textViewColor = itemView.findViewById(R.id.textViewColor);
                textViewTime = itemView.findViewById(R.id.textViewTime);
                textViewDob = itemView.findViewById(R.id.textViewDob);
                textViewEmail = itemView.findViewById(R.id.textViewEmail);
                textViewFirstName = itemView.findViewById(R.id.textViewFirstName);
                textViewLastName = itemView.findViewById(R.id.textViewLastName);
                textViewRole = itemView.findViewById(R.id.textViewRole);
            }
        }
    }
}
