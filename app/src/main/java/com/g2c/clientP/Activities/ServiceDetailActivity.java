package com.g2c.clientP.Activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.g2c.clientP.Adapters.PaymentAdapter;
import com.g2c.clientP.Helpers.NetworkChangeListener;
import com.g2c.clientP.Helpers.PaymentCalculator;
import com.g2c.clientP.Helpers.ServiceDataContainer;
import com.g2c.clientP.Helpers.TimeManager;
import com.g2c.clientP.Notifications.FcmNotificationsSender;
import com.g2c.clientP.SessionManager;
import com.g2c.clientP.model.ModelFinalService;
import com.g2c.clientP.model.ModelPackage;
import com.g2c.clientP.model.ModelValidity;
import com.g2c.clientP.model.ModelVehicle;
import com.g2c.clientP.model.MyPackageModel;
import com.g2c.clientP.model.PaymentBoxModel;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.g2c.clientP.Helpers.CustomProgressDialog;
import com.g2c.clientP.R;
import com.g2c.clientP.databinding.ActivityServiceDetailBinding;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.razorpay.Checkout;
import com.razorpay.PaymentData;
import com.razorpay.PaymentResultListener;
import com.razorpay.PaymentResultWithDataListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class ServiceDetailActivity extends AppCompatActivity implements PaymentResultWithDataListener {
    ActivityServiceDetailBinding binding;
    String serviceID;
    PaymentAdapter paymentAdapter;
    ArrayList<MyPackageModel> existingPackages;
    String svPrice;
    String vehicleID;
    PaymentCalculator paymentCalculator;
    CustomProgressDialog progressDialog;
    SessionManager sessionManager;
    TimeManager timeManager;
    ServiceDataContainer serviceDetails;


    DatabaseReference DBref, SlotRef;
    RequestQueue requestQueue;
    String postUrl = "https://api.msg91.com/api/v5/flow/";
    String authKey = "371903ABidvYyDKby61e12476P1";
    JSONArray token_IDs;
    ModelFinalService finalServiceHelper;
    ModelVehicle vehicleHelper;
    SimpleDateFormat sdf;

    FirebaseFunctions mFunctions;
    SimpleDateFormat oidSDF;
    String order_id;

    NetworkChangeListener networkChangeListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        networkChangeListener = new NetworkChangeListener(ServiceDetailActivity.this);
        binding = ActivityServiceDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Checkout.preload(ServiceDetailActivity.this);

        sessionManager = new SessionManager(ServiceDetailActivity.this);
        FirebaseApp.initializeApp(this);
        mFunctions = FirebaseFunctions.getInstance("us-central1");

        progressDialog = new CustomProgressDialog(ServiceDetailActivity.this);
        progressDialog.show();
        timeManager = new TimeManager();

        serviceDetails = (ServiceDataContainer) getIntent().getSerializableExtra("serviceDetails");
        svPrice = serviceDetails.getServicePrice();

        //SimpleDateFormat dateFormat = new SimpleDateFormat("d MMM yyyy");
        existingPackages = new ArrayList<>();

        binding.itemDate.setText(serviceDetails.getServiceData().getDate().replace("_", "/"));
        //Toast.makeText(this, dateFormat.format(timeManager.getSpecificDayTimeStamp(serviceDetails.getServiceData().getDate())), Toast.LENGTH_SHORT).show();
        binding.itemTime.setText(timeManager.getTimeFormat().format(timeManager.getCurrentDayTimeStamp(serviceDetails.getServiceData().getTime())));
        binding.itemC.setText(serviceDetails.getVehicle().getCompany());
        binding.itemM.setText(serviceDetails.getVehicle().getModel());
        binding.itemVhNo.setText(serviceDetails.getVehicle().getVehicleNo());
        binding.itemLocation.setText(serviceDetails.getServiceData().getLocation().getTxt());
        oidSDF = new SimpleDateFormat("_hh_mm_SS");
        sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
        DBref = FirebaseDatabase.getInstance().getReference("Users").child(sessionManager.getUsersDetailsFromSessions().get(SessionManager.KEY_UID));
        SlotRef = FirebaseDatabase.getInstance().getReference("AppManager").child("SlotManager").child("Slots");


        paymentCalculator = new PaymentCalculator();
        serviceDetails.setPaymentCalculator(paymentCalculator);

        paymentAdapter = new PaymentAdapter(ServiceDetailActivity.this);
        paymentAdapter.setServiceDetails(serviceDetails);
        paymentAdapter.setPaymentCalculator(paymentCalculator);

        paymentAdapter.setTotalTextView(binding.totalPrice);
        binding.svPrice.setText("Rs." + svPrice + "/-");


        binding.paymentBox.setAdapter(paymentAdapter);
        binding.paymentBox.setLayoutManager(new LinearLayoutManager(ServiceDetailActivity.this));
        binding.paymentBox.setHasFixedSize(true);


        vehicleID = serviceDetails.getVehicle().getCompany() + "_" + serviceDetails.getVehicle().getModel() + "_" + serviceDetails.getVehicle().getVehicleNo();

        FirebaseDatabase.getInstance().getReference("Services").child(serviceDetails.getServiceID())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot serviceDetails) {

                        if (serviceDetails.exists()) {


                            paymentCalculator.addToOriginalList("Original", "Item Price", serviceDetails.child("servicePrice").getValue(String.class));

                            if (serviceDetails.child("Pricing").exists()) {
                                for (DataSnapshot ps : serviceDetails.child("Pricing").getChildren()) {

                                    paymentCalculator.addToOriginalList("Original", ps.getKey(), ps.getValue(String.class));

                                }
                            }


                            if (ServiceDetailActivity.this.serviceDetails.getCurrentDayDiscount() != null) {
                                paymentCalculator.getOtherOfferList().add(ServiceDetailActivity.this.serviceDetails.getCurrentDayDiscount());
                            }


                            FirebaseDatabase.getInstance().getReference("Users").child(sessionManager.getUsersDetailsFromSessions().get(SessionManager.KEY_UID))
                                    .child("Packages").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot packageListSnap) {


                                            if (packageListSnap.exists()) {
                                                existingPackages.clear();
                                                for (DataSnapshot packageSnap : packageListSnap.getChildren()) {

                                                    MyPackageModel myPackage = packageSnap.getValue(MyPackageModel.class);

                                                    if (Integer.valueOf(myPackage.getServiceCount()) > 0 && Long.valueOf(myPackage.getValidity().getEndDate()) > timeManager.getCurrentTimeStamp()) {

                                                        existingPackages.add(myPackage);
                                                    }

                                                }

                                                Collections.sort(existingPackages, new Comparator<MyPackageModel>() {
                                                    @Override
                                                    public int compare(MyPackageModel modelPackage, MyPackageModel t1) {
                                                        return modelPackage.getValidity().getEndDate().compareTo(t1.getValidity().getEndDate());
                                                    }
                                                });

                                                if (existingPackages.size() > 0) {


                                                    ServiceDetailActivity.this.serviceDetails.setPackageDetails(existingPackages.get(0));

                                                    Integer packageServiceDiscount = (Integer.valueOf(ServiceDetailActivity.this.serviceDetails.getServicePrice()) - Integer.valueOf(existingPackages.get(0).getServiceCost()));
                                                    paymentCalculator.addToOtherOfferList("existingPackage", "Package Service Discount", packageServiceDiscount.toString());

                                                }


                                            }

                                            paymentCalculator.calculateTotal();


                                            paymentAdapter.notifyDataSetChanged();
                                            binding.totalPrice.setText(paymentCalculator.getTotal().toString());


                                            progressDialog.dismiss();


                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });


                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        binding.continueBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createOrderFunction();


            }
        });


        ActivityResultLauncher<Intent> packageLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == Activity.RESULT_OK) {

                    Intent data = result.getData();


                    if (data.getStringExtra("includeType").equals("ownedPackage")) {

                        MyPackageModel myPackage = (MyPackageModel) data.getSerializableExtra("packageDetails");

                        ServiceDetailActivity.this.serviceDetails.setPackageDetails(myPackage);

                        Integer packageServiceDiscount = (Integer.valueOf(ServiceDetailActivity.this.serviceDetails.getServicePrice()) - Integer.valueOf(myPackage.getServiceCost()));
                        paymentCalculator.addToOtherOfferList("existingPackage", "Package Service Discount", packageServiceDiscount.toString());

                        paymentCalculator.calculateTotal();
                        paymentAdapter.notifyDataSetChanged();
                        binding.totalPrice.setText(paymentCalculator.getTotal().toString());

                    }


                    if (data.getStringExtra("includeType").equals("newPackage")) {
                        ModelPackage packageDetails = (ModelPackage) result.getData().getSerializableExtra("packageDetails");


                        paymentCalculator.addToPackageList("package", packageDetails.getName(), packageDetails.getCost());


                        Integer packageServiceDiscount = (Integer.valueOf(serviceDetails.getServicePrice()) - Integer.valueOf(packageDetails.getServiceCost()));
                        paymentCalculator.addToOtherOfferList("otherOffer", "Package Service Discount", packageServiceDiscount.toString());

                        MyPackageModel packageModel = new MyPackageModel();

                        packageModel.setName(packageDetails.getName());
                        packageModel.setPackageID(FirebaseDatabase.getInstance().getReference("Users").child("Packages").push().getKey());
                        packageModel.setCost(packageDetails.getCost());
                        packageModel.setServiceCost(packageDetails.getServiceCost());
                        packageModel.setServiceCount(packageDetails.getServiceCount());

                        Calendar c = Calendar.getInstance();

                        ModelValidity modelValidity = new ModelValidity();

                        modelValidity.setStartDate(String.valueOf(c.getTime().getTime()));

                        c.add(Calendar.DAY_OF_YEAR, Integer.valueOf(packageDetails.getValidity()));

                        modelValidity.setEndDate(String.valueOf(c.getTime().getTime()));

                        packageModel.setValidity(modelValidity);

                        serviceDetails.setPackageDetails(packageModel);

                        paymentCalculator.calculateTotal();
                        paymentAdapter.notifyDataSetChanged();
                        binding.totalPrice.setText(paymentCalculator.getTotal().toString());
                    }


                }
            }
        });


        ActivityResultLauncher<Intent> offerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == Activity.RESULT_OK) {

                    Intent data = result.getData();

                    PaymentBoxModel discountDetails = (PaymentBoxModel) data.getSerializableExtra("discountDetails");

                    paymentCalculator.addToOfferList(discountDetails.getType(), discountDetails.getField(), discountDetails.getValue());

                    paymentCalculator.calculateTotal();

                    paymentAdapter.notifyDataSetChanged();

                    binding.totalPrice.setText(paymentCalculator.getTotal().toString());

                }
            }
        });


        binding.packageLauncher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(ServiceDetailActivity.this, PackageActivity.class);
                i.putExtra("purpose", "include");
                i.putExtra("ownedPackages", existingPackages);
                packageLauncher.launch(i);

            }
        });


        binding.disBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ServiceDetailActivity.this, DiscountActivity.class);
                i.putExtra("productID", serviceDetails.getServiceID());
                offerLauncher.launch(i);
            }
        });


    }


    private void createOrderFunction() {
        progressDialog.show();
        // Create the arguments to the callable function.
        Map<String, Object> data = new HashMap<>();
        data.put("amt", paymentCalculator.getTotal() * 100);
        data.put("receipt", sessionManager.getUsersDetailsFromSessions().get(SessionManager.KEY_PHONE) + sdf.format(Calendar.getInstance().getTime().getTime()));

        mFunctions
                .getHttpsCallable("createOrderID")
                .call(data)
                .continueWith(new Continuation<HttpsCallableResult, String>() {
                    @Override
                    public String then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        // This continuation runs on either success or failure, but if the task
                        // has failed then getResult() will throw an Exception which will be
                        // propagated down.

                        // If Cloud function returns JSON value then Parse JSON array by following method

                        //String return_result = (String) task.getResult().getData();
                        HttpsCallableResult result = task.getResult();


                        Map<String, Object> jsonData = (Map<String, Object>) result.getData();
                        order_id = (String) jsonData.get("id");

                        System.out.println("result from cloud function :" + result.getData().toString());
                        System.out.println("order_id from cloud function  :" + order_id);

                        startPayment();
                        return result.getData().toString();
                    }
                });


    }


    private void startPayment() {


        Checkout checkout = new Checkout();
        checkout.setKeyID("rzp_test_lZQYZSVQyrdHCs");
        checkout.setImage(R.drawable.ic_gear);

        JSONObject object = new JSONObject();
        try {


            object.put("name", "GearToCare");
            object.put("description", "Service payment");
            object.put("theme.color", "#3E64FF");
            object.put("currency", "INR");
            object.put("theme.backdrop_color", "#ffffff");
            object.put("order_id", order_id);
            object.put("amount", paymentCalculator.getTotal());
            object.put("prefill.contact", sessionManager.getUsersDetailsFromSessions().get(SessionManager.KEY_PHONE));
            JSONObject retryObj = new JSONObject();
            retryObj.put("enabled", true);
            retryObj.put("max_count", 4);
            object.put("retry", retryObj);
            progressDialog.dismiss();
            checkout.open(ServiceDetailActivity.this, object);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private void processPayment(String paymentType, String paymentStatus) {

        try {
            progressDialog.show();


            String phone = sessionManager.getUsersDetailsFromSessions().get(SessionManager.KEY_PHONE);

            serviceID = DBref.child("services").push().getKey();

            serviceDetails.getServiceData().setServiceID(serviceID);
            serviceDetails.getServiceData().getPayment().setPaymentStatus(paymentStatus);
            serviceDetails.getServiceData().getPayment().setPaymentType(paymentType);

            serviceDetails.getServiceData().setPhone(phone);
            serviceDetails.getServiceData().setMechanicID("No_Mechanic");
            serviceDetails.getServiceData().setStatus("On_Hold");
            finalServiceHelper = serviceDetails.getServiceData();


            vehicleHelper = serviceDetails.getVehicle();


            DBref.child("vehicles").orderByChild("vehicleNo").equalTo(serviceDetails.getVehicle().getVehicleNo()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot vehiclesSnap) {

                    if (vehiclesSnap.exists()) {//Vehicle with this no. already exists


                        for (DataSnapshot vehicleSnap : vehiclesSnap.getChildren()) {

                            ModelVehicle alreadyPresentVehicle = vehicleSnap.getValue(ModelVehicle.class);

                            serviceDetails.getVehicle().setVehicleID(alreadyPresentVehicle.getVehicleID());

                            createService(false);


                            break;
                        }


                    } else {//Vehicle with this no. doesn't exists

                        String vehicleID = FirebaseDatabase.getInstance().getReference("Users").child(new SessionManager(ServiceDetailActivity.this).getUsersDetailsFromSessions().get(SessionManager.KEY_UID)).child("vehicles").push().getKey();
                        serviceDetails.getVehicle().setVehicleID(vehicleID);

                        DBref.child("vehicles").child(serviceDetails.getVehicle().getVehicleID()).setValue(vehicleHelper)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if (task.isSuccessful()) {

                                            createService(true);
                                        } else {
                                            progressDialog.dismiss();
                                            Toast.makeText(ServiceDetailActivity.this, "Same error occurred please try again", Toast.LENGTH_SHORT).show();
                                        }


                                    }
                                });


                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        } catch (Exception e) {

        }

    }


    public void createService(Boolean newVehicle) {
        progressDialog.show();

        DBref.child("vehicles").child(serviceDetails.getVehicle().getVehicleID()).child("services").child(serviceID).setValue(finalServiceHelper).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {


                    if (newVehicle) {

                        addToSlot();


                    } else {
                        HashMap<String, Object> updateVehicle = new HashMap<>();
                        updateVehicle.put("company", serviceDetails.getVehicle().getCompany());
                        updateVehicle.put("model", serviceDetails.getVehicle().getModel());

                        DBref.child("vehicles").child(serviceDetails.getVehicle().getVehicleID()).updateChildren(updateVehicle).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {

                                    addToSlot();
                                } else {
                                    progressDialog.dismiss();
                                    Toast.makeText(ServiceDetailActivity.this, "Same error occurred please try again", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }


                }


            }
        });


    }


    public void addToSlot() {
        progressDialog.show();
        HashMap<String, Object> sv_det = new HashMap<>();
        sv_det.put("uid", sessionManager.getUsersDetailsFromSessions().get(SessionManager.KEY_UID));
        sv_det.put("serviceID", finalServiceHelper.getServiceID());
        sv_det.put("vehicleID", serviceDetails.getVehicle().getVehicleID());
        sv_det.put("mechanicID", "No_Mechanic");
        sv_det.put("status", "On_Hold");


        SlotRef.child(serviceDetails.getServiceData().getDate()).child(serviceDetails.getServiceData().getTime()).child(serviceID)
                .setValue(sv_det).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            //if added to slot
                            updatePackage();
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(ServiceDetailActivity.this, "Same error occurred please try again", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    public void updatePackage() {
        progressDialog.show();
        if (serviceDetails.getPackageDetails() != null) {
            Integer updatedServiceCount = Integer.valueOf(serviceDetails.getPackageDetails().getServiceCount()) - 1;
            FirebaseDatabase.getInstance().getReference("Users").child(sessionManager.getUsersDetailsFromSessions().get(SessionManager.KEY_UID))
                    .child("Packages").child(serviceDetails.getPackageDetails().getPackageID()).child("serviceCount")
                    .setValue(updatedServiceCount.toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                HashMap<String, Object> hmap = new HashMap<>();
                                hmap.put("date", sdf.format(timeManager.getCurrentTimeStamp()));
                                hmap.put("vehicleID", serviceDetails.getVehicle().getVehicleID());
                                hmap.put("serviceID", serviceDetails.getServiceData().getServiceID());

                                FirebaseDatabase.getInstance().getReference("Users").child(sessionManager.getUsersDetailsFromSessions().get(SessionManager.KEY_UID))
                                        .child("Packages").child(serviceDetails.getPackageDetails().getPackageID()).child("ServiceHistory").child(serviceDetails.getServiceData().getServiceID())
                                        .setValue(hmap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    updateRewards();
                                                } else {
                                                    Toast.makeText(ServiceDetailActivity.this, task.getResult().toString(), Toast.LENGTH_SHORT).show();
                                                    progressDialog.dismiss();
                                                }
                                            }
                                        });


                            } else {
                                Toast.makeText(ServiceDetailActivity.this, task.getResult().toString(), Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        }
                    });

        } else {
            updateRewards();
        }

    }

    public void updateRewards() {

        if (serviceDetails.getPaymentCalculator().getOfferList().size() > 0) {//offers are applied
            for (PaymentBoxModel offer : serviceDetails.getPaymentCalculator().getOfferList()) {

                if (offer.getType().equals("points")) {

                    progressDialog.show();
                    DBref.child("Referral").child("points").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot pointsSnap) {
                            if (pointsSnap.exists()) {

                                Integer originalPoints = Integer.valueOf(pointsSnap.getValue(String.class));
                                Integer usedPoints = Integer.valueOf(offer.getValue());
                                Integer finalPoints;
                                if (originalPoints > usedPoints) {
                                    finalPoints = originalPoints - usedPoints;
                                } else {
                                    finalPoints = 0;
                                }

                                DBref.child("Referral").child("points").setValue(finalPoints.toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {

                                            HashMap<String, Object> hmap = new HashMap<>();
                                            hmap.put("date", sdf.format(timeManager.getCurrentTimeStamp()));
                                            hmap.put("value", usedPoints);
                                            hmap.put("vehicleID", serviceDetails.getVehicle().getVehicleID());
                                            hmap.put("serviceID", serviceDetails.getServiceData().getServiceID());
                                            DBref.child("RewardHistory").child("Points").child("Used").child(serviceDetails.getServiceData().getServiceID())
                                                    .setValue(hmap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                makeBill();
                                                            } else {
                                                                Toast.makeText(ServiceDetailActivity.this, task.getResult().toString(), Toast.LENGTH_SHORT).show();
                                                                progressDialog.dismiss();
                                                            }
                                                        }
                                                    });
                                        } else {
                                            Toast.makeText(ServiceDetailActivity.this, task.getResult().toString(), Toast.LENGTH_SHORT).show();
                                            progressDialog.dismiss();
                                        }
                                    }
                                });

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                } else if (offer.getType().equals("coupon")) {
                    progressDialog.show();
                    HashMap<String, Object> hmap = new HashMap<>();
                    hmap.put("date", sdf.format(timeManager.getCurrentTimeStamp()));
                    hmap.put("value", offer.getValue());
                    hmap.put("vehicleID", serviceDetails.getVehicle().getVehicleID());
                    hmap.put("serviceID", serviceDetails.getServiceData().getServiceID());
                    DBref.child("RewardHistory").child("Coupons").child(offer.getField()).child(serviceDetails.getServiceData().getServiceID())
                            .setValue(hmap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        makeBill();
                                    } else {
                                        Toast.makeText(ServiceDetailActivity.this, task.getResult().toString(), Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                    }
                                }
                            });
                }

                break;
            }
        } else {
            makeBill();
        }


    }

    public void makeBill() {
        progressDialog.show();

        if (serviceDetails.getPaymentCalculator().getPaymentList().size() > 0) {
            HashMap<String, Object> billMap = new HashMap<>();
            Toast.makeText(this, String.valueOf(serviceDetails.getPaymentCalculator().getPaymentList().size()), Toast.LENGTH_SHORT).show();

            for (PaymentBoxModel pbm : serviceDetails.getPaymentCalculator().getPaymentList()) {

                billMap.put(pbm.getField(), pbm);
            }

            DBref.child("vehicles").child(serviceDetails.getVehicle().getVehicleID()).child("services").child(serviceID)
                    .child("payment").child("bill").setValue(billMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                //sendConfirmationSms();
                                sendNotification();
                            } else {
                                Toast.makeText(ServiceDetailActivity.this, task.getResult().toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            Toast.makeText(this, "Some error occurred, try again.", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }


    }

    public void sendConfirmationSms() {

        requestQueue = Volley.newRequestQueue(ServiceDetailActivity.this);
        JSONObject mainObj = new JSONObject();
        try {

            JSONObject body = new JSONObject();

            body.put("flow_id", "6239d0f7c8913c3c9e532962");
            body.put("sender", "GTCTWS");
            body.put("mobiles", "91" + sessionManager.getUsersDetailsFromSessions().get(SessionManager.KEY_PHONE));
            body.put("Name", sessionManager.getUsersDetailsFromSessions().get(SessionManager.KEY_FULLNAME));
            body.put("vhno", serviceDetails.getVehicle().getCompany() + " | " + serviceDetails.getVehicle().getModel() + " | " + serviceDetails.getVehicle().getVehicleNo());
            body.put("date", timeManager.getDateFormat().format(timeManager.getSpecificDayTimeStamp(serviceDetails.getServiceData().getDate())));


            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, postUrl, body, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    // code run is got response

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // code run is got error

                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {


                    Map<String, String> header = new HashMap<>();
                    header.put("content-type", "application/json");
                    header.put("authkey", authKey);
                    return header;


                }
            };
            requestQueue.add(request);
            sendNotification();

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    public void sendNotification() {
        token_IDs = new JSONArray();
        FcmNotificationsSender notificationsSender = new FcmNotificationsSender("none", getApplicationContext(), ServiceDetailActivity.this);

        try {
            notificationsSender.setNotificationData("Service Booked", sessionManager.getUsersDetailsFromSessions().get(SessionManager.KEY_FULLNAME) + " booked a service");
            notificationsSender.getNotificationData().put("purpose", "New_Booking");
            notificationsSender.getNotificationData().put("uid", sessionManager.getUsersDetailsFromSessions().get(SessionManager.KEY_UID));
            notificationsSender.getNotificationData().put("fullName", sessionManager.getUsersDetailsFromSessions().get(SessionManager.KEY_FULLNAME));
            notificationsSender.getNotificationData().put("serviceID", serviceID);
            notificationsSender.getNotificationData().put("vehicleID", serviceDetails.getVehicle().getVehicleID());
            notificationsSender.getNotificationData().put("phone", sessionManager.getUsersDetailsFromSessions().get(SessionManager.KEY_PHONE));


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
                    Intent i = new Intent(ServiceDetailActivity.this, ConfirmationActivity.class);
                    i.putExtra("serviceDetails", serviceDetails);
                    progressDialog.dismiss();
                    startActivity(i);
                    finishAffinity();
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
        registerReceiver(networkChangeListener, filter);

    }

    @Override
    protected void onStop() {
        unregisterReceiver(networkChangeListener);
        super.onStop();
    }

    @Override
    public void onPaymentSuccess(String s, PaymentData paymentData) {
        progressDialog.show();

        Map<String, Object> data_res = new HashMap<>();
        data_res.put("razorpay_payment_id", paymentData.getPaymentId());
        data_res.put("razorpay_order_id", order_id);
        data_res.put("razorpay_signature", paymentData.getSignature());

        mFunctions
                .getHttpsCallable("verifySignature")
                .call(data_res)
                .continueWith(new Continuation<HttpsCallableResult, String>() {
                    @Override
                    public String then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        String result = (String) task.getResult().getData();
                        System.out.println("Signature result from cloud function :" + result);

                        //checking the signature
                        if (result.equals(paymentData.getSignature()))//if payment is successful
                        {
                            processPayment("payAfterService", "On_Hold");

                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(ServiceDetailActivity.this, "Payment failed try again.", Toast.LENGTH_SHORT).show();
                        }
                        return result;
                    }
                });
    }

    @Override
    public void onPaymentError(int i, String s, PaymentData paymentData) {
        Toast.makeText(this, "Payment failed! try again", Toast.LENGTH_SHORT).show();
    }


}