package com.example.dania;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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

public class RemoveClientActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ClientAdapter clientAdapter;
    private ArrayList<Client> clientList;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private Button removeButton;
    private ArrayList<Client> clientsToRemove;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove_client);

        // Initialize views
        recyclerView = findViewById(R.id.recyclerViewClients);
        progressBar = findViewById(R.id.progressBar);
        removeButton = findViewById(R.id.removeButton);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        clientList = new ArrayList<>();
        clientAdapter = new ClientAdapter(clientList);
        recyclerView.setAdapter(clientAdapter);

        mAuth = FirebaseAuth.getInstance();
        clientsToRemove = new ArrayList<>();

        // Show progress bar while loading data
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);

        // Fetch provider's clients from the Realtime Database
        fetchProviderClients();

        // Handle remove button click
        removeButton.setOnClickListener(v -> removeSelectedClients());
    }

    private void fetchProviderClients() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference providerRef = database.getReference("users");

        String providerEmail = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getEmail() : null;

        if (providerEmail == null) {
            Toast.makeText(RemoveClientActivity.this, "No logged-in provider found", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);  // Hide the progress bar if no provider found
            return;
        }

        providerRef.orderByChild("email").equalTo(providerEmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot providerSnapshot : dataSnapshot.getChildren()) {
                        // Retrieve the clients list from the provider data
                        for (DataSnapshot clientSnapshot : providerSnapshot.child("clients").getChildren()) {
                            String clientEmail = clientSnapshot.getValue(String.class);
                            fetchClientDetails(clientEmail);
                        }
                    }
                } else {
                    Toast.makeText(RemoveClientActivity.this, "Provider not found", Toast.LENGTH_SHORT).show();
                }

                // Hide progress bar and show recyclerView after fetching data
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(RemoveClientActivity.this, "Error fetching provider data", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);  // Hide progress bar on error
                recyclerView.setVisibility(View.VISIBLE);  // Ensure RecyclerView is visible in case of error
            }
        });
    }

    private void fetchClientDetails(String clientEmail) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference clientRef = database.getReference("users");

        clientRef.orderByChild("email").equalTo(clientEmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot clientSnapshot : dataSnapshot.getChildren()) {
                        String email = clientSnapshot.child("email").getValue(String.class);
                        String firstName = clientSnapshot.child("firstName").getValue(String.class);
                        String lastName = clientSnapshot.child("lastName").getValue(String.class);
                        String role = clientSnapshot.child("role").getValue(String.class);
                        String dob = clientSnapshot.child("dob").getValue(String.class);

                        // Add client data to the list
                        clientList.add(new Client(email, firstName, lastName, role, dob));
                    }

                    // Notify adapter that data has changed
                    clientAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(RemoveClientActivity.this, "Client not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(RemoveClientActivity.this, "Error fetching client details", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void removeSelectedClients() {
        if (clientsToRemove.isEmpty()) {
            Toast.makeText(RemoveClientActivity.this, "No clients selected", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show progress bar while removing clients
        progressBar.setVisibility(View.VISIBLE);

        // Remove each selected client from the provider's list of clients
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference providerRef = database.getReference("users");

        String providerEmail = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getEmail() : null;

        if (providerEmail == null) {
            Toast.makeText(RemoveClientActivity.this, "No logged-in provider found", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            return;
        }

        // Iterate over selected clients and remove from database
        for (Client client : clientsToRemove) {
            // Fetch provider's data by email
            providerRef.orderByChild("email").equalTo(providerEmail).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot providerSnapshot : dataSnapshot.getChildren()) {
                            // Get the reference to the provider's clients list
                            DatabaseReference providerClientListRef = providerSnapshot.child("clients").getRef();

                            // Remove the client email from the provider's "clients" list by email
                            providerClientListRef.orderByValue().equalTo(client.getEmail()).getRef().removeValue()
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            // Client removed successfully from the database
                                            clientList.remove(client);  // Remove from local list
                                            clientsToRemove.remove(client);  // Remove from selected clients
                                            clientAdapter.notifyDataSetChanged();  // Notify the adapter for data change
                                            Toast.makeText(RemoveClientActivity.this, "Client removed successfully", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(RemoveClientActivity.this, "Failed to remove client", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(RemoveClientActivity.this, "Error removing client", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Hide the progress bar after removal is complete
        progressBar.setVisibility(View.GONE);
    }

    // Define a Client model class
    public static class Client {
        private String email;
        private String firstName;
        private String lastName;
        private String role;
        private String dob;

        public Client(String email, String firstName, String lastName, String role, String dob) {
            this.email = email;
            this.firstName = firstName;
            this.lastName = lastName;
            this.role = role;
            this.dob = dob;
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

        public String getDob() {
            return dob;
        }
    }

    // Define the RecyclerView Adapter
    public class ClientAdapter extends RecyclerView.Adapter<ClientAdapter.ClientViewHolder> {

        private ArrayList<Client> clientList;

        public ClientAdapter(ArrayList<Client> clientList) {
            this.clientList = clientList;
        }

        @Override
        public ClientViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.item_client, parent, false);
            return new ClientViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ClientViewHolder holder, int position) {
            Client client = clientList.get(position);
            holder.textViewFirstName.setText(client.getFirstName());
            holder.textViewLastName.setText(client.getLastName());
            holder.textViewEmail.setText(client.getEmail());
            holder.textViewRole.setText(client.getRole());
            holder.textViewDob.setText(client.getDob());

            // Set onClick listener to select/unselect client
            holder.itemView.setOnClickListener(v -> {
                if (clientsToRemove.contains(client)) {
                    clientsToRemove.remove(client);
                    holder.itemView.setBackgroundColor(getResources().getColor(android.R.color.white));
                } else {
                    clientsToRemove.add(client);
                    holder.itemView.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                }
            });
        }

        @Override
        public int getItemCount() {
            return clientList.size();
        }

        public class ClientViewHolder extends RecyclerView.ViewHolder {
            TextView textViewFirstName, textViewLastName, textViewEmail, textViewRole, textViewDob;

            public ClientViewHolder(View itemView) {
                super(itemView);
                textViewFirstName = itemView.findViewById(R.id.textViewFirstName);
                textViewLastName = itemView.findViewById(R.id.textViewLastName);
                textViewEmail = itemView.findViewById(R.id.textViewEmail);
                textViewRole = itemView.findViewById(R.id.textViewRole);
                textViewDob = itemView.findViewById(R.id.textViewDob);
            }
        }
    }
}
