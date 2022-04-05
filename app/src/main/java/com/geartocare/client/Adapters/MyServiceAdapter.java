package com.geartocare.client.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.geartocare.client.Activities.BookingDetailActivity;
import com.geartocare.client.Activities.ServiceListActivity;
import com.geartocare.client.R;
import com.geartocare.client.databinding.CardBookingBinding;
import com.geartocare.client.databinding.CardServiceBinding;
import com.geartocare.client.model.ModelFinalService;
import com.geartocare.client.model.ModelVehicle;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class MyServiceAdapter extends RecyclerView.Adapter<MyServiceAdapter.MyViewHolder> {

    Context context;
    ArrayList<ModelFinalService> serviceList;


    public MyServiceAdapter(Context context, ArrayList<ModelFinalService> serviceList) {
        this.context = context;
        this.serviceList = serviceList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.card_service, parent, false);
        return new MyServiceAdapter.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        ModelFinalService service = serviceList.get(position);


        SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d ''yy");

        long l = Long.valueOf(service.getDate());

        String date_time = sdf.format(l)+" at "+ service.getTime();

        holder.binding.dateTime.setText(date_time);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, BookingDetailActivity.class).putExtra("serviceID",service.getServiceID()));


            }
        });


    }

    @Override
    public int getItemCount() {
        return serviceList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        CardServiceBinding binding;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            binding = CardServiceBinding.bind(itemView);

        }
    }
}
