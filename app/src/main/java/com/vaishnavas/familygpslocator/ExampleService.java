package com.vaishnavas.familygpslocator;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import static com.vaishnavas.familygpslocator.App.CHANNEL_ID;


public class ExampleService extends Service {
  MediaPlayer player;
  FirebaseDatabase database = FirebaseDatabase.getInstance();
  DatabaseReference databaseRefrence= database.getReference();
  @Override
  public void onCreate() {

    super.onCreate();
  }
  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {

    Intent notificationIntent = new Intent(this, loading.class);
    PendingIntent pendingIntent = PendingIntent.getActivity(this,
            0, notificationIntent, 0);
    Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("tracking service started")
            .setContentText("do no open it")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentIntent(pendingIntent)
            .build();
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
            databaseRefrence.child("UsersLocation").child(GlobalInfo.phonenumber).child("Location").child("LastSeen").
                   setValue(df.format(date));
          }
          else{
           Toast.makeText(ExampleService.this,"User information not found,please Login again",Toast.LENGTH_LONG).show();
          }

        }
        else{
          Log.i("location","null");
        }
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {

      }
    });

    databaseRefrence.child("Users").child(GlobalInfo.phonenumber).child("letstrack").addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot datasnapshot) {

        for(DataSnapshot snapshot:datasnapshot.getChildren() ){
if(Objects.equals(snapshot.getValue(), "trackable")) {
  if(!GlobalInfo.totrack.containsKey(snapshot.getKey())) {
    GlobalInfo.totrack.put(snapshot.getKey(), snapshot.getValue().toString());
  }
}
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
          if(Objects.equals(snapshot.child("presently").getValue(), "tracking")){
            //   Toast.makeText(this,"tracking service is started now",Toast.LENGTH_LONG).show();
            @SuppressLint("SimpleDateFormat") DateFormat dfa = new SimpleDateFormat("yyyy/MM/dd HH:MM:ss");
            Date date = new Date();
            databaseRefrence.child("Users").child(GlobalInfo.presentlytotrack).child("Updates").setValue(dfa.format(date));

            databaseRefrence.child("UsersLocation").child(GlobalInfo.presentlytotrack).child("Location").addValueEventListener(new ValueEventListener() {
              @Override
              public void onDataChange(@NonNull DataSnapshot snapshot) {
                GlobalInfo.presentlytotracklatitude = Objects.requireNonNull(snapshot.child("latitude").getValue()).toString().trim();
                GlobalInfo.presentlytotracklongitude = Objects.requireNonNull(snapshot.child("longitude").getValue()).toString().trim();
                GlobalInfo.lastupdatetotrack = Objects.requireNonNull(snapshot.child("LastSeen").getValue()).toString().trim();

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

    startForeground(1, notification);
    //do heavy work on a background thread
    //    stopSelf();
    return START_STICKY;
  }
  @Override
  public void onDestroy() {
    super.onDestroy();
    Log.i("servicedemo","ondestroycalled");
    Intent broadcastIntent = new Intent();
    broadcastIntent.setAction("com.vaishnavas.familygpslocator.restartingservice");
    sendBroadcast(broadcastIntent);}



  @Override
  public void onTaskRemoved(Intent rootIntent) {
    Intent restartservice = new Intent(getApplicationContext(),ExampleService.class);
    restartservice.setPackage(getPackageName());
    PendingIntent restartpending = PendingIntent.getService(this,1,restartservice,PendingIntent.FLAG_ONE_SHOT);
    AlarmManager nyalarm = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
    nyalarm.set(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime() + 250,restartpending);
    super.onTaskRemoved(rootIntent);
    Log.i("onswipeout","restarting service");  }



  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }


}