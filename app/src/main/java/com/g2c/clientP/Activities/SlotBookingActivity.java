package com.g2c.clientP.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;


import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;


import com.g2c.clientP.Adapters.PickerAdapter;
import com.g2c.clientP.Helpers.NetworkChangeListener;
import com.g2c.clientP.Helpers.ServiceDataContainer;
import com.g2c.clientP.Helpers.TimeManager;
import com.g2c.clientP.SessionManager;
import com.g2c.clientP.databinding.NoVehicleBinding;
import com.g2c.clientP.databinding.PickerLayoutBinding;
import com.g2c.clientP.model.PaymentBoxModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.g2c.clientP.Adapters.DateBoxAdapter;
import com.g2c.clientP.Adapters.TimeBoxAdapter;
import com.g2c.clientP.Helpers.CustomProgressDialog;
import com.g2c.clientP.R;
import com.g2c.clientP.databinding.ActivitySlotBookingBinding;
import com.g2c.clientP.databinding.SelectBrandDialogBinding;
import com.g2c.clientP.model.TimeBoxModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

public class SlotBookingActivity extends AppCompatActivity {
    ActivitySlotBookingBinding binding;
    Dialog companyPicker, modelPicker;
    PickerAdapter companyAdapter, modelAdapter;
    ArrayList<String> companyList, modelList, offDays;
    ArrayList<Long> timeSlots;
    ArrayList<Long> dateList;
    DatabaseReference DBref;
    ArrayList<TimeBoxModel> timeList;
    DateBoxAdapter dateBoxAdapter;
    TimeBoxAdapter timeBoxAdapter;
    SessionManager sessionManager;
    ServiceDataContainer serviceDetails;
    Integer regularOFF;
    CustomProgressDialog progressDialog;
    TimeManager timeManager;

    NetworkChangeListener networkChangeListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        networkChangeListener = new NetworkChangeListener(SlotBookingActivity.this);
        binding = ActivitySlotBookingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        progressDialog = new CustomProgressDialog(SlotBookingActivity.this);
        sessionManager = new SessionManager(SlotBookingActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.show();
        timeManager = new TimeManager();
        serviceDetails = (ServiceDataContainer) getIntent().getSerializableExtra("serviceDetails");


        DBref = FirebaseDatabase.getInstance().getReference("Services").child("TwoWheelerService").child("CompanyList");


        offDays = new ArrayList<>();
        timeSlots = new ArrayList<>();


        initializePickers();


        binding.continueBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                continueCheck();

            }
        });


    }


    private void initializePickers() {
        progressDialog.show();


        FirebaseDatabase.getInstance().getReference("Services").child("TwoWheelerService").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot twsSnap) {

                if (twsSnap.exists()) {
                    initializeCompanyPicker(twsSnap);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void initializeCompanyPicker(DataSnapshot twsSnap) {
        progressDialog.show();
        PickerLayoutBinding company_bind = PickerLayoutBinding.inflate(getLayoutInflater());

        companyPicker = new Dialog(SlotBookingActivity.this);
        companyPicker.requestWindowFeature(Window.FEATURE_NO_TITLE);
        companyPicker.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        companyPicker.setCancelable(true);
        companyPicker.setContentView(company_bind.getRoot());

        company_bind.head.setText("Select company");

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(companyPicker.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        companyPicker.getWindow().setAttributes(lp);


        companyList = new ArrayList<>();
        companyAdapter = new PickerAdapter(companyList, SlotBookingActivity.this, companyPicker);

        //company_bind.list.setLayoutManager(new LinearLayoutManager(SlotBookingActivity.this));
        company_bind.list.setLayoutManager((new GridLayoutManager(this, 2)));
        company_bind.list.setHasFixedSize(true);
        company_bind.list.setAdapter(companyAdapter);

        if (twsSnap.child("CompanyList").exists()) {

            for (DataSnapshot ss : twsSnap.child("CompanyList").getChildren()) {

                companyList.add(ss.getKey());

            }
            companyAdapter.notifyDataSetChanged();
            binding.company.setText(companyList.get(0));
            companyAdapter.setSelected(0);
        }


        companyPicker.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {

                if (companyAdapter.getSelected() != (-1)) {
                    progressDialog.show();
                    binding.company.setText(companyList.get(companyAdapter.getSelected()));
                    modelList.clear();
                    if (companyAdapter.getSelected() != -1) {
                        if (twsSnap.child("CompanyList").child(companyList.get(companyAdapter.getSelected())).child("ModelList").exists()) {

                            for (DataSnapshot ss : twsSnap.child("CompanyList").child(companyList.get(companyAdapter.getSelected())).child("ModelList").getChildren()) {

                                modelList.add(ss.getValue(String.class));

                            }

                            modelAdapter.notifyDataSetChanged();
                            binding.model.setText(modelList.get(0));
                            progressDialog.dismiss();
                        }
                    } else {
                        Toast.makeText(SlotBookingActivity.this, "Select company first", Toast.LENGTH_SHORT).show();
                    }

                }

            }
        });

        initializeModelPicker(twsSnap);

        binding.company.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                companyPicker.show();
            }
        });
    }

    private void initializeModelPicker(DataSnapshot twsSnap) {
        progressDialog.show();
        PickerLayoutBinding model_bind = PickerLayoutBinding.inflate(getLayoutInflater());

        modelPicker = new Dialog(SlotBookingActivity.this);
        modelPicker.requestWindowFeature(Window.FEATURE_NO_TITLE);
        modelPicker.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        modelPicker.setCancelable(true);
        modelPicker.setContentView(model_bind.getRoot());

        model_bind.head.setText("Select model");

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(modelPicker.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        modelPicker.getWindow().setAttributes(lp);


        modelList = new ArrayList<>();
        modelAdapter = new PickerAdapter(modelList, SlotBookingActivity.this, modelPicker);

        model_bind.list.setLayoutManager(new LinearLayoutManager(SlotBookingActivity.this));
        model_bind.list.setHasFixedSize(true);
        model_bind.list.setAdapter(modelAdapter);


        if (twsSnap.child("CompanyList").child(companyList.get(0)).child("ModelList").exists()) {

            for (DataSnapshot ss : twsSnap.child("CompanyList").child(companyList.get(0)).child("ModelList").getChildren()) {

                modelList.add(ss.getValue(String.class));

            }
            binding.model.setText(modelList.get(0));
            modelAdapter.setSelected(0);
            modelAdapter.notifyDataSetChanged();

        }


        modelPicker.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {

                if (modelAdapter.getSelected() != (-1)) {
                    binding.model.setText(modelList.get(modelAdapter.getSelected()));
                    progressDialog.dismiss();
                }

            }
        });


        binding.model.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                if (companyAdapter.getSelected() != -1) {
                    modelPicker.show();
                } else {
                    Toast.makeText(SlotBookingActivity.this, "Select company first", Toast.LENGTH_SHORT).show();
                }
            }
        });

        manageDateBox();
    }


    public void continueCheck() {
        if (dateBoxAdapter.getSelectedPosition() != (-1) && timeBoxAdapter.getSelectedPosition() != (-1) && !binding.company.getText().toString().isEmpty()
                && !binding.model.getText().toString().isEmpty() && !binding.vhNo.getText().toString().isEmpty()) {

            Intent i = new Intent(SlotBookingActivity.this, PlacePickerActivity.class).putExtra("Activity", ServiceDetailActivity.class);

            serviceDetails.getServiceData().setDate(timeManager.getUsDate(dateList.get(dateBoxAdapter.getSelectedPosition())));
            //Toast.makeText(this, timeManager.getUsDate(dateList.get(dateBoxAdapter.getSelectedPosition())), Toast.LENGTH_SHORT).show();
            serviceDetails.getServiceData().setTime(timeManager.getUsTime(timeBoxAdapter.getMlist().get(timeBoxAdapter.getSelectedPosition()).getTimeStamp()));
            serviceDetails.getVehicle().setCompany(binding.company.getText().toString());
            serviceDetails.getVehicle().setModel(binding.model.getText().toString());
            serviceDetails.getVehicle().setVehicleNo(binding.vhNo.getText().toString().trim().toUpperCase());
            //serviceDetails.getVehicle().setVehicleID(vehicleID);


            i.putExtra("serviceDetails", serviceDetails);

            startActivity(i);
        } else {

            Toast.makeText(SlotBookingActivity.this, "Please fill all the details", Toast.LENGTH_SHORT).show();

        }


    }


    private void generateClick() {
        final int pos = 0;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                binding.dateBox.findViewHolderForAdapterPosition(pos).itemView.performClick();
            }
        }, 1000);
    }


    private void manageDateBox() {
        progressDialog.show();
        dateList = new ArrayList<>();
        timeList = new ArrayList<>();

        dateBoxAdapter = new DateBoxAdapter(dateList, SlotBookingActivity.this);
        timeBoxAdapter = new TimeBoxAdapter(timeList, SlotBookingActivity.this);
        timeBoxAdapter.setServiceDetails(serviceDetails);

        binding.dateBox.setAdapter(dateBoxAdapter);
        LinearLayoutManager manager = new LinearLayoutManager(SlotBookingActivity.this, LinearLayoutManager.HORIZONTAL, false);
        binding.dateBox.setLayoutManager(manager);
        binding.dateBox.setHasFixedSize(true);


        //Setting timebox
        binding.timeBox.setAdapter(timeBoxAdapter);
        binding.timeBox.setLayoutManager(new LinearLayoutManager(SlotBookingActivity.this, LinearLayoutManager.HORIZONTAL, false));
//        binding.timeBox.setLayoutManager((new GridLayoutManager(this, 3)));
        binding.timeBox.setHasFixedSize(true);


        dateBoxAdapter.setTimeBoxAdapter(timeBoxAdapter);
        dateBoxAdapter.setDialog(progressDialog);

        FirebaseDatabase.getInstance().getReference("AppManager").child("SlotManager").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot SlotManager) {

                if (SlotManager.exists()) {

                    if (SlotManager.child("timeSlots").exists()) {

                        for (DataSnapshot timeSlot : SlotManager.child("timeSlots").getChildren()) {
                            timeSlots.add(timeManager.getCurrentDayTimeStamp(timeSlot.getValue(String.class)));
                        }

                        Collections.sort(timeSlots, new Comparator<Long>() {

                            public int compare(Long o1, Long o2) {

                                return o1.compareTo(o2);
                            }
                        });


                        dateBoxAdapter.setTimeSlots(timeSlots);

                    }


                    if (SlotManager.child("currentDayDiscount").exists()) {


                        if (!SlotManager.child("currentDayDiscount").getValue(String.class).equals("no")) {
                            PaymentBoxModel pbm = new PaymentBoxModel();
                            pbm.setType("otherOffer");
                            pbm.setField("Extra discount");

                            Integer svPrice = Integer.valueOf(serviceDetails.getServicePrice());
                            Integer discountPercent = Integer.valueOf(SlotManager.child("currentDayDiscount").getValue(String.class));

                            pbm.setValue(String.valueOf(Integer.valueOf((svPrice * discountPercent) / 100)));

                            timeBoxAdapter.setDiscountPercentage(SlotManager.child("currentDayDiscount").getValue(String.class));

                            timeBoxAdapter.setCurrentDayDiscount(pbm);

                        }


                    }
                    if (SlotManager.child("regularOff").exists()) {
                        regularOFF = Integer.valueOf(SlotManager.child("regularOff").getValue(String.class));
                    } else {
                        regularOFF = 2;
                    }

                    if (SlotManager.child("offDays").exists()) {
                        for (DataSnapshot offDay : SlotManager.child("offDays").getChildren()) {
                            offDays.add(offDay.getValue(String.class));
                        }
                    }


                    if (SlotManager.child("mechanicCount").exists()) {//checking mechanic count

                        dateBoxAdapter.setMechanicCount(Integer.valueOf(SlotManager.child("mechanicCount").getValue(String.class)));


                        if (SlotManager.child("currentMechanicCount").exists()) {//checking currentMechanic count

                            dateBoxAdapter.setCurrentMechanicCount(Integer.valueOf(SlotManager.child("currentMechanicCount").getValue(String.class)));
                        } else {
                            dateBoxAdapter.setMechanicCount(Integer.valueOf(SlotManager.child("mechanicCount").getValue(String.class)));
                        }
                    }


                    if (SlotManager.child("dayDisplayLimit").exists()) {//checking upcoming days limit
                        int limit = Integer.valueOf(SlotManager.child("dayDisplayLimit").getValue(String.class));


                        Calendar c = Calendar.getInstance();


                        for (int i = 0; i < limit; i++) {

                            if (i != 0) {
                                c.add(Calendar.DAY_OF_MONTH, +1);

                            }


                            if (c.get(Calendar.DAY_OF_WEEK) != regularOFF && !offDays.contains(timeManager.getUsDate(c.getTime().getTime()))) {//No Mondays

                                dateList.add(c.getTime().getTime());

                            } else {
                                limit++;
                            }


                        }

                        dateBoxAdapter.notifyDataSetChanged();
                        generateClick();
                        //initializePickers();


                    }


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