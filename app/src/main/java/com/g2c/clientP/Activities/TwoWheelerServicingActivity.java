package com.g2c.clientP.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.g2c.clientP.Adapters.FaqAdapter;
import com.g2c.clientP.Adapters.ReviewSliderAdapter;
import com.g2c.clientP.Helpers.NetworkChangeListener;
import com.g2c.clientP.Helpers.ServiceDataContainer;
import com.g2c.clientP.databinding.ActivityTwoWheelerServicingBinding;
import com.g2c.clientP.model.ModelFaq;
import com.g2c.clientP.model.ModelReview;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.g2c.clientP.Helpers.CustomProgressDialog;

import java.util.ArrayList;
import java.util.List;

public class TwoWheelerServicingActivity extends AppCompatActivity {
    ActivityTwoWheelerServicingBinding binding;
    String svPrice;
    List<ModelReview> sliderItems;
    ReviewSliderAdapter reviewSliderAdapter;
    FaqAdapter faqAdapter;
    ArrayList<ModelFaq> faqList;
    CustomProgressDialog dialog;
    ServiceDataContainer serviceDetails;
    private Handler sliderHandler = new Handler();

    NetworkChangeListener networkChangeListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        networkChangeListener = new NetworkChangeListener(TwoWheelerServicingActivity.this);
        binding = ActivityTwoWheelerServicingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        dialog = new CustomProgressDialog(TwoWheelerServicingActivity.this);

        dialog.show();
        serviceDetails = new ServiceDataContainer();

        serviceDetails.setServiceID("TwoWheelerService");

        serviceDetails.setServiceName("Two Wheeler Service");

        sliderItems = new ArrayList<>();


        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        FirebaseDatabase.getInstance().getReference("Services").child("TwoWheelerService").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot serviceDetail) {

                if (serviceDetail.exists()) {
                    svPrice = serviceDetail.child("servicePrice").getValue(String.class);
                    serviceDetails.setServicePrice(svPrice);
                    binding.compSvPrice.setText(svPrice);
                    dialog.dismiss();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        binding.seeServiceCheckList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(TwoWheelerServicingActivity.this, CheckListActivity.class)
                        .putExtra("serviceID", "TwoWheelerService")
                );
            }
        });

        binding.twSvBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent i = new Intent(TwoWheelerServicingActivity.this, SlotBookingActivity.class);
                i.putExtra("serviceDetails", serviceDetails);
                startActivity(i);


            }
        });


        initializeReviews();

        initializefaq();


    }

    private void initializefaq() {

        faqList = new ArrayList<>();
        faqAdapter = new FaqAdapter(TwoWheelerServicingActivity.this, faqList);


        binding.faq.setLayoutManager(new LinearLayoutManager(TwoWheelerServicingActivity.this));
        binding.faq.setAdapter(faqAdapter);
        binding.faq.setHasFixedSize(true);


        ModelFaq q1 = new ModelFaq();
        q1.setQuestion("What is your name?");
        q1.setAnswer("My name is jay");

        ModelFaq q2 = new ModelFaq();
        q2.setQuestion("What is your name?");
        q2.setAnswer("My name is jay");

        ModelFaq q3 = new ModelFaq();
        q3.setQuestion("What is your name?");
        q3.setAnswer("My name is jay");


        faqList.add(q1);
        faqList.add(q2);
        faqList.add(q3);
        faqAdapter.notifyDataSetChanged();


    }


    private void initializeReviews() {

        ModelReview review1 = new ModelReview();
        ModelReview review2 = new ModelReview();
        ModelReview review3 = new ModelReview();


        review1.setName("Jay");
        review1.setRating(4);
        review1.setDescription("Very Good");
        review2.setName("Mangesh");
        review2.setRating(4);
        review2.setDescription("Professional service by them. Best part is gear to care provides price details up front and informs the technician arrival time while booking itself.i m Happy with the service");
        review3.setName("Champak");
        review3.setRating(4);
        review3.setDescription("Very Good");


        sliderItems.add(review1);
        sliderItems.add(review2);
        sliderItems.add(review3);


        reviewSliderAdapter = new ReviewSliderAdapter(sliderItems, binding.viewPagerImageSlider);
        binding.viewPagerImageSlider.setAdapter(reviewSliderAdapter);
        binding.viewPagerImageSlider.setClipChildren(false);
        binding.viewPagerImageSlider.setClipToPadding(false);
        binding.viewPagerImageSlider.setOffscreenPageLimit(3);
        binding.viewPagerImageSlider.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);


        binding.viewPagerImageSlider.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                sliderHandler.removeCallbacks(sliderRunnable);
                sliderHandler.postDelayed(sliderRunnable, 3000);
                /*if (position < binding.ll.getChildCount()) {
                    setCurrentBannerIndicator(position);
                } else {
                    setCurrentBannerIndicator((position % binding.ll.getChildCount()));
                }*/

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


    private Runnable sliderRunnable = new Runnable() {
        @Override
        public void run() {
            binding.viewPagerImageSlider.setCurrentItem(binding.viewPagerImageSlider.getCurrentItem() + 1);
        }
    };
}