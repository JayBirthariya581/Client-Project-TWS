package com.geartocare.client.Activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.geartocare.client.Adapters.PDVehicleAdapter;
import com.geartocare.client.Adapters.PaymentAdapter;
import com.geartocare.client.R;
import com.geartocare.client.SessionManager;
import com.geartocare.client.databinding.ActivityPackageDetailBinding;
import com.geartocare.client.model.ModelPackage;
import com.geartocare.client.model.ModelVehicle;
import com.geartocare.client.model.PaymentBoxModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PackageDetailActivity extends AppCompatActivity implements PaymentResultListener {
    ActivityPackageDetailBinding binding;
    PDVehicleAdapter vehicleAdapter;
    ArrayList<ModelVehicle> vehicles;
    String[] sv;
    ModelPackage m;
    Integer total = 0, orignalTotal = 0;
    PaymentAdapter paymentAdapter;
    ArrayList<PaymentBoxModel> paymentList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPackageDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        vehicles = new ArrayList<>();
        vehicleAdapter = new PDVehicleAdapter(PackageDetailActivity.this, vehicles);

        sv = getIntent().getStringExtra("selectedVehicles").split(",");

        paymentList = new ArrayList<>();
        paymentAdapter = new PaymentAdapter(PackageDetailActivity.this, paymentList);

        //Toast.makeText(PackageDetailActivity.this, , Toast.LENGTH_SHORT).show();
        binding.rvPackageVehicles.setLayoutManager(new LinearLayoutManager(PackageDetailActivity.this));
        binding.rvPackageVehicles.setHasFixedSize(true);
        binding.rvPackageVehicles.setAdapter(vehicleAdapter);

        binding.paymentBox.setAdapter(paymentAdapter);
        binding.paymentBox.setLayoutManager(new LinearLayoutManager(PackageDetailActivity.this));
        binding.paymentBox.setHasFixedSize(true);


        makePackage();
        makeList();

        binding.continueBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Checkout checkout = new Checkout();
                int amount = Math.round(Float.parseFloat(m.getCost()) * 100);
                // set your id as below
                checkout.setKeyID("rzp_test_uHXTStGEWf82i1");

                // set image
                checkout.setImage(R.drawable.ic_gear);

                // initialize json object
                JSONObject object = new JSONObject();
                try {
                    // to put name
                    object.put("name", "GearToCare");

                    // put description
                    object.put("description", "Two wheeler service");

                    // to set theme color
                    object.put("theme.color", "");

                    // put the currency
                    object.put("currency", "INR");

                    // put amount
                    object.put("amount", amount);

                    // put mobile number
                    object.put("prefill.contact", new SessionManager(PackageDetailActivity.this).getUsersDetailsFromSessions().get(SessionManager.KEY_PHONE));

                    // put email
                    object.put("prefill.email", "chaitanyamunje@gmail.com");

                    // open razorpay to checkout activity
                    checkout.open(PackageDetailActivity.this, object);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });


    }

    private void makePackage() {
        FirebaseDatabase.getInstance().getReference("AppManager").child("PackageManager").child(getIntent().getStringExtra("packageID")).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot pack) {
                if (pack.exists()) {

                    m = pack.getValue(ModelPackage.class);

                    binding.name.setText(m.getName());
                    binding.cost.setText(m.getCost());
                    binding.serviceCost.setText(m.getServiceCost());
                    binding.validity.setText(m.getValidity());
                    binding.vehicleCount.setText(m.getVehicleCount());
                    binding.description.setText(m.getDescription());
                    binding.svPckPrice.setText(m.getCost());
                    orignalTotal = Integer.valueOf(m.getCost());
                    total = orignalTotal;

                    PaymentBoxModel pbm = new PaymentBoxModel();
                    pbm.setField("Item Price");
                    pbm.setValue(String.valueOf(total));

                    paymentList.add(pbm);
                    paymentAdapter.notifyDataSetChanged();


                    binding.totalPrice.setText(String.valueOf(total));


                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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
                        binding.svPckPrice.setText(total.toString());

                    } else if (data.getStringExtra("type").equals("points")) {
                        PaymentBoxModel pbm = new PaymentBoxModel();
                        pbm.setField("G2C points");
                        Integer val = Integer.valueOf(data.getStringExtra("pointValue")) * -1;
                        pbm.setValue(String.valueOf(val));

                        paymentList.add(pbm);
                        total += Integer.valueOf(pbm.getValue());
                        paymentAdapter.notifyDataSetChanged();


                        binding.totalPrice.setText(total.toString());
                        binding.svPckPrice.setText(total.toString());
                    }


                }
            }
        });


        binding.disBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(PackageDetailActivity.this, DiscountActivity.class);
                //i.putExtra("svPrice", svPrice);
                //i.putExtra("serviceID", serviceID);
                lau.launch(i);
            }
        });


    }


    private void makeList() {
        vehicles.clear();
        FirebaseDatabase.getInstance().getReference("Users").child(new SessionManager(PackageDetailActivity.this).getUsersDetailsFromSessions().get(SessionManager.KEY_UID)).child("vehicles")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot bookings) {


                        if (bookings.exists()) {


                            for (int j = 0; j < sv.length; j++) {


                                ModelVehicle bookingFromDB = bookings.child(sv[j]).getValue(ModelVehicle.class);

                                vehicles.add(bookingFromDB);
                            }


                            vehicleAdapter.notifyDataSetChanged();
                            //dialog.dismiss();


                        } else {
                            //dialog.dismiss();
                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public void onPaymentSuccess(String s) {

    }

    @Override
    public void onPaymentError(int i, String s) {

    }
}