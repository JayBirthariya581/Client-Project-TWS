package com.g2c.clientP.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.g2c.clientP.Helpers.CustomProgressDialog;
import com.g2c.clientP.R;
import com.g2c.clientP.SessionManager;
import com.g2c.clientP.databinding.CardMechanicReferralBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;

import java.util.ArrayList;

public class MechanicReferAdapter extends RecyclerView.Adapter<MechanicReferAdapter.MyViewHolder> {

    ArrayList<ModelMechanic> mechanicList;
    Context context;
    CustomProgressDialog progressDialog;
    SessionManager sessionManager;
    Uri shortLink;
    String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public MechanicReferAdapter(ArrayList<ModelMechanic> mechanicList, Context context) {
        this.mechanicList = mechanicList;
        this.context = context;
        sessionManager = new SessionManager(context);
        progressDialog = new CustomProgressDialog(context);
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MechanicReferAdapter.MyViewHolder(LayoutInflater.from(context).inflate(R.layout.card_mechanic_referral, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ModelMechanic mechanic = mechanicList.get(position);

        holder.binding.name.setText(mechanic.getFirstName() + " " + mechanic.getLastName());
        holder.binding.code.setText(mechanic.getReferral().getReferCode());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                createReferLink(sessionManager.getUsersDetailsFromSessions().get(SessionManager.KEY_UID), code, mechanic.mechanicID, mechanic.getReferral().getReferCode());

            }
        });
    }

    @Override
    public int getItemCount() {
        return mechanicList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        CardMechanicReferralBinding binding;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = CardMechanicReferralBinding.bind(itemView);
        }
    }


    public void createReferLink(String userUid, String userReferCode, String mechanicID, String mechanicReferCode) {

        String shareLink = "https://geartocareproto.page.link/?" +
                "link=https://www.example.com/?custid=" + "m2c" +"/"+ userUid + "/" + userReferCode + "/" + mechanicID + "/" + mechanicReferCode+
                "&apn=" + context.getPackageName() +
                "&st=" + "My Demo Refer Link" +
                "&sd=" + "Reward gift" +
                "&si=" + "https://www.uffizio.com/blog/content/public/upload/share-link_2_o.png";

        Log.e("MainActivity", "sharelink=" + shareLink);

        Task<ShortDynamicLink> shortLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLongLink(Uri.parse(shareLink))
                .buildShortDynamicLink();

        shortLinkTask.addOnCompleteListener((Activity) context, new OnCompleteListener<ShortDynamicLink>() {
            @Override
            public void onComplete(@NonNull Task<ShortDynamicLink> task) {
                if (task.isSuccessful()) {

                    shortLink = task.getResult().getShortLink();

                    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");

                    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "GearToCare");
                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shortLink.toString());
                    progressDialog.dismiss();
                    context.startActivity(Intent.createChooser(sharingIntent, "Share via"));


                } else {


                    Log.e("main", " Error! " + task.getException());

                }
            }
        });
    }

}
