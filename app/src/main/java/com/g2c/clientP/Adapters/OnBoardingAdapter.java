package com.g2c.clientP.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.g2c.clientP.R;
import com.g2c.clientP.databinding.CardOnBoardingBinding;

import java.util.ArrayList;

public class OnBoardingAdapter extends RecyclerView.Adapter<OnBoardingAdapter.MyViewHolder>{
    Context context;
    ArrayList<Integer> images;


    public OnBoardingAdapter(Context context, ArrayList<Integer> images) {
        this.context = context;
        this.images = images;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new OnBoardingAdapter.MyViewHolder(LayoutInflater.from(context).inflate(R.layout.card_on_boarding,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.binding.imageView.setImageResource(images.get(position));
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        CardOnBoardingBinding binding;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = CardOnBoardingBinding.bind(itemView);
        }
    }
}
