package com.g2c.clientP.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.g2c.clientP.Helpers.CouponChecker;
import com.g2c.clientP.Helpers.NetworkChangeListener;
import com.g2c.clientP.SessionManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.g2c.clientP.R;
import com.g2c.clientP.databinding.ActivityDiscountBinding;

public class DiscountActivity extends AppCompatActivity {
    ActivityDiscountBinding binding;
    String coupon;
    SessionManager sessionManager;
    CouponChecker couponChecker;
    String productID;

     NetworkChangeListener networkChangeListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        networkChangeListener = new NetworkChangeListener(DiscountActivity.this);
        binding = ActivityDiscountBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        sessionManager = new SessionManager(DiscountActivity.this);
        couponChecker = new CouponChecker(DiscountActivity.this);
        couponChecker.fetchCouponsAndOffers();
        productID = getIntent().getStringExtra("productID");


        couponChecker.getCouponAlertBinding().btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.couponBox.getEditText().setText("");
                couponChecker.getCouponAlertDialog().dismiss();
            }
        });

        View.OnClickListener applyCLick = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                coupon = binding.couponBox.getEditText().getText().toString();

                couponChecker.checkCoupon(coupon, productID);

            }
        };


        FirebaseDatabase.getInstance().getReference("Users").child(sessionManager.getUsersDetailsFromSessions().get(SessionManager.KEY_UID))
                .child("Referral").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot Referral) {

                        if (Referral.exists()) {


                            binding.points.setText(Referral.child("points").getValue(String.class));


                            binding.useBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    couponChecker.checkPoints(Integer.valueOf(Referral.child("points").getValue(String.class)), productID);
                                }
                            });

                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        binding.couponBox.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {


                if (editable.toString().length() > 0) {
                    binding.applyBtn.setTextColor(Color.parseColor("#395cee"));
                    binding.applyBtn.setOnClickListener(applyCLick);

                } else {
                    binding.applyBtn.setTextColor(Color.parseColor("#9aa4ec"));
                    binding.applyBtn.setOnClickListener(null);
                }
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