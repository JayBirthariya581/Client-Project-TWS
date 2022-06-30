package com.g2c.clientP.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.g2c.clientP.SessionManager;
import com.g2c.clientP.model.ModelVehicle;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.g2c.clientP.Adapters.MyBookingAdapter;
import com.g2c.clientP.Helpers.CustomProgressDialog;
import com.g2c.clientP.databinding.FragmentBookingsBinding;

import java.util.ArrayList;


public class BookingsFragment extends Fragment {
    FragmentBookingsBinding binding;
    MyBookingAdapter adapter;
    ArrayList<ModelVehicle> bookingList;
    CustomProgressDialog dialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentBookingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dialog = new CustomProgressDialog(getContext());

        bookingList = new ArrayList<>();
        adapter = new MyBookingAdapter(getContext(),bookingList);


        binding.rvBookingList.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvBookingList.setHasFixedSize(true);
        binding.rvBookingList.setAdapter(adapter);




        makeList();


    }

    private void makeList() {
        dialog.show();
        bookingList.clear();

        FirebaseDatabase.getInstance().getReference("Users").child(new SessionManager(getContext()).getUsersDetailsFromSessions().get(SessionManager.KEY_UID)).child("vehicles")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot bookings) {


                        if(bookings.exists()){

                            for(DataSnapshot booking : bookings.getChildren()){

                                ModelVehicle bookingFromDB = booking.getValue(ModelVehicle.class);

                                bookingList.add(bookingFromDB);




                            }
                            adapter.notifyDataSetChanged();
                            dialog.dismiss();


                        }else{
                            dialog.dismiss();
                        }



                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


}