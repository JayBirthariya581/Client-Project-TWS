package com.g2c.clientP.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.widget.Toast;

import com.g2c.clientP.Helpers.CustomProgressDialog;
import com.g2c.clientP.Helpers.LocationHelper;
import com.g2c.clientP.Helpers.NetworkChangeListener;
import com.g2c.clientP.Notifications.FcmNotificationsSender;
import com.g2c.clientP.R;
import com.g2c.clientP.SessionManager;
import com.g2c.clientP.databinding.ActivityQueryTimerBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class QueryTimerActivity extends AppCompatActivity {
    ActivityQueryTimerBinding binding;
    HashMap<String, String> givenDetails;
    SessionManager sessionManager;
    CustomProgressDialog dialog;
    JSONArray token_IDs;

     NetworkChangeListener networkChangeListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        networkChangeListener = new NetworkChangeListener(QueryTimerActivity.this);
        binding = ActivityQueryTimerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        sessionManager = new SessionManager(QueryTimerActivity.this);
        givenDetails = (HashMap<String, String>) getIntent().getSerializableExtra("serviceDetails");
        dialog = new CustomProgressDialog(QueryTimerActivity.this);

        dialog.show();
        if (givenDetails != null) {
            HashMap<String, Object> queryDetails = new HashMap<>();
            Date date = new Date();

            long generatedTime = date.getTime();

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.MINUTE, 25);

            long endTime = calendar.getTime().getTime();

            //SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");


            LocationHelper locationHelper = new LocationHelper(givenDetails.get("lat"), givenDetails.get("lng"), givenDetails.get("location_txt"));
            queryDetails.put("name", givenDetails.get("name"));
            queryDetails.put("location", locationHelper);
            queryDetails.put("generatedTime", String.valueOf(generatedTime));
            queryDetails.put("endTime", String.valueOf(endTime));
            queryDetails.put("category", givenDetails.get("category"));
            queryDetails.put("vehicle", givenDetails.get("company") + "_" + givenDetails.get("model") + "_" + givenDetails.get("vhNo"));
            String token = FirebaseDatabase.getInstance().getReference("Users").child(sessionManager.getUsersDetailsFromSessions().get(SessionManager.KEY_UID)).child("Query").push().getKey();
            queryDetails.put("token", token);
            queryDetails.put("status", "Open");

            FirebaseDatabase.getInstance().getReference("Users").child(sessionManager.getUsersDetailsFromSessions().get(SessionManager.KEY_UID)).child("Query").child(token)
                    .setValue(queryDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {

                        HashMap<String, Object> managerQ = new HashMap<>();
                        managerQ.put("uid", sessionManager.getUsersDetailsFromSessions().get(SessionManager.KEY_UID));
                        managerQ.put("category", queryDetails.get("category"));
                        managerQ.put("token", token);

                        FirebaseDatabase.getInstance().getReference("AppManager").child("QueryManager").child(givenDetails.get("category"))
                                .child(token).setValue(managerQ).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {

                                    //Send Notification to Managers
                                    token_IDs = new JSONArray();
                                    FcmNotificationsSender notificationsSender = new FcmNotificationsSender("none", getApplicationContext(), QueryTimerActivity.this);

                                    try {
                                        notificationsSender.setNotificationData("Query!", queryDetails.get("name") + " generated a query regarding " + queryDetails.get("category"));
                                        notificationsSender.getNotificationData().put("goTo","onHold");
                                    } catch (JSONException e) {
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

                                            }

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });


                                    dialog.dismiss();
                                }
                            }
                        });


                    }
                }
            });


        } else {
            Toast.makeText(this, "o", Toast.LENGTH_SHORT).show();
        }


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