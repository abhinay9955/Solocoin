package com.example.solocoin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener, GoogleMap.OnMapClickListener {

    private GoogleMap mMap;
    private PendingIntent geofencePendingIntent;
    private GeofencingClient geofencingClient;
    private Button start,stop;
    private Marker geoMarker,curMarker;
    private Circle circle;
    private ArrayList<Geofence> geofenceList;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    Double lat,lng ; // latitude and longitude of already present geofence
    Boolean isGeofence; //check if there is already a geofence

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        sharedPreferences = getSharedPreferences("location",MODE_PRIVATE);
        editor = sharedPreferences.edit();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        start = findViewById(R.id.start);
        stop = findViewById(R.id.stop);
        start.setOnClickListener(this);
        stop.setOnClickListener(this);
        geofencingClient = LocationServices.getGeofencingClient(this);
        geofenceList = new ArrayList<>();
        isGeofence = sharedPreferences.getBoolean("geofence",false);
        Toast.makeText(this,""+isGeofence,Toast.LENGTH_LONG).show();
        if(isGeofence)
        {
            lat = Double.parseDouble(sharedPreferences.getString("lat","0.0"));
            lng = Double.parseDouble(sharedPreferences.getString("lng","0.0"));
            if(mMap!=null)
            {
                if(geoMarker!=null)
                    geoMarker.remove();
               geoMarker= mMap.addMarker(new MarkerOptions().position(new LatLng(lat,lng)).title("Geofence Marker"));
            }
        }

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        mMap.setOnMapClickListener(this);
        isGeofence = sharedPreferences.getBoolean("geofence",false);
        Toast.makeText(this,""+isGeofence,Toast.LENGTH_LONG).show();
        if(isGeofence)
        {
            lat = Double.parseDouble(sharedPreferences.getString("lat","0.0"));
            lng = Double.parseDouble(sharedPreferences.getString("lng","0.0"));
            if(mMap!=null)
            {
                if(geoMarker!=null)
                    geoMarker.remove();
                geoMarker= mMap.addMarker(new MarkerOptions().position(new LatLng(lat,lng)).title("Geofence Marker"));
            }
            CircleOptions circleOptions =new CircleOptions().center(new LatLng(lat,lng)).radius(100f).strokeColor(Color.parseColor("#543535")).fillColor(Color.parseColor("#a44dc6"));
            if(circle!=null)
                circle.remove();
            circle = mMap.addCircle(circleOptions);
            float zoomLevel = 16.0f; //This goes up to 21
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat,lng), zoomLevel));
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {
        if(mMap!=null) {

            MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("Geofence Marker");
            CircleOptions circleOptions =new CircleOptions().center(latLng).radius(100f).strokeColor(Color.parseColor("#543535")).fillColor(Color.parseColor("#a44dc6"));
            if (geoMarker != null) {
                geoMarker.remove();
            }
            if(circle!=null)
                circle.remove();
            geoMarker = mMap.addMarker(markerOptions);
            circle = mMap.addCircle(circleOptions);

        }
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.start:  if (geoMarker == null) {
                               Toast.makeText(MapsActivity.this, "Select a Geofence marker", Toast.LENGTH_SHORT).show();
                               break;
                               }
                              addGeofences();
                              break;

            case R.id.stop :  stopGeofence();
                              break;
        }
    }



    private void addGeofences(){
        geofencingClient.addGeofences(getGeofencingRequest(),getGeofencePendingIntent()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                   Toast.makeText(MapsActivity.this,"Successfully added geofences",Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MapsActivity.this,"Error in adding geofences",Toast.LENGTH_LONG).show();
            }
        });
    }


    private PendingIntent getGeofencePendingIntent() {
        if (geofencePendingIntent != null) {
            return geofencePendingIntent;
        }
        Intent intent = new Intent(MapsActivity.this, GeofenceBroadcastReciever.class);
        geofencePendingIntent = PendingIntent.getBroadcast(MapsActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return geofencePendingIntent;
    }




    private void stopGeofence(){
        geofencingClient.removeGeofences(getGeofencePendingIntent()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                   Toast.makeText(MapsActivity.this,"Successfully removed",Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                   Toast.makeText(MapsActivity.this,"Failed removing",Toast.LENGTH_LONG).show();
            }
        });
    }




   private Geofence getGeofence(){
        return  new Geofence.Builder()
                .setCircularRegion(geoMarker.getPosition().latitude,geoMarker.getPosition().longitude,100f)
                .setRequestId("GeofenceID")
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT |Geofence.GEOFENCE_TRANSITION_DWELL)
                .setLoiteringDelay(5000)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .build();
   }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofence(getGeofence());
        return builder.build();
    }

    @Override
    protected void onPause() {
        super.onPause();




        editor.apply();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("stop", "onStop: ");
    }

    @Override
    protected void onDestroy() {
        Toast.makeText(this, "Destroyed", Toast.LENGTH_SHORT).show();
        Log.i("dest", "onDestroy: ");
        if(geoMarker!=null)
        {
            editor.putBoolean("geofence",true);
            editor.putString("lat",String.valueOf(geoMarker.getPosition().latitude));
            editor.putString("lng",String.valueOf(geoMarker.getPosition().longitude));
        }
        else
        {
            editor.putBoolean("geofence",false);
            editor.putString("lat",String.valueOf(0.0));
            editor.putString("lng",String.valueOf(0.0));

        }
        editor.apply();
        super.onDestroy();
    }
}
