package com.g2c.clientP.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import com.g2c.clientP.Helpers.CustomProgressDialog;
import com.g2c.clientP.Helpers.NetworkChangeListener;
import com.g2c.clientP.Helpers.ServiceDataContainer;
import com.g2c.clientP.Helpers.TimeManager;
import com.g2c.clientP.Notifications.FcmNotificationsSender;
import com.g2c.clientP.model.ModelFinalService;
import com.g2c.clientP.model.ModelVehicle;
import com.g2c.clientP.model.PaymentBoxModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.g2c.clientP.R;
import com.g2c.clientP.SessionManager;
import com.g2c.clientP.databinding.ActivityPaymentBinding;
import com.google.firebase.database.ValueEventListener;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;


public class PaymentActivity extends AppCompatActivity {
    ActivityPaymentBinding binding;
    ServiceDataContainer serviceDetails;
    CustomProgressDialog progressDialog;
    SessionManager sessionManager;

    DatabaseReference DBref, SlotRef;
    RequestQueue requestQueue;
    String postUrl = "https://api.msg91.com/api/v5/flow/";
    String authKey = "371903ABidvYyDKby61e12476P1";
    JSONArray token_IDs;
    String serviceID;
    TimeManager timeManager;
    ModelFinalService finalServiceHelper;
    ModelVehicle vehicleHelper;
    SimpleDateFormat sdf;

     NetworkChangeListener networkChangeListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        networkChangeListener = new NetworkChangeListener(PaymentActivity.this);
        binding = ActivityPaymentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        progressDialog = new CustomProgressDialog(PaymentActivity.this);
        progressDialog.setCancelable(false);
        timeManager = new TimeManager();
        sessionManager = new SessionManager(PaymentActivity.this);
        serviceDetails = (ServiceDataContainer) getIntent().getSerializableExtra("serviceDetails");



        binding.paymentAmount.setText(serviceDetails.getServiceData().getPayment().getPrice());

        sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
        DBref = FirebaseDatabase.getInstance().getReference("Users").child(sessionManager.getUsersDetailsFromSessions().get(SessionManager.KEY_UID));
        SlotRef = FirebaseDatabase.getInstance().getReference("AppManager").child("SlotManager").child("Slots");


        binding.payAfterService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                processPayment("payAfterService", "On_Hold");


            }
        });


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

                        String vehicleID = FirebaseDatabase.getInstance().getReference("Users").child(new SessionManager(PaymentActivity.this).getUsersDetailsFromSessions().get(SessionManager.KEY_UID)).child("vehicles").push().getKey();
                        serviceDetails.getVehicle().setVehicleID(vehicleID);

                        DBref.child("vehicles").child(serviceDetails.getVehicle().getVehicleID()).setValue(vehicleHelper)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if (task.isSuccessful()) {

                                            createService(true);
                                        } else {
                                            progressDialog.dismiss();
                                            Toast.makeText(PaymentActivity.this, "Same error occurred please try again", Toast.LENGTH_SHORT).show();
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
                                    Toast.makeText(PaymentActivity.this, "Same error occurred please try again", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(PaymentActivity.this, "Same error occurred please try again", Toast.LENGTH_SHORT).show();
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
                                                }else {
                                                    Toast.makeText(PaymentActivity.this, task.getResult().toString(), Toast.LENGTH_SHORT).show();
                                                    progressDialog.dismiss();
                                                }
                                            }
                                        });


                            } else {
                                Toast.makeText(PaymentActivity.this, task.getResult().toString(), Toast.LENGTH_SHORT).show();
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
                                                                Toast.makeText(PaymentActivity.this, task.getResult().toString(), Toast.LENGTH_SHORT).show();
                                                                progressDialog.dismiss();
                                                            }
                                                        }
                                                    });
                                        } else {
                                            Toast.makeText(PaymentActivity.this, task.getResult().toString(), Toast.LENGTH_SHORT).show();
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
                                        Toast.makeText(PaymentActivity.this, task.getResult().toString(), Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(PaymentActivity.this, task.getResult().toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            Toast.makeText(this, "Some error occurred, try again.", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }


    }

    public void sendConfirmationSms() {

        requestQueue = Volley.newRequestQueue(PaymentActivity.this);
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
        FcmNotificationsSender notificationsSender = new FcmNotificationsSender("none", getApplicationContext(), PaymentActivity.this);

        try {
            notificationsSender.setNotificationData("Service Booked", sessionManager.getUsersDetailsFromSessions().get(SessionManager.KEY_FULLNAME) + " booked a service");
            notificationsSender.getNotificationData().put("purpose", "New_Booking");
            notificationsSender.getNotificationData().put("uid", sessionManager.getUsersDetailsFromSessions().get(SessionManager.KEY_UID));
            notificationsSender.getNotificationData().put("fullName", sessionManager.getUsersDetailsFromSessions().get(SessionManager.KEY_FULLNAME));
            notificationsSender.getNotificationData().put("serviceID", serviceID);
            notificationsSender.getNotificationData().put("vehicleID", serviceDetails.getVehicle().getVehicleID());
            notificationsSender.getNotificationData().put("phone", sessionManager.getUsersDetailsFromSessions().get(SessionManager.KEY_PHONE));


        } catch (JSONException e) {
            Toast.makeText(PaymentActivity.this, "hey", Toast.LENGTH_SHORT).show();
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
                    Intent i = new Intent(PaymentActivity.this, ConfirmationActivity.class);
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
        registerReceiver(networkChangeListener,filter);

    }

    @Override
    protected void onStop() {
        unregisterReceiver(networkChangeListener);
        super.onStop();
    }
}