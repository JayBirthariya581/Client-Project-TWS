package com.g2c.clientP.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.g2c.clientP.R;
import com.g2c.clientP.databinding.CardAvailableAreaBinding;
import com.g2c.clientP.model.ModelArea;
import com.g2c.clientP.model.ModelLocation;

import java.util.ArrayList;

public class AvailableAreaAdapter extends RecyclerView.Adapter<AvailableAreaAdapter.MyViewHolder>{

    Context context;
    ArrayList<ModelArea> areaList;


    public AvailableAreaAdapter(Context context, ArrayList<ModelArea> areaList) {
        this.context = context;
        this.areaList = areaList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AvailableAreaAdapter.MyViewHolder(LayoutInflater.from(context).inflate(R.layout.card_available_area,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ModelArea area = areaList.get(position);

        holder.binding.area.setText(area.getName());
        holder.binding.landmark.setText("Landmark : "+area.getLandMarkName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ModelLocation a = new ModelLocation();
                a.setLat(String.valueOf(area.getLandMark().latitude));
                a.setLng(String.valueOf(area.getLandMark().longitude));
                a.setTxt(area.getLandMarkName());

                ((Activity) context).setResult(Activity.RESULT_OK,new Intent().putExtra("areaDetails",a));
                ((Activity) context).finish();
            }
        });

    }

    @Override
    public int getItemCount() {
        return areaList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        CardAvailableAreaBinding binding;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = CardAvailableAreaBinding.bind(itemView);
        }
    }
}
