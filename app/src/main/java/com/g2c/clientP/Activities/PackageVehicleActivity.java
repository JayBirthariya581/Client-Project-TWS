package com.g2c.clientP.Activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;

import com.g2c.clientP.Adapters.PackageVehicleAdapter;
import com.g2c.clientP.Helpers.NetworkChangeListener;
import com.g2c.clientP.SessionManager;
import com.g2c.clientP.databinding.ActivityPackageVehicleBinding;
import com.g2c.clientP.databinding.SelectBrandDialogBinding;
import com.g2c.clientP.model.ModelVehicle;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PackageVehicleActivity extends AppCompatActivity {
    ActivityPackageVehicleBinding binding;
    PackageVehicleAdapter vehicleAdapter;
    ArrayList<ModelVehicle> vehicles, selectedList;


     NetworkChangeListener networkChangeListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        networkChangeListener = new NetworkChangeListener(PackageVehicleActivity.this);

        binding = ActivityPackageVehicleBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        vehicles = new ArrayList<>();
        selectedList = new ArrayList<>();
        vehicleAdapter = new PackageVehicleAdapter(PackageVehicleActivity.this, vehicles,selectedList,2);


        binding.rvPackageVehicles.setLayoutManager(new LinearLayoutManager(PackageVehicleActivity.this));
        binding.rvPackageVehicles.setHasFixedSize(true);
        binding.rvPackageVehicles.setAdapter(vehicleAdapter);


        ActivityResultLauncher lau = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {

                if (result.getResultCode() == Activity.RESULT_OK) {

                    Intent data = result.getData();


                    ModelVehicle mv = new ModelVehicle();
                    mv.setCompany(data.getStringExtra("Company"));
                    mv.setModel(data.getStringExtra("Model"));
                    mv.setVehicleNo(data.getStringExtra("VhNo"));

                    vehicles.add(mv);
                    vehicleAdapter.notifyDataSetChanged();


                }

            }
        });


        binding.addNewVehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(PackageVehicleActivity.this, AddVehicleActivity.class);
                lau.launch(i);


            }
        });


        binding.continuePackage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent i = new Intent(PackageVehicleActivity.this,PackageDetailActivity.class);


                i.putExtra("packageID",getIntent().getStringExtra("packageID"));
                String selectedVehicles="";
                for(int j=0;j<selectedList.size();j++){

                    selectedVehicles+=selectedList.get(j).getCompany()+"_"+selectedList.get(j).getModel()+"_"+selectedList.get(j).getVehicleNo()+",";
                }
                i.putExtra("selectedVehicles",selectedVehicles);

                startActivity(i);





            }
        });


        makeList();

    }


    private void makeList() {
        vehicles.clear();
        FirebaseDatabase.getInstance().getReference("Users").child(new SessionManager(PackageVehicleActivity.this).getUsersDetailsFromSessions().get(SessionManager.KEY_UID)).child("vehicles")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot bookings) {


                        if (bookings.exists()) {

                            for (DataSnapshot booking : bookings.getChildren()) {

                                ModelVehicle bookingFromDB = booking.getValue(ModelVehicle.class);

                                vehicles.add(bookingFromDB);


                            }
                            vehicleAdapter.notifyDataSetChanged();
                            //dialog.dismiss();


                        } else {
                            //dialog.dismiss();
                        }


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