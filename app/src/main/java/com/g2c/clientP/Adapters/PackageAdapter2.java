package com.g2c.clientP.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.g2c.clientP.Helpers.TimeManager;
import com.g2c.clientP.R;
import com.g2c.clientP.SessionManager;
import com.g2c.clientP.databinding.CardPackageBinding;
import com.g2c.clientP.databinding.CardPackageUseBinding;
import com.g2c.clientP.model.MyPackageModel;

import java.util.ArrayList;

public class PackageAdapter2 extends RecyclerView.Adapter<PackageAdapter2.MyViewHolder> {
    Context context;
    ArrayList<MyPackageModel> packages;
    SessionManager sessionManager;
    TimeManager timeManager;

    public PackageAdapter2(Context context, ArrayList<MyPackageModel> packages) {
        this.context = context;
        this.packages = packages;
        timeManager = new TimeManager();
        sessionManager = new SessionManager(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PackageAdapter2.MyViewHolder(LayoutInflater.from(context).inflate(R.layout.card_package_use, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        MyPackageModel m = packages.get(position);

        holder.binding.name.setText(m.getName());
        holder.binding.serviceCost.setText(m.getServiceCost());
        holder.binding.validity.setText(timeManager.getDateFormat().format(Long.valueOf(m.getValidity().getEndDate())));
        holder.binding.serviceCount.setText(m.getServiceCount());



        holder.binding.use.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent data = new Intent();
                data.putExtra("packageDetails", m);
                data.putExtra("includeType","ownedPackage");
                ((Activity) context).setResult(Activity.RESULT_OK, data);
                ((Activity) context).finish();

            }
        });



    }

    @Override
    public int getItemCount() {
        return packages.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        CardPackageUseBinding binding;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = CardPackageUseBinding.bind(itemView);
        }
    }
}
