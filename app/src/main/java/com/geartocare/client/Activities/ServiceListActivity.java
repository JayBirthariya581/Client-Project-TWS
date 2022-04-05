package com.geartocare.client.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;

import com.geartocare.client.Adapters.MyBookingAdapter;
import com.geartocare.client.Adapters.MyServiceAdapter;
import com.geartocare.client.Helpers.CustomProgressDialog;
import com.geartocare.client.SessionManager;
import com.geartocare.client.databinding.ActivityServiceListBinding;
import com.geartocare.client.model.ModelFinalService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class ServiceListActivity extends AppCompatActivity {
    ActivityServiceListBinding binding;

    MyServiceAdapter adapter;
    ArrayList<ModelFinalService> serviceList;
    CustomProgressDialog dialog;
    HashMap<String,String> vehicleDetails;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityServiceListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        vehicleDetails = (HashMap<String, String>) getIntent().getSerializableExtra("vehicleDetails");
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

        FirebaseDatabase.getInstance().getReference("Users").child(new SessionManager(ServiceListActivity.this).getUsersDetailsFromSessions().get(SessionManager.KEY_UID)).child("vehicles").child(vehicleDetails.get("VehicleID")).child("services")
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
}