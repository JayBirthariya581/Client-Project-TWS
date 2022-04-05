package com.geartocare.client.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.geartocare.client.R;
import com.geartocare.client.model.ServiceDomainModel;

import java.util.ArrayList;

public class ServiceCatlogAdapter extends RecyclerView.Adapter<ServiceCatlogAdapter.serviceDomainHolder> {
    Context context;
    ArrayList<ServiceDomainModel> mlist;


    public ServiceCatlogAdapter(Context context, ArrayList<ServiceDomainModel> mlist) {
        this.context = context;
        this.mlist = mlist;
    }

    @NonNull
    @Override
    public serviceDomainHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.card_service_domain,parent,false);
        return new ServiceCatlogAdapter.serviceDomainHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull serviceDomainHolder holder, int position) {
        ServiceDomainModel s = mlist.get(position);
        Glide.with(context).load(s.getDomain_ic()).into(holder.domainIc);
        holder.domainName.setText(s.getDomain_name());
    }

    @Override
    public int getItemCount() {
        return mlist.size();
    }

    class serviceDomainHolder extends RecyclerView.ViewHolder {
        ImageView domainIc;
        TextView domainName;
        public serviceDomainHolder(@NonNull View itemView) {
            super(itemView);
            domainIc = itemView.findViewById(R.id.service_domain_ic);
            domainName = itemView.findViewById(R.id.service_domain_name);

        }
    }
}
