package com.g2c.clientP.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.g2c.clientP.Adapters.OnBoardingAdapter;
import com.g2c.clientP.Helpers.SpeedyLinearLayoutManager;
import com.g2c.clientP.R;
import com.g2c.clientP.SessionManager;
import com.g2c.clientP.databinding.ActivityOnboardingBinding;

import java.util.ArrayList;

public class OnBoardingActivity extends AppCompatActivity {
    ActivityOnboardingBinding binding;
    OnBoardingAdapter adapter;
    ArrayList<Integer> images;
    SnapHelper snapHelper;
    SpeedyLinearLayoutManager layoutManager;
    String Referred;
    SessionManager sessionManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOnboardingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Referred = getIntent().getStringExtra("Referred");
        images = new ArrayList<>();
        adapter = new OnBoardingAdapter(OnBoardingActivity.this, images);
        sessionManager = new SessionManager(OnBoardingActivity.this);
        snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(binding.recyclerView);



        layoutManager = new SpeedyLinearLayoutManager(this, SpeedyLinearLayoutManager.HORIZONTAL, false);

        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setLayoutManager(layoutManager);


        images.add(R.drawable.ic_india);
        images.add(R.drawable.ic_bike);
        images.add(R.drawable.ic_share);
        adapter.notifyDataSetChanged();

        binding.circle1.setColorFilter(getResources().getColor(R.color.black));

        binding.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    // Dragging
                } else
                    // idle state
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        int position = layoutManager.findFirstVisibleItemPosition();

                        if (position == 0) {
                            binding.heading.setText("Your Car Service");
                            binding.desc.setText("Service in less than 20 minutes");
                            binding.circle1.setColorFilter(getResources().getColor(R.color.black));

                            binding.circle2.setColorFilter(getResources().getColor(R.color.white));
                            binding.circle3.setColorFilter(getResources().getColor(R.color.white));
                            binding.circle4.setColorFilter(getResources().getColor(R.color.white));

                        } else if (position == 1) {
                            binding.heading.setText("Second");
                            binding.desc.setText("2");
                            binding.circle2.setColorFilter(getResources().getColor(R.color.black));

                            binding.circle1.setColorFilter(getResources().getColor(R.color.white));
                            binding.circle3.setColorFilter(getResources().getColor(R.color.white));
                            binding.circle4.setColorFilter(getResources().getColor(R.color.white));

                        } else if (position == 2) {
                            binding.heading.setText("Third");
                            binding.desc.setText("3");
                            binding.circle3.setColorFilter(getResources().getColor(R.color.black));

                            binding.circle1.setColorFilter(getResources().getColor(R.color.white));
                            binding.circle2.setColorFilter(getResources().getColor(R.color.white));
                            binding.circle4.setColorFilter(getResources().getColor(R.color.white));

                        }
                        if (position == 3) {
                            binding.heading.setText("Fourth");
                            binding.desc.setText("4");
                            binding.circle4.setColorFilter(getResources().getColor(R.color.black));

                            binding.circle1.setColorFilter(getResources().getColor(R.color.white));
                            binding.circle2.setColorFilter(getResources().getColor(R.color.white));
                            binding.circle3.setColorFilter(getResources().getColor(R.color.white));


                        }
                    }


            }
        });

        binding.cont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OnBoardingActivity.this, GetPhoneActivity.class);

                if (Referred.equals("YES")) {
                    intent.putExtra("ReferLink", getIntent().getStringExtra("ReferLink"));

                }
                intent.putExtra("Referred", Referred);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (sessionManager.checkLogin()) {
            if (sessionManager.getUsersDetailsFromSessions().get(SessionManager.KEY_LOCATION_Txt).equals("none")) {
                startActivity(new Intent(OnBoardingActivity.this, LocationMenuActivity.class));

            } else {
                startActivity(new Intent(OnBoardingActivity.this, MainActivity.class));
            }
            finish();
        }


    }
}