package com.g2c.clientP.Helpers;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.g2c.clientP.databinding.UnavailableDialogBinding;
import com.g2c.clientP.model.ModelArea;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class LocationChecker {
    ArrayList<ModelArea> areaList;
    Context context;
    CustomProgressDialog progressDialog;
    DatabaseReference areaListRef;
    LatLng defaultLocation;
    Dialog unavailableDialog;
    UnavailableDialogBinding uvBinding;


    public LocationChecker(Context context) {
        this.context = context;
        progressDialog = new CustomProgressDialog(context);
        areaListRef = FirebaseDatabase.getInstance().getReference("Services").child("TwoWheelerService").child("AreaList");
        defaultLocation = new LatLng(21.1899607643344, 79.07170057612956);
        this.areaList = new ArrayList<>();

        uvBinding = UnavailableDialogBinding.inflate(((Activity) context).getLayoutInflater());

        unavailableDialog = new Dialog(context);
        unavailableDialog.setContentView(uvBinding.getRoot());
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(unavailableDialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        unavailableDialog.getWindow().setAttributes(lp);


    }

    public UnavailableDialogBinding getUvBinding() {
        return uvBinding;
    }

    public void setUvBinding(UnavailableDialogBinding uvBinding) {
        this.uvBinding = uvBinding;
    }

    public Dialog getUnavailableDialog() {
        return unavailableDialog;
    }

    public void setUnavailableDialog(Dialog unavailableDialog) {
        this.unavailableDialog = unavailableDialog;
    }

    public LatLng getDefaultLocation() {
        return defaultLocation;
    }

    public void setDefaultLocation(LatLng defaultLocation) {
        this.defaultLocation = defaultLocation;
    }

    public ArrayList<ModelArea> getAreaList() {
        return areaList;
    }

    public void setAreaList(ArrayList<ModelArea> areaList) {
        this.areaList = areaList;
    }


    public void fetchAreaList() {
        progressDialog.show();



        areaListRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot areaListSnap) {
                if (areaListSnap.exists()) {


                    for (DataSnapshot areaSnap : areaListSnap.getChildren()) {


                        ModelArea area = new ModelArea();
                        area.setBottomLeft(new LatLng(Double.valueOf(areaSnap.child("bottomLeft").child("lat").getValue(String.class)), Double.valueOf(areaSnap.child("bottomLeft").child("lng").getValue(String.class))));
                        area.setTopRight(new LatLng(Double.valueOf(areaSnap.child("topRight").child("lat").getValue(String.class)), Double.valueOf(areaSnap.child("topRight").child("lng").getValue(String.class))));
                        area.setLandMark(new LatLng(Double.valueOf(areaSnap.child("landMark").child("lat").getValue(String.class)), Double.valueOf(areaSnap.child("landMark").child("lng").getValue(String.class))));
                        area.setName(areaSnap.child("name").getValue(String.class));
                        area.setLandMarkName(areaSnap.child("landMarkName").getValue(String.class));
                        areaList.add(area);
                    }


                }
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


    public boolean verifyLocation(Double givenLat, Double givenLng) {
        if(areaList.size()>0){
            for (ModelArea area : areaList) {
                if (givenLat >= area.getBottomLeft().latitude && givenLat <= area.getTopRight().latitude && givenLng >= area.getBottomLeft().longitude && givenLng <= area.getTopRight().longitude) {
                    return true;
                }

            }

        }

        return false;
    }



}
