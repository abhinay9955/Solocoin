package com.example.solocoin;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

import static android.content.ContentValues.TAG;

public class GeofenceBroadcastReciever extends BroadcastReceiver {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    public void onReceive(Context context, Intent intent) {


        sharedPreferences = context.getSharedPreferences("wallet",Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        //Handling Error;
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            String errorMessage =GeofenceStatusCodes.getStatusCodeString(geofencingEvent.getErrorCode());
            Log.i("error", errorMessage);
            return;
        }

        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();


        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER )
        {
            editor.putBoolean("inside",true);
            editor.apply();
        }
        else if(geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT)
        {
            editor.putBoolean("inside",false);
            editor.apply();
        }
        else
        {
            Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
        }
        sharedPreferences.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

            }
        });
    }

    public void showNotification(Context context,String title , String message)
    {

    }


}


