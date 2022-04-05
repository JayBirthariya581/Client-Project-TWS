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


            if(selectedPosition==position){
                holder.binding.main.setBackgroundResource(R.drawable.selected_back);
                holder.binding.date.setTextColor(Color.parseColor("#4a60d0"));
                holder.binding.day.setTextColor(Color.parseColor("#4a60d0"));
            }else{
                holder.binding.main.setBackgroundResource(R.drawable.time_box_back_1);
                holder.binding.date.setTextColor(Color.parseColor("#000000"));
                holder.binding.day.setTextColor(Color.parseColor("#7c7c7c"));
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    selectedPosition=position;
                    notifyDataSetChanged();


                }
            });
        }else{

            holder.binding.main.setBackgroundResource(R.drawable.not_available_back);
            holder.binding.date.setTextColor(context.getResources().getColor(R.color.grey));
            holder.binding.day.setTextColor(context.getResources().getColor(R.color.grey));
            holder.itemView.setFocusable(false);
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
