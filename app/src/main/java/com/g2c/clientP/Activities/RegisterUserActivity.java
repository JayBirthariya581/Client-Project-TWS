package com.g2c.clientP.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;

import com.g2c.clientP.Helpers.NetworkChangeListener;
import com.g2c.clientP.R;
import com.g2c.clientP.SessionManager;
import com.g2c.clientP.databinding.ActivityRegisterUserBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.HashMap;

public class RegisterUserActivity extends AppCompatActivity {
    ActivityRegisterUserBinding binding;
    HashMap<String, String> userDetails;
    int selected;
    String newName, newEmail, referCode;
    Integer ownerPoints, newUserPoints;

     NetworkChangeListener networkChangeListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        networkChangeListener = new NetworkChangeListener(RegisterUserActivity.this);
        binding = ActivityRegisterUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        userDetails = new SessionManager(RegisterUserActivity.this).getUsersDetailsFromSessions();


        if (userDetails.get(SessionManager.KEY_FULLNAME).substring(0, 3).equals("Ms.")) {
            selected = 2;
        } else {
            selected = 1;
        }


        binding.fullName.setText(userDetails.get(SessionManager.KEY_FULLNAME).substring(3, userDetails.get(SessionManager.KEY_FULLNAME).length()));
        if (!userDetails.get(SessionManager.KEY_EMAIL).equals("none")) {
            binding.email.setText(userDetails.get(SessionManager.KEY_EMAIL));
        }

        binding.phone.setText(userDetails.get(SessionManager.KEY_PHONE));


        binding.ms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selected = 2;
                manageGender();
            }
        });
        binding.mr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selected = 1;
                manageGender();
            }
        });


        binding.continueR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();

            }
        });

        manageGender();
    }

    private void register() {


        referCode = binding.referCode.getText().toString();
        newName = binding.fullName.getText().toString();
        newEmail = binding.email.getText().toString();


        if (referCode.length() > 0) {

            FirebaseDatabase.getInstance().getReference("AppManager").child("ReferralManager").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot referManager) {

                    if (referManager.exists()) {


                        if (referManager.child("Users").child(referCode).exists()) {


                            String referCodeOwnerID = referManager.child("Users").child(referCode).getValue(String.class);


                            FirebaseDatabase.getInstance().getReference("Users").child(referCodeOwnerID).child("Referral")
                                    .child("points").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                    if (snapshot.exists()) {

                                        ownerPoints = Integer.valueOf(snapshot.getValue(String.class));

                                        ownerPoints += Integer.valueOf(referManager.child("ReferValue").getValue(String.class));


                                        FirebaseDatabase.getInstance().getReference("Users").child(referCodeOwnerID).child("Referral")
                                                .child("points").child("Refers").child(userDetails.get(SessionManager.KEY_UID))
                                                .setValue(String.valueOf(Calendar.getInstance().getTime().getTime())).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {


                                                    FirebaseDatabase.getInstance().getReference("Users").child(referCodeOwnerID).child("Referral")
                                                            .child("points").setValue(String.valueOf(ownerPoints)).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {

                                                            if (task.isSuccessful()) {


                                                                FirebaseDatabase.getInstance().getReference("Users").child(userDetails.get(SessionManager.KEY_UID)).child("Referral")
                                                                        .child("points").addListenerForSingleValueEvent(new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                        if (snapshot.exists()) {

                                                                            newUserPoints = Integer.valueOf(snapshot.getValue(String.class));
                                                                            newUserPoints += Integer.valueOf(referManager.child("NewUserValue").getValue(String.class));


                                                                            FirebaseDatabase.getInstance().getReference("Users").child(userDetails.get(SessionManager.KEY_UID)).child("Referral")
                                                                                    .child("points").setValue(String.valueOf(newUserPoints)).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {


                                                                                    if (task.isSuccessful()) {
                                                                                        //Refer points are given to both now


                                                                                        DatabaseReference UserDB = FirebaseDatabase.getInstance().getReference("Users").child(userDetails.get(SessionManager.KEY_UID));

                                                                                        if (selected == 1) {
                                                                                            newName = "Mr." + newName;
                                                                                        } else {
                                                                                            newName = "Ms." + newName;
                                                                                        }

                                                                                        UserDB.child("email").setValue(newEmail);
                                                                                        UserDB.child("fullName").setValue(newName).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                            @Override
                                                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                                                SessionManager sessionManager = new SessionManager(RegisterUserActivity.this);

                                                                                                SharedPreferences.Editor editor = sessionManager.getEditor();


                                                                                                editor.putString(SessionManager.KEY_FULLNAME, newName);
                                                                                                editor.putString(SessionManager.KEY_EMAIL, newEmail);
                                                                                                editor.commit();

                                                                                                startActivity(new Intent(RegisterUserActivity.this, LocationMenuActivity.class));


                                                                                                finish();
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


                        } else {

                            //if refercode does not exist

                            AlertDialog.Builder builder = new AlertDialog.Builder(RegisterUserActivity.this);
                            builder.setMessage("Invalid Refer Code");
                            builder.setCancelable(true);
                            binding.referCode.setText("");
                            builder.show();


                        }


                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        } else {

            DatabaseReference UserDB = FirebaseDatabase.getInstance().getReference("Users").child(userDetails.get(SessionManager.KEY_UID));

            if (selected == 1) {
                newName = "Mr." + newName;
            } else {
                newName = "Ms." + newName;
            }

            UserDB.child("email").setValue(newEmail);
            UserDB.child("fullName").setValue(newName).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    SessionManager sessionManager = new SessionManager(RegisterUserActivity.this);

                    SharedPreferences.Editor editor = sessionManager.getEditor();


                    editor.putString(SessionManager.KEY_FULLNAME, newName);
                    editor.putString(SessionManager.KEY_EMAIL, newEmail);
                    editor.commit();

                    startActivity(new Intent(RegisterUserActivity.this, LocationMenuActivity.class));


                    finish();
                }
            });


        }


    }


    private void manageGender() {

        if (selected == 1) {

            binding.mr.setBackgroundColor(Color.parseColor("#5729c7"));
            binding.mr.setTextColor(getResources().getColor(R.color.white));
            binding.ms.setTextColor(getResources().getColor(R.color.black));
            binding.ms.setBackgroundColor(getResources().getColor(R.color.white));
        } else {
            binding.ms.setBackgroundColor(Color.parseColor("#5729c7"));
            binding.ms.setTextColor(getResources().getColor(R.color.white));
            binding.mr.setTextColor(getResources().getColor(R.color.black));
            binding.mr.setBackgroundColor(getResources().getColor(R.color.white));
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