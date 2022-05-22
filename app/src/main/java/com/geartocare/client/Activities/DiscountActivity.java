package com.geartocare.client.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.geartocare.client.SessionManager;
import com.geartocare.client.model.ModelCoupon;
import com.geartocare.client.model.PaymentBoxModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.geartocare.client.R;
import com.geartocare.client.databinding.ActivityDiscountBinding;

public class DiscountActivity extends AppCompatActivity {
    ActivityDiscountBinding binding;
    String coupon;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDiscountBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        sessionManager = new SessionManager(DiscountActivity.this);
        //serviceID = getIntent().getStringExtra("serviceID");

        View.OnClickListener applyCLick = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                coupon = binding.couponBox.getEditText().getText().toString();


                FirebaseDatabase.getInstance().getReference("AppManager").child("CouponsAndOffers")
                        .child("Codes").child(coupon).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot couponDB) {

                        if (couponDB.exists()) {
                            //String finalPrice = String.valueOf(Integer.valueOf(svPrice) - Integer.valueOf(couponDB.getValue(String.class)));

                            Intent i = new Intent();
                            PaymentBoxModel pbm = new PaymentBoxModel();
                            pbm.setField(couponDB.getKey());
                            pbm.setValue(couponDB.getValue(String.class));
                            pbm.setType("coupon");
                            i.putExtra("discountDetails",pbm);


                            setResult(Activity.RESULT_OK, i);
                            finish();
                        } else {
                            binding.couponBox.getEditText().setText("");
                            AlertDialog.Builder builder = new AlertDialog.Builder(DiscountActivity.this);
                            builder.setMessage("Invalid Coupon");
                            builder.setTitle("GearToCare");
                            builder.setIcon(R.drawable.ic_baseline_error_24);
                            builder.setCancelable(true);
                            builder.show();
                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }
        };


        FirebaseDatabase.getInstance().getReference("Users").child(sessionManager.getUsersDetailsFromSessions().get(SessionManager.KEY_UID))
                .child("Referral").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot Referral) {

                if (Referral.exists()) {


                    binding.points.setText(Referral.child("points").getValue(String.class));
                    //binding.code.setText(Referral.child("code").getValue(String.class));


                    binding.useBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent i = new Intent();
                            PaymentBoxModel pbm = new PaymentBoxModel();
                            pbm.setField("G2C Points");
                            pbm.setValue(String.valueOf(Integer.valueOf(Referral.child("points").getValue(String.class))*(-1)));
                            pbm.setType("points");
                            i.putExtra("discountDetails",pbm);

                            setResult(Activity.RESULT_OK, i);
                            finish();

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
}