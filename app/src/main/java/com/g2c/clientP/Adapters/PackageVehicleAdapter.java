package com.g2c.clientP.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.g2c.clientP.R;
import com.g2c.clientP.databinding.CardPackageVehicleBinding;
import com.g2c.clientP.model.ModelVehicle;

import java.util.ArrayList;

public class PackageVehicleAdapter extends RecyclerView.Adapter<PackageVehicleAdapter.MyViewHolder> {
    Context context;
    ArrayList<ModelVehicle> mList,selectedItems;
    int selectionLimit;



    public PackageVehicleAdapter(Context context, ArrayList<ModelVehicle> mList, ArrayList<ModelVehicle> selectedItems,int selectionLimit) {
        this.context = context;
        this.mList = mList;
        this.selectedItems = selectedItems;
        this.selectionLimit=selectionLimit;

    }

    public ArrayList<ModelVehicle> getSelectedItems() {
        return selectedItems;
    }

    public void setSelectedItems(ArrayList<ModelVehicle> selectedItems) {
        this.selectedItems = selectedItems;
    }





    public int getSelectionLimit() {
        return selectionLimit;
    }

    public void setSelectionLimit(int selectionLimit) {
        this.selectionLimit = selectionLimit;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PackageVehicleAdapter.MyViewHolder(LayoutInflater.from(context).inflate(R.layout.card_package_vehicle,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ModelVehicle m = mList.get(position);


        holder.binding.Company.setText(m.getCompany());
        holder.binding.Model.setText(m.getModel());
        holder.binding.vhNo.setText(m.getVehicleNo());


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                  /*  if(m.isSelected()){
                        m.setSelected(false);
                        selectedItems.remove(m);
                        holder.itemView.setBackgroundColor(Color.WHITE);
                    }else if(selectedItems.size()<selectionLimit){
                        m.setSelected(true);
                        selectedItems.add(m);
                        holder.itemView.setBackgroundColor(Color.CYAN);



                    }*/


               // holder.itemView.setBackgroundColor(m.isSelected() ? Color.CYAN : ;

            }
        });


    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        CardPackageVehicleBinding binding;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = CardPackageVehicleBinding.bind(itemView);


        }
    }
}
