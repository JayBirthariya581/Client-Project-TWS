package com.g2c.clientP.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.g2c.clientP.Adapters.TwBrandListAdapter;
import com.g2c.clientP.Adapters.TwModelListAdapter;
import com.g2c.clientP.Helpers.NetworkChangeListener;
import com.g2c.clientP.R;
import com.g2c.clientP.SessionManager;
import com.g2c.clientP.databinding.ActivityQueryDetailBinding;
import com.g2c.clientP.databinding.SelectBrandDialogBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class QueryDetailActivity extends AppCompatActivity {
    ActivityQueryDetailBinding binding;
    HashMap<String, String> queryDetails;

    DatabaseReference DBref;
    TwBrandListAdapter adapter;
    TwModelListAdapter ModelAdapter;
    String Category;
    RecyclerView cL;
    ArrayList<String> CompanyList, ModelList;

     NetworkChangeListener networkChangeListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        networkChangeListener = new NetworkChangeListener(QueryDetailActivity.this);
        binding = ActivityQueryDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Category = getIntent().getStringExtra("category");


        binding.head.setText(Category);
        switch (Category) {

            case "Tyre":
                binding.bannerImg.setImageResource(R.drawable.tyre_banner);
                break;


            case "Battery":

                binding.bannerImg.setImageResource(R.drawable.battery_banner);

                break;

            default:


                break;
        }


        queryDetails = new HashMap<>();
        SessionManager sessionManager = new SessionManager(QueryDetailActivity.this);
        DBref = FirebaseDatabase.getInstance().getReference("Services").child("TwoWheelerService").child("CompanyList");


        binding.nameTv.setText(sessionManager.getUsersDetailsFromSessions().get(SessionManager.KEY_FULLNAME));


        binding.proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                proceedCheck();

            }
        });


        manageVehicleBox();


    }

    private void proceedCheck() {

        if (binding.brandTv.getText().toString().isEmpty() || binding.brandTv.getText().toString().isEmpty() || binding.brandTv.getText().toString().isEmpty() || binding.brandTv.getText().toString().isEmpty()) {
            Toast.makeText(QueryDetailActivity.this, "Please fill all the details", Toast.LENGTH_SHORT).show();
            return;
        }

        queryDetails.put("name", binding.nameTv.getText().toString());
        queryDetails.put("company", binding.brandTv.getText().toString());
        queryDetails.put("model", binding.modelTv.getText().toString());
        queryDetails.put("vhNo", binding.vhNoTv.getText().toString());
        queryDetails.put("category", getIntent().getStringExtra("category"));

        Intent i = new Intent(QueryDetailActivity.this, PlacePickerActivity.class);
        i.putExtra("Activity", QueryTimerActivity.class);
        i.putExtra("serviceDetails", queryDetails);
        startActivity(i);
    }


    private void manageVehicleBox() {


        ModelList = new ArrayList<>();
        ModelAdapter = new TwModelListAdapter(QueryDetailActivity.this, ModelList, binding.modelTv);


        CompanyList = new ArrayList<>();
        adapter = new TwBrandListAdapter(QueryDetailActivity.this, CompanyList, binding.brandTv, binding.modelTv);


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
                            AlertDialog.Builder builder = new AlertDialog.Builder(QueryDetailActivity.this);
                            builder.setView(b.getRoot());


                            QueryDetailActivity.this.cL = b.brandList;

                            b.heading.setText("Select Brand");
                            b.brandList.setAdapter(adapter);
                            b.brandList.setLayoutManager(new LinearLayoutManager(QueryDetailActivity.this));
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
                            AlertDialog.Builder builder = new AlertDialog.Builder(QueryDetailActivity.this);
                            builder.setView(mb.getRoot());


                            mb.heading.setText("Select Model");
                            mb.brandList.setAdapter(ModelAdapter);
                            mb.brandList.setLayoutManager(new LinearLayoutManager(QueryDetailActivity.this));
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