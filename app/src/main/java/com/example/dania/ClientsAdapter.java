package com.example.dania;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ClientsAdapter extends RecyclerView.Adapter<ClientsAdapter.ClientViewHolder> {

    private ArrayList<String> clientsList;

    public ClientsAdapter(ArrayList<String> clientsList) {
        this.clientsList = clientsList;
    }

    @Override
    public ClientViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        return new ClientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ClientViewHolder holder, int position) {
        String clientEmail = clientsList.get(position);
        holder.clientEmailTextView.setText(clientEmail);
    }

    @Override
    public int getItemCount() {
        return clientsList.size();
    }

    public static class ClientViewHolder extends RecyclerView.ViewHolder {

        public TextView clientEmailTextView;

        public ClientViewHolder(View itemView) {
            super(itemView);
            clientEmailTextView = itemView.findViewById(android.R.id.text1);
        }
    }
}

