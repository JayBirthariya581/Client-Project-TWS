package com.g2c.clientP.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.g2c.clientP.Activities.PackageDetailActivity;
import com.g2c.clientP.R;
import com.g2c.clientP.SessionManager;
import com.g2c.clientP.databinding.CardPackageBinding;
import com.g2c.clientP.model.ModelPackage;

import java.util.ArrayList;

public class PackageAdapter extends RecyclerView.Adapter<PackageAdapter.MyViewHolder> {
    Context context;
    ArrayList<ModelPackage> packages;
    SessionManager sessionManager;
    String purpose;

    public PackageAdapter(Context context, ArrayList<ModelPackage> packages) {
        this.context = context;
        this.packages = packages;
        sessionManager = new SessionManager(context);
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PackageAdapter.MyViewHolder(LayoutInflater.from(context).inflate(R.layout.card_package, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ModelPackage m = packages.get(position);

        holder.binding.name.setText(m.getName());
        holder.binding.cost.setText(m.getCost());
        holder.binding.serviceCost.setText(m.getServiceCost());
        holder.binding.validity.setText(m.getValidity());
        holder.binding.serviceCount.setText(m.getServiceCount());
        holder.binding.description.setText(m.getDescription());


        if (purpose.equals("buy")) {
            holder.binding.buy.setVisibility(View.VISIBLE);
        }

        if (purpose.equals("include")) {
            holder.binding.include.setVisibility(View.VISIBLE);

        }

        holder.binding.include.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent data = new Intent();
                data.putExtra("packageDetails", m);
                data.putExtra("includeType","newPackage");
                ((Activity) context).setResult(Activity.RESULT_OK, data);
                ((Activity) context).finish();


            }
        });


        holder.binding.buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                context.startActivity(new Intent(context, PackageDetailActivity.class).putExtra("packageDetails", m));


            }
        });


    }

    @Override
    public int getItemCount() {
        return packages.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        CardPackageBinding binding;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = CardPackageBinding.bind(itemView);
        }
    }
}
