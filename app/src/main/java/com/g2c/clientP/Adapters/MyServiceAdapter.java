package com.g2c.clientP.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.g2c.clientP.Activities.BookingDetailActivity;
import com.g2c.clientP.Helpers.TimeManager;
import com.g2c.clientP.R;
import com.g2c.clientP.databinding.CardBookingBinding;
import com.g2c.clientP.databinding.CardServiceBinding;
import com.g2c.clientP.model.ModelFinalService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class MyServiceAdapter extends RecyclerView.Adapter<MyServiceAdapter.MyViewHolder> {

    Context context;
    ArrayList<ModelFinalService> serviceList;
    SimpleDateFormat dateFormat;
    TimeManager timeManager;

    public MyServiceAdapter(Context context, ArrayList<ModelFinalService> serviceList) {
        this.context = context;
        this.serviceList = serviceList;
        timeManager = new TimeManager();
        dateFormat = new SimpleDateFormat("EEE, MMM d ''yy");

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


        String date_time = dateFormat.format(timeManager.getSpecificDayTimeStamp(service.getDate())) + " at " + timeManager.getTimeFormat().format(timeManager.getCurrentDayTimeStamp(service.getTime()));

        holder.binding.dateTime.setText(date_time);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, BookingDetailActivity.class).putExtra("serviceDetails", service));


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
