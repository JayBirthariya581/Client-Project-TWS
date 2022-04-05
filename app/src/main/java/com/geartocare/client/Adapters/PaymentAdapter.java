package com.geartocare.client.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.geartocare.client.R;
import com.geartocare.client.databinding.CardPaymentBinding;
import com.geartocare.client.model.ModelPayment;
import com.geartocare.client.model.PaymentBoxModel;

import java.util.ArrayList;

public class PaymentAdapter extends RecyclerView.Adapter<PaymentAdapter.MyViewHolder> {
    Context context;
    ArrayList<PaymentBoxModel> paymentList;


    public PaymentAdapter(Context context, ArrayList<PaymentBoxModel> paymentList) {
        this.context = context;
        this.paymentList = paymentList;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public ArrayList<PaymentBoxModel> getPaymentList() {
        return paymentList;
    }

    public void setPaymentList(ArrayList<PaymentBoxModel> paymentList) {
        this.paymentList = paymentList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PaymentAdapter.MyViewHolder(LayoutInflater.from(context).inflate(R.layout.card_payment,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        PaymentBoxModel p = paymentList.get(position);




        holder.binding.field.setText(p.getField());


        if(p.getField().equals("Coupon value") || p.getField().equals("G2C points")){

            holder.binding.value.setText(String.valueOf(Integer.valueOf(p.getValue())*-1));
            holder.binding.r.setText("-₹");
            holder.binding.value.setTextColor(Color.parseColor("#019A49"));
            holder.binding.r.setTextColor(Color.parseColor("#019A49"));
        }else{

            holder.binding.value.setText(p.getValue());
        }


    }

    @Override
    public int getItemCount() {
        return paymentList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        CardPaymentBinding binding;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = CardPaymentBinding.bind(itemView);


        }
    }
}
