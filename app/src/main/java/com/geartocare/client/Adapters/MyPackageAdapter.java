package com.geartocare.client.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.geartocare.client.R;
import com.geartocare.client.databinding.CardMyPackageBinding;
import com.geartocare.client.databinding.CardPackageBinding;
import com.geartocare.client.model.ModelMyPackage;
import com.geartocare.client.model.ModelPackage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class MyPackageAdapter extends RecyclerView.Adapter<MyPackageAdapter.MyViewHolder> {
    Context context;
    ArrayList<ModelMyPackage> packages;

    public MyPackageAdapter(Context context, ArrayList<ModelMyPackage> packages) {
        this.context = context;
        this.packages = packages;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyPackageAdapter.MyViewHolder(LayoutInflater.from(context).inflate(R.layout.card_my_package, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ModelMyPackage m = packages.get(position);

        long l = Long.valueOf(m.getValidity().getEndDate());

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String endDate = sdf.format(l);

        holder.binding.name.setText(m.getName());
        holder.binding.cost.setText(m.getCost());
        holder.binding.serviceCost.setText(m.getServiceCost());
        holder.binding.endDate.setText(endDate);
        holder.binding.vehicleCount.setText(m.getVehicleCount());


    }

    @Override
    public int getItemCount() {
        return packages.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        CardMyPackageBinding binding;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = CardMyPackageBinding.bind(itemView);
        }
    }
}
