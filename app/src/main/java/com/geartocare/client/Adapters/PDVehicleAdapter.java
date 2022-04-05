package com.geartocare.client.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.geartocare.client.Activities.ServiceListActivity;
import com.geartocare.client.R;
import com.geartocare.client.databinding.CardBookingBinding;
import com.geartocare.client.model.ModelVehicle;

import java.util.ArrayList;
import java.util.HashMap;

public class PDVehicleAdapter extends RecyclerView.Adapter<PDVehicleAdapter.MyViewHolder> {

    Context context;
    ArrayList<ModelVehicle> bookingList;


    public PDVehicleAdapter(Context context, ArrayList<ModelVehicle> bookingList) {
        this.context = context;
        this.bookingList = bookingList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.card_booking,parent,false);
        return new PDVehicleAdapter.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        ModelVehicle booking = bookingList.get(position);


        holder.binding.Company.setText(booking.getCompany());
        holder.binding.Model.setText(booking.getModel());
        holder.binding.vhNo.setText(booking.getVehicleNo());

        String vehicleID = booking.getCompany()+"_"+booking.getModel()+"_"+booking.getVehicleNo();


        HashMap<String,String> vehicleDetails = new HashMap<>();

        vehicleDetails.put("Company",booking.getCompany());
        vehicleDetails.put("Model",booking.getModel());
        vehicleDetails.put("VhNo",booking.getVehicleNo());
        vehicleDetails.put("VehicleID",vehicleID);



        /*holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, ServiceListActivity.class).putExtra("vehicleDetails",vehicleDetails));


            }
        });*/


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
