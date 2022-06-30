package com.g2c.clientP.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;

import com.g2c.clientP.Adapters.FaqAdapter;
import com.g2c.clientP.Helpers.NetworkChangeListener;
import com.g2c.clientP.R;
import com.g2c.clientP.databinding.ActivityOtherServiceBinding;
import com.g2c.clientP.model.ModelFaq;

import java.util.ArrayList;

public class OtherServiceActivity extends AppCompatActivity {
    ActivityOtherServiceBinding binding;
    FaqAdapter faqAdapter;
    ArrayList<ModelFaq> faqList;
    Intent i;
     NetworkChangeListener networkChangeListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        networkChangeListener = new NetworkChangeListener(OtherServiceActivity.this);
        binding =  ActivityOtherServiceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        i=new Intent(OtherServiceActivity.this,QueryDetailActivity.class);

        initializefaq();




         binding.engineWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i.putExtra("category","Engine");
                startActivity(i);
            }
        });
        binding.shockUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i.putExtra("category","ShockUp");
                startActivity(i);
            }
        });
        binding.clutchWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i.putExtra("category","Clutch");
                startActivity(i);
            }
        });
        binding.otherWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i.putExtra("category","Other");
                startActivity(i);
            }
        });



/*

        binding.proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(OtherServiceActivity.this,QueryDetailActivity.class));
            }
        });
*/

    }

    private void initializefaq() {

        faqList = new ArrayList<>();
        faqAdapter = new FaqAdapter(OtherServiceActivity.this, faqList);


        binding.faq.setLayoutManager(new LinearLayoutManager(OtherServiceActivity.this));
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