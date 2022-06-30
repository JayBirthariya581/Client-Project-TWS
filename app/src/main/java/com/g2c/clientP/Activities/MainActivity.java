package com.g2c.clientP.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.MenuItem;

import com.g2c.clientP.Helpers.NetworkChangeListener;
import com.g2c.clientP.SessionManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.g2c.clientP.Fragments.BookingsFragment;
import com.g2c.clientP.Fragments.HomeFragment;
import com.g2c.clientP.Fragments.ProfileFragment;
import com.g2c.clientP.R;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    Fragment selectorFragment;
    SessionManager sessionManager;
    String token;
    NetworkChangeListener networkChangeListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        networkChangeListener = new NetworkChangeListener(MainActivity.this);
        setContentView(R.layout.activity_main);
        sessionManager = new SessionManager(MainActivity.this);
        Fragment f1 = new HomeFragment();
        Fragment f2 = new BookingsFragment();
        Fragment f3 = new ProfileFragment();
        FragmentManager fm = getSupportFragmentManager();


        fm.beginTransaction().add(R.id.fragmentContainerView, f3, "3").hide(f3).commit();
        fm.beginTransaction().add(R.id.fragmentContainerView, f2, "2").hide(f2).commit();
        fm.beginTransaction().add(R.id.fragmentContainerView, f1, "1").commit();
        selectorFragment = f1;

        bottomNavigationView = findViewById(R.id.bnv);


        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {


                switch (item.getItemId()) {

                    case R.id.home:
                        getSupportFragmentManager().beginTransaction().hide(selectorFragment).show(f1).commit();
                        selectorFragment = f1;
                        break;


                    case R.id.bookings:
                        getSupportFragmentManager().beginTransaction().hide(selectorFragment).show(f2).commit();
                        selectorFragment = f2;
                        break;
                    case R.id.profile:
                        getSupportFragmentManager().beginTransaction().hide(selectorFragment).show(f3).commit();
                        selectorFragment = f3;
                        break;


                }


                return true;
            }
        });

        processToken();


    }

    private void processToken() {

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {

                if (task.isSuccessful()) {

                    token = task.getResult();


                    FirebaseDatabase.getInstance().getReference("Users").child(sessionManager.getUsersDetailsFromSessions().get(SessionManager.KEY_UID)).child("token").setValue(token)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()) {
                                        SharedPreferences.Editor editor = new SessionManager(MainActivity.this).getEditor();


                                        editor.putString(SessionManager.KEY_TOKEN, token);
                                        editor.commit();


                                    }

                                }
                            });


                }

            }
        });


    }


    @Override
    public void onBackPressed() {
        finishAffinity();
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeListener, filter);

    }

    @Override
    protected void onStop() {
        unregisterReceiver(networkChangeListener);
        super.onStop();
    }
}