package com.geartocare.client.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.geartocare.client.Adapters.ServiceCheckListActAdapter;
import com.geartocare.client.R;
import com.geartocare.client.databinding.ActivityCompleteCheckListBinding;

import java.util.ArrayList;

public class CompleteCheckListActivity extends AppCompatActivity {
    ActivityCompleteCheckListBinding binding;
    String serviceID;
    ServiceCheckListActAdapter adapter;
    ArrayList<String> svCheckList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCompleteCheckListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getWindow().setStatusBarColor(ContextCompat.getColor(CompleteCheckListActivity.this,R.color.black));

        serviceID = getIntent().getStringExtra("serviceID");

        svCheckList = new ArrayList<>();
        adapter = new ServiceCheckListActAdapter(CompleteCheckListActivity.this,svCheckList,serviceID);

        binding.rvSvCl.setAdapter(adapter);
        binding.rvSvCl.setLayoutManager(new LinearLayoutManager(CompleteCheckListActivity.this));
        binding.rvSvCl.setHasFixedSize(true);
        binding.rvSvCl.setNestedScrollingEnabled(false);


        FirebaseDatabase.getInstance().getReference("Services").child(serviceID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot serviceDetails) {

                if(serviceDetails.exists()){
                    binding.svName.setText(serviceDetails.child("serviceName").getValue(String.class));


                    for(DataSnapshot cl_value : serviceDetails.child("serviceCheckList").getChildren()){
                        svCheckList.add(cl_value.getValue(String.class));

                    }
                    adapter.notifyDataSetChanged();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }
}