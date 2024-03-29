package com.g2c.clientP.Activities;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.g2c.clientP.Adapters.TwBrandListAdapter;
import com.g2c.clientP.Adapters.TwModelListAdapter;
import com.g2c.clientP.Helpers.NetworkChangeListener;
import com.g2c.clientP.Helpers.VehicleHelper;
import com.g2c.clientP.SessionManager;
import com.g2c.clientP.databinding.ActivityAddVehicleBinding;
import com.g2c.clientP.databinding.SelectBrandDialogBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AddVehicleActivity extends AppCompatActivity {
    ActivityAddVehicleBinding binding;
    TwBrandListAdapter adapter;
    TwModelListAdapter ModelAdapter;
    ArrayList<String>  CompanyList, ModelList;
    DatabaseReference DBref;
     NetworkChangeListener networkChangeListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        networkChangeListener = new NetworkChangeListener(AddVehicleActivity.this);
        binding = ActivityAddVehicleBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        DBref = FirebaseDatabase.getInstance().getReference("Services").child("TwoWheelerService").child("CompanyList");

        binding.addVehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                continueCheck();
            }
        });

        manageVehicleBox();

    }



    private void manageVehicleBox() {


        ModelList = new ArrayList<>();
        ModelAdapter = new TwModelListAdapter(AddVehicleActivity.this,ModelList,  binding.modelTv);


        CompanyList = new ArrayList<>();
        adapter = new TwBrandListAdapter(AddVehicleActivity.this,CompanyList, binding.brandTv, binding.modelTv);


        adapter.setModelListAdapter(ModelAdapter);


        DBref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot companyList) {


                if (companyList.exists()) {
                    CompanyList.clear();


                    for (DataSnapshot company : companyList.getChildren()) {

                        CompanyList.add(company.getKey());
                    }


                    adapter.setCompanyList(companyList);

                    binding.brandTv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {


                            SelectBrandDialogBinding b = SelectBrandDialogBinding.inflate(getLayoutInflater());
                            AlertDialog.Builder builder = new AlertDialog.Builder(AddVehicleActivity.this);
                            builder.setView(b.getRoot());


                            //AddVehicleActivity.this.cL = b.brandList;

                            b.heading.setText("Select Brand");
                            b.brandList.setAdapter(adapter);
                            b.brandList.setLayoutManager(new LinearLayoutManager(AddVehicleActivity.this));
                            b.brandList.setHasFixedSize(true);


                            //builder.create().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            adapter.notifyDataSetChanged();
                            AlertDialog a = builder.show();
                            a.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            a.getWindow().setLayout(800, 1000);
                            adapter.setAlertDialog(a);


                        }
                    });

                    binding.modelTv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {


                            SelectBrandDialogBinding mb = SelectBrandDialogBinding.inflate(getLayoutInflater());
                            AlertDialog.Builder builder = new AlertDialog.Builder(AddVehicleActivity.this);
                            builder.setView(mb.getRoot());


                            mb.heading.setText("Select Model");
                            mb.brandList.setAdapter(ModelAdapter);
                            mb.brandList.setLayoutManager(new LinearLayoutManager(AddVehicleActivity.this));
                            mb.brandList.setHasFixedSize(true);


                            //builder.create().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            ModelAdapter.notifyDataSetChanged();
                            AlertDialog am = builder.show();
                            am.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            am.getWindow().setLayout(800, 1000);
                            ModelAdapter.setAlertDialog(am);


                        }
                    });


                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }




    public void continueCheck() {
        if (!binding.brandTv.getText().toString().isEmpty()
                && !binding.modelTv.getText().toString().isEmpty() && !binding.vhNoTv.getText().toString().isEmpty()) {

            String vehicleID = FirebaseDatabase.getInstance().getReference("Users").child(new SessionManager(AddVehicleActivity.this).getUsersDetailsFromSessions().get(SessionManager.KEY_UID)).child("vehicles").push().getKey();
            VehicleHelper vehicleHelper = new VehicleHelper(binding.brandTv.getText().toString(),binding.modelTv.getText().toString(),binding.vhNoTv.getText().toString(),vehicleID);


            FirebaseDatabase.getInstance().getReference("Users").child(new SessionManager(AddVehicleActivity.this).getUsersDetailsFromSessions().get(SessionManager.KEY_UID)).child("vehicles").child(vehicleID).setValue(vehicleHelper).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if(task.isSuccessful()){
                        Intent data = new Intent();
                        data.putExtra("Company",binding.brandTv.getText().toString());
                        data.putExtra("Model",binding.modelTv.getText().toString());
                        data.putExtra("VhNo",binding.vhNoTv.getText().toString());
                        setResult(Activity.RESULT_OK,data);
                        finish();

                    }

                }
            });


        } else {

            Toast.makeText(AddVehicleActivity.this, "Please fill all the details", Toast.LENGTH_SHORT).show();

        }


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