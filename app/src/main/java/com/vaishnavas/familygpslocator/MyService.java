package com.vaishnavas.familygpslocator;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

public class MyService extends Service {
    // jab app khula nahi hoga tab bhi location update hoti rahe usske liye yein class banayi hai
    public static boolean IsRunning = false;
    DatabaseReference databaseRefrence;

    public MyService() {
        IsRunning = true;
        databaseRefrence = FirebaseDatabase.getInstance().getReference();

    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        databaseRefrence.child("Users").child(GlobalInfo.phonenumber).child("Updates").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:MM:ss");
                Date date = new Date();
                Location location = TrackLocation.loc;
                if(location!=null) {
                    if(GlobalInfo.phonenumber!=null){
                        databaseRefrence.child("UsersLocation").child(GlobalInfo.phonenumber).child("Location").
                                child("latitude").setValue(location.getLatitude());
                        databaseRefrence.child("UsersLocation").child(GlobalInfo.phonenumber).child("Location").
                                child("longitude").setValue(location.getLongitude());
                        databaseRefrence.child("UsersLocation").child(GlobalInfo.phonenumber).child("LastSeen").
                                setValue(df.format(date));
                    }
                    else{
                        Toast.makeText(MyService.this,"User information not found,please Login again",Toast.LENGTH_LONG).show();
                    }

                }
                else{
                    Toast.makeText(MyService.this,"location is null,try to run after some time",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
       });

        databaseRefrence.child("Users").child(GlobalInfo.phonenumber).child("letstrack").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                GlobalInfo.totrack.clear();
                for(DataSnapshot snapshot:datasnapshot.getChildren() ){

                    GlobalInfo.totrack.put(snapshot.getKey().toString(),snapshot.getValue().toString());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

      databaseRefrence.child("Users").child(GlobalInfo.phonenumber).child("status").addValueEventListener(new ValueEventListener() {
          @Override
          public void onDataChange(@NonNull DataSnapshot snapshot) {
               if(GlobalInfo.presentlytotrack.length()!=0){
                  if(snapshot.child("presently").getValue().equals("tracking")){
                   //   Toast.makeText(this,"tracking service is started now",Toast.LENGTH_LONG).show();
                      @SuppressLint("SimpleDateFormat") DateFormat dfa = new SimpleDateFormat("yyyy/MM/dd HH:MM:ss");
                     Date date = new Date();
                      databaseRefrence.child("Users").child(GlobalInfo.presentlytotrack).child("Updates").setValue(dfa.format(date));

                      databaseRefrence.child("UsersLocation").child(GlobalInfo.presentlytotrack).child("Location").addValueEventListener(new ValueEventListener() {
                          @Override
                          public void onDataChange(@NonNull DataSnapshot snapshot) {
                             GlobalInfo.presentlytotracklatitude = snapshot.child("latitude").getValue().toString().trim();
                             GlobalInfo.presentlytotracklongitude = snapshot.child("longitude").getValue().toString().trim();

                             databaseRefrence.child("Users").child(GlobalInfo.phonenumber).child("status").child("Mapping").child("latitude").setValue(GlobalInfo.presentlytotracklatitude);
                              databaseRefrence.child("Users").child(GlobalInfo.phonenumber).child("status").child("Mapping").child("longitude").setValue(GlobalInfo.presentlytotracklongitude).addOnSuccessListener(new OnSuccessListener<Void>() {
                                  @Override
                                  public void onSuccess(Void aVoid) {
                                      databaseRefrence.child("Users").child(GlobalInfo.phonenumber).child("status").child("presently").setValue("stoptracking");
 
                                  }
                              });

                          }

                          @Override
                          public void onCancelled(@NonNull DatabaseError error) {

                          }
                      });
                  }
              }
          }

          @Override
         public void onCancelled(@NonNull DatabaseError error) {

          }
      });
       return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
