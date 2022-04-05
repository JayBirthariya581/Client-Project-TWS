package com.geartocare.client.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.geartocare.client.R;

import java.util.ArrayList;

public class ServiceCheckListActAdapter extends RecyclerView.Adapter<ServiceCheckListActAdapter.MyViewHolder> {
    Context context;
    ArrayList<String> list;
    String serviceID;


    public ServiceCheckListActAdapter(Context context, ArrayList<String> list, String serviceID) {
        this.context = context;
        this.list = list;
        this.serviceID = serviceID;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.sv_cl_card_act,parent,false);
        return new ServiceCheckListActAdapter.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.cl_value.setText(list.get(position));

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView cl_value;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            cl_value = itemView.findViewById(R.id.cl_value);
        }
    }
}
