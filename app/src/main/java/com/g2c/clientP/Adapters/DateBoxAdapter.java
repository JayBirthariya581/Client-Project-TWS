package com.g2c.clientP.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.g2c.clientP.Helpers.TimeManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.g2c.clientP.Helpers.CustomProgressDialog;
import com.g2c.clientP.R;
import com.g2c.clientP.databinding.CardDateBoxBinding;
import com.g2c.clientP.model.TimeBoxModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class DateBoxAdapter extends RecyclerView.Adapter<DateBoxAdapter.MyViewHolder> {
    ArrayList<Long> date_list;
    ArrayList<Long> timeSlots;
    Context context;
    int selectedPosition = -1;
    TimeBoxAdapter timeBoxAdapter;
    CustomProgressDialog dialog;
    int mechanicCount, currentMechanicCount;
    SimpleDateFormat dayFormat, dateFormat, hour_format;
    TimeManager timeManager;

    public DateBoxAdapter(ArrayList<Long> date_list, Context context) {
        this.date_list = date_list;
        this.context = context;
        timeManager = new TimeManager();
        dayFormat = new SimpleDateFormat("EEE");
        dateFormat = new SimpleDateFormat("dd");
        hour_format = new SimpleDateFormat("kk");

    }

    public int getCurrentMechanicCount() {
        return currentMechanicCount;
    }

    public void setCurrentMechanicCount(int currentMechanicCount) {
        this.currentMechanicCount = currentMechanicCount;
    }

    public ArrayList<Long> getTimeSlots() {
        return timeSlots;
    }

    public void setTimeSlots(ArrayList<Long> timeSlots) {
        this.timeSlots = timeSlots;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    public void setSelectedPosition(int selectedPosition) {
        this.selectedPosition = selectedPosition;
    }

    public CustomProgressDialog getDialog() {
        return dialog;
    }

    public void setDialog(CustomProgressDialog dialog) {
        this.dialog = dialog;
    }


    public int getMechanicCount() {
        return mechanicCount;
    }

    public void setMechanicCount(int mechanicCount) {
        this.mechanicCount = mechanicCount;
    }

    public TimeBoxAdapter getTimeBoxAdapter() {
        return timeBoxAdapter;
    }

    public void setTimeBoxAdapter(TimeBoxAdapter timeBoxAdapter) {
        this.timeBoxAdapter = timeBoxAdapter;
    }


    public ArrayList<Long> getMlist() {
        return date_list;
    }

    public void setMlist(ArrayList<Long> date_list) {
        this.date_list = date_list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.card_date_box, parent, false);

        return new DateBoxAdapter.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Long m = date_list.get(position);


        holder.binding.date.setText(dateFormat.format(m));
        holder.binding.day.setText(dayFormat.format(m));


        if (selectedPosition == position) {

            holder.binding.getRoot().setCardBackgroundColor(Color.parseColor("#3E64FF"));
            holder.binding.getRoot().setStrokeColor(Color.parseColor("#3E64FF"));
            holder.binding.date.setTextColor(Color.parseColor("#ffffff"));
            holder.binding.day.setTextColor(Color.parseColor("#ffffff"));
        } else {
            holder.binding.getRoot().setCardBackgroundColor(Color.parseColor("#ffffff"));
            holder.binding.getRoot().setStrokeColor(Color.parseColor("#6B779A"));
            holder.binding.date.setTextColor(Color.parseColor("#6B779A"));
            holder.binding.day.setTextColor(Color.parseColor("#6B779A"));
        }


        if (timeSlots.size() > 0) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.show();
                    selectedPosition = position;
                    notifyDataSetChanged();

                    timeBoxAdapter.getMlist().clear();
                    timeBoxAdapter.setSelectedPosition(-1);


                    DatabaseReference slots = FirebaseDatabase.getInstance().getReference("AppManager").child("SlotManager")
                            .child("Slots").child(timeManager.getUsDate(m));


                    slots.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot date) {

                            if (date.exists()) {//if selected date exists in database

                                if (timeManager.getUsDate(timeManager.getCurrentTimeStamp()).equals(timeManager.getUsDate(m))) {//if its current day
                                    timeBoxAdapter.setCurrentDay("yes");
                                    for (Long timeSlot : timeSlots) {

                                        if (timeManager.getCurrentTimeStamp() <= timeManager.checkCurrentDaySlot(timeSlot, 2)) {//two hours before the shown time slot

                                            if (date.child(timeManager.getUsTime(timeSlot)).exists()) {//if this(j) time slot exists for that day

                                                if (date.child(timeManager.getUsTime(timeSlot)).getChildrenCount() < currentMechanicCount) {//if a mechanic is available

                                                    timeBoxAdapter.getMlist().add(new TimeBoxModel(timeSlot, "Available"));


                                                } else {
                                                    timeBoxAdapter.getMlist().add(new TimeBoxModel(timeSlot, "NotAvailable"));
                                                }

                                            } else {//if time slot doesn't exist in database
                                                timeBoxAdapter.getMlist().add(new TimeBoxModel(timeSlot, "Available"));
                                            }


                                        } else {
                                            timeBoxAdapter.getMlist().add(new TimeBoxModel(timeSlot, "NotAvailable"));
                                        }

                                    }
                                } else {//if its not current day
                                    timeBoxAdapter.setCurrentDay("no");
                                    for (Long timeSlot : timeSlots) {

                                        if (date.child(timeManager.getUsTime(timeSlot)).exists()) {//if this(j) time slot exists for that day

                                            if (date.child(timeManager.getUsTime(timeSlot)).getChildrenCount() < mechanicCount) {//if a mechanic is available

                                                timeBoxAdapter.getMlist().add(new TimeBoxModel(timeSlot, "Available"));


                                            } else {
                                                timeBoxAdapter.getMlist().add(new TimeBoxModel(timeSlot, "NotAvailable"));
                                            }

                                        } else {//if time slot doesn't exist in database
                                            timeBoxAdapter.getMlist().add(new TimeBoxModel(timeSlot, "Available"));
                                        }
                                    }

                                }
                            } else {//when selected date is not in database


                                if (timeManager.getUsDate(timeManager.getCurrentTimeStamp()).equals(timeManager.getUsDate(m))) {//if its current day
                                    timeBoxAdapter.setCurrentDay("yes");
                                    for (Long timeSlot : timeSlots) {

                                        if (timeManager.getCurrentTimeStamp() <= timeManager.checkCurrentDaySlot(timeSlot, 2)) {//show slots for two hours later from now
                                            timeBoxAdapter.getMlist().add(new TimeBoxModel(timeSlot, "Available"));
                                        } else {
                                            timeBoxAdapter.getMlist().add(new TimeBoxModel(timeSlot, "NotAvailable"));
                                        }
                                    }

                                } else {//when not current day
                                    timeBoxAdapter.setCurrentDay("no");
                                    for (Long timeSlot : timeSlots) {

                                        timeBoxAdapter.getMlist().add(new TimeBoxModel(timeSlot, "Available"));

                                    }


                                }


                            }

                            timeBoxAdapter.notifyDataSetChanged();
                            dialog.dismiss();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


                }
            });
        }


    }

    @Override
    public int getItemCount() {
        return date_list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        CardDateBoxBinding binding;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            binding = CardDateBoxBinding.bind(itemView);

        }
    }
}
