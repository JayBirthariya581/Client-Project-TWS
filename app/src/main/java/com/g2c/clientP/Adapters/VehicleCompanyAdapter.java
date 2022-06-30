package com.g2c.clientP.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.g2c.clientP.R;
import com.g2c.clientP.databinding.CardVhCompanyBinding;

import java.util.ArrayList;

public class VehicleCompanyAdapter extends RecyclerView.Adapter<VehicleCompanyAdapter.MyViewHolder> {

    Context context;
    ArrayList<String> logoList;


    public VehicleCompanyAdapter(Context context, ArrayList<String> logoList) {
        this.context = context;
        this.logoList = logoList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VehicleCompanyAdapter.MyViewHolder(LayoutInflater.from(context).inflate(R.layout.card_vh_company, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {


    }

    @Override
    public int getItemCount() {
        return logoList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        CardVhCompanyBinding binding;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            binding = CardVhCompanyBinding.bind(itemView);
        }
    }
}
