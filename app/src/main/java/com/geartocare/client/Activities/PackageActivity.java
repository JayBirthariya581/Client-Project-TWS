package com.geartocare.client.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;

import com.geartocare.client.Adapters.PackageAdapter;
import com.geartocare.client.databinding.ActivityPackageBinding;
import com.geartocare.client.model.ModelPackage;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PackageActivity extends AppCompatActivity {
    ActivityPackageBinding binding;
    PackageAdapter packageAdapter;
    ArrayList<ModelPackage> packages;
    String purpose;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPackageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        purpose = getIntent().getStringExtra("purpose");

        packages = new ArrayList<>();
        packageAdapter = new PackageAdapter(PackageActivity.this,packages);
        packageAdapter.setPurpose(purpose);
        binding.rvPackages.setLayoutManager(new LinearLayoutManager(PackageActivity.this));
        binding.rvPackages.setHasFixedSize(true);
        binding.rvPackages.setAdapter(packageAdapter);

        showPackages();

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
}