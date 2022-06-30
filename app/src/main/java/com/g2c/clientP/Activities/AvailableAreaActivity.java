package com.g2c.clientP.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;

import com.g2c.clientP.Adapters.AvailableAreaAdapter;
import com.g2c.clientP.Helpers.CustomProgressDialog;
import com.g2c.clientP.Helpers.NetworkChangeListener;
import com.g2c.clientP.databinding.ActivityAvailableAreaBinding;
import com.g2c.clientP.model.ModelArea;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AvailableAreaActivity extends AppCompatActivity {
    ActivityAvailableAreaBinding binding;
    AvailableAreaAdapter adapter;
    ArrayList<ModelArea> areaList;
    DatabaseReference areaListRef;
    CustomProgressDialog progressDialog;

     NetworkChangeListener networkChangeListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        networkChangeListener = new NetworkChangeListener(AvailableAreaActivity.this);
        binding = ActivityAvailableAreaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        areaList = new ArrayList<>();
        adapter = new AvailableAreaAdapter(AvailableAreaActivity.this, areaList);
        areaListRef = FirebaseDatabase.getInstance().getReference("Services").child("TwoWheelerService").child("AreaList");
        progressDialog = new CustomProgressDialog(AvailableAreaActivity.this);
        binding.areaList.setLayoutManager(new LinearLayoutManager(AvailableAreaActivity.this));
        binding.areaList.setAdapter(adapter);
        binding.areaList.setHasFixedSize(true);

        makeList();

    }

    private void makeList() {
        progressDialog.show();
        areaListRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot areaListSnap) {
                if (areaListSnap.exists()) {


                    for (DataSnapshot areaSnap : areaListSnap.getChildren()) {
                        ModelArea area = new ModelArea();
                        area.setBottomLeft(new LatLng(Double.valueOf(areaSnap.child("bottomLeft").child("lat").getValue(String.class)), Double.valueOf(areaSnap.child("bottomLeft").child("lng").getValue(String.class))));
                        area.setTopRight(new LatLng(Double.valueOf(areaSnap.child("topRight").child("lat").getValue(String.class)), Double.valueOf(areaSnap.child("topRight").child("lng").getValue(String.class))));
                        area.setLandMark(new LatLng(Double.valueOf(areaSnap.child("landMark").child("lat").getValue(String.class)), Double.valueOf(areaSnap.child("landMark").child("lng").getValue(String.class))));
                        area.setName(areaSnap.child("name").getValue(String.class));
                        area.setLandMarkName(areaSnap.child("landMarkName").getValue(String.class));
                        areaList.add(area);
                    }


                }
                adapter.notifyDataSetChanged();
                progressDialog.dismiss();
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