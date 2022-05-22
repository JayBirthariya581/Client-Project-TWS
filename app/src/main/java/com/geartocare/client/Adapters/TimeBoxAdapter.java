package com.geartocare.client.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.geartocare.client.R;
import com.geartocare.client.databinding.CardDateBoxBinding;
import com.geartocare.client.databinding.CardTimeBoxBinding;
import com.geartocare.client.model.TimeBoxModel;

import java.util.ArrayList;

public class TimeBoxAdapter extends RecyclerView.Adapter<TimeBoxAdapter.MyViewHolder> {
    ArrayList<TimeBoxModel> mlist;
    Context context;

    int selectedPosition=-1;

    public TimeBoxAdapter(ArrayList<TimeBoxModel> mlist, Context context) {
        this.mlist = mlist;
        this.context = context;
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    public void setSelectedPosition(int selectedPosition) {
        this.selectedPosition = selectedPosition;
    }

    public ArrayList<TimeBoxModel> getMlist() {
        return mlist;
    }

    public void setMlist(ArrayList<TimeBoxModel> mlist) {
        this.mlist = mlist;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.card_time_box, parent, false);

        return new TimeBoxAdapter.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String time = mlist.get(position).getTime();
        holder.binding.date.setText(time);
        holder.binding.day.setText("");






        if(mlist.get(position).getStatus().equals("Available")){


            if (selectedPosition == position) {

                holder.binding.getRoot().setCardBackgroundColor(Color.parseColor("#3E64FF"));
                holder.binding.getRoot().setStrokeColor(Color.parseColor("#3E64FF"));
                holder.binding.date.setTextColor(Color.parseColor("#ffffff"));
                holder.binding.day.setTextColor(Color.parseColor("#ffffff"));
            } else {
                holder.binding.getRoot().setCardBackgroundColor(Color.parseColor("#ffffff"));
                holder.binding.getRoot().setStrokeColor(Color.parseColor("#6B779A"));
                holder.binding.date.setTextColor(Color.parseColor("#6B779A"));
                holder.binding.day.setTextColor(Color.parseColor("#6B779A"));
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    selectedPosition=position;
                    notifyDataSetChanged();


                }
            });
        }else{

            holder.binding.getRoot().setCardBackgroundColor(context.getResources().getColor(R.color.cream));
            holder.binding.getRoot().setStrokeColor(context.getResources().getColor(R.color.cream));
            holder.binding.date.setTextColor(Color.parseColor("#6B779A"));
            holder.binding.day.setTextColor(Color.parseColor("#6B779A"));
            holder.itemView.setFocusable(false);
            holder.itemView.setClickable(false);
            holder.itemView.setOnClickListener(null);
            holder.binding.getRoot().setOnClickListener(null);
        }








    }

    @Override
    public int getItemCount() {
        return mlist.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        CardTimeBoxBinding binding;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            binding = CardTimeBoxBinding.bind(itemView);

        }


    }
}
