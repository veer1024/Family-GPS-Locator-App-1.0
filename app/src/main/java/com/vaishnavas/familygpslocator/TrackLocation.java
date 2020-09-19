package com.vaishnavas.familygpslocator;

import android.Manifest;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import static android.content.Context.LOCATION_SERVICE;


public class TrackLocation implements LocationListener {
    public static Location loc;
    public static double latitudetrack = 0;
    public static double longitudetrack = 0;
    public static boolean isRunning = false;
    private final Context mContext;

    boolean checkGPS = false;


    boolean checkNetwork = false;

    boolean canGetLocation = false;


    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0;


    private static final long MIN_TIME_BW_UPDATES = 1000;
    protected LocationManager locationManager;
    private FusedLocationProviderClient fusedlocationprovider;
    private LocationRequest locationupdaterequest;
    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult != null) {
                return;
            }
            for (Location location : locationResult.getLocations()) {
                Log.d("updates", location.toString());
            }
        }
    };

    public TrackLocation(Context mContext) {

        isRunning = true;
        this.mContext = mContext;
        getLocation();


    }




    @SuppressLint("MissingPermission")
    private Location getLocation() {

        try {
            locationManager = (LocationManager)
                    mContext.getSystemService(LOCATION_SERVICE);
             fusedlocationprovider = LocationServices.getFusedLocationProviderClient(mContext);
             locationupdaterequest.setInterval(4000);
             locationupdaterequest.setFastestInterval(2000);
             locationupdaterequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            // get GPS status
            checkGPS = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // get network provider status
            checkNetwork = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!checkGPS && !checkNetwork) {
                // Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                //  mContext.startActivity(intent);

                fusedlocationprovider.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
if(location!=null){
    loc = location;
}else{
Toast.makeText(mContext,"location servicess not working properly",Toast.LENGTH_LONG).show();
}
                   }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(mContext,"Fused location failed",Toast.LENGTH_LONG).show();
                    }
                });
           } else {
               this.canGetLocation = true;

                // if GPS Enabled get lat/long using GPS Services
                if (checkGPS) {


                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                           MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    if (locationManager != null) {
                        loc = locationManager
                                .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (loc != null) {
                            latitudetrack= loc.getLatitude();
                            longitudetrack = loc.getLongitude();
                        }else{
                            fusedlocationprovider.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {
                                    if(location!=null){
                                        loc = location;
                                    }else{
                                        Toast.makeText(mContext,"location servicess not working properly",Toast.LENGTH_LONG).show();
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(mContext,"Fused location failed",Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }


                }

                if(!checkGPS) {


                    if (checkNetwork) {


                        assert locationManager != null;
                        locationManager.requestLocationUpdates(
                                LocationManager.NETWORK_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                        if (locationManager != null) {
                            loc = locationManager
                                    .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                        }

                        if (loc != null) {
                            latitudetrack = loc.getLatitude();
                            longitudetrack = loc.getLongitude();
                        }else{
                            fusedlocationprovider.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {
                                    if(location!=null){
                                        loc = location;
                                    }else{
                                        Toast.makeText(mContext,"location servicess not working properly",Toast.LENGTH_LONG).show();
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(mContext,"Fused location failed",Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        return loc;
    }











    @Override
    public void onLocationChanged( Location location) {



    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
    // dialog box agar location null ho tab gps on krne ke liye request)



}
