package com.geartocare.client.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.geartocare.client.Activities.EditProfileActivity;
import com.geartocare.client.Activities.GetPhoneActivity;
import com.geartocare.client.SessionManager;
import com.geartocare.client.databinding.FragmentProfileBinding;


public class ProfileFragment extends Fragment {
    FragmentProfileBinding binding;
    SessionManager sessionManager;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sessionManager = new SessionManager(getContext());

        binding.userName.setText(sessionManager.getUsersDetailsFromSessions().get(SessionManager.KEY_FULLNAME));
        binding.userPhone.setText(sessionManager.getUsersDetailsFromSessions().get(SessionManager.KEY_PHONE));
        if(!sessionManager.getUsersDetailsFromSessions().get(SessionManager.KEY_EMAIL).equals("none")){
            binding.userEmail.setText(sessionManager.getUsersDetailsFromSessions().get(SessionManager.KEY_EMAIL));
        }else{
            binding.userEmail.setText(" ");
        }


        binding.logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                sessionManager.logoutSession();


                startActivity(new Intent(getContext(), GetPhoneActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                ((Activity) getContext()).finish();


            }
        });


        binding.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");

                String shareBody = "Hey,This is GearToCare share link \n\nVisit : blahblah";




                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "GearToCare");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));

            }
        });


        ActivityResultLauncher<Intent> lau = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {

                if(result.getResultCode()==Activity.RESULT_OK){

                    Intent i=result.getData();

                    binding.userEmail.setText(i.getStringExtra("email"));
                    binding.userName.setText(i.getStringExtra("name"));


                }

            }
        });

        binding.editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lau.launch(new Intent(getContext(), EditProfileActivity.class));
            }
        });



    }
}