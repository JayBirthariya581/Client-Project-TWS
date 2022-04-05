package com.geartocare.client.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.geartocare.client.R;
import com.geartocare.client.SessionManager;
import com.geartocare.client.databinding.ActivityGetPhoneBinding;

public class GetPhoneActivity extends AppCompatActivity {
    ActivityGetPhoneBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGetPhoneBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().setStatusBarColor(ContextCompat.getColor(GetPhoneActivity.this, R.color.white));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        binding.logSig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GetPhoneActivity.this,VerifyPhoneActivity.class);
                intent.putExtra("phone",binding.phoneL.getEditText().getText().toString());
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
                if(binding.phoneL.getEditText().getText().toString().length()==10){
                    binding.logSig.setVisibility(View.VISIBLE);
                }else{
                    binding.logSig.setVisibility(View.GONE);
                }
            }
        });


    }


    @Override
    protected void onStart() {
        super.onStart();

        if(FirebaseAuth.getInstance().getCurrentUser()!=null){
            if(new SessionManager(GetPhoneActivity.this).getUsersDetailsFromSessions().get(SessionManager.KEY_LOCATION_Txt).isEmpty()){
                startActivity(new Intent(GetPhoneActivity.this,LocationMenuActivity.class));

            }else{
                startActivity(new Intent(GetPhoneActivity.this,MainActivity.class));

            }

                finish();

        }
    }
}