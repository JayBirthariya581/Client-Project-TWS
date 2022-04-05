package com.geartocare.client.Adapters;

import android.content.Context;
import android.renderscript.ScriptGroup;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;

import com.geartocare.client.R;
import com.geartocare.client.databinding.ActivitySlotBookingBinding;
import com.geartocare.client.databinding.TwBrandCardBinding;

import java.util.ArrayList;

public class TwModelListAdapter extends RecyclerView.Adapter<TwModelListAdapter.MyViewHoldeer> {
    ArrayList<String> modelList;
    Context context;
    TextView modelTv;
    AlertDialog alertDialog;

    public void setAlertDialog(AlertDialog alertDialog) {
        this.alertDialog = alertDialog;
    }

    public TwModelListAdapter(Context context,ArrayList<String> modelList, TextView modelTv) {
        this.modelList = modelList;
        this.context = context;
        this.modelTv = modelTv;
    }

    public ArrayList<String> getModelList() {
        return modelList;
    }

    public void setModelList(ArrayList<String> modelList) {
        this.modelList = modelList;
    }

    @NonNull
    @Override
    public MyViewHoldeer onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.tw_brand_card,parent,false);
        return new TwModelListAdapter.MyViewHoldeer(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHoldeer holder, int position) {

        holder.binding.brandName.setText(modelList.get(position));


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 modelTv.setText(modelList.get(position));
                alertDialog.dismiss();
            }
        });

    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    class MyViewHoldeer extends RecyclerView.ViewHolder{
        TwBrandCardBinding binding;
        public MyViewHoldeer(@NonNull View itemView) {
            super(itemView);
            binding = TwBrandCardBinding.bind(itemView);
        }
    }

}
