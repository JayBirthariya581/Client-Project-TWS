package com.g2c.clientP.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;

import com.g2c.clientP.Adapters.MechanicReferAdapter;
import com.g2c.clientP.Adapters.ModelMechanic;
import com.g2c.clientP.Helpers.CustomProgressDialog;
import com.g2c.clientP.Helpers.NetworkChangeListener;
import com.g2c.clientP.SessionManager;
import com.g2c.clientP.databinding.ActivityMechanicReferralBinding;
import com.g2c.clientP.model.UserMechanicReferral;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MechanicReferralActivity extends AppCompatActivity {
    ActivityMechanicReferralBinding binding;
    MechanicReferAdapter adapter;
    ArrayList<ModelMechanic> mechanic_list;
    ArrayList<UserMechanicReferral> userMechanicRefList;
    CustomProgressDialog progressDialog;
    SessionManager sessionManager;
    DatabaseReference mechanicRef;

     NetworkChangeListener networkChangeListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        networkChangeListener = new NetworkChangeListener(MechanicReferralActivity.this);
        binding = ActivityMechanicReferralBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        sessionManager = new SessionManager(MechanicReferralActivity.this);
        progressDialog = new CustomProgressDialog(MechanicReferralActivity.this);
        mechanic_list = new ArrayList<>();
        userMechanicRefList = new ArrayList<>();
        mechanicRef = FirebaseDatabase.getInstance().getReference("mechanics");
        adapter = new MechanicReferAdapter(mechanic_list, MechanicReferralActivity.this);
        adapter.setCode(getIntent().getStringExtra("code"));
        binding.mechanicList.setAdapter(adapter);
        binding.mechanicList.setLayoutManager(new LinearLayoutManager(MechanicReferralActivity.this));
        binding.mechanicList.setHasFixedSize(true);


        makeList();

    }

    private void makeList() {
        progressDialog.show();
        mechanic_list.clear();


        FirebaseDatabase.getInstance().getReference("Users").child(sessionManager.getUsersDetailsFromSessions().get(SessionManager.KEY_UID))
                .child("Referral").child("Mechanic_Referral").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot mechanicRefSnap) {
                        if (mechanicRefSnap.exists()) {


                            for (DataSnapshot snap : mechanicRefSnap.getChildren()) {
                                userMechanicRefList.add(snap.getValue(UserMechanicReferral.class));
                            }


                            for (UserMechanicReferral mechanicReferral : userMechanicRefList) {

                                mechanicRef.child(mechanicReferral.getMechanicID()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        mechanic_list.add(snapshot.getValue(ModelMechanic.class));
                                        adapter.notifyDataSetChanged();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                            }
                            progressDialog.dismiss();


                        } else {
                            progressDialog.dismiss();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

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