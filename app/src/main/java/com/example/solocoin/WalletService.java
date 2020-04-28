package com.example.solocoin;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

public class WalletService extends Service {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Boolean isInside,loop;
    public  final String CHANNEL_ID = "servicechannel";
    public final String CHANNEL_NAME = "Notification Service Channel";
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            Intent notificationintent = new Intent(this,MapsActivity.class);
            notificationintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingintent = PendingIntent.getActivity(this,0,notificationintent,0);
            startServiceInForeground(this,notificationintent,pendingintent);
        }

            sharedPreferences = getSharedPreferences("wallet",MODE_PRIVATE);
            editor = sharedPreferences.edit();
            loop = true;
            checkStatusOnInterval();


        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public  void checkStatusOnInterval()
    {
        Handler handler =new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.i("run", "run: ");
                isInside = sharedPreferences.getBoolean("inside",true);
                 if(isInside)
                 {
                     int walletPoint = sharedPreferences.getInt("amount",0);
                     editor.putInt("amount",walletPoint+10);
                     editor.apply();
                 }
                 else
                 {
                     int walletPoint = sharedPreferences.getInt("amount",0);
                     editor.putInt("amount",walletPoint-10);
                     editor.apply();
                 }
                 if(loop)
                 checkStatusOnInterval();
            }
        },1000);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void startServiceInForeground(Context context, Intent intent , PendingIntent pendingIntent)
    {
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID,CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(channel);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,CHANNEL_ID);
        Notification notification = builder.setOngoing(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .build();

        startForeground(101,notification);
    }


    @Override
    public void onDestroy() {
        loop=false;
        super.onDestroy();

    }
}
