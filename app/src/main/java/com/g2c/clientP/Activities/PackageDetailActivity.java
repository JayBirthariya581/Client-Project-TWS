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

import com.g2c.clientP.Adapters.PaymentAdapter;
import com.g2c.clientP.Helpers.CustomProgressDialog;
import com.g2c.clientP.Helpers.NetworkChangeListener;
import com.g2c.clientP.Helpers.PaymentCalculator;
import com.g2c.clientP.R;
import com.g2c.clientP.SessionManager;
import com.g2c.clientP.databinding.ActivityPackageDetailBinding;
import com.g2c.clientP.model.ModelPackage;
import com.g2c.clientP.model.ModelPayment;
import com.g2c.clientP.model.ModelValidity;
import com.g2c.clientP.model.MyPackageModel;
import com.g2c.clientP.model.PaymentBoxModel;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.razorpay.Checkout;
import com.razorpay.PaymentData;
import com.razorpay.PaymentResultWithDataListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class PackageDetailActivity extends AppCompatActivity implements PaymentResultWithDataListener {
    ActivityPackageDetailBinding binding;
    ModelPackage packageDetails;
    PaymentAdapter paymentAdapter;
    PaymentCalculator paymentCalculator;
    ActivityResultLauncher<Intent> offerLauncher;
    SessionManager sessionManager;
    CustomProgressDialog progressDialog;
    FirebaseFunctions mFunctions;
    SimpleDateFormat oidSDF;
    String order_id;
    NetworkChangeListener networkChangeListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        networkChangeListener = new NetworkChangeListener(PackageDetailActivity.this);
        binding = ActivityPackageDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Checkout.preload(PackageDetailActivity.this);
        progressDialog = new CustomProgressDialog(PackageDetailActivity.this);
        sessionManager = new SessionManager(PackageDetailActivity.this);
        packageDetails = (ModelPackage) getIntent().getSerializableExtra("packageDetails");
        paymentCalculator = new PaymentCalculator();
        paymentAdapter = new PaymentAdapter(PackageDetailActivity.this);
        paymentAdapter.setPaymentCalculator(paymentCalculator);
        paymentAdapter.setTotalTextView(binding.totalPrice);
        oidSDF = new SimpleDateFormat("_hh_mm_SS");


        binding.paymentBox.setAdapter(paymentAdapter);
        binding.paymentBox.setLayoutManager(new LinearLayoutManager(PackageDetailActivity.this));
        binding.paymentBox.setHasFixedSize(true);


        offerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
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

        FirebaseApp.initializeApp(this);
        mFunctions = FirebaseFunctions.getInstance("us-central1");



        binding.continueBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                createOrderFunction();

            }
        });

        makePackage();
        //payments


    }


    private void createOrderFunction() {
        progressDialog.show();
        // Create the arguments to the callable function.
        Map<String, Object> data = new HashMap<>();
        data.put("amt", paymentCalculator.getTotal() * 100);
        data.put("receipt", sessionManager.getUsersDetailsFromSessions().get(SessionManager.KEY_PHONE) + oidSDF.format(Calendar.getInstance().getTime().getTime()));

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


            object.put("name", "Gear to Care");
            object.put("description", "Test payment");
            object.put("theme.color", "#3399CC");
            object.put("currency", "INR");
            object.put("order_id", order_id);
            object.put("amount", paymentCalculator.getTotal());
            object.put("prefill.contact", sessionManager.getUsersDetailsFromSessions().get(SessionManager.KEY_PHONE));
           // object.put("prefill.email", sessionManager.getUsersDetailsFromSessions().get(SessionManager.KEY_EMAIL));
//            JSONObject retryObj = new JSONObject();
//            retryObj.put("enabled", true);
//            retryObj.put("max_count", 4);
//            object.put("retry", retryObj);

            Toast.makeText(this, "awdawd", Toast.LENGTH_SHORT).show();
            checkout.open(PackageDetailActivity.this, object);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    public void processPackage(String paymentID) {

        progressDialog.show();
        MyPackageModel myPackage = new MyPackageModel();

        myPackage.setName(packageDetails.getName());
        myPackage.setCost(packageDetails.getCost());
        myPackage.setPackageID(FirebaseDatabase.getInstance().getReference("Users").child(sessionManager.getUsersDetailsFromSessions().get(SessionManager.KEY_UID))
                .child("Packages").push().getKey());
        myPackage.setServiceCost(packageDetails.getServiceCost());

        ModelPayment mp = new ModelPayment();
        mp.setPaymentStatus("Done");
        mp.setPaymentType("Online");
        mp.setPrice(paymentCalculator.getTotal().toString());
        mp.setPaymentID(paymentID);
        myPackage.setPayment(mp);

        Calendar c = Calendar.getInstance();

        ModelValidity modelValidity = new ModelValidity();
        modelValidity.setStartDate(String.valueOf(c.getTime().getTime()));

        c.add(Calendar.DAY_OF_YEAR, Integer.valueOf(packageDetails.getValidity()));

        modelValidity.setEndDate(String.valueOf(c.getTime().getTime()));

        myPackage.setValidity(modelValidity);

        myPackage.setServiceCount(packageDetails.getServiceCount());

        FirebaseDatabase.getInstance().getReference("Users").child(sessionManager.getUsersDetailsFromSessions().get(SessionManager.KEY_UID))
                .child("Packages").child(myPackage.getPackageID()).setValue(myPackage).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                        }
                    }
                });
    }


    private void makePackage() {
        progressDialog.show();
        binding.packageName.setText(packageDetails.getName());
        binding.packageCost.setText("Rs." + packageDetails.getCost() + "/-");
        binding.serviceCost.setText("Rs." + packageDetails.getServiceCost() + "/-");
        binding.serviceCount.setText(packageDetails.getServiceCount() + " services");
        binding.valDays.setText(packageDetails.getValidity() + " days");
        binding.packageDescription.setText(packageDetails.getDescription());

        Calendar c = Calendar.getInstance();

        ModelValidity modelValidity = new ModelValidity();

        modelValidity.setStartDate(String.valueOf(c.getTime().getTime()));

        c.add(Calendar.DAY_OF_YEAR, Integer.valueOf(packageDetails.getValidity()));

        SimpleDateFormat oidSDF = new SimpleDateFormat("d MMM yyyy");

        binding.valExp.setText("Expires on " + oidSDF.format(c.getTime().getTime()));


        paymentCalculator.addToOriginalList("Original", "Package cost", packageDetails.getCost());

        paymentCalculator.calculateTotal();

        binding.totalPrice.setText(paymentCalculator.getTotal().toString());


        binding.disBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(PackageDetailActivity.this, DiscountActivity.class);
                offerLauncher.launch(i);
            }
        });

        progressDialog.dismiss();
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
                            processPackage(paymentData.getPaymentId());

                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(PackageDetailActivity.this, "Payment failed try again.", Toast.LENGTH_SHORT).show();
                        }
                        return result;
                    }
                });
    }

    @Override
    public void onPaymentError(int i, String s, PaymentData paymentData) {
        progressDialog.dismiss();
        Toast.makeText(this, "Payment failed! try again", Toast.LENGTH_SHORT).show();
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
}