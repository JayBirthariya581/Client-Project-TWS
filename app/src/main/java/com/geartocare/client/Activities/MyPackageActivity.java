package com.geartocare.client.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;

import com.geartocare.client.Adapters.MyPackageAdapter;
import com.geartocare.client.SessionManager;
import com.geartocare.client.databinding.ActivityMyPackageBinding;
import com.geartocare.client.model.ModelMyPackage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MyPackageActivity extends AppCompatActivity {
    ActivityMyPackageBinding binding;
    ArrayList<ModelMyPackage> packages;
    MyPackageAdapter packageAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyPackageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        packages = new ArrayList<>();
        packageAdapter = new MyPackageAdapter(MyPackageActivity.this,packages);
        binding.rvPackages.setLayoutManager(new LinearLayoutManager(MyPackageActivity.this));
        binding.rvPackages.setHasFixedSize(true);
        binding.rvPackages.setAdapter(packageAdapter);



        showPackages();





    }

    private void showPackages() {
        packages.clear();
        FirebaseDatabase.getInstance().getReference("Users").child(new SessionManager(MyPackageActivity.this).getUsersDetailsFromSessions().get(SessionManager.KEY_UID)).child("Packages").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot packageList) {

                if(packageList.exists()){

                    for(DataSnapshot myPackage : packageList.getChildren()){

                        ModelMyPackage m = myPackage.getValue(ModelMyPackage.class);

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