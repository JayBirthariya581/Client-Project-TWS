package com.geartocare.client.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.geartocare.client.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {
    ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());





    }
}