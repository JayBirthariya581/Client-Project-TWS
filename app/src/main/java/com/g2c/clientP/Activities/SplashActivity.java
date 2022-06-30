package com.g2c.clientP.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;

import com.g2c.clientP.R;
import com.g2c.clientP.databinding.ActivitySplashBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;

public class SplashActivity extends AppCompatActivity {
    ActivitySplashBinding binding;
    String TAG = "referred";
    String Referred;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        Referred = "NO";


        


    }

    @Override
    protected void onStart() {
        super.onStart();
        extracted();
    }

    private void extracted() {

        try {

            FirebaseDynamicLinks.getInstance()
                    .getDynamicLink(getIntent())
                    .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                        @Override
                        public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                            // Get deep link from result (may be null if no link is found)
                            Uri deepLink = null;

                            if (pendingDynamicLinkData != null) {
                                deepLink = pendingDynamicLinkData.getLink();

                            }

                            if (deepLink != null) {

                                Referred = "YES";
                                String ReferLink = deepLink.toString();


                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent i = new Intent(SplashActivity.this, OnBoardingActivity.class);
                                        i.putExtra("Referred", Referred);
                                        i.putExtra("ReferLink", ReferLink);
                                        startActivity(i);
                                        finish();
                                    }
                                }, 300);


                            } else {
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        startActivity(new Intent(SplashActivity.this, OnBoardingActivity.class).putExtra("Referred", Referred));
                                        finish();
                                    }
                                }, 500);
                            }


                        }
                    })
                    .addOnFailureListener(this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    startActivity(new Intent(SplashActivity.this, OnBoardingActivity.class).putExtra("Referred", "NO"));
                                    finish();
                                }
                            }, 400);

                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(SplashActivity.this, OnBoardingActivity.class).putExtra("Referred", "NO"));
                    finish();
                }
            }, 400);
        }


    }


}