package com.g2c.clientP.Adapters;


import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.g2c.clientP.R;
import com.g2c.clientP.databinding.CardPickerBinding;

import java.util.ArrayList;

public class PickerAdapter extends RecyclerView.Adapter<PickerAdapter.MyViewHolder>{

    ArrayList<String> mlist;
    Context context;
    Dialog dPicker;
    int selected;

    public PickerAdapter(ArrayList<String> mlist, Context context, Dialog dPicker) {
        this.mlist = mlist;
        this.context = context;
        this.dPicker = dPicker;
        selected = -1;
    }

    public Dialog getDayPicker() {
        return dPicker;
    }

    public void setDayPicker(Dialog dPicker) {
        this.dPicker = dPicker;
    }

    public int getSelected() {
        return selected;
    }

    public void setSelected(int selected) {
        this.selected = selected;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.card_picker,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String d = mlist.get(position);

        holder.binding.value.setText(d);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selected = position;
                dPicker.dismiss();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mlist.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{


        CardPickerBinding binding;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = CardPickerBinding.bind(itemView);
        }
    }
}
