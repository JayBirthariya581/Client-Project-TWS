package com.geartocare.client.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.geartocare.client.Helpers.ShareTo;
import com.geartocare.client.SessionManager;
import com.geartocare.client.databinding.ActivityReferralBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ReferralActivity extends AppCompatActivity {
    ActivityReferralBinding binding;
    SessionManager sessionManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReferralBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        sessionManager = new SessionManager(ReferralActivity.this);




        FirebaseDatabase.getInstance().getReference("Users").child(sessionManager.getUsersDetailsFromSessions().get(SessionManager.KEY_UID))
                .child("Referral").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot Referral) {

                if(Referral.exists()){



                    binding.points.setText(Referral.child("points").getValue(String.class));
                    binding.code.setText(Referral.child("code").getValue(String.class));

                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });









        binding.cdkey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String shareBody = sessionManager.getUsersDetailsFromSessions().get(SessionManager.KEY_UID);
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Key", shareBody);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(ReferralActivity.this, "Coupon copied to clipboard", Toast.LENGTH_SHORT).show();
            }
        });

        binding.shareWhatsapp.setOnClickListener(new View.OnClickListener () {
            @Override
            public void onClick(View view) {

                ShareTo shareTo = new ShareTo(ReferralActivity.this);

                shareTo.shareToApp("com.whatsapp");

            }
        });


        binding.shareInsta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ShareTo shareTo = new ShareTo(ReferralActivity.this);

                shareTo.shareToApp("com.instagram.android");

            }
        });


        binding.shareFace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ShareTo shareTo = new ShareTo(ReferralActivity.this);

                shareTo.shareToApp("com.facebook.katana");

            }
        });

        binding.shareMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                //String couponCode = sessionManager.getUsersDetailsFromSessions().get(SessionManager.KEY_UID);

                String shareBody = sessionManager.getUsersDetailsFromSessions().get(SessionManager.KEY_UID);


                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "GearToCare");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
               /* FirebaseDatabase.getInstance().getReference().child("AppStatus").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if(snapshot.exists()){
                            String shareBody = "Hey, \nNow Two-Wheeler servicing is possible at home | office or at any place \n\nVisit : "+snapshot.child("webLink").getValue(String.class) + "\n\n"+ "And use coupon code : \""+couponCode+ "\" to get Rs."+snapshot.child("discount").getValue(String.class)+" OFF on your first vehicle servicing";




                            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Ninjas");
                            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                            startActivity(Intent.createChooser(sharingIntent, "Share via"));
                        }else{
                            Toast.makeText(ReferralActivity.this, "Error", Toast.LENGTH_SHORT).show();
                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
*/
            }
        });













    }
}