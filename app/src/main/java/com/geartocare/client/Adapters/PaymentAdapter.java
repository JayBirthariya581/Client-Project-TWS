package com.geartocare.client.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.geartocare.client.Helpers.CustomProgressDialog;
import com.geartocare.client.R;
import com.geartocare.client.databinding.CardPaymentBinding;
import com.geartocare.client.model.ModelPackage;
import com.geartocare.client.model.ModelPayment;
import com.geartocare.client.model.PaymentBoxModel;

import java.util.ArrayList;

public class PaymentAdapter extends RecyclerView.Adapter<PaymentAdapter.MyViewHolder> {
    Context context;
    ArrayList<PaymentBoxModel> paymentList;
    CustomProgressDialog dialog;
    ArrayList<PaymentBoxModel> originalPaymentList;
    ModelPackage packageDetails;
    PaymentBoxModel discountDetails;
    Integer total, orignalTotal;


    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getOrignalTotal() {
        return orignalTotal;
    }

    public void setOrignalTotal(Integer orignalTotal) {
        this.orignalTotal = orignalTotal;
    }

    public ModelPackage getPackageDetails() {
        return packageDetails;
    }

    public void setPackageDetails(ModelPackage packageDetails) {
        this.packageDetails = packageDetails;
    }

    public PaymentBoxModel getDiscountDetails() {
        return discountDetails;
    }

    public void setDiscountDetails(PaymentBoxModel discountDetails) {
        this.discountDetails = discountDetails;
    }

    public CustomProgressDialog getDialog() {
        return dialog;
    }

    public void setDialog(CustomProgressDialog dialog) {
        this.dialog = dialog;
    }

    public ArrayList<PaymentBoxModel> getOriginalPaymentList() {
        return originalPaymentList;
    }

    public void setOriginalPaymentList(ArrayList<PaymentBoxModel> originalPaymentList) {
        this.originalPaymentList = originalPaymentList;
    }

    public PaymentAdapter(Context context, ArrayList<PaymentBoxModel> paymentList) {
        this.context = context;
        this.paymentList = paymentList;
        dialog = new CustomProgressDialog(context);


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
        return new PaymentAdapter.MyViewHolder(LayoutInflater.from(context).inflate(R.layout.card_payment, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        PaymentBoxModel p = paymentList.get(position);


        holder.binding.field.setText(p.getField());


        holder.binding.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
                //total = total - Integer.valueOf(paymentList.get(position).getValue());

                if (p.getType().equals("package")) {
                    packageDetails = null;
                }
                if (p.getType().equals("coupon") || p.getType().equals("points")) {
                    discountDetails = null;

                }
                paymentList.remove(position);
                notifyDataSetChanged();
                dialog.dismiss();
            }
        });

        if (p.getType().equals("coupon") || p.getType().equals("points")) {
            holder.binding.remove.setVisibility(View.VISIBLE);
            holder.binding.value.setText(String.valueOf(Integer.valueOf(p.getValue()) * -1));
            holder.binding.r.setText("-₹");
            holder.binding.value.setTextColor(Color.parseColor("#019A49"));
            holder.binding.r.setTextColor(Color.parseColor("#019A49"));

        } else if (p.getType().equals("package")) {
            holder.binding.r.setText("₹");
            holder.binding.value.setTextColor(Color.parseColor("#000000"));
            holder.binding.r.setTextColor(Color.parseColor("#000000"));
            holder.binding.remove.setVisibility(View.VISIBLE);
            holder.binding.value.setText(p.getValue());
        } else {
            holder.binding.r.setText("₹");
            holder.binding.value.setTextColor(Color.parseColor("#000000"));
            holder.binding.r.setTextColor(Color.parseColor("#000000"));
            holder.binding.value.setText(p.getValue());
        }


    }

    @Override
    public int getItemCount() {
        return paymentList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        CardPaymentBinding binding;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = CardPaymentBinding.bind(itemView);


        }
    }
}
