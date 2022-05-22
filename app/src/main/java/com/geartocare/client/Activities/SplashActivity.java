package com.geartocare.client.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import com.geartocare.client.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;

public class SplashActivity extends AppCompatActivity {
    String TAG = "referred";
    String Referred;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        Referred = "NO";



        extracted();






    }


    private void extracted() {
        System.out.println("Inside the extracted");
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
                                    Intent i = new Intent(SplashActivity.this,GetPhoneActivity.class);
                                    i.putExtra("Referred",Referred);
                                    i.putExtra("ReferLink",ReferLink);
                                    startActivity(i);
                                    finish();
                                }
                            },300);


                        }else{
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    startActivity(new Intent(SplashActivity.this,GetPhoneActivity.class).putExtra("Referred",Referred));
                                    finish();
                                }
                            },500);
                        }
//                        else
//                            Log.e(TAG,"my refer Link: "+ deepLink.toString());

                       /* try {
                            String ReferLink = String.valueOf(deepLink);
                            ReferLink = ReferLink.substring(ReferLink.lastIndexOf("=") + 1);
                            Log.e(TAG, "substring " + ReferLink);

                            String custId = ReferLink.substring(0, ReferLink.indexOf("-"));
                            String prodId = ReferLink.substring(ReferLink.indexOf("-") + 1);

                            Log.e(TAG, "custId" + custId + "----prodId" + prodId);

                            Query checkUser = FirebaseDatabase.getInstance().getReference("Users")
                                    .orderByChild("userId");


                            checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.exists()){
                                        for(DataSnapshot snapshot1: snapshot.getChildren() ){
                                            String userId = snapshot1.getKey();
                                            FirebaseDatabase.getInstance().getReference("Users").child(userId).child("refReward").setValue("20");
                                            break;
                                        }
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                        } catch (Exception e) {
                            Log.e(TAG, "error" + e.toString());
                        }*/

                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("Bhai link nhi hai");
                        Log.e(TAG, "getDynamicLink:onFailure");
                    }
                });
    }


}