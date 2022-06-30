package com.g2c.clientP.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.g2c.clientP.Helpers.ServiceDataContainer;
import com.g2c.clientP.R;
import com.g2c.clientP.databinding.CardDateBoxBinding;
import com.g2c.clientP.databinding.CardTimeBoxBinding;
import com.g2c.clientP.model.PaymentBoxModel;
import com.g2c.clientP.model.TimeBoxModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class TimeBoxAdapter extends RecyclerView.Adapter<TimeBoxAdapter.MyViewHolder> {
    ArrayList<TimeBoxModel> time_list;
    Context context;
    String currentDay;
    PaymentBoxModel currentDayDiscount;
    String discountPercentage;
    SimpleDateFormat timeFormat, apFormat;
    ServiceDataContainer serviceDetails;
    int selectedPosition = -1;

    public TimeBoxAdapter(ArrayList<TimeBoxModel> time_list, Context context) {
        this.time_list = time_list;
        this.context = context;
        timeFormat = new SimpleDateFormat("hh:mm");
        apFormat = new SimpleDateFormat("a");
        currentDay = "no";
    }

    public String getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(String discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public ServiceDataContainer getServiceDetails() {
        return serviceDetails;
    }

    public void setServiceDetails(ServiceDataContainer serviceDetails) {
        this.serviceDetails = serviceDetails;
    }

    public PaymentBoxModel getCurrentDayDiscount() {
        return currentDayDiscount;
    }

    public void setCurrentDayDiscount(PaymentBoxModel currentDayDiscount) {
        this.currentDayDiscount = currentDayDiscount;
    }

    public String getCurrentDay() {
        return currentDay;
    }

    public void setCurrentDay(String currentDay) {
        this.currentDay = currentDay;
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    public void setSelectedPosition(int selectedPosition) {
        this.selectedPosition = selectedPosition;
    }

    public ArrayList<TimeBoxModel> getMlist() {
        return time_list;
    }

    public void setMlist(ArrayList<TimeBoxModel> time_list) {
        this.time_list = time_list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.card_time_box, parent, false);

        return new TimeBoxAdapter.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Long timeStamp = time_list.get(position).getTimeStamp();

        holder.binding.time.setText(timeFormat.format(timeStamp));
        holder.binding.ap.setText(apFormat.format(timeStamp));


        if (time_list.get(position).getStatus().equals("Available")) {

            if (currentDay.equals("yes")) {
                holder.binding.currentDayOff.setText(discountPercentage+"% OFF");
                holder.binding.currentDayOff.setVisibility(View.VISIBLE);
            } else {
                holder.binding.currentDayOff.setVisibility(View.GONE);

            }


            if (selectedPosition == position) {

                holder.binding.mainBox.setCardBackgroundColor(Color.parseColor("#3E64FF"));
                holder.binding.mainBox.setStrokeColor(Color.parseColor("#3E64FF"));
                holder.binding.time.setTextColor(Color.parseColor("#ffffff"));
                holder.binding.ap.setTextColor(Color.parseColor("#ffffff"));
            } else {
                holder.binding.mainBox.setCardBackgroundColor(Color.parseColor("#ffffff"));
                holder.binding.mainBox.setStrokeColor(Color.parseColor("#6B779A"));
                holder.binding.time.setTextColor(Color.parseColor("#6B779A"));
                holder.binding.ap.setTextColor(Color.parseColor("#6B779A"));
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (currentDay.equals("yes")) {
                        serviceDetails.setCurrentDayDiscount(currentDayDiscount);
                    } else {
                        serviceDetails.setCurrentDayDiscount(null);
                    }
                    selectedPosition = position;
                    notifyDataSetChanged();


                }
            });
        } else {
            holder.binding.currentDayOff.setVisibility(View.GONE);
            holder.binding.mainBox.setCardBackgroundColor(context.getResources().getColor(R.color.cream));
            holder.binding.mainBox.setStrokeColor(context.getResources().getColor(R.color.cream));
            holder.binding.time.setTextColor(Color.parseColor("#6B779A"));
            holder.binding.ap.setTextColor(Color.parseColor("#6B779A"));
            holder.itemView.setFocusable(false);
            holder.itemView.setClickable(false);
            holder.itemView.setOnClickListener(null);
            holder.binding.getRoot().setOnClickListener(null);
        }


    }

    @Override
    public int getItemCount() {
        return time_list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        CardTimeBoxBinding binding;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            binding = CardTimeBoxBinding.bind(itemView);

        }


    }
}
