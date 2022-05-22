package com.geartocare.client.Activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
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
import com.geartocare.client.SessionManager;
import com.geartocare.client.model.ModelCoupon;
import com.geartocare.client.model.ModelPackage;
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
import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class ServiceDetailActivity extends AppCompatActivity implements PaymentResultListener {
    ActivityServiceDetailBinding binding;
    String serviceID;
    ServiceCheckListAdapter adapter;
    PaymentAdapter paymentAdapter;
    ArrayList<PaymentBoxModel> paymentList, originalPaymentList;
    ArrayList<String> svCheckList;
    String svPrice;
    String vehicleID;
    Integer total, orignalTotal;
    CustomProgressDialog dialog;
    SessionManager sessionManager;
    JSONArray token_IDs;
    ModelPackage packageDetails;
    PaymentBoxModel discountDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityServiceDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        HashMap<String, String> hm = (HashMap) getIntent().getSerializableExtra("serviceDetails");
        svPrice = hm.get("svPrice");
        dialog = new CustomProgressDialog(ServiceDetailActivity.this);
        dialog.show();
        serviceID = hm.get("serviceID");
        sessionManager = new SessionManager(ServiceDetailActivity.this);

        svCheckList = new ArrayList<>();
        paymentList = new ArrayList<>();
        originalPaymentList = new ArrayList<>();
        adapter = new ServiceCheckListAdapter(ServiceDetailActivity.this, svCheckList, serviceID);
        paymentAdapter = new PaymentAdapter(ServiceDetailActivity.this, paymentList);

        binding.svPrice.setText("Rs." + svPrice + "/-");

        paymentAdapter.setOriginalPaymentList(originalPaymentList);
        paymentAdapter.setPackageDetails(packageDetails);
        paymentAdapter.setDiscountDetails(discountDetails);
        paymentAdapter.setTotal(total);
        paymentAdapter.setOrignalTotal(orignalTotal);

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


                            for (DataSnapshot ps : serviceDetails.child("Pricing").getChildren()) {

                                PaymentBoxModel pbm = new PaymentBoxModel();
                                pbm.setField(ps.getKey());
                                pbm.setValue(ps.getValue(String.class));
                                pbm.setType("Original");

                                originalPaymentList.add(pbm);
                                orignalTotal += Integer.valueOf(pbm.getValue());

                            }

                            PaymentBoxModel svp = new PaymentBoxModel();
                            svp.setField("Item Price");
                            svp.setValue(serviceDetails.child("servicePrice").getValue(String.class));
                            svp.setType("Original");
                            originalPaymentList.add(svp);
                            orignalTotal += Integer.valueOf(svp.getValue());

                            total = orignalTotal;

                            paymentList.clear();
                            for (PaymentBoxModel p : originalPaymentList) {
                                paymentList.add(p);
                            }
                            paymentAdapter.notifyDataSetChanged();
                            binding.totalPrice.setText(String.valueOf(total));


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


        binding.continueBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {




           /*     token_IDs = new JSONArray();
                FcmNotificationsSender notificationsSender = new FcmNotificationsSender("none", getApplicationContext(), ServiceDetailActivity.this);

                try {
                    notificationsSender.setNotificationData("Service Booked",sessionManager.getUsersDetailsFromSessions().get(SessionManager.KEY_FULLNAME) + " booked a service");
                    notificationsSender.getNotificationData().put("goTo","onHold");
                    notificationsSender.getNotificationData().put("uid",sessionManager.getUsersDetailsFromSessions().get(SessionManager.KEY_UID));
                    notificationsSender.getNotificationData().put("fullName",sessionManager.getUsersDetailsFromSessions().get(SessionManager.KEY_FULLNAME));
                    notificationsSender.getNotificationData().put("serviceID",serviceID);
                    notificationsSender.getNotificationData().put("vehicleID",vehicleID);
                    notificationsSender.getNotificationData().put("phone",sessionManager.getUsersDetailsFromSessions().get(SessionManager.KEY_PHONE));



                } catch (JSONException e) {
                    Toast.makeText(ServiceDetailActivity.this, "hey", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }


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
*/

//
                Intent i = new Intent(ServiceDetailActivity.this, PaymentActivity.class);
                hm.put("totalPrice", binding.totalPrice.getText().toString());
                i.putExtra("serviceDetails", hm);
                startActivity(i);
//
            }
        });


        ActivityResultLauncher<Intent> packageLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == Activity.RESULT_OK) {

                    packageDetails = (ModelPackage) result.getData().getSerializableExtra("packageDetails");

                    total = orignalTotal;


                    paymentList.clear();
                    for (PaymentBoxModel p : originalPaymentList) {
                        paymentList.add(p);
                    }


                    PaymentBoxModel packageValue = new PaymentBoxModel();
                    packageValue.setField(packageDetails.getName());
                    packageValue.setValue(packageDetails.getCost());
                    packageValue.setType("package");
                    addToBill(packageValue);


                    if (discountDetails != null) {

                        if (discountDetails.getType().equals("coupon")) {


                            addToBill(discountDetails);


                        } else if (discountDetails.getType().equals("points")) {


                            addToBill(discountDetails);


                        }

                    }
                    paymentAdapter.notifyDataSetChanged();
                    binding.totalPrice.setText(total.toString());

                }
            }
        });


        ActivityResultLauncher<Intent> offerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    total = orignalTotal;

                    paymentList.clear();
                    for (PaymentBoxModel p : originalPaymentList) {
                        paymentList.add(p);
                    }


                    Intent data = result.getData();
                    discountDetails = (PaymentBoxModel) data.getSerializableExtra("discountDetails");

                    if (packageDetails != null) {
                        PaymentBoxModel packageValue = new PaymentBoxModel();
                        packageValue.setField(packageDetails.getName());
                        packageValue.setValue(packageDetails.getCost());
                        packageValue.setType("package");
                        addToBill(packageValue);
                    }


                    if (discountDetails.getType().equals("coupon")) {


                        addToBill(discountDetails);



                    } else if (discountDetails.getType().equals("points")) {


                        addToBill(discountDetails);


                    }
                    paymentAdapter.notifyDataSetChanged();
                    binding.totalPrice.setText(total.toString());
                }
            }
        });

        binding.packageLauncher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(ServiceDetailActivity.this, PackageActivity.class);
                i.putExtra("purpose", "include");
                packageLauncher.launch(i);

            }
        });


        binding.disBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ServiceDetailActivity.this, DiscountActivity.class);
                i.putExtra("svPrice", svPrice);
                i.putExtra("serviceID", serviceID);
                offerLauncher.launch(i);
            }
        });


    }

    public void addToBill(PaymentBoxModel pbm) {
        paymentList.add(pbm);
        total += Integer.valueOf(pbm.getValue());

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