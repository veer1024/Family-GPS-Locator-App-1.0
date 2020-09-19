package com.vaishnavas.familygpslocator;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;


import android.provider.Settings;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    VideoView videoview;

    private static final String TAG = "MainActivity";
    private int x=0;
int versioncode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
      DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        try {
           PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(),0);
           versioncode = packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        database.child("Developer").addValueEventListener(new ValueEventListener() {
          @Override
          public void onDataChange(@NonNull DataSnapshot datasnapshot) {
              for(DataSnapshot snapshot:datasnapshot.getChildren()){
                  if(Integer.parseInt(String.valueOf(snapshot.getValue()))>versioncode){
                      final AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                      alertDialog.setTitle("New Update is available...");
                      alertDialog.setMessage("do you want to Update your app");
                      alertDialog.setIcon(R.drawable.homelogo);

                      alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Update", new DialogInterface.OnClickListener() {
                          public void onClick(DialogInterface dialog, int which) {
                              Toast.makeText(getApplicationContext(), "getting server...", Toast.LENGTH_SHORT).show();
startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse("market://details?id="+getPackageName())));
                    alertDialog.dismiss();      }
                      });
                      alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Not Now", new DialogInterface.OnClickListener() {
                          public void onClick(DialogInterface dialog, int which) {
                              Toast.makeText(getApplicationContext(), "request denied", Toast.LENGTH_SHORT).show();
alertDialog.dismiss();
                          }
                      });
                      alertDialog.show();
                  }
              }
          }

          @Override
          public void onCancelled(@NonNull DatabaseError error) {

          }
      });
       videoview = (VideoView)findViewById(R.id.videoView);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        MediaController mc = new MediaController(this);
        videoview.setMediaController(mc);

        Uri video = Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.video);
        videoview.setVideoURI(video);
       videoview.start();
       videoview.setEnabled(false);
        videoview.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                // before going to loginactivity for registration lets check is the user is alredy login on this device or not ,if yes/ then directly movie to
                // and this will done by global info load data();
            GlobalInfo globalInfo = new GlobalInfo(MainActivity.this);
               globalInfo.LoadData();
                CheckUserPermsions();
                startServices();
              //videoview.start();
               finish();
            }
        });
;

    }


    //access to permsions
    void CheckUserPermsions() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                   PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{
                               Manifest.permission.ACCESS_FINE_LOCATION
                        },
                        REQUEST_CODE_ASK_PERMISSIONS);
                return;
            }
        }
        else {
            startServices();

        }
    }

    //get acces to location permsion
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_ASK_PERMISSIONS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
             startServices();
                // init the contact list
            } else {
                // Permission Denied
                Toast.makeText(this, "you denied the required permissions", Toast.LENGTH_LONG)
                       .show();
            }
        } else {

            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    void startServices() {
        if (true) {
            // we will start it

            TrackLocation trackLocation = new TrackLocation(MainActivity.this);
            LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

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


         lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, trackLocation);

}







    }





}