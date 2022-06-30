package com.g2c.clientP.Helpers;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.WindowManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.g2c.clientP.databinding.InternetAlertBinding;

public class NetworkChangeListener extends BroadcastReceiver {
    InternetAlertBinding binding;
    Dialog dialog;
    Context context;

    public NetworkChangeListener(Context context) {
        binding = InternetAlertBinding.inflate(((Activity) context).getLayoutInflater());

        dialog = new Dialog(context);
        dialog.setContentView(binding.getRoot());
        dialog.setCancelable(false);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(lp);

        dialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Dialog;



    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(!CheckConnectivity.isConnectedToInternet(context)) //Internet is not connected
        {

            binding.retry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    onReceive(context,intent);
                }
            });
            dialog.show();
        }
    }
}



class CheckConnectivity {
    public static boolean isConnectedToInternet(Context context){
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        if (connectivity != null)
        {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (NetworkInfo networkInfo : info)
                    if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }

        }
        return false;
    }

}