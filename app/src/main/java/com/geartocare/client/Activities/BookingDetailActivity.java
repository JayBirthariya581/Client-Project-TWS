package com.geartocare.client.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.geartocare.client.R;
import com.geartocare.client.SessionManager;
import com.geartocare.client.databinding.ActivityBookingDetailBinding;
import com.geartocare.client.model.ModelFinalService;

import java.text.SimpleDateFormat;
import java.util.HashMap;

public class BookingDetailActivity extends AppCompatActivity {
    ActivityBookingDetailBinding binding;
    String serviceID;
    HashMap<String, String> userDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBookingDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().setStatusBarColor(ContextCompat.getColor(BookingDetailActivity.this, R.color.black));
        userDetails = new SessionManager(BookingDetailActivity.this).getUsersDetailsFromSessions();
        serviceID = getIntent().getStringExtra("serviceID");


        FirebaseDatabase.getInstance().getReference("Users").child(userDetails.get(SessionManager.KEY_UID)).child("services")
                .child(serviceID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot service) {

                if (service.exists()) {

                    ModelFinalService serviceModel = service.getValue(ModelFinalService.class);


                    binding.itemTime.setText(serviceModel.getTime());

                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    long l = Long.valueOf(serviceModel.getDate());
                    binding.itemDate.setText(sdf.format(l));

                    binding.itemLocation.setText(serviceModel.getLocation().getTxt());


                    /*binding.itemC.setText(serviceModel.getVehicle().getCompany());
                    binding.itemM.setText(serviceModel.getVehicle().getModel());
                    binding.itemVhNo.setText(serviceModel.getVehicle().getVehicleNo());*/


                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        binding.help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });


    }
}