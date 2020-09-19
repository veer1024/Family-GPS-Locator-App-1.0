package com.vaishnavas.familygpslocator;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class trackinglocationservice extends Service {
    public static boolean Running = false;
    DatabaseReference mdatabase;
    public trackinglocationservice(){
        mdatabase = FirebaseDatabase.getInstance().getReference();
        Toast.makeText(this,"live tracking is started now ",Toast.LENGTH_LONG).show();
  Running = true;  }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
           Toast.makeText(this,"tracking service is started now",Toast.LENGTH_LONG).show();
      /* @SuppressLint("SimpleDateFormat") DateFormat dfa = new SimpleDateFormat("yyyy/MM/dd HH:MM:ss");
        Date date = new Date();
       mdatabase.child("Users").child(GlobalInfo.presentlytotrack).child("Updates").setValue(dfa.format(date));
       mdatabase.child("UsersLocation").child(GlobalInfo.presentlytotrack).child("Location").addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {
               GlobalInfo.presentlytotracklatitude = Objects.requireNonNull(snapshot.child("Latitude").getValue()).toString().trim();
               GlobalInfo.presentlytotracklongitude = Objects.requireNonNull(snapshot.child("Longitude").getValue()).toString().trim();
           }

           @Override
           public void onCancelled(@NonNull DatabaseError error) {

           }
       }); */
   return START_STICKY; }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
