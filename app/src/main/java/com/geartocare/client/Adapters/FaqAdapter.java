package com.geartocare.client.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.geartocare.client.R;
import com.geartocare.client.databinding.CardFaqBinding;
import com.geartocare.client.model.ModelFaq;

import java.util.ArrayList;

public class FaqAdapter extends RecyclerView.Adapter<FaqAdapter.MyViewHolder> {
    Context context;
    ArrayList<ModelFaq> mList;


    public FaqAdapter(Context context, ArrayList<ModelFaq> mList) {
        this.context = context;
        this.mList = mList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FaqAdapter.MyViewHolder(LayoutInflater.from(context).inflate(R.layout.card_faq,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ModelFaq m = mList.get(position);


        holder.binding.question.setText(m.getQuestion());
        holder.binding.answer.setText(m.getAnswer());


        holder.binding.open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.binding.open.setVisibility(View.GONE);
                holder.binding.close.setVisibility(View.VISIBLE);
                holder.binding.answer.setVisibility(View.VISIBLE);

            }
        });


        holder.binding.close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.binding.close.setVisibility(View.GONE);
                holder.binding.answer.setVisibility(View.GONE);
                holder.binding.open.setVisibility(View.VISIBLE);
            }
        });


    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class MyViewHolder extends  RecyclerView.ViewHolder{

        CardFaqBinding binding;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            binding = CardFaqBinding.bind(itemView);
        }
    }
}
