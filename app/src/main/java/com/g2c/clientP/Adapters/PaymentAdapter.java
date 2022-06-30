package com.g2c.clientP.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.g2c.clientP.Helpers.CustomProgressDialog;
import com.g2c.clientP.Helpers.PaymentCalculator;
import com.g2c.clientP.Helpers.ServiceDataContainer;
import com.g2c.clientP.R;
import com.g2c.clientP.databinding.ActivityServiceDetailBinding;
import com.g2c.clientP.databinding.CardPaymentBinding;
import com.g2c.clientP.model.PaymentBoxModel;

public class PaymentAdapter extends RecyclerView.Adapter<PaymentAdapter.MyViewHolder> {
    Context context;

    CustomProgressDialog dialog;
    ServiceDataContainer serviceDetails;
    PaymentCalculator paymentCalculator;

    TextView totalTextView;

    public ServiceDataContainer getServiceDetails() {
        return serviceDetails;
    }

    public void setServiceDetails(ServiceDataContainer serviceDetails) {
        this.serviceDetails = serviceDetails;
    }

    public TextView getTotalTextView() {
        return totalTextView;
    }

    public void setTotalTextView(TextView totalTextView) {
        this.totalTextView = totalTextView;
    }


    public CustomProgressDialog getDialog() {
        return dialog;
    }

    public void setDialog(CustomProgressDialog dialog) {
        this.dialog = dialog;
    }


    public PaymentAdapter(Context context) {
        this.context = context;

        dialog = new CustomProgressDialog(context);


    }


    public PaymentCalculator getPaymentCalculator() {
        return paymentCalculator;
    }

    public void setPaymentCalculator(PaymentCalculator paymentCalculator) {
        this.paymentCalculator = paymentCalculator;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PaymentAdapter.MyViewHolder(LayoutInflater.from(context).inflate(R.layout.card_payment, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        PaymentBoxModel p = paymentCalculator.getPaymentList().get(position);


        holder.binding.field.setText(p.getField());


        holder.binding.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();


                if (p.getType().equals("package")) {
                    paymentCalculator.getPackageList().clear();

                    for (int i = 0; i < paymentCalculator.getOtherOfferList().size(); i++) {
                        if (paymentCalculator.getOtherOfferList().get(i).getField().equals("Package Service Discount")) {
                            paymentCalculator.getOtherOfferList().remove(i);
                        }
                    }

                    if (serviceDetails != null) {
                        serviceDetails.setPackageDetails(null);
                    }

                }


                if(p.getType().equals("existingPackage")){
                    for (int i = 0; i < paymentCalculator.getOtherOfferList().size(); i++) {
                        if (paymentCalculator.getOtherOfferList().get(i).getField().equals("Package Service Discount")) {
                            paymentCalculator.getOtherOfferList().remove(i);
                        }
                    }

                    if (serviceDetails != null) {
                        serviceDetails.setPackageDetails(null);
                    }
                }

                if (p.getType().equals("coupon") || p.getType().equals("points")) {

                    paymentCalculator.getOfferList().clear();

                }


                paymentCalculator.calculateTotal();


                if (totalTextView != null) {

                    totalTextView.setText(paymentCalculator.getTotal().toString());
                }


                notifyDataSetChanged();
                dialog.dismiss();
            }
        });


        if (p.getType().equals("coupon") || p.getType().equals("points") || p.getType().equals("existingPackage")) {
            holder.binding.remove.setVisibility(View.VISIBLE);
            holder.binding.value.setText(p.getValue());
            holder.binding.r.setText("-₹");
            holder.binding.value.setTextColor(Color.parseColor("#019A49"));
            holder.binding.r.setTextColor(Color.parseColor("#019A49"));

        } else if (p.getType().equals("otherOffer")) {
            holder.binding.remove.setVisibility(View.GONE);
            holder.binding.value.setText(p.getValue());
            holder.binding.r.setText("-₹");
            holder.binding.value.setTextColor(Color.parseColor("#019A49"));
            holder.binding.r.setTextColor(Color.parseColor("#019A49"));

        } else if (p.getType().equals("package")) {
            holder.binding.remove.setVisibility(View.GONE);
            holder.binding.r.setText("₹");
            holder.binding.value.setTextColor(Color.parseColor("#000000"));
            holder.binding.r.setTextColor(Color.parseColor("#000000"));
            holder.binding.remove.setVisibility(View.VISIBLE);
            holder.binding.value.setText(String.valueOf(Integer.valueOf(p.getValue())));
        } else {
            holder.binding.remove.setVisibility(View.GONE);
            holder.binding.r.setText("₹");
            holder.binding.value.setTextColor(Color.parseColor("#000000"));
            holder.binding.r.setTextColor(Color.parseColor("#000000"));
            holder.binding.value.setText(p.getValue());
        }


    }

    @Override
    public int getItemCount() {
        return paymentCalculator.getPaymentList().size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        CardPaymentBinding binding;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = CardPaymentBinding.bind(itemView);


        }
    }
}
