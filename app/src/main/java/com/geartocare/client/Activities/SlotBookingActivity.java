package com.geartocare.client.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;


import com.geartocare.client.SessionManager;
import com.geartocare.client.databinding.NoVehicleBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.geartocare.client.Adapters.DateBoxAdapter;
import com.geartocare.client.Adapters.TimeBoxAdapter;
import com.geartocare.client.Adapters.TwBrandListAdapter;
import com.geartocare.client.Adapters.TwModelListAdapter;
import com.geartocare.client.Helpers.CustomProgressDialog;
import com.geartocare.client.R;
import com.geartocare.client.databinding.ActivitySlotBookingBinding;
import com.geartocare.client.databinding.SelectBrandDialogBinding;
import com.geartocare.client.model.DateBoxModel;
import com.geartocare.client.model.TimeBoxModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class SlotBookingActivity extends AppCompatActivity {
    ActivitySlotBookingBinding binding;
    TwBrandListAdapter adapter;
    TwModelListAdapter ModelAdapter;
    ArrayList<String> CompanyList, ModelList, offDays, timeSlots;
    ArrayList<DateBoxModel> mList;
    DatabaseReference DBref;
    ArrayList<TimeBoxModel> timeList;
    DateBoxAdapter dateBoxAdapter;
    TimeBoxAdapter timeBoxAdapter;
    RecyclerView cL;
    SessionManager sessionManager;
    HashMap<String, String> serviceDetails;
    int Place_Picker_Request = 1;
    CustomProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySlotBookingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dialog = new CustomProgressDialog(SlotBookingActivity.this);
        sessionManager = new SessionManager(SlotBookingActivity.this);
        dialog.setCancelable(false);
        dialog.show();

        serviceDetails = (HashMap<String, String>) getIntent().getSerializableExtra("serviceDetails");

        //binding.svDetailHead.setText(serviceDetails.get("serviceName"));

        DBref = FirebaseDatabase.getInstance().getReference("Services").child("TwoWheelerService").child("CompanyList");


        offDays = new ArrayList<>();
        timeSlots = new ArrayList<>();


        //Select Brand
        manageDateBox();


        manageVehicleBox();

        /*binding.vhNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                FirebaseDatabase.getInstance().getReference("Users").child(sessionManager.getUsersDetailsFromSessions().get(SessionManager.KEY_UID))
                        .child("vehicles").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot vehicleSnap) {

                        if(!vehicleSnap.exists()){





                        }else{


                            NoVehicleBinding noVehicleBinding = NoVehicleBinding.inflate(getLayoutInflater());
                            Dialog nvDialog = new Dialog(SlotBookingActivity.this);
                            nvDialog.setContentView(noVehicleBinding.getRoot());

                            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                            lp.copyFrom(nvDialog.getWindow().getAttributes());
                            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                            nvDialog.getWindow().setAttributes(lp);


                            nvDialog.show();













                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });*/


        binding.continueBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                continueCheck();

            }
        });





    }


    public void continueCheck() {
        if (dateBoxAdapter.getSelectedPosition() != (-1) && timeBoxAdapter.getSelectedPosition() != (-1) && !binding.brandTv.getText().toString().isEmpty()
                && !binding.modelTv.getText().toString().isEmpty() && !binding.vhNoTv.getText().toString().isEmpty()) {

            Intent i = new Intent(SlotBookingActivity.this, PlacePickerActivity.class).putExtra("Activity",ServiceDetailActivity.class);

            serviceDetails.put("Date", mList.get(dateBoxAdapter.getSelectedPosition()).getDateF());
            serviceDetails.put("Time", timeBoxAdapter.getMlist().get(timeBoxAdapter.getSelectedPosition()).getTime());
            serviceDetails.put("Company", binding.brandTv.getText().toString());
            serviceDetails.put("Model", binding.modelTv.getText().toString());
            serviceDetails.put("VehicleNo", binding.vhNoTv.getText().toString().trim());


            i.putExtra("serviceDetails", serviceDetails);

            startActivity(i);
        } else {

            Toast.makeText(SlotBookingActivity.this, "Please fill all the details", Toast.LENGTH_SHORT).show();

        }


    }


    private void manageVehicleBox() {


        ModelList = new ArrayList<>();
        ModelAdapter = new TwModelListAdapter(SlotBookingActivity.this, ModelList, binding.modelTv);


        CompanyList = new ArrayList<>();
        adapter = new TwBrandListAdapter(SlotBookingActivity.this, CompanyList, binding.brandTv, binding.modelTv);


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
                            AlertDialog.Builder builder = new AlertDialog.Builder(SlotBookingActivity.this);
                            builder.setView(b.getRoot());


                            SlotBookingActivity.this.cL = b.brandList;

                            b.heading.setText("Select Brand");
                            b.brandList.setAdapter(adapter);
                            b.brandList.setLayoutManager(new LinearLayoutManager(SlotBookingActivity.this));
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
                            AlertDialog.Builder builder = new AlertDialog.Builder(SlotBookingActivity.this);
                            builder.setView(mb.getRoot());


                            mb.heading.setText("Select Model");
                            mb.brandList.setAdapter(ModelAdapter);
                            mb.brandList.setLayoutManager(new LinearLayoutManager(SlotBookingActivity.this));
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

    private void generateClick() {

        final int pos = 0;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                binding.dateBox.findViewHolderForAdapterPosition(pos).itemView.performClick();
                //cL.findViewHolderForAdapterPosition(pos).itemView.performClick();
                dialog.dismiss();


            }
        }, 1000);
    }


    private void manageDateBox() {
        mList = new ArrayList<>();
        timeList = new ArrayList<>();
        timeBoxAdapter = new TimeBoxAdapter(timeList, SlotBookingActivity.this);
        dateBoxAdapter = new DateBoxAdapter(mList, SlotBookingActivity.this);
        binding.dateBox.setAdapter(dateBoxAdapter);
        LinearLayoutManager manager = new LinearLayoutManager(SlotBookingActivity.this, LinearLayoutManager.HORIZONTAL, false);
        // manager.setStackFromEnd(true);
        binding.dateBox.setLayoutManager(manager);
        binding.dateBox.setHasFixedSize(true);


        //Setting timebox
        binding.timeBox.setAdapter(timeBoxAdapter);
        binding.timeBox.setLayoutManager(new LinearLayoutManager(SlotBookingActivity.this, LinearLayoutManager.HORIZONTAL, false));
        binding.timeBox.setHasFixedSize(true);


        dateBoxAdapter.setTimeBoxAdapter(timeBoxAdapter);
        dateBoxAdapter.setDialog(dialog);

        FirebaseDatabase.getInstance().getReference("AppManager").child("SlotManager").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot SlotManager) {

                if (SlotManager.exists()) {

                    if (SlotManager.child("timeSlots").exists()) {

                        for (DataSnapshot timeSlot : SlotManager.child("timeSlots").getChildren()) {
                            timeSlots.add(timeSlot.getValue(String.class));
                        }
                        dateBoxAdapter.setTimeSlots(timeSlots);

                    }

                    if (SlotManager.child("offDays").exists()) {
                        for (DataSnapshot offDay : SlotManager.child("offDays").getChildren()) {
                            offDays.add(offDay.getValue(String.class));
                        }
                    }


                    if (SlotManager.child("mechanicCount").exists()) {//checking mechanic count

                        dateBoxAdapter.setMechanicCount(Integer.valueOf(SlotManager.child("mechanicCount").getValue(String.class)));
                    }


                    if (SlotManager.child("dayDisplayLimit").exists()) {//checking upcoming days limit
                        int limit = Integer.valueOf(SlotManager.child("dayDisplayLimit").getValue(String.class));


                        HashMap<Integer, String> dayFromCode = new HashMap<>();
                        dayFromCode.put(1, "Sun");
                        dayFromCode.put(2, "Mon");
                        dayFromCode.put(3, "Tue");
                        dayFromCode.put(4, "Wed");
                        dayFromCode.put(5, "Thu");
                        dayFromCode.put(6, "Fri");
                        dayFromCode.put(7, "Sat");


                        Calendar c = Calendar.getInstance();

                        int hour = c.get(Calendar.HOUR_OF_DAY);
                        SimpleDateFormat sdf = new SimpleDateFormat("dd_MM_yyyy");
                        dateBoxAdapter.setHourOfDay(hour);

                        dateBoxAdapter.setDateOfDay(String.valueOf(c.get(Calendar.DAY_OF_MONTH)));

                        //Toast.makeText(SlotBookingActivity.this, new SimpleDateFormat("dd_MM_yyyy").format(c.getTime().getTime()), Toast.LENGTH_SHORT).show();


                        for (int i = 0; i < limit; i++) {

                            if (i != 0) {
                                c.add(Calendar.DATE, +1);

                            }


                            if (c.get(Calendar.DAY_OF_WEEK) != 2 && !offDays.contains(sdf.format(c.getTime().getTime()))) {//No Mondays


                                String date = String.valueOf(c.get(Calendar.DAY_OF_MONTH));

                                DateBoxModel m = new DateBoxModel();
                                m.setDate(date);
                                m.setDateF(String.valueOf(c.getTime().getTime()));
                                m.setDay(dayFromCode.get(c.get(Calendar.DAY_OF_WEEK)));
                                mList.add(m);


                            } else {
                                limit++;
                            }


                        }

                        dateBoxAdapter.notifyDataSetChanged();
                        generateClick();


                    }


                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

}