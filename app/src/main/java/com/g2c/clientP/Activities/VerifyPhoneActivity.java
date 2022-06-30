package com.g2c.clientP.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
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
import com.g2c.clientP.Helpers.AlarmReceiver;
import com.g2c.clientP.Helpers.NetworkChangeListener;
import com.g2c.clientP.Helpers.ReferralHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.g2c.clientP.Helpers.CustomProgressDialog;
import com.g2c.clientP.Helpers.LocationHelper;
import com.g2c.clientP.Helpers.UserHelper;
import com.g2c.clientP.SessionManager;
import com.g2c.clientP.databinding.ActivityVerifyPhoneBinding;
import com.g2c.clientP.model.ModelUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class VerifyPhoneActivity extends AppCompatActivity {
    ActivityVerifyPhoneBinding binding;
    CustomProgressDialog dialog;
    String UID;
    String phone;
    RequestQueue requestQueue;
    String Referred;
    String code;
    UserHelper user;
    Calendar calendar;
    SimpleDateFormat sdf;
    String s;
    SimpleDateFormat dSDF, mSDF, ySDF;
    NetworkChangeListener networkChangeListener;

    AlarmManager alarmManager;
    int pendingIntentRequestCode = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        networkChangeListener = new NetworkChangeListener(VerifyPhoneActivity.this);
        binding = ActivityVerifyPhoneBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        requestQueue = Volley.newRequestQueue(VerifyPhoneActivity.this);
        dialog = new CustomProgressDialog(VerifyPhoneActivity.this);
        //dialog.show();
        Referred = getIntent().getStringExtra("Referred");


        calendar = Calendar.getInstance();
        sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
        phone = getIntent().getStringExtra("phone");

        s = sdf.format(calendar.getTime().getTime());
        //sendVerificationCodeToUser(phone);
        ySDF = new SimpleDateFormat("yyyy");
        dSDF = new SimpleDateFormat("dd");
        mSDF = new SimpleDateFormat("MMM");

        binding.verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();

                String otp = binding.otpView.getText().toString();
                verifyCode(otp);

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeListener,filter);
        binding.otpView.requestFocus();
    }


    @Override
    protected void onStop() {
        unregisterReceiver(networkChangeListener);
        super.onStop();
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

                                Query checkUser = FirebaseDatabase.getInstance().getReference("Users").orderByChild("phone").equalTo(getIntent().getStringExtra("phone"));


                                checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        if (snapshot.exists()) {


                                            for (DataSnapshot us : snapshot.getChildren()) {
                                                ModelUser user = snapshot.child(us.getKey()).getValue(ModelUser.class);

                                                SessionManager sessionManager = new SessionManager(VerifyPhoneActivity.this);

                                                sessionManager.createLoginSession(user.getUid(), user.getFullName(), user.getPhone(), user.getEmail(), user.getLocation().getLat(), user.getLocation().getLng(), user.getLocation().getTxt());
                                                initializeReminderNotifications();
                                                dialog.dismiss();
                                                startActivity(new Intent(VerifyPhoneActivity.this, LocationMenuActivity.class));
                                                finish();
                                                break;
                                            }


                                        } else {//IF NEW USER


                                            Date date = Calendar.getInstance().getTime();

                                            SimpleDateFormat sdf = new SimpleDateFormat("ddSSS");

                                            code = "GTC" + sdf.format(date.getTime());


                                            UID = FirebaseDatabase.getInstance().getReference("Users").push().getKey();

                                            LocationHelper location = new LocationHelper("none", "none", "none");

                                            user = new UserHelper(UID, "Mr.Verified User", getIntent().getStringExtra("phone"), "none", location);

                                            FirebaseDatabase.getInstance().getReference("Users").child(UID).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                    if (task.isSuccessful()) {


                                                        if (Referred.equals("YES")) {//if new user came through refer link
                                                            String ReferLink = getIntent().getStringExtra("ReferLink");
                                                            String rd = ReferLink.substring(ReferLink.lastIndexOf("=") + 1);

                                                            String[] referDetails = rd.split("/");

                                                            if (referDetails[0].equals("c2c")) {


                                                                processRefer(referDetails[1], referDetails[2]);

                                                            } else if (referDetails[0].equals("m2c")) {


                                                                processMechanicRefer(referDetails[1], referDetails[2], referDetails[3], referDetails[4]);

                                                            } else {


                                                                processWithoutRefer();
                                                            }


                                                        } else {//came without refer

                                                            processWithoutRefer();
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


    private void processWithoutRefer() {
        ReferralHelper referralHelper = new ReferralHelper(code, "0");
        FirebaseDatabase.getInstance().getReference("Users").child(UID).child("Referral").setValue(referralHelper).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {
                    SessionManager sessionManager = new SessionManager(VerifyPhoneActivity.this);

                    sessionManager.createLoginSession(user.getUid(), user.getFullName(), user.getPhone(), user.getEmail(), user.getLocation().getLat(), user.getLocation().getLng(), user.getLocation().getTxt());
                    initializeReminderNotifications();
                    dialog.dismiss();
                    startActivity(new Intent(VerifyPhoneActivity.this, RegisterUserActivity.class));
                    finish();

                }


            }
        });
    }

    private void processRefer(String referrerUid, String referCode) {


        FirebaseDatabase.getInstance().getReference("AppManager").child("ReferralManager").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot ReferralManagerSnap) {
                if (ReferralManagerSnap.exists()) {
                    //Getting reward value for both the users
                    String NewUserValue = ReferralManagerSnap.child("NewUserValue").getValue(String.class);
                    String ReferValue = ReferralManagerSnap.child("ReferValue").getValue(String.class);


                    //Giving points to the one who referred
                    FirebaseDatabase.getInstance().getReference("Users").child(referrerUid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {

                                if (snapshot.child("Referral").child("code").getValue(String.class).equals(referCode)) {
                                    String originalPoints = snapshot.child("Referral").child("points").getValue(String.class);
                                    String newPoints = String.valueOf(Integer.valueOf(originalPoints) + Integer.valueOf(ReferValue));


                                    FirebaseDatabase.getInstance().getReference("Users").child(referrerUid).child("Referral")
                                            .child("points").setValue(newPoints).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {//points given to 1st person(Referrer)


                                                        FirebaseDatabase.getInstance().getReference("Users").child(referrerUid).child("Referral")
                                                                .child("ReferralList").child(UID).setValue(s).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        HashMap<String, Object> hmap = new HashMap<>();
                                                                        hmap.put("date", s);
                                                                        hmap.put("value", ReferValue);
                                                                        FirebaseDatabase.getInstance().getReference("Users").child(referrerUid).child("RewardHistory")
                                                                                .child("Points").child("Earned").child(UID).setValue(hmap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<Void> task) {


                                                                                        ReferralHelper referralHelper = new ReferralHelper(code, NewUserValue);
                                                                                        FirebaseDatabase.getInstance().getReference("Users").child(UID).child("Referral").setValue(referralHelper).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                            @Override
                                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                                if (task.isSuccessful()) {//points given to 2nd person(new user)


                                                                                                    FirebaseDatabase.getInstance().getReference("Users").child(UID).child("Referral").child("referredBy").setValue(referrerUid).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                        @Override
                                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                                            HashMap<String, Object> hmap = new HashMap<>();
                                                                                                            hmap.put("date", s);
                                                                                                            hmap.put("value", NewUserValue);


                                                                                                            FirebaseDatabase.getInstance().getReference("Users").child(UID).child("RewardHistory")
                                                                                                                    .child("Points").child("Earned").child(referrerUid).setValue(hmap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                        @Override
                                                                                                                        public void onComplete(@NonNull Task<Void> task) {

                                                                                                                            if (task.isSuccessful()) {
                                                                                                                                SessionManager sessionManager = new SessionManager(VerifyPhoneActivity.this);

                                                                                                                                sessionManager.createLoginSession(user.getUid(), user.getFullName(), user.getPhone(), user.getEmail(), user.getLocation().getLat(), user.getLocation().getLng(), user.getLocation().getTxt());
                                                                                                                                initializeReminderNotifications();
                                                                                                                                dialog.dismiss();
                                                                                                                                startActivity(new Intent(VerifyPhoneActivity.this, RegisterUserActivity.class));
                                                                                                                                finish();
                                                                                                                            }


                                                                                                                        }
                                                                                                                    });

                                                                                                        }
                                                                                                    });
                                                                                                }
                                                                                            }
                                                                                        });


                                                                                    }
                                                                                });


                                                                    }
                                                                });


                                                    }
                                                }
                                            });
                                } else {//if code doesn't match

                                    processWithoutRefer();
                                }


                            } else {//no such referr exists

                                processWithoutRefer();

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


    }

    private void processMechanicRefer(String userUid, String userReferCode, String mechanicID, String mechanicReferCode) {


        FirebaseDatabase.getInstance().getReference("AppManager").child("ReferralManager").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot ReferralManagerSnap) {
                if (ReferralManagerSnap.exists()) {
                    //Getting reward value for both the users
                    String NewUserValue = ReferralManagerSnap.child("NewUserValue").getValue(String.class);
                    String ReferValue = ReferralManagerSnap.child("ReferValue").getValue(String.class);
                    String MechanicReferValue = ReferralManagerSnap.child("MechanicReferValue").getValue(String.class);


                    //Giving points to the one who referred
                    FirebaseDatabase.getInstance().getReference("Users").child(userUid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {

                                if (snapshot.child("Referral").child("code").getValue(String.class).equals(userReferCode)) {
                                    String originalPoints = snapshot.child("Referral").child("points").getValue(String.class);
                                    String newPoints = String.valueOf(Integer.valueOf(originalPoints) + Integer.valueOf(ReferValue));

                                    FirebaseDatabase.getInstance().getReference("Users").child(userUid).child("Referral")
                                            .child("points").setValue(newPoints).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {//points given to 1st person(Referrer)

                                                        String s = sdf.format(calendar.getTime().getTime());

                                                        FirebaseDatabase.getInstance().getReference("Users").child(userUid).child("Referral")
                                                                .child("ReferralList").child(UID).setValue(s).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        HashMap<String, Object> hmap = new HashMap<>();
                                                                        hmap.put("date", s);
                                                                        hmap.put("value", ReferValue);
                                                                        FirebaseDatabase.getInstance().getReference("Users").child(userUid).child("RewardHistory")
                                                                                .child("Points").child("Earned").child(UID).setValue(hmap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<Void> task) {


                                                                                        ReferralHelper referralHelper = new ReferralHelper(code, NewUserValue);
                                                                                        FirebaseDatabase.getInstance().getReference("Users").child(UID).child("Referral").setValue(referralHelper).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                            @Override
                                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                                if (task.isSuccessful()) {//points given to 2nd person(new user)

                                                                                                    FirebaseDatabase.getInstance().getReference("Users").child(UID).child("Referral").child("referredBy").setValue(userUid).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                        @Override
                                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                                            HashMap<String, Object> hmap = new HashMap<>();
                                                                                                            hmap.put("date", s);
                                                                                                            hmap.put("value", NewUserValue);
                                                                                                            FirebaseDatabase.getInstance().getReference("Users").child(UID).child("RewardHistory")
                                                                                                                    .child("Points").child("Earned").child(userUid).setValue(hmap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                        @Override
                                                                                                                        public void onComplete(@NonNull Task<Void> task) {

                                                                                                                            FirebaseDatabase.getInstance().getReference("mechanics").child(mechanicID).child("referral")
                                                                                                                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                                                                        @Override
                                                                                                                                        public void onDataChange(@NonNull DataSnapshot mechanicReferSnap) {
                                                                                                                                            if (mechanicReferSnap.exists()) {

                                                                                                                                                String mechanicOriginalPoint = mechanicReferSnap.child("points").getValue(String.class);
                                                                                                                                                String mechanicNewPoint = String.valueOf(Integer.valueOf(mechanicOriginalPoint) + Integer.valueOf(MechanicReferValue));


                                                                                                                                                FirebaseDatabase.getInstance().getReference("mechanics").child(mechanicID).child("referral").child("points")
                                                                                                                                                        .setValue(mechanicNewPoint).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                                                            @Override
                                                                                                                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                                                                                                                if (task.isSuccessful()) {//points given to mechanic
                                                                                                                                                                    HashMap<String, Object> hmap = new HashMap<>();
                                                                                                                                                                    hmap.put("date", s);
                                                                                                                                                                    hmap.put("value", MechanicReferValue);
                                                                                                                                                                    FirebaseDatabase.getInstance().getReference("mechanics").child(mechanicID).child("Records").child("Referral")
                                                                                                                                                                            .child(ySDF.format(calendar.getTime().getTime())).child(mSDF.format(calendar.getTime().getTime()))
                                                                                                                                                                            .child(dSDF.format(calendar.getTime().getTime()))
                                                                                                                                                                            .child("ReferralList")
                                                                                                                                                                            .child(UID).setValue(hmap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                                                                                @Override
                                                                                                                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                                                                                                                    if (task.isSuccessful()) {
                                                                                                                                                                                        SessionManager sessionManager = new SessionManager(VerifyPhoneActivity.this);

                                                                                                                                                                                        sessionManager.createLoginSession(user.getUid(), user.getFullName(), user.getPhone(), user.getEmail(), user.getLocation().getLat(), user.getLocation().getLng(), user.getLocation().getTxt());
                                                                                                                                                                                        initializeReminderNotifications();
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
                                                                                                                    });

                                                                                                        }
                                                                                                    });


                                                                                                }
                                                                                            }
                                                                                        });


                                                                                    }
                                                                                });


                                                                    }
                                                                });


                                                    }
                                                }
                                            });
                                } else {//if code doesn't match

                                    processWithoutRefer();
                                }


                            } else {//no such referr exists

                                processWithoutRefer();

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


    }


    private void initializeReminderNotifications(){

        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.setAction("FOO_ACTION");
        intent.putExtra("KEY_FOO_STRING", "Medium AlarmManager Demo");

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,pendingIntentRequestCode,
                intent,PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.HOUR_OF_DAY, 10);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);


//        int ALARM_DELAY_IN_SECOND = 10;
//        long alarmTimeAtUTC = System.currentTimeMillis() + ALARM_DELAY_IN_SECOND * 1_000L;

//        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,alarmTimeAtUTC,
//                ,pendingIntent);



//        alarmManager.set(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),pendingIntent);

//        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),
//                AlarmManager.INTERVAL_DAY,pendingIntent);


        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY,
                pendingIntent);

        calendar.set(Calendar.HOUR_OF_DAY, 11);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY,
                pendingIntent);

        calendar.set(Calendar.HOUR_OF_DAY, 12);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY,
                pendingIntent);
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