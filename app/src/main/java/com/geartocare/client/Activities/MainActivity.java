package com.geartocare.client.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.geartocare.client.SessionManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.geartocare.client.Fragments.BookingsFragment;
import com.geartocare.client.Fragments.HomeFragment;
import com.geartocare.client.Fragments.ProfileFragment;
import com.geartocare.client.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    Fragment selectorFragment;
    SessionManager sessionManager;
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setStatusBarColor(ContextCompat.getColor(MainActivity.this, R.color.white));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
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
}