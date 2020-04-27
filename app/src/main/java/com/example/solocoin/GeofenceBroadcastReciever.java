package com.example.solocoin;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

import static android.content.ContentValues.TAG;

public class GeofenceBroadcastReciever extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("recieved", "onReceive: ");
        int x = context.getSharedPreferences("location",Context.MODE_PRIVATE).getInt("count",0);
        context.getSharedPreferences("location",Context.MODE_PRIVATE).edit().putInt("count",x+1);
        context.getSharedPreferences("location",Context.MODE_PRIVATE).edit().apply();
        Toast.makeText(context,"Recieved",Toast.LENGTH_LONG).show();
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            String errorMessage =GeofenceStatusCodes.getStatusCodeString(geofencingEvent.getErrorCode());
            Log.i("error", errorMessage);
            return;
        }

        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
                geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {

            // Get the geofences that were triggered. A single event can trigger
            // multiple geofences.
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();

            // Get the transition details as a String.


            // Send notification and log the transition details.
            Log.i("onReceive: ","changed");
        } else {
            // Log the error.
            Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
        }
    }
}


