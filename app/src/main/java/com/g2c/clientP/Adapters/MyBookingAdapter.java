package com.g2c.clientP.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.g2c.clientP.Activities.ServiceListActivity;
import com.g2c.clientP.R;
import com.g2c.clientP.databinding.CardBookingBinding;
import com.g2c.clientP.model.ModelVehicle;

import java.util.ArrayList;

public class MyBookingAdapter extends RecyclerView.Adapter<MyBookingAdapter.MyViewHolder> {

    Context context;
    ArrayList<ModelVehicle> bookingList;


    public MyBookingAdapter(Context context, ArrayList<ModelVehicle> bookingList) {
        this.context = context;
        this.bookingList = bookingList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.card_booking,parent,false);
        return new MyBookingAdapter.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        ModelVehicle vehicle = bookingList.get(position);


        holder.binding.Company.setText(vehicle.getCompany());
        holder.binding.Model.setText(vehicle.getModel());
        holder.binding.vhNo.setText(vehicle.getVehicleNo());



        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, ServiceListActivity.class).putExtra("vehicleDetails",vehicle));


            }
        });


    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        CardBookingBinding binding;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            binding = CardBookingBinding.bind(itemView);

        }
    }
}
