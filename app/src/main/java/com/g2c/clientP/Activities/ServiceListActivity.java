package com.g2c.clientP.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;

import com.g2c.clientP.Adapters.MyServiceAdapter;
import com.g2c.clientP.Helpers.CustomProgressDialog;
import com.g2c.clientP.Helpers.NetworkChangeListener;
import com.g2c.clientP.SessionManager;
import com.g2c.clientP.databinding.ActivityServiceListBinding;
import com.g2c.clientP.model.ModelFinalService;
import com.g2c.clientP.model.ModelVehicle;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ServiceListActivity extends AppCompatActivity {
    ActivityServiceListBinding binding;

    MyServiceAdapter adapter;
    ArrayList<ModelFinalService> serviceList;
    CustomProgressDialog dialog;
    ModelVehicle vehicleDetails;
     NetworkChangeListener networkChangeListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        networkChangeListener = new NetworkChangeListener(ServiceListActivity.this);
        binding = ActivityServiceListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        vehicleDetails = (ModelVehicle) getIntent().getSerializableExtra("vehicleDetails");
        serviceList= new ArrayList<>();
        dialog = new CustomProgressDialog(ServiceListActivity.this);




        adapter = new MyServiceAdapter(ServiceListActivity.this,serviceList);

        binding.rvServiceList.setLayoutManager(new LinearLayoutManager(ServiceListActivity.this));
        binding.rvServiceList.setHasFixedSize(true);
        binding.rvServiceList.setAdapter(adapter);

        makeList();

    }

    private void makeList() {
        //dialog.show();
        serviceList.clear();

        FirebaseDatabase.getInstance().getReference("Users").child(new SessionManager(ServiceListActivity.this).getUsersDetailsFromSessions().get(SessionManager.KEY_UID)).child("vehicles").child(vehicleDetails.getVehicleID()).child("services")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot services) {


                        if(services.exists()){

                            for(DataSnapshot service : services.getChildren()){

                                ModelFinalService bookingFromDB = service.getValue(ModelFinalService.class);

                                serviceList.add(bookingFromDB);




                            }
                            adapter.notifyDataSetChanged();


                        }
                       // dialog.dismiss();


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

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