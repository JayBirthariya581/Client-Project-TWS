package com.geartocare.client.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.geartocare.client.Adapters.FaqAdapter;
import com.geartocare.client.R;
import com.geartocare.client.databinding.ActivityOtherServiceBinding;
import com.geartocare.client.model.ModelFaq;

import java.util.ArrayList;

public class OtherServiceActivity extends AppCompatActivity {
    ActivityOtherServiceBinding binding;
    FaqAdapter faqAdapter;
    ArrayList<ModelFaq> faqList;
    Intent i;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
}