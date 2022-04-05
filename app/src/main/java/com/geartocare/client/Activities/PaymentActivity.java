package com.geartocare.client.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.geartocare.client.Helpers.FinalServiceHelper;
import com.geartocare.client.Helpers.LocationHelper;
import com.geartocare.client.Helpers.PaymentHelper;
import com.geartocare.client.Helpers.VehicleHelper;
import com.geartocare.client.R;
import com.geartocare.client.SessionManager;
import com.geartocare.client.databinding.ActivityPaymentBinding;


import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;


public class PaymentActivity extends AppCompatActivity {
    ActivityPaymentBinding binding;
    HashMap<String, String> serviceDetails;
    DatabaseReference DBref, SlotRef;
    ProgressDialog dialog;
    SessionManager sessionManager;
    RequestQueue requestQueue;
    String postUrl = "https://api.msg91.com/api/v5/flow/";
    String authKey = "371903ABidvYyDKby61e12476P1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPaymentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().setStatusBarColor(ContextCompat.getColor(PaymentActivity.this, R.color.black));

        dialog = new ProgressDialog(PaymentActivity.this);
        dialog.setMessage("Please Wait");
        dialog.setCancelable(false);

        sessionManager = new SessionManager(PaymentActivity.this);
        serviceDetails = (HashMap<String, String>) getIntent().getSerializableExtra("serviceDetails");


        binding.paymentAmount.setText(serviceDetails.get("totalPrice"));


        DBref = FirebaseDatabase.getInstance().getReference("Users").child(sessionManager.getUsersDetailsFromSessions().get(SessionManager.KEY_UID));
        SlotRef = FirebaseDatabase.getInstance().getReference("AppManager").child("SlotManager").child("Slots");
        binding.payAfterService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                processpayment("payAfterService", "On_Hold");


            }
        });


    }


    private void processpayment(String paymentType, String paymentStatus) {

        try {
            dialog.show();


            String phone = sessionManager.getUsersDetailsFromSessions().get(SessionManager.KEY_PHONE);

            String serviceID = DBref.child("services").push().getKey();


            PaymentHelper paymentHelper = new PaymentHelper(serviceDetails.get("totalPrice"), paymentType, paymentStatus);


            LocationHelper locationHelper = new LocationHelper(serviceDetails.get("lat"), serviceDetails.get("lng"), serviceDetails.get("location_txt"));


            FinalServiceHelper finalServiceHelper = new FinalServiceHelper(serviceID, phone, serviceDetails.get("Date"), serviceDetails.get("Time"), locationHelper, paymentHelper, "On_Hold", "No_Mechanic");

            VehicleHelper vehicleHelper = new VehicleHelper(serviceDetails.get("Company"), serviceDetails.get("Model"), serviceDetails.get("VehicleNo"));

            String vehicleID = serviceDetails.get("Company") + "_" + serviceDetails.get("Model") + "_" + serviceDetails.get("VehicleNo");

            DBref.child("vehicles").child(vehicleID).setValue(vehicleHelper)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {

                                DBref.child("vehicles").child(vehicleID).child("services").child(serviceID).setValue(finalServiceHelper).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {


                                            HashMap<String, Object> sv_det = new HashMap<>();
                                            sv_det.put("uid", sessionManager.getUsersDetailsFromSessions().get(SessionManager.KEY_UID));
                                            sv_det.put("serviceID", serviceID);
                                            sv_det.put("vehicleID", vehicleID);
                                            sv_det.put("mechanicID", "No_Mechanic");
                                            sv_det.put("status", "On_Hold");

                                            SimpleDateFormat sdf = new SimpleDateFormat("dd_MM_yyyy");
                                            long ts = Long.valueOf(serviceDetails.get("Date"));

                                            SlotRef.child(sdf.format(ts)).child(serviceDetails.get("Time").split(" ")[0]).child(serviceID)
                                                    .setValue(sv_det).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        dialog.dismiss();
                                                        sendConfirmationSms();

                                                        Intent i = new Intent(PaymentActivity.this, ConfirmationActivity.class);
                                                        i.putExtra("serviceDetails", serviceDetails);
                                                        finishAffinity();

                                                        startActivity(i);
                                                        //finish();
                                                    }
                                                }
                                            });


                                        }


                                    }
                                });

                            }


                        }
                    });


        } catch (Exception e) {

        }

    }


    public void sendConfirmationSms() {

            requestQueue = Volley.newRequestQueue(PaymentActivity.this);
            JSONObject mainObj = new JSONObject();
            try {

                JSONObject body = new JSONObject();

                body.put("flow_id", "6239d0f7c8913c3c9e532962");
                body.put("sender", "GTCTWS");
                body.put("mobiles", "91"+sessionManager.getUsersDetailsFromSessions().get(SessionManager.KEY_PHONE));
                body.put("Name", sessionManager.getUsersDetailsFromSessions().get(SessionManager.KEY_FULLNAME));
                body.put("vhno", serviceDetails.get("Company")+" | "+ serviceDetails.get("Model")+" | "+serviceDetails.get("VehicleNo"));
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                long ts = Long.valueOf(serviceDetails.get("Date"));
                body.put("date", sdf.format(ts));



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


            } catch (JSONException e) {
                e.printStackTrace();
            }


    }


}