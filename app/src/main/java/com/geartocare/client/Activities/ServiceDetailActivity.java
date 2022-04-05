package com.geartocare.client.Activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.geartocare.client.Adapters.PaymentAdapter;
import com.geartocare.client.Notifications.FcmNotificationsSender;
import com.geartocare.client.model.PaymentBoxModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.geartocare.client.Adapters.ServiceCheckListAdapter;
import com.geartocare.client.Helpers.CustomProgressDialog;
import com.geartocare.client.R;
import com.geartocare.client.databinding.ActivityServiceDetailBinding;
import com.razorpay.PaymentResultListener;

import org.json.JSONArray;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class ServiceDetailActivity extends AppCompatActivity implements PaymentResultListener {
    ActivityServiceDetailBinding binding;
    String serviceID;
    ServiceCheckListAdapter adapter;
    PaymentAdapter paymentAdapter;
    ArrayList<PaymentBoxModel> paymentList;
    ArrayList<String> svCheckList;
    String svPrice;
    String vehicleID;
    Integer total, orignalTotal;
    CustomProgressDialog dialog;
    JSONArray token_IDs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityServiceDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().setStatusBarColor(ContextCompat.getColor(ServiceDetailActivity.this, R.color.black));
        HashMap<String, String> hm = (HashMap) getIntent().getSerializableExtra("serviceDetails");
        svPrice = hm.get("svPrice");
        dialog = new CustomProgressDialog(ServiceDetailActivity.this);
        dialog.show();
        serviceID = hm.get("serviceID");
        binding.svPrice.setText(svPrice);


        svCheckList = new ArrayList<>();
        paymentList = new ArrayList<>();
        adapter = new ServiceCheckListAdapter(ServiceDetailActivity.this, svCheckList, serviceID);
        paymentAdapter = new PaymentAdapter(ServiceDetailActivity.this, paymentList);
        binding.rvSvCl.setAdapter(adapter);
        binding.rvSvCl.setLayoutManager(new LinearLayoutManager(ServiceDetailActivity.this));
        binding.rvSvCl.setHasFixedSize(true);
        binding.rvSvCl.setNestedScrollingEnabled(false);

        binding.paymentBox.setAdapter(paymentAdapter);
        binding.paymentBox.setLayoutManager(new LinearLayoutManager(ServiceDetailActivity.this));
        binding.paymentBox.setHasFixedSize(true);


        vehicleID = hm.get("Company") + "_" + hm.get("Model") + "_" + hm.get("VehicleNo");

        FirebaseDatabase.getInstance().getReference("Services").child(serviceID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot serviceDetails) {

                        if (serviceDetails.exists()) {
                            total = 0;
                            orignalTotal = 0;
                            binding.svName.setText(serviceDetails.child("serviceName").getValue(String.class));


                            for (DataSnapshot cl_value : serviceDetails.child("serviceCheckList").getChildren()) {
                                svCheckList.add(cl_value.getValue(String.class));

                                if (svCheckList.size() > 4) {
                                    binding.seeServiceCheckList.setVisibility(View.VISIBLE);

                                    break;
                                }
                            }
                            adapter.notifyDataSetChanged();


                            for (DataSnapshot ps : serviceDetails.child("Pricing").getChildren()) {

                                PaymentBoxModel pbm = new PaymentBoxModel();
                                pbm.setField(ps.getKey());
                                pbm.setValue(ps.getValue(String.class));

                                paymentList.add(pbm);
                                orignalTotal += Integer.valueOf(pbm.getValue());

                            }

                            PaymentBoxModel svp = new PaymentBoxModel();
                            svp.setField("Item Price");
                            svp.setValue(serviceDetails.child("servicePrice").getValue(String.class));
                            paymentList.add(svp);
                            orignalTotal += Integer.valueOf(svp.getValue());

                            paymentAdapter.notifyDataSetChanged();
                            total = orignalTotal;
                            binding.totalPrice.setText(String.valueOf(total));

                            binding.svBtnPrice.setText(binding.totalPrice.getText().toString());


                            dialog.dismiss();

                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        long l = Long.valueOf(hm.get("Date"));

        binding.itemDate.setText(sdf.format(l));

        binding.itemTime.setText(hm.get("Time"));
        binding.itemC.setText(hm.get("Company"));
        binding.itemM.setText(hm.get("Model"));
        binding.itemLocation.setText(hm.get("location_txt"));
        binding.itemVhNo.setText(hm.get("VehicleNo"));

        binding.seeServiceCheckList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ServiceDetailActivity.this, CompleteCheckListActivity.class)
                        .putExtra("serviceID", serviceID)
                );
            }
        });

        binding.continueBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                /*
                 *
                 *
                 *
                 * */


                token_IDs = new JSONArray();
                FcmNotificationsSender notificationsSender = new FcmNotificationsSender("none"
                        , "Service Bookied", "hey hey", getApplicationContext(), ServiceDetailActivity.this);

                FirebaseDatabase.getInstance().getReference("managers").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot managers) {

                        if (managers.exists()) {

                            for (DataSnapshot manager : managers.getChildren()) {


                                token_IDs.put(manager.child("token").getValue(String.class));


                            }


                            notificationsSender.SendNotificationsTo(token_IDs);

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


//
                Intent i = new Intent(ServiceDetailActivity.this, PaymentActivity.class);
                hm.put("totalPrice", binding.totalPrice.getText().toString());
                i.putExtra("serviceDetails", hm);
                startActivity(i);
//                Checkout checkout = new Checkout();
//
//
//                int amount = Math.round(Float.parseFloat(String.valueOf(total)) * 100);
//                // set your id as below
//                checkout.setKeyID("rzp_test_uHXTStGEWf82i1");
//
//                // set image
//                checkout.setImage(R.drawable.ic_gear);
//
//                // initialize json object
//                JSONObject object = new JSONObject();
//                try {
//                    // to put name
//                    object.put("name", "GearToCare");
//
//                    // put description
//                    object.put("description", "Two wheeler service");
//
//                    // to set theme color
//                    object.put("theme.color", "");
//
//                    // put the currency
//                    object.put("currency", "INR");
//
//                    // put amount
//                    object.put("amount", amount);
//
//                    // put mobile number
//                    object.put("prefill.contact", hm.get("phone"));
//
//                    // put email
//                    object.put("prefill.email", "chaitanyamunje@gmail.com");
//
//                    // open razorpay to checkout activity
//                    checkout.open(ServiceDetailActivity.this, object);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//

            }
        });


        ActivityResultLauncher<Intent> lau = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    total = orignalTotal;

                    for (PaymentBoxModel p : paymentList) {

                        if (p.getField().equals("Coupon value") || p.getField().equals("G2C points")) {
                            paymentList.remove(p);
                        }

                    }


                    Intent data = result.getData();

                    if (data.getStringExtra("type").equals("coupon")) {
                        PaymentBoxModel pbm = new PaymentBoxModel();
                        pbm.setField("Coupon value");
                        pbm.setValue(data.getStringExtra("couponValue"));
                        paymentList.add(pbm);
                        total += Integer.valueOf(pbm.getValue());
                        paymentAdapter.notifyDataSetChanged();


                        binding.totalPrice.setText(total.toString());
                        binding.svBtnPrice.setText(total.toString());

                    } else if (data.getStringExtra("type").equals("points")) {
                        PaymentBoxModel pbm = new PaymentBoxModel();
                        pbm.setField("G2C points");
                        Integer val = Integer.valueOf(data.getStringExtra("pointValue")) * -1;
                        pbm.setValue(String.valueOf(val));

                        paymentList.add(pbm);
                        total += Integer.valueOf(pbm.getValue());
                        paymentAdapter.notifyDataSetChanged();


                        binding.totalPrice.setText(total.toString());
                        binding.svBtnPrice.setText(total.toString());
                    }


                }
            }
        });


        binding.disBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ServiceDetailActivity.this, DiscountActivity.class);
                i.putExtra("svPrice", svPrice);
                i.putExtra("serviceID", serviceID);
                lau.launch(i);
            }
        });


    }

    @Override
    public void onPaymentSuccess(String s) {

        Toast.makeText(ServiceDetailActivity.this, s, Toast.LENGTH_SHORT).show();


    }

    @Override
    public void onPaymentError(int i, String s) {
        Toast.makeText(ServiceDetailActivity.this, s, Toast.LENGTH_SHORT).show();
    }
}