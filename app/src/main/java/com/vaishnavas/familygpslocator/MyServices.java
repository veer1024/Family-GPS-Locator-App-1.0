package com.vaishnavas.familygpslocator;

import android.Manifest;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MyServices extends IntentService {
    // jab app khula nahi hoga tab bhi location update hoti rahe usske liye yein class banayi hai
    public static boolean IsRunning = false;
    static Location lastlocation = null;
    DatabaseReference databaseRefrence;

    public MyServices() {
        super("myservices");
        IsRunning = true;
        databaseRefrence = FirebaseDatabase.getInstance().getReference();

    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        databaseRefrence.child("Users").child(GlobalInfo.phonenumber).child("Updates").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:MM:ss");
                Date date = new Date();
                Location location = TrackLocation.loc;
                databaseRefrence.child("Users").child(GlobalInfo.phonenumber).child("Location").
                        child("latitude").setValue(location.getLatitude());
                databaseRefrence.child("Users").child(GlobalInfo.phonenumber).child("Location").
                        child("longitude").setValue(location.getLongitude());
                databaseRefrence.child("Users").child(GlobalInfo.phonenumber).child("LastSeen").
                        setValue(df.format(date).toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onCreate() {
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                if (lastlocation == null) {
                    lastlocation = location;
                }

            }

            @Override
            public void onProviderEnabled(@NonNull String provider) {

            }

            @Override
            public void onProviderDisabled(@NonNull String provider) {

            }
        };
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    }
}
