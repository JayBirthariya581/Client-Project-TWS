package com.g2c.clientP.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;

import com.g2c.clientP.Helpers.NetworkChangeListener;
import com.g2c.clientP.Helpers.ServiceDataContainer;
import com.g2c.clientP.Helpers.TimeManager;
import com.g2c.clientP.R;
import com.g2c.clientP.databinding.ActivityConfirmationBinding;

public class ConfirmationActivity extends AppCompatActivity {
    ActivityConfirmationBinding binding;
    ServiceDataContainer serviceDetails;
    TimeManager timeManager;

     NetworkChangeListener networkChangeListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        networkChangeListener = new NetworkChangeListener(ConfirmationActivity.this);
        binding = ActivityConfirmationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        timeManager = new TimeManager();
        serviceDetails = (ServiceDataContainer) getIntent().getSerializableExtra("serviceDetails");
        


        binding.serviceDate.setText(serviceDetails.getServiceData().getDate().replace("_","/"));
        binding.serviceTime.setText(timeManager.getTimeFormat().format(timeManager.getCurrentDayTimeStamp(serviceDetails.getServiceData().getTime())));
        binding.Company.setText(serviceDetails.getVehicle().getCompany());
        binding.Model.setText(serviceDetails.getVehicle().getModel());
        binding.vhNo.setText(serviceDetails.getVehicle().getVehicleNo());
        binding.serviceLocation.setText(serviceDetails.getServiceData().getLocation().getTxt());




        binding.returnMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ConfirmationActivity.this,MainActivity.class);

                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                //finish();


            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeListener,filter);

    }

    @Override
    protected void onStop() {
        unregisterReceiver(networkChangeListener);
        super.onStop();
    }
}