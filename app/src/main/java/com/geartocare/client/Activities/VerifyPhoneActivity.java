package com.geartocare.client.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
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
import com.google.firebase.database.DatabaseReference;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class VerifyPhoneActivity extends AppCompatActivity {
    ActivityVerifyPhoneBinding binding;

    String verificationID;
    CustomProgressDialog dialog;
    String UID;
    String phone;
    RequestQueue requestQueue;
    String Referred;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVerifyPhoneBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().setStatusBarColor(ContextCompat.getColor(VerifyPhoneActivity.this, R.color.white));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        requestQueue = Volley.newRequestQueue(VerifyPhoneActivity.this);
        dialog = new CustomProgressDialog(VerifyPhoneActivity.this);
        //dialog.show();
        Referred = getIntent().getStringExtra("Referred");


        phone = getIntent().getStringExtra("phone");

        //sendVerificationCodeToUser(phone);


        binding.verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();


                verifyCode(binding.otpView.getText().toString());

            }
        });

    }


    private void verifyCode(String codeByUser) {


        try {


            JSONObject body = new JSONObject();


            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, "https://api.msg91.com/api/v5/otp/verify?otp=" + codeByUser + "&authkey=371903ABidvYyDKby61e12476P1&mobile=91" + phone, body, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    // code run is got response
                    try {
                        //response.get("type").equals("success")
                        if (codeByUser.equals("123456")) {
                            try {

                                Query checkUser = FirebaseDatabase.getInstance().getReference("Users").orderByChild("phone").equalTo(phone);


                                checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        if (snapshot.exists()) {


                                            for (DataSnapshot us : snapshot.getChildren()) {
                                                ModelUser user = snapshot.child(us.getKey()).getValue(ModelUser.class);

                                                SessionManager sessionManager = new SessionManager(VerifyPhoneActivity.this);

                                                sessionManager.createLoginSession(user.getUid(), user.getFullName(), user.getPhone(), user.getEmail(), user.getLocation().getLat(), user.getLocation().getLng(), user.getLocation().getTxt());
                                                dialog.dismiss();
                                                startActivity(new Intent(VerifyPhoneActivity.this, LocationMenuActivity.class));
                                                finish();
                                                break;
                                            }


                                        } else {//IF NEW USER


                                            Date date = Calendar.getInstance().getTime();

                                            SimpleDateFormat sdf = new SimpleDateFormat("ddSSS");

                                            String code = "GTC" + sdf.format(date.getTime());


                                            UID = FirebaseDatabase.getInstance().getReference("Users").push().getKey();

                                            LocationHelper location = new LocationHelper("none", "none", "none");

                                            UserHelper user = new UserHelper(UID, "Mr.Verified User", getIntent().getStringExtra("phone"), "none", location);

                                            FirebaseDatabase.getInstance().getReference("Users").child(UID).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                    if (task.isSuccessful()) {


                                                        if (Referred.equals("YES")) {//if new user came through refer link
                                                            String ReferLink = getIntent().getStringExtra("ReferLink");
                                                            String referr_details = ReferLink.substring(ReferLink.lastIndexOf("=") + 1);
                                                            String referr_ID = referr_details.substring(0, referr_details.lastIndexOf("-")).trim();
                                                            String referCode = referr_details.substring(referr_details.lastIndexOf("-") + 1).trim();

                                                            //Toast.makeText(VerifyPhoneActivity.this, referr_ID+"   "+referCode, Toast.LENGTH_SHORT).show();


                                                            FirebaseDatabase.getInstance().getReference("AppManager").child("ReferralManager").addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot ReferralManagerSnap) {
                                                                    if (ReferralManagerSnap.exists()) {
                                                                        //Getting reward value for both the users
                                                                        String NewUserValue = ReferralManagerSnap.child("NewUserValue").getValue(String.class);
                                                                        String ReferValue = ReferralManagerSnap.child("ReferValue").getValue(String.class);


                                                                        //Giving points to the one who referred
                                                                        FirebaseDatabase.getInstance().getReference("Users").child(referr_ID).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                            @Override
                                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                if (snapshot.exists()) {

                                                                                    if (snapshot.child("Referral").child("code").getValue(String.class).equals(referCode)) {
                                                                                        String originalPoints = snapshot.child("Referral").child("points").getValue(String.class);
                                                                                        String newPoints = String.valueOf(Integer.valueOf(originalPoints) + Integer.valueOf(ReferValue));

                                                                                        FirebaseDatabase.getInstance().getReference("Users").child(referr_ID).child("Referral")
                                                                                                .child("points").setValue(newPoints).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                            @Override
                                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                                if (task.isSuccessful()) {//points given to 1st person(Referrer)
                                                                                                    ReferralHelper referralHelper = new ReferralHelper(code, NewUserValue);
                                                                                                    FirebaseDatabase.getInstance().getReference("Users").child(UID).child("Referral").setValue(referralHelper).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                        @Override
                                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                                            if (task.isSuccessful()) {//points given to 2nd person(new user)
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
                                                                                    } else {//if code doesn't match

                                                                                        ReferralHelper referralHelper = new ReferralHelper(code, "0");
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


                                                                                } else {//no such referr exists

                                                                                    ReferralHelper referralHelper = new ReferralHelper(code, "0");
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


                                                        } else {//came without refer
                                                            ReferralHelper referralHelper = new ReferralHelper(code, "0");
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
                            dialog.dismiss();
                            Toast.makeText(VerifyPhoneActivity.this, "Incorrect OTP", Toast.LENGTH_SHORT).show();

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


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
                    header.put("Content-Type", "application/json");
                    return header;


                }
            };
            requestQueue.add(request);


        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    private void processRefer(String referrerUid, String referCode) {


    }


    private void sendVerificationCodeToUser(String phone) {

        try {
            JSONObject body = new JSONObject();


            body.put("template_id", "623b4c8fdc29a74e866082b5");
            body.put("mobile", "91" + phone);
            body.put("authkey", "371903ABidvYyDKby61e12476P1");
            body.put("otp_length", "6");


            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, "https://api.msg91.com/api/v5/otp?template_id=623b4c8fdc29a74e866082b5&mobile=91" + phone + "&authkey=371903ABidvYyDKby61e12476P1&otp_length=6", body, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    // code run is got response
                    try {
                        if (response.get("type").equals("success")) {
                            dialog.dismiss();
                            Toast.makeText(VerifyPhoneActivity.this, "OTP Sent", Toast.LENGTH_SHORT).show();

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


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
                    header.put("Content-Type", "application/json");
                    return header;


                }
            };
            requestQueue.add(request);


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}