package com.g2c.clientP.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;

import com.g2c.clientP.Adapters.PackageAdapter;
import com.g2c.clientP.Adapters.PackageAdapter2;
import com.g2c.clientP.Helpers.NetworkChangeListener;
import com.g2c.clientP.SessionManager;
import com.g2c.clientP.databinding.ActivityPackageBinding;
import com.g2c.clientP.model.ModelPackage;
import com.g2c.clientP.model.MyPackageModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PackageActivity extends AppCompatActivity {
    ActivityPackageBinding binding;
    PackageAdapter packageAdapter;
    PackageAdapter2 packageAdapter2;
    ArrayList<ModelPackage> packages;
    ArrayList<MyPackageModel> ownedPackages;
    SessionManager sessionManager;
    String purpose;
     NetworkChangeListener networkChangeListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        networkChangeListener = new NetworkChangeListener(PackageActivity.this);
        binding = ActivityPackageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        purpose = getIntent().getStringExtra("purpose");

        if(purpose.equals("include")){
            showAvailablePackages();
        }

        sessionManager = new SessionManager(PackageActivity.this);
        packages = new ArrayList<>();
        packageAdapter = new PackageAdapter(PackageActivity.this,packages);
        packageAdapter.setPurpose(purpose);
        binding.rvPackages.setLayoutManager(new LinearLayoutManager(PackageActivity.this));
        binding.rvPackages.setHasFixedSize(true);
        binding.rvPackages.setAdapter(packageAdapter);

        showPackages();

    }


    public void showAvailablePackages(){
        ownedPackages = (ArrayList<MyPackageModel>) getIntent().getSerializableExtra("ownedPackages");
        packageAdapter2 = new PackageAdapter2(PackageActivity.this,ownedPackages);
        binding.rvMyPackages.setLayoutManager(new LinearLayoutManager(PackageActivity.this));
        binding.rvMyPackages.setHasFixedSize(true);
        binding.rvMyPackages.setAdapter(packageAdapter2);
        binding.rvMyPackages.setVisibility(View.VISIBLE);
        packageAdapter2.notifyDataSetChanged();

    }

    private void showPackages() {
        packages.clear();
        FirebaseDatabase.getInstance().getReference("AppManager").child("PackageManager").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot packageList) {


                if(packageList.exists()){


                    for(DataSnapshot pac : packageList.getChildren()){
                        ModelPackage m = pac.getValue(ModelPackage.class);

                        packages.add(m);
                    }

                    packageAdapter.notifyDataSetChanged();


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