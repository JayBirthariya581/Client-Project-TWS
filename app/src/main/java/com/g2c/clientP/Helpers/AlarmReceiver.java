package com.g2c.clientP.Helpers;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.g2c.clientP.Activities.MainActivity;
import com.g2c.clientP.R;
import com.g2c.clientP.SessionManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;


public class AlarmReceiver extends BroadcastReceiver {
    SessionManager sessionManager;
    DatabaseReference UserRef;


    NotificationManager notificationManager;
    NotificationChannel channel;
    Intent resultIntent;
    String CHANNEL_ID;
    CharSequence channel_name;
    String channel_description;
    Calendar calendar;
    Long timeStamp, timeStamp2;

    @Override
    public void onReceive(Context context, Intent intent) {

        calendar = Calendar.getInstance();
        timeStamp = calendar.getTime().getTime();
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        int hr = calendar.get(Calendar.HOUR_OF_DAY);
        String weekDay = String.valueOf(calendar.get(Calendar.DAY_OF_WEEK));
        calendar.add(Calendar.DAY_OF_WEEK, 3);
        timeStamp2 = calendar.getTime().getTime();

        sessionManager = new SessionManager(context);
        UserRef = FirebaseDatabase.getInstance().getReference("Users");

        if (hr == 10) {
            //show notification


            if (sessionManager.checkLogin()) {

                UserRef.child(sessionManager.getUsersDetailsFromSessions().get(SessionManager.KEY_UID)).child("vehicles").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot vehicleListSnap) {


                        if (vehicleListSnap.exists()) {


                            //==========================================================================================================================
                            CHANNEL_ID = "GTC_BOOKING";
                            channel_name = "GTC_BOOKING";
                            channel_description = "Channel for bookings";

                            notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                            resultIntent = new Intent(context, MainActivity.class);

                            resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_IMMUTABLE);

                            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);


                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                channel = notificationManager.getNotificationChannel(CHANNEL_ID);
                                if (channel == null) {
                                    channel = new NotificationChannel(CHANNEL_ID, channel_name, NotificationManager.IMPORTANCE_HIGH);
                                    channel.setDescription(channel_description);
                                }

                                notificationManager.createNotificationChannel(channel);

                            }

                            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                                    .setSmallIcon(R.drawable.marker)
                                    .setVibrate(new long[]{100, 300, 300, 300})
                                    .setContentIntent(pendingIntent)
                                    .setSound(alarmSound)
                                    .setAutoCancel(true)
                                    .setPriority(NotificationCompat.PRIORITY_MAX);


                            NotificationManagerCompat m = NotificationManagerCompat.from(context);


                            //==========================================================================================================================


                            for (DataSnapshot vehicleSnap : vehicleListSnap.getChildren()) {

                                if (vehicleSnap.child("ServiceDue").exists()) {
                                    Long ts = Long.valueOf(vehicleSnap.child("ServiceDue").getValue(String.class));

                                    if (ts > timeStamp && ts < timeStamp2) {
                                        Toast.makeText(context, "12345", Toast.LENGTH_SHORT).show();

                                        builder.setContentTitle("GearToCare");
                                        builder.setContentText("Service due for " + vehicleSnap.child("vehicleNo").getValue(String.class));

                                        m.notify((int) System.currentTimeMillis(), builder.build());
                                    } else {
                                        Toast.makeText(context, "1234", Toast.LENGTH_SHORT).show();

                                    }
                                } else {
                                    Toast.makeText(context, "123", Toast.LENGTH_SHORT).show();
                                }

                            }

                        } else {
                            Toast.makeText(context, "noVhs", Toast.LENGTH_SHORT).show();

                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            } else {
                Toast.makeText(context, "nolog", Toast.LENGTH_SHORT).show();

            }

        } else {
            Toast.makeText(context, "no10", Toast.LENGTH_SHORT).show();

        }


        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            restartAlarmManagerOnBootUp(context);
        }

    }

    private void restartAlarmManagerOnBootUp(Context context) {

        Log.d("Status", "reboot's alarm");

        AlarmManager alarmManager;
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent1 = new Intent(context, AlarmReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0,
                intent1, PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar calendar1 = Calendar.getInstance();

        calendar1.set(Calendar.HOUR_OF_DAY, 10);
        calendar1.set(Calendar.MINUTE, 0);
        calendar1.set(Calendar.SECOND, 0);


        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar1.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY,
                pendingIntent);

    }


}
