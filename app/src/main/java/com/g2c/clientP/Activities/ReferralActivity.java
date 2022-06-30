package com.g2c.clientP.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.g2c.clientP.Adapters.FaqAdapter;
import com.g2c.clientP.Helpers.CustomProgressDialog;
import com.g2c.clientP.Helpers.NetworkChangeListener;
import com.g2c.clientP.Helpers.ShareTo;
import com.g2c.clientP.SessionManager;
import com.g2c.clientP.databinding.ActivityReferralBinding;
import com.g2c.clientP.model.ModelFaq;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;

import java.util.ArrayList;

public class ReferralActivity extends AppCompatActivity {
    ActivityReferralBinding binding;
    SessionManager sessionManager;
    FaqAdapter faqAdapter;
    ArrayList<ModelFaq> faqList;
    Uri shortLink;
    CustomProgressDialog progressDialog;

    NetworkChangeListener networkChangeListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        networkChangeListener = new NetworkChangeListener(ReferralActivity.this);
        binding = ActivityReferralBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        sessionManager = new SessionManager(ReferralActivity.this);
        progressDialog = new CustomProgressDialog(ReferralActivity.this);
        progressDialog.show();


        FirebaseDatabase.getInstance().getReference("Users").child(sessionManager.getUsersDetailsFromSessions().get(SessionManager.KEY_UID))
                .child("Referral").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot Referral) {

                        if (Referral.exists()) {


                            binding.points.setText(Referral.child("points").getValue(String.class));
                            binding.code.setText(Referral.child("code").getValue(String.class));

                            binding.mechanicReferral.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    startActivity(new Intent(ReferralActivity.this, MechanicReferralActivity.class).putExtra("code", Referral.child("code").getValue(String.class)));
                                }
                            });


                            createReferLink(sessionManager.getUsersDetailsFromSessions().get(SessionManager.KEY_UID), Referral.child("code").getValue(String.class));
                            initializefaq();
                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });










        /*binding.cdkey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String shareBody = sessionManager.getUsersDetailsFromSessions().get(SessionManager.KEY_UID);
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Key", shareBody);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(ReferralActivity.this, "Coupon copied to clipboard", Toast.LENGTH_SHORT).show();
            }
        });*/

        binding.shareWhatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ShareTo shareTo = new ShareTo(ReferralActivity.this);

                shareTo.shareToAppRefer("com.whatsapp", shortLink.toString());

            }
        });


        /*binding.shareInsta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ShareTo shareTo = new ShareTo(ReferralActivity.this);

                shareTo.shareToAppRefer("com.instagram.android", shortLink.toString());

            }
        });


        binding.shareFace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ShareTo shareTo = new ShareTo(ReferralActivity.this);

                shareTo.shareToAppRefer("com.facebook.katana", shortLink.toString());

            }
        });*/


        binding.shareGeneral.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                //String couponCode = sessionManager.getUsersDetailsFromSessions().get(SessionManager.KEY_UID);

                String shareBody = shortLink.toString();


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

    private void initializefaq() {

        faqList = new ArrayList<>();
        faqAdapter = new FaqAdapter(ReferralActivity.this, faqList);


        binding.faq.setLayoutManager(new LinearLayoutManager(ReferralActivity.this));
        binding.faq.setAdapter(faqAdapter);
        binding.faq.setHasFixedSize(true);


        ModelFaq q1 = new ModelFaq();
        q1.setQuestion("Which engine oil do you use ?");
        q1.setAnswer("My name is jay");

        ModelFaq q2 = new ModelFaq();
        q2.setQuestion("Are there any additional charges other than mentioned price");
        q2.setAnswer("My name is jay");

        ModelFaq q3 = new ModelFaq();
        q3.setQuestion("Which engine oil do you use ?");
        q3.setAnswer("My name is jay");


        faqList.add(q1);
        faqList.add(q2);
        faqList.add(q3);
        faqAdapter.notifyDataSetChanged();


    }


    public void createReferLink(String uid, String referCode) {

        String shareLink = "https://geartocareproto.page.link/?" +
                "link=https://www.example.com/?custid=" + "c2c" + "/" + uid + "/" + referCode +
                "&apn=" + getPackageName() +
                "&st=" + "My Demo Refer Link" +
                "&sd=" + "Reward gift" +
                "&si=" + "https://www.uffizio.com/blog/content/public/upload/share-link_2_o.png";

        Log.e("MainActivity", "sharelink=" + shareLink);

        Task<ShortDynamicLink> shortLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLongLink(Uri.parse(shareLink))
                .buildShortDynamicLink();

        shortLinkTask.addOnCompleteListener(this, new OnCompleteListener<ShortDynamicLink>() {
            @Override
            public void onComplete(@NonNull Task<ShortDynamicLink> task) {
                if (task.isSuccessful()) {

                    shortLink = task.getResult().getShortLink();
                    progressDialog.dismiss();


                } else {


                    Log.e("main", " Error! " + task.getException());

                }
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
}