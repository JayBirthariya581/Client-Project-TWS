package com.g2c.clientP.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.g2c.clientP.R;
import com.g2c.clientP.databinding.CardPaymentHolderBinding;
import com.g2c.clientP.model.PaymentHolderModel;

import java.util.ArrayList;

public class PaymentHolderAdapter extends RecyclerView.Adapter<PaymentHolderAdapter.MyViewHolder> {
    Context context;
    ArrayList<PaymentHolderModel> mList;

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.card_payment_holder,parent,false);


        return new PaymentHolderAdapter.MyViewHolder(v);


    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        PaymentHolderModel p = mList.get(position);


        holder.binding.field.setText(p.getField());
        holder.binding.value.setText(p.getValue());


    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class MyViewHolder extends  RecyclerView.ViewHolder{
        CardPaymentHolderBinding binding;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            binding = CardPaymentHolderBinding.bind(itemView);
        }
    }

}
