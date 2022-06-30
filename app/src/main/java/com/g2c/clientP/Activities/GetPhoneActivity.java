package com.g2c.clientP.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.g2c.clientP.Helpers.NetworkChangeListener;
import com.g2c.clientP.R;
import com.g2c.clientP.SessionManager;
import com.g2c.clientP.databinding.ActivityGetPhoneBinding;

public class GetPhoneActivity extends AppCompatActivity {
    ActivityGetPhoneBinding binding;
    String Referred;
    SessionManager sessionManager;

    NetworkChangeListener networkChangeListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        networkChangeListener = new NetworkChangeListener(GetPhoneActivity.this);
        binding = ActivityGetPhoneBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        sessionManager = new SessionManager(GetPhoneActivity.this);

        Referred = getIntent().getStringExtra("Referred");


        binding.login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent = new Intent(GetPhoneActivity.this, VerifyPhoneActivity.class);

                if (Referred.equals("YES")) {
                    intent.putExtra("ReferLink", getIntent().getStringExtra("ReferLink"));

                }
                intent.putExtra("Referred", Referred);
                intent.putExtra("phone", binding.phoneL.getEditText().getText().toString());
                startActivity(intent);

            }
        });


        binding.phoneL.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (binding.phoneL.getEditText().getText().toString().length() == 10) {
                    binding.login.setVisibility(View.VISIBLE);
                } else {
                    binding.login.setVisibility(View.GONE);
                }
            }
        });


    }


    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeListener, filter);
        if (sessionManager.checkLogin()) {

            if (sessionManager.getUsersDetailsFromSessions().get(SessionManager.KEY_LOCATION_Txt).equals("none")) {
                startActivity(new Intent(GetPhoneActivity.this, LocationMenuActivity.class));

            } else {
                startActivity(new Intent(GetPhoneActivity.this, MainActivity.class));
            }
            finish();
        }


    }


    @Override
    protected void onStop() {
        unregisterReceiver(networkChangeListener);
        super.onStop();
    }
}