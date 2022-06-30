package com.g2c.clientP.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;

import com.g2c.clientP.Helpers.NetworkChangeListener;
import com.g2c.clientP.R;
import com.g2c.clientP.SessionManager;
import com.g2c.clientP.databinding.ActivityBookingDetailBinding;
import com.g2c.clientP.model.ModelFinalService;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class BookingDetailActivity extends AppCompatActivity {
    ActivityBookingDetailBinding binding;
    HashMap<String, String> userDetails;
    ModelFinalService serviceDetails;
    Calendar calendar;
    String[] timeArray, dateArray;
    String Tarray[];

     NetworkChangeListener networkChangeListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        networkChangeListener = new NetworkChangeListener(BookingDetailActivity.this);
        binding = ActivityBookingDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        userDetails = new SessionManager(BookingDetailActivity.this).getUsersDetailsFromSessions();
        serviceDetails = (ModelFinalService) getIntent().getSerializableExtra("serviceDetails");
        calendar = Calendar.getInstance();


        fillDetails();


        binding.help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });


    }

    private void fillDetails() {
        Tarray = serviceDetails.getTime().split("_");
        calendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf(Tarray[0]));
        calendar.set(Calendar.MINUTE, Integer.valueOf(Tarray[1]));
        dateArray = serviceDetails.getDate().split("_");

        calendar.set(Calendar.DATE, Integer.valueOf(dateArray[0]));
        calendar.set(Calendar.MONTH, Integer.valueOf(dateArray[1]));
        calendar.set(Calendar.YEAR, Integer.valueOf(dateArray[2]));

        binding.itemTime.setText(new SimpleDateFormat("hh:mm a").format(calendar.getTime().getTime()));


        binding.itemDate.setText(new SimpleDateFormat("EEE, MMM d ''yy").format(calendar.getTime().getTime()));

        binding.itemLocation.setText(serviceDetails.getLocation().getTxt());
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