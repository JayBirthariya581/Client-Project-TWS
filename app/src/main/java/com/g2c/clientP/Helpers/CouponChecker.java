package com.g2c.clientP.Helpers;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.g2c.clientP.SessionManager;
import com.g2c.clientP.databinding.CouponAlertBinding;
import com.g2c.clientP.databinding.UnavailableDialogBinding;
import com.g2c.clientP.model.ModelCoupon;
import com.g2c.clientP.model.PaymentBoxModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

public class CouponChecker {

    Context context;
    CustomProgressDialog progressDialog;
    DatabaseReference DBRef;
    DataSnapshot couponAndOffersSnap;
    TimeManager timeManager;
    SessionManager sessionManager;
    Dialog couponAlertDialog;
    CouponAlertBinding couponAlertBinding;

    public CouponChecker(Context context) {
        this.context = context;
        progressDialog = new CustomProgressDialog(context);
        DBRef = FirebaseDatabase.getInstance().getReference("AppManager").child("CouponsAndOffers");
        timeManager = new TimeManager();
        sessionManager = new SessionManager(context);

        couponAlertBinding = CouponAlertBinding.inflate(((Activity) context).getLayoutInflater());

        couponAlertDialog = new Dialog(context);
        couponAlertDialog.setContentView(couponAlertBinding.getRoot());
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(couponAlertDialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        couponAlertDialog.getWindow().setAttributes(lp);


    }

    public Dialog getCouponAlertDialog() {
        return couponAlertDialog;
    }

    public void setCouponAlertDialog(Dialog couponAlertDialog) {
        this.couponAlertDialog = couponAlertDialog;
    }

    public CouponAlertBinding getCouponAlertBinding() {
        return couponAlertBinding;
    }

    public void setCouponAlertBinding(CouponAlertBinding couponAlertBinding) {
        this.couponAlertBinding = couponAlertBinding;
    }

    public void fetchCouponsAndOffers() {
        progressDialog.show();
        DBRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot couponAndOffersSnap) {


                if (couponAndOffersSnap.exists()) {


                    CouponChecker.this.couponAndOffersSnap = couponAndOffersSnap;

                    progressDialog.dismiss();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    public void checkCoupon(String coupon, String productID) {
        progressDialog.show();

        if (couponAndOffersSnap != null) {
            FirebaseDatabase.getInstance().getReference("Users").child(sessionManager.getUsersDetailsFromSessions().get(SessionManager.KEY_UID))
                    .child("RewardHistory").child("Coupons").child(coupon).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot couponHistorySnap) {
                            if (couponAndOffersSnap.child("Codes").child(coupon).exists()) {//if coupon exists

                                ModelCoupon m = couponAndOffersSnap.child("Codes").child(coupon).getValue(ModelCoupon.class);
                                long difference_In_Days
                                        = TimeUnit
                                        .MILLISECONDS
                                        .toDays(Long.valueOf(m.getExpiry()) - timeManager.getCurrentTimeStamp())
                                        % 365;
                                if (difference_In_Days >= 0) {//if coupon is within expiry date

                                    if (couponAndOffersSnap.child("Codes").child(coupon).child("ApplicableList").child(productID).exists()) {//if coupon is applicable here

                                        if (couponHistorySnap.exists()) {
                                            if (couponHistorySnap.getChildrenCount() < Integer.valueOf(m.getUseLimit())) {

                                                Intent i = new Intent();
                                                PaymentBoxModel pbm = new PaymentBoxModel();
                                                pbm.setField(coupon);
                                                pbm.setValue(String.valueOf(Integer.valueOf(m.getValue())));
                                                pbm.setType("coupon");
                                                i.putExtra("discountDetails", pbm);
                                                ((Activity) context).setResult(Activity.RESULT_OK, i);
                                                ((Activity) context).finish();


                                            } else {
                                                couponAlertBinding.msg.setText("Coupon limit exceeded!");
                                                couponAlertDialog.show();
                                            }
                                        } else {

                                            Intent i = new Intent();
                                            PaymentBoxModel pbm = new PaymentBoxModel();
                                            pbm.setField(coupon);
                                            pbm.setValue(String.valueOf(Integer.valueOf(m.getValue())));
                                            pbm.setType("coupon");
                                            i.putExtra("discountDetails", pbm);
                                            ((Activity) context).setResult(Activity.RESULT_OK, i);
                                            ((Activity) context).finish();
                                        }


                                    } else {
                                        couponAlertBinding.msg.setText("Coupon not applicable for selected product!");
                                        couponAlertDialog.show();
                                    }

                                } else {
                                    couponAlertBinding.msg.setText("Coupon expired!");
                                    couponAlertDialog.show();
                                }


                            } else {
                                couponAlertBinding.msg.setText("Invalid coupon !");
                                couponAlertDialog.show();
                            }

                            progressDialog.dismiss();

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


        } else {
            Toast.makeText(context, "Some error occurred", Toast.LENGTH_SHORT).show();
            ((Activity) context).finish();
        }


    }


    public void checkPoints(Integer points, String productID) {
        progressDialog.show();
        if (couponAndOffersSnap.child("Points").child("SpecificPointLimit").child(productID).exists()) {

            Integer specificLimit = Integer.valueOf(couponAndOffersSnap.child("Points").child("SpecificPointLimit").child(productID).getValue(String.class));


            Intent i = new Intent();
            PaymentBoxModel pbm = new PaymentBoxModel();
            pbm.setField("G2C Points");
            if (points <= specificLimit) {
                pbm.setValue(points.toString());
            }else{
                pbm.setValue(String.valueOf(specificLimit));
            }
            pbm.setType("points");
            i.putExtra("discountDetails", pbm);
            ((Activity) context).setResult(Activity.RESULT_OK, i);
            ((Activity) context).finish();

        } else if (couponAndOffersSnap.child("Points").child("GeneralPointLimit").exists()) {

            Integer generalLimit = Integer.valueOf(couponAndOffersSnap.child("Points").child("GeneralPointLimit").getValue(String.class));


            Intent i = new Intent();
            PaymentBoxModel pbm = new PaymentBoxModel();
            pbm.setField("G2C Points");
            if (points <= generalLimit) {
                pbm.setValue(points.toString());
            }else{
                pbm.setValue(String.valueOf(generalLimit));
            }
            pbm.setType("points");
            i.putExtra("discountDetails", pbm);
            ((Activity) context).setResult(Activity.RESULT_OK, i);
            ((Activity) context).finish();

        } else {
            Toast.makeText(context, "Some error occurred", Toast.LENGTH_SHORT).show();
            ((Activity) context).finish();
        }
        progressDialog.dismiss();

    }


}
