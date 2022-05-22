package com.geartocare.client.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.geartocare.client.Activities.MyPackageActivity;
import com.geartocare.client.Activities.PackageVehicleActivity;
import com.geartocare.client.Activities.ServiceListActivity;
import com.geartocare.client.Helpers.MyPackageHelper;
import com.geartocare.client.Helpers.ValidityHelper;
import com.geartocare.client.R;
import com.geartocare.client.SessionManager;
import com.geartocare.client.databinding.CardPackageBinding;
import com.geartocare.client.model.ModelPackage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class PackageAdapter extends RecyclerView.Adapter<PackageAdapter.MyViewHolder> {
    Context context;
    ArrayList<ModelPackage> packages;
    SessionManager sessionManager;
    String purpose;

    public PackageAdapter(Context context, ArrayList<ModelPackage> packages) {
        this.context = context;
        this.packages = packages;
        sessionManager = new SessionManager(context);
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PackageAdapter.MyViewHolder(LayoutInflater.from(context).inflate(R.layout.card_package,parent,false));

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ModelPackage m = packages.get(position);

        holder.binding.name.setText(m.getName());
        holder.binding.cost.setText(m.getCost());
        holder.binding.serviceCost.setText(m.getServiceCost());
        holder.binding.validity.setText(m.getValidity());
        holder.binding.vehicleCount.setText(m.getVehicleCount());
        holder.binding.description.setText(m.getDescription());


        if(purpose.equals("buy")){
            holder.binding.buy.setVisibility(View.VISIBLE);
        }

        if(purpose.equals("include")){
            holder.binding.include.setVisibility(View.VISIBLE);

        }

        holder.binding.include.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent data = new Intent();
                data.putExtra("packageDetails",m);
                ((Activity) context).setResult(Activity.RESULT_OK,data);
                ((Activity) context).finish();



            }
        });


        holder.binding.buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Calendar c = Calendar.getInstance();

                Date startDate = c.getTime();
                c.add(Calendar.DATE,+365);
                Date endDate = c.getTime();




                String packageID=FirebaseDatabase.getInstance().getReference("Users").child(sessionManager.getUsersDetailsFromSessions().get(SessionManager.KEY_UID)).child("Packages").push().getKey();
                ValidityHelper validity = new ValidityHelper(String.valueOf(startDate.getTime()),String.valueOf(endDate.getTime()));

                MyPackageHelper packageHelper = new MyPackageHelper(packageID,m.getName(),m.getCost(),m.getServiceCost(),m.getVehicleCount(),validity);

                FirebaseDatabase.getInstance().getReference("Users").child(sessionManager.getUsersDetailsFromSessions().get(SessionManager.KEY_UID)).child("Packages").child(packageID).setValue(packageHelper).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()){

                            context.startActivity(new Intent(context, PackageVehicleActivity.class).putExtra("packageID",m.getId()));



                        }


                    }
                });


            }
        });


    }

    @Override
    public int getItemCount() {
        return packages.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        CardPackageBinding binding;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = CardPackageBinding.bind(itemView);
        }
    }
}
