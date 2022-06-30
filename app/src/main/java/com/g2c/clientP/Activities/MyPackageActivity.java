package com.g2c.clientP.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;

import com.g2c.clientP.Adapters.MyPackageAdapter;
import com.g2c.clientP.Helpers.NetworkChangeListener;
import com.g2c.clientP.SessionManager;
import com.g2c.clientP.databinding.ActivityMyPackageBinding;
import com.g2c.clientP.model.MyPackageModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MyPackageActivity extends AppCompatActivity {
    ActivityMyPackageBinding binding;
    ArrayList<MyPackageModel> packages;
    MyPackageAdapter packageAdapter;
     NetworkChangeListener networkChangeListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        networkChangeListener = new NetworkChangeListener(MyPackageActivity.this);
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

                        MyPackageModel m = myPackage.getValue(MyPackageModel.class);

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