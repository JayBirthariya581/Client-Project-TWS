package com.g2c.clientP.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.g2c.clientP.R;
import com.g2c.clientP.databinding.ServiceCheckListBinding;

import java.util.ArrayList;

public class ServiceCheckListAdapter extends RecyclerView.Adapter<ServiceCheckListAdapter.MyViewHolder> {
    Context context;
    ArrayList<String> list;
    String serviceID;


    public ServiceCheckListAdapter(Context context, ArrayList<String> list, String serviceID) {
        this.context = context;
        this.list = list;
        this.serviceID = serviceID;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.sv_cl_card,parent,false);
        return new ServiceCheckListAdapter.MyViewHolder(v);
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
