package com.geartocare.client.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.geartocare.client.Helpers.ReferralHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.geartocare.client.Helpers.CustomProgressDialog;
import com.geartocare.client.Helpers.LocationHelper;
import com.geartocare.client.Helpers.UserHelper;
import com.geartocare.client.R;
import com.geartocare.client.SessionManager;
import com.geartocare.client.databinding.ActivityVerifyPhoneBinding;
import com.geartocare.client.model.ModelUser;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class VerifyPhoneActivity extends AppCompatActivity {
    ActivityVerifyPhoneBinding binding;
    FirebaseAuth auth;
    String verificationID;
    CustomProgressDialog dialog;
    String UID;
    String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVerifyPhoneBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().setStatusBarColor(ContextCompat.getColor(VerifyPhoneActivity.this, R.color.white));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);


        dialog = new CustomProgressDialog(VerifyPhoneActivity.this);
        dialog.show();


        auth = FirebaseAuth.getInstance();
        phone = "+91" + getIntent().getStringExtra("phone");
        //String ph = getIntent().getStringExtra("phone");


        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(phone)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(VerifyPhoneActivity.this)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {

                    }

                    @Override
                    public void onCodeSent(@NonNull String verifyID, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(verifyID, forceResendingToken);
                        dialog.dismiss();

                        verificationID = verifyID;

                    }
                }).build();


        PhoneAuthProvider.verifyPhoneNumber(options);


        binding.verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationID, binding.otpView.getText().toString());


                auth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {

                            try {

                                Query checkUser = FirebaseDatabase.getInstance().getReference("Users").orderByChild("phone").equalTo(getIntent().getStringExtra("phone"));


                                checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        if (snapshot.exists()) {

                                            ModelUser user = snapshot.child(auth.getUid()).getValue(ModelUser.class);

                                            SessionManager sessionManager = new SessionManager(VerifyPhoneActivity.this);

                                            sessionManager.createLoginSession(user.getUid(), user.getFullName(), user.getPhone(), user.getEmail(), user.getLocation().getLat(), user.getLocation().getLng(), user.getLocation().getTxt());
                                            dialog.dismiss();
                                            startActivity(new Intent(VerifyPhoneActivity.this, LocationMenuActivity.class));
                                            finish();

                                        } else {



                                            Date date = Calendar.getInstance().getTime();

                                            SimpleDateFormat sdf = new SimpleDateFormat("ddSSS");

                                            String code = "GTC" + sdf.format(date.getTime());


                                            ReferralHelper referralHelper = new ReferralHelper(code, "0");


                                            UID = auth.getCurrentUser().getUid();

                                            LocationHelper location = new LocationHelper("none", "none", "none");

                                            UserHelper user = new UserHelper(UID, "Mr.Verified User", getIntent().getStringExtra("phone"), "none", location);

                                            FirebaseDatabase.getInstance().getReference("Users").child(UID).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                    if (task.isSuccessful()) {


                                                        FirebaseDatabase.getInstance().getReference("Users").child(UID).child("Referral").setValue(referralHelper).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                if (task.isSuccessful()) {


                                                                    FirebaseDatabase.getInstance().getReference("AppManager").child("ReferralManager").child("Users")
                                                                            .child(referralHelper.getCode()).setValue(user.getUid()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if (task.isSuccessful()) {
                                                                                SessionManager sessionManager = new SessionManager(VerifyPhoneActivity.this);

                                                                                sessionManager.createLoginSession(user.getUid(), user.getFullName(), user.getPhone(), user.getEmail(), user.getLocation().getLat(), user.getLocation().getLng(), user.getLocation().getTxt());
                                                                                dialog.dismiss();
                                                                                startActivity(new Intent(VerifyPhoneActivity.this, RegisterUserActivity.class));
                                                                                finish();
                                                                            }
                                                                        }
                                                                    });


                                                                }


                                                            }
                                                        });


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
                                e.printStackTrace();
                            }


                        } else {
                            Toast.makeText(VerifyPhoneActivity.this, "Invalid OTP", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
        });

    }
}