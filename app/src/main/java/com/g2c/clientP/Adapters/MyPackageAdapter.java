package com.g2c.clientP.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.g2c.clientP.R;
import com.g2c.clientP.databinding.CardMyPackageBinding;
import com.g2c.clientP.model.MyPackageModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class MyPackageAdapter extends RecyclerView.Adapter<MyPackageAdapter.MyViewHolder> {
    Context context;
    ArrayList<MyPackageModel> packages;

    public MyPackageAdapter(Context context, ArrayList<MyPackageModel> packages) {
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
        MyPackageModel m = packages.get(position);

        long l = Long.valueOf(m.getValidity().getEndDate());

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String endDate = sdf.format(l);

        holder.binding.name.setText(m.getName());
        holder.binding.cost.setText(m.getCost());
        holder.binding.serviceCost.setText(m.getServiceCost());
        holder.binding.endDate.setText(endDate);
        holder.binding.serviceCount.setText(m.getServiceCount());


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
