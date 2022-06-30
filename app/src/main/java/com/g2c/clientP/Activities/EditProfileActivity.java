package com.g2c.clientP.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;

import com.g2c.clientP.Helpers.NetworkChangeListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.g2c.clientP.R;
import com.g2c.clientP.SessionManager;
import com.g2c.clientP.databinding.ActivityEditProfileBinding;

import java.util.HashMap;

public class EditProfileActivity extends AppCompatActivity {
    ActivityEditProfileBinding binding;
    int selected;
    HashMap<String, String> userDetails;
    String newName, newEmail;

     NetworkChangeListener networkChangeListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        networkChangeListener = new NetworkChangeListener(EditProfileActivity.this);

        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userDetails = new SessionManager(EditProfileActivity.this).getUsersDetailsFromSessions();


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


        manageGender();

        binding.update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update();

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

    private void update() {
        /*String newPhone = binding.phone.getText().toString();

        if(!newPhone.equals(userDetails.get(SessionManager.KEY_PHONE))){

            if(newPhone.length()==13){

                Intent



            }

        }*/

        newName = binding.fullName.getText().toString();
        newEmail = binding.email.getText().toString();

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

                SessionManager sessionManager = new SessionManager(EditProfileActivity.this);

                SharedPreferences.Editor editor = sessionManager.getEditor();


                editor.putString(SessionManager.KEY_FULLNAME, newName);
                editor.putString(SessionManager.KEY_EMAIL, newEmail);
                editor.commit();

                Intent i = new Intent();
                i.putExtra("name", newName);
                i.putExtra("email", newEmail);
                setResult(Activity.RESULT_OK, i);

                finish();
            }
        });


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


}