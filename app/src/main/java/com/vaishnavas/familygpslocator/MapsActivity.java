package com.vaishnavas.familygpslocator;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    //adapter class

    final ArrayList<Trackers_list> Trackerslistarray = new ArrayList<>();
    final ArrayList<to_track> Totracklistarray = new ArrayList<>();
    final static List phonenumberlist = new ArrayList();
    private MyCustomAdapter myadapter;
    private Adapter_for_Android_Contacts adapterForAndroidContacts;
    FloatingActionButton addbutton;
    FloatingActionButton minusbutton;
    FloatingActionButton sharebutton;
    FloatingActionButton Mylocation;
    FloatingActionButton letstrackbutton;
    ListView ContactList;
    ListView Trackerslist;
    ListView totracklist;
    FloatingActionButton cross;
    int x = 0;
    int z = 0;
    int l = 0;
    TrackLocation TrackLocation;
    TextView explaner;

    private FusedLocationProviderClient fusedLocationProviderClient;
    Marker totrack;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // ( animation of floating button
        explaner = (TextView) findViewById(R.id.textView2);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        cross = (FloatingActionButton) findViewById(R.id.cross);
        letstrackbutton = (FloatingActionButton) findViewById(R.id.trackerslist);
        addbutton = (FloatingActionButton) findViewById(R.id.add);
        minusbutton = (FloatingActionButton) findViewById(R.id.minus);
        sharebutton = (FloatingActionButton) findViewById(R.id.share);
        Mylocation = (FloatingActionButton) findViewById(R.id.mylocation);
        ContactList = (ListView) findViewById(R.id.contactlistview);
        Trackerslist = (ListView) findViewById(R.id.trackerslistview);
        totracklist = (ListView) findViewById(R.id.totracklistview);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        Toast.makeText(this, GlobalInfo.phonenumber, Toast.LENGTH_LONG).show();
        mapFragment.getMapAsync(this);
        // ( intializing contactlist
        addbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (x == 0) {
                    minusbutton.setVisibility(View.VISIBLE);
                    sharebutton.setVisibility(View.VISIBLE);
                    explaner.setVisibility(View.VISIBLE);
                    explaner.setText("Your ContactList");
                    addbutton.setRotation((float) 45.0);
                    ContactList.setVisibility(View.VISIBLE);
                    Trackerslist.setVisibility(View.INVISIBLE);
                    totracklist.setVisibility(View.INVISIBLE);
                    addbutton.setBackgroundColor(Color.RED);
                    x = 1;
                    CheckUserPermsions();
                } else if (x == 1) {
                    explaner.setVisibility(View.INVISIBLE);
                    minusbutton.setVisibility(View.INVISIBLE);
                    sharebutton.setVisibility(View.INVISIBLE);
                    ContactList.setVisibility(View.INVISIBLE);
                    addbutton.setBackgroundColor(Color.BLUE);
                    Trackerslist.setVisibility(View.INVISIBLE);

                    addbutton.setRotation((float) 0.0);
                    x = 0;
                }


            }
        });
        // intializing contactlist )


    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(final GoogleMap googleMap) {
        checksettingsforupdate();
        final DatabaseReference database;
        database = FirebaseDatabase.getInstance().getReference();
        Location location = com.vaishnavas.familygpslocator.TrackLocation.loc;
        if (!com.vaishnavas.familygpslocator.TrackLocation.isRunning) {

                // we will start it

                TrackLocation trackLocation = new TrackLocation(MapsActivity.this);
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


                } else {
          //  Toast.makeText(this, "service is  enabled now and running ", Toast.LENGTH_LONG).show();
        }
        if (location != null) {

            LatLng mylocation = new LatLng(location.getLatitude(), location.getLongitude());
            googleMap.addMarker(new MarkerOptions().position(mylocation).title("MyLocation"));
            CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(mylocation, 17);
            googleMap.animateCamera(yourLocation);
            checksettingsforupdate();
        } else {
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
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                       com.vaishnavas.familygpslocator.TrackLocation.loc = location;
                        LatLng mylocation = new LatLng(location.getLatitude(), location.getLongitude());
                        googleMap.addMarker(new MarkerOptions().position(mylocation).title("MyLocation"));
                        CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(mylocation, 17);
                        googleMap.animateCamera(yourLocation);
                    } else {
                        Toast.makeText(MapsActivity.this, "location servicess not working properly", Toast.LENGTH_LONG).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MapsActivity.this, "Fused location failed", Toast.LENGTH_LONG).show();
                }
            });

         //  Toast.makeText(MapsActivity.this, "location provider is not responding", Toast.LENGTH_LONG).show();
        }

        Mylocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Location location = com.vaishnavas.familygpslocator.TrackLocation.loc;
                if (location != null) {
                    ContactList.setVisibility(View.INVISIBLE);
                    totracklist.setVisibility(View.INVISIBLE);
                    Trackerslist.setVisibility((View.INVISIBLE));
                    cross.setVisibility(View.INVISIBLE);
                    addbutton.setVisibility(View.VISIBLE);
                    LatLng mylocation = new LatLng(location.getLatitude(), location.getLongitude());
                    googleMap.addMarker(new MarkerOptions().position(mylocation).title("MyLocation"));
                    CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(mylocation, 17);
                    googleMap.animateCamera(yourLocation);
                    minusbutton.setVisibility(View.INVISIBLE);
                    sharebutton.setVisibility(View.INVISIBLE);
                  explaner.setVisibility(View.INVISIBLE);
                    addbutton.setRotation((float) 0.0);
                    x = 0;
                } else {
                    Toast.makeText(MapsActivity.this, "location provider is not responding", Toast.LENGTH_LONG).show();
                }
            }
        });
        database.child("Users").child(GlobalInfo.phonenumber).child("status").child("Mapping").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (GlobalInfo.presentlytotracklongitude.length() != 0) {
                    if (GlobalInfo.presentlytotrack.length() != 0) {
                        LatLng locationtotrack = new LatLng(Double.parseDouble(GlobalInfo.presentlytotracklatitude), Double.parseDouble(GlobalInfo.presentlytotracklongitude));

                         totrack =     googleMap.addMarker(new MarkerOptions().position(locationtotrack).title("location of " + GlobalInfo.presentlytotrack+ ":"+GlobalInfo.lastupdatetotrack));

                        CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(locationtotrack, 17);
                        if (z == 0) {
                            googleMap.animateCamera(yourLocation);
                            z = 1;
                        }

                        database.child("Users").child(GlobalInfo.phonenumber).child("status").child("Mapping").setValue("notrunning").addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                database.child("Users").child(GlobalInfo.phonenumber).child("status").child("presently").setValue("tracking");
                            }
                        });


                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void checksettingsforupdate() {
        LocationSettingsRequest request = new LocationSettingsRequest.Builder().addLocationRequest(locationupdaterequest).build();
        SettingsClient client = LocationServices.getSettingsClient(MapsActivity.this);
        Task<LocationSettingsResponse> locationSettingsResponseTask = client.checkLocationSettings(request);
        locationSettingsResponseTask.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                // settings of device are okay
                startupdate();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if(e instanceof ResolvableApiException){
ResolvableApiException apiException = (ResolvableApiException) e;
                    try {
                        apiException.startResolutionForResult(MapsActivity.this,1001);
                    } catch (IntentSender.SendIntentException ex) {
                        ex.printStackTrace();
                   }
                }
            }
        });
    }

    private void startupdate() {
        if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationupdaterequest, locationCallback, Looper.getMainLooper());
    }

   public void refreshingserver(View view) {
       final AlertDialog alertDialog = new AlertDialog.Builder(MapsActivity.this).create();
       alertDialog.setTitle("Refreshing data with server...");
       alertDialog.setMessage("do you want to refresh your app data with the server \n Note: need internet, do only if required ,or trackerslist is null!!");
       alertDialog.setIcon(R.drawable.ic_3d_rotation_white_24dp);

       alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Refresh Data", new DialogInterface.OnClickListener() {
           public void onClick(DialogInterface dialog, int which) {
               Toast.makeText(getApplicationContext(), "getting server...", Toast.LENGTH_SHORT).show();
               refreshserverdata();
                alertDialog.dismiss();
           }
       });
       alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Not Now", new DialogInterface.OnClickListener() {
           public void onClick(DialogInterface dialog, int which) {
               Toast.makeText(getApplicationContext(), "request denied", Toast.LENGTH_SHORT).show();

           }
       });
       alertDialog.show();
    }

    private void refreshserverdata() {
     DatabaseReference databaseRefrence = FirebaseDatabase.getInstance().getReference();
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
                Toast.makeText(MapsActivity.this,"User information not found,please Login again",Toast.LENGTH_LONG).show();
            }

        }
        else{
            Log.i("location","null");
        }
        databaseRefrence.child("Users").child(GlobalInfo.phonenumber).child("letstrack").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
GlobalInfo.totrack.clear();
                for(DataSnapshot snapshot:datasnapshot.getChildren() ){
                    if(Objects.equals(snapshot.getValue(), "trackable")) {

                           GlobalInfo.totrack.put(snapshot.getKey(), snapshot.getValue().toString());

                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        databaseRefrence.child("Users").child(GlobalInfo.phonenumber).child("MyTrackers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                GlobalInfo.myTrackers.clear();
                for(DataSnapshot snapshot:datasnapshot.getChildren() ){
                    if(Objects.equals(snapshot.getValue(), "allowtotrack")) {

                            GlobalInfo.myTrackers.put(snapshot.getKey(), snapshot.getValue().toString());

                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
   Refresh();
    }

    // ( for list of people whom we can track
    public static class to_track {
        //----------------< fritzbox_Contacts() >----------------
        public String username = "";

        public String phoneNr = "";
        //---------------</ fritzbox_Contacts() >----------------
    }

    public void getting_to_track_list() {
        explaner.setVisibility(View.VISIBLE);
        explaner.setText("People Whom You Can Track");
        totracklist.setVisibility(View.VISIBLE);
        Refresh();
        Adapterfortoallowtrack Adapter = new Adapterfortoallowtrack(this, Totracklistarray);
        totracklist.setAdapter(Adapter);
        totracklist.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, final View view, final int i, long l) {

                final Animation animdel = AnimationUtils.loadAnimation(MapsActivity.this, R.anim.shake);
                animdel.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        GlobalInfo.presentlytotrack = Adapterfortoallowtrack.to_tracklist.get(i).phoneNr;
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                        letstrack();
                        cross.setVisibility(View.VISIBLE);
                        cross.setRotation((float) 45.0);
                        letstrackbutton.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                view.startAnimation(animdel);

            }
        });
    }

    public void letstrack() {
        final DatabaseReference mdatabase;
        mdatabase = FirebaseDatabase.getInstance().getReference();
        mdatabase.child("Users").child(GlobalInfo.phonenumber).child("letstrack").addValueEventListener(new ValueEventListener() {
            @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(GlobalInfo.presentlytotrack).getValue().toString().equals("trackable")) {
                    mdatabase.child("Users").child(GlobalInfo.phonenumber).child("status").child("presently").setValue("tracking");

                    // tracking(0,GlobalInfo.presentlytotrack);
                    totracklist.setVisibility(View.INVISIBLE);
                    Toast.makeText(MapsActivity.this, "starting live tracking", Toast.LENGTH_LONG).show();
                } else {
                    mdatabase.child("Users").child(GlobalInfo.phonenumber).child("letstrack").child(GlobalInfo.presentlytotrack).setValue(null);
                    // tracking(1,GlobalInfo.presentlytotrack);
cross.setVisibility(View.INVISIBLE);
cross.setRotation((float) 0.0);
                    Toast.makeText(MapsActivity.this, "live tracking got dissmissed,may be user denied your request for tracking", Toast.LENGTH_LONG).show();
                    minusbutton.setVisibility(View.INVISIBLE);
                    sharebutton.setVisibility(View.INVISIBLE);
                    ContactList.setVisibility(View.INVISIBLE);
                    Trackerslist.setVisibility(View.INVISIBLE);
                    letstrackbutton.setVisibility(View.INVISIBLE);
                    addbutton.setVisibility(View.VISIBLE);
                    letstrackbutton.setVisibility(View.VISIBLE);
                    cross.setVisibility(View.INVISIBLE);
                    cross.setRotation((float) 0.0);
                    addbutton.setRotation((float) 0.0);

                    totrack.remove();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    public static class Adapterfortoallowtrack extends BaseAdapter {
        static ArrayList<to_track> to_tracklist;
        // public ArrayList<AdaptersItem> listnewsDataAdpater ;
        Context mContext;

        public Adapterfortoallowtrack(Context mContext, ArrayList<to_track> listnewsDataAdpater) {
            this.mContext = mContext;
            this.to_tracklist = listnewsDataAdpater;
        }


        @Override
        public int getCount() {
            return to_tracklist.size();
        }

        @Override
        public Object getItem(int position) {
            return to_tracklist.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }


        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.contactlist_ticket, parent, false);
            }
            to_track k = to_tracklist.get(position);
            if (phonenumberlist.contains(k.phoneNr)) {
                for (int i = 0; i < Adapter_for_Android_Contacts.mList_Android_Contacts.size(); i++) {
                    if (Adapter_for_Android_Contacts.mList_Android_Contacts.get(i).android_contact_TelefonNr.equals(k.phoneNr)) {
                        //< get controls >
                        TextView textview_contact_Name = (TextView) convertView.findViewById(R.id.contactname);
                        TextView textview_contact_TelefonNr = (TextView) convertView.findViewById(R.id.phonenumber);
//</ get controls >

//< show values >

                            textview_contact_Name.setText(Adapter_for_Android_Contacts.mList_Android_Contacts.get(i).android_contact_Name);
                            textview_contact_TelefonNr.setText(k.phoneNr);



                        break;
//</ show values >
                    }
                }
            } else {
                TextView textview_contact_Name = (TextView) convertView.findViewById(R.id.contactname);
                TextView textview_contact_TelefonNr = (TextView) convertView.findViewById(R.id.phonenumber);
//</ get controls >

//< show values >
                if(!k.username.equals("UserName")) {
                    textview_contact_Name.setText(k.username);
                    textview_contact_TelefonNr.setText(k.phoneNr);
                }    }


            //  (adding animation in listview
            // Animation animation = AnimationUtils.loadAnimation(mContext,R.anim.fade_in);
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.slide_up);
            //   Animation animation = AnimationUtils.loadAnimation(mContext,R.anim.shake);
            //   Animation animation = AnimationUtils.loadAnimation(mContext,R.anim.slide_left);
            //  Animation animation = AnimationUtils.loadAnimation(mContext,R.anim.slide_up);
            convertView.startAnimation(animation);
            convertView.setTag(k.username);
            return convertView;
        }

    }

    // for list of people whom we can track )
    public void share(View view) {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plains");
        String sharebody = "Family GPS Locator App is one of the finest app for live tracking based on GPS location tracking service.you can allow peoples to track your " +
                "location and you can track peoples whom allowed you to track them using this app.this will provide the lastknown location with high accuracy.you can " +
                "update the trackersList with the server at any time.Auto Location Update Service will automatically update your location even when you are " +
               "not interacting with your app.always shows a notification in the notification panel,which indicates you that the Background  services are " +
                "running or not so you can aware about the location updates.automatic restart of backgrounnd services after every boot. " +
                "It is completely ads free App.consist of an attractive UI and beautiful animations , and you will become familier with it in couple of seconds.Automatically delete your\n" +
                "account whenever you delete the app. so no one can keep your location records.\n"+
                "https://play.google.com/store/apps/details?id=com.vaishnavas.familygpslocator";
        String sharesubject = "Family GPS Locator";
        sharingIntent.putExtra(Intent.EXTRA_TEXT, sharebody);
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, sharesubject);
        startActivity(Intent.createChooser(sharingIntent, "Share Using"));
    }

    public void aboutus(View view) {
        Intent intent = new Intent(MapsActivity.this,about_us.class);
        startActivity(intent);
    }


    public void trackerlist(View view) {
        ContactList.setVisibility(View.INVISIBLE);
        x = 0;
        addbutton.setRotation((float) 0.0);
        if (Trackerslistarray != null) {
            getting_trackers_list();
        } else {
            Toast.makeText(MapsActivity.this, "no trackers found", Toast.LENGTH_LONG).show();
        }
    }

    public void letstrack(View view) {

        Refresh();
           minusbutton.setVisibility(View.INVISIBLE);
           sharebutton.setVisibility(View.INVISIBLE);
           ContactList.setVisibility(View.INVISIBLE);
           Trackerslist.setVisibility(View.INVISIBLE);
           addbutton.setVisibility(View.INVISIBLE);
           cross.setVisibility(View.VISIBLE);
             cross.setRotation((float) 45.0);
           x = 0;
           addbutton.setRotation((float) 0.0);
           if (Totracklistarray != null) {
               getting_to_track_list();
           } else {
               Toast.makeText(MapsActivity.this, "no people found to whom you can track", Toast.LENGTH_LONG).show();
           }

    }
public void close(View view){
    minusbutton.setVisibility(View.INVISIBLE);
    sharebutton.setVisibility(View.INVISIBLE);
    ContactList.setVisibility(View.INVISIBLE);
 totracklist.setVisibility(View.INVISIBLE);
   Trackerslist.setVisibility(View.INVISIBLE);
    letstrackbutton.setVisibility(View.INVISIBLE);
    addbutton.setVisibility(View.VISIBLE);
    letstrackbutton.setVisibility(View.VISIBLE);
    cross.setVisibility(View.INVISIBLE);
    cross.setRotation((float) 0.0);
    addbutton.setRotation((float) 0.0);
if(totrack!=null) {
    totrack.remove();
}

}

    // (list of contacts
    //display news list
    public static class MyCustomAdapter extends BaseAdapter {
        static ArrayList<Trackers_list> trackerslist;
        // public ArrayList<AdaptersItem> listnewsDataAdpater ;
        Context mContext;

        public MyCustomAdapter(Context mContext, ArrayList<Trackers_list> listnewsDataAdpater) {
            this.mContext = mContext;
            this.trackerslist = listnewsDataAdpater;
        }


        @Override
        public int getCount() {
            return trackerslist.size();
        }

        @Override
        public Object getItem(int position) {
            return trackerslist.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }


        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.contactlist_ticket, parent, false);
            }
            Trackers_list s = trackerslist.get(position);
            //< get controls >
            TextView textview_contact_Name = (TextView) convertView.findViewById(R.id.contactname);
            TextView textview_contact_TelefonNr = (TextView) convertView.findViewById(R.id.phonenumber);
//</ get controls >

//< show values >
if(s.username.equals("allowtotrack")){
    if (phonenumberlist.contains(s.phoneNr)) {
        for (int i = 0; i < Adapter_for_Android_Contacts.mList_Android_Contacts.size(); i++) {
            if (Adapter_for_Android_Contacts.mList_Android_Contacts.get(i).android_contact_TelefonNr.equals(s.phoneNr)) {
                //< get controls >

//</ get controls >

//< show values >

                textview_contact_Name.setText(Adapter_for_Android_Contacts.mList_Android_Contacts.get(i).android_contact_Name);
                textview_contact_TelefonNr.setText(s.phoneNr);



                break;
//</ show values >
            }
        }
    }else{
        textview_contact_Name.setText(s.username);
        textview_contact_TelefonNr.setText(s.phoneNr);
    }
}
           else{ textview_contact_Name.setText(s.username);
            textview_contact_TelefonNr.setText(s.phoneNr);}
//</ show values >
            //  (adding animation in listview
            // Animation animation = AnimationUtils.loadAnimation(mContext,R.anim.fade_in);
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.scale);
            //   Animation animation = AnimationUtils.loadAnimation(mContext,R.anim.shake);
            //   Animation animation = AnimationUtils.loadAnimation(mContext,R.anim.slide_left);
            //  Animation animation = AnimationUtils.loadAnimation(mContext,R.anim.slide_up);
            convertView.startAnimation(animation);
            convertView.setTag(s.username);
            return convertView;
        }

    }

    // ( getting trackers list
    public void getting_trackers_list() {
       explaner.setVisibility(View.VISIBLE);
       explaner.setText("Peoples allowed to track you");
        Trackerslist.setVisibility(View.VISIBLE);
        Refresh();
        final MyCustomAdapter Adapter = new MyCustomAdapter(this, Trackerslistarray);
        Trackerslist.setAdapter(Adapter);
        Trackerslist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, final View view, final int i, long l) {
                final Animation animdel = AnimationUtils.loadAnimation(MapsActivity.this, R.anim.shake);
                animdel.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        DatabaseReference mdatabase = FirebaseDatabase.getInstance().getReference();
                        mdatabase.child("Users").child(GlobalInfo.phonenumber).child("MyTrackers").child(MyCustomAdapter.trackerslist.get(i).phoneNr).setValue(null);
                        mdatabase.child("Users").child(MyCustomAdapter.trackerslist.get(i).phoneNr).child("letstrack").child(GlobalInfo.phonenumber).setValue("notallowtotrack");


                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        view.setAlpha((float) 0.0);
                        GlobalInfo.myTrackers.remove(MyCustomAdapter.trackerslist.get(i).phoneNr);
                        GlobalInfo globalInfo = new GlobalInfo(MapsActivity.this);
                        globalInfo.SaveData();
                        Trackerslist.setEnabled(false);
                        Refresh();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                view.startAnimation(animdel);


                //  Adapter.notifyDataSetChanged();
                //   Adapter.notifyDataSetInvalidated();


            }
        });
    }

    // getting trackers list )
    // ( yaha hum contact list ko access krenge




    public static class Trackers_list {
        //----------------< fritzbox_Contacts() >----------------
        public String username = "";

        public String phoneNr = "";
        //---------------</ fritzbox_Contacts() >----------------
    }

    // yaha hum contact list ko access krenge )
    public void Refresh() {
        GlobalInfo globalInfo = new GlobalInfo(this);
        globalInfo.SaveData();
        Trackerslistarray.clear();
        Totracklistarray.clear();
        GlobalInfo.TrackersMobileNumbers.clear();

        for (Map.Entry m : GlobalInfo.myTrackers.entrySet()) {
            Trackers_list trackers_list = new Trackers_list();
            trackers_list.phoneNr = m.getKey().toString();
            trackers_list.username = m.getValue().toString();
            Trackerslistarray.add(trackers_list);
            GlobalInfo.TrackersMobileNumbers.add(m.getKey().toString());
            // mein isis form mein show hoga.

        }
        for (Map.Entry m : GlobalInfo.totrack.entrySet()) {
            to_track totracklist = new to_track();
            totracklist.phoneNr = m.getKey().toString();
            totracklist.username = m.getValue().toString();
            Totracklistarray.add(totracklist);

            // mein isis form mein show hoga.

        }
//myadapter.notifyDataSetChanged();

        Trackerslist.setEnabled(true);
    }

    // asking for permissions
    //access to permsions
    void CheckUserPermsions() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) !=
                    PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{
                                Manifest.permission.READ_CONTACTS},
                        REQUEST_CODE_ASK_PERMISSIONS);
                return;
            }
        }
        fp_get_Android_Contacts();
        //   Contactlist(); // init the contact list

    }

    //get acces to location permsion
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_ASK_PERMISSIONS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //   Contactlist();
                fp_get_Android_Contacts();
            } else {
                // Permission Denied
                Toast.makeText(this, "your message", Toast.LENGTH_SHORT)
                        .show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    //=============================< Functions: Android.Contacts >=============================
    public static class Android_Contact {
        //----------------< fritzbox_Contacts() >----------------
        public String android_contact_Name = "";
        public String android_contact_TelefonNr = "";
        //---------------</ fritzbox_Contacts() >----------------
    }

    public void fp_get_Android_Contacts() {

//----------------< fp_get_Android_Contacts() >----------------
        final ArrayList<Android_Contact> arrayList_Android_Contacts = new ArrayList<>();

//--< get all Contacts >--
        Cursor cursor_Android_Contacts = null;
        ContentResolver contentResolver = getContentResolver();
        try {
            cursor_Android_Contacts = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        } catch (Exception ex) {
            Log.e("Error on contact", Objects.requireNonNull(ex.getMessage()));
        }
//--</ get all Contacts >--

//----< Check: hasContacts >----
        assert cursor_Android_Contacts != null;
        if (cursor_Android_Contacts.getCount() > 0) {
//----< has Contacts >----
//----< @Loop: all Contacts >----
            while (cursor_Android_Contacts.moveToNext()) {
//< init >
                Android_Contact android_contact = new Android_Contact();
                String contact_id = cursor_Android_Contacts.getString(cursor_Android_Contacts.getColumnIndex(ContactsContract.Contacts._ID));
                String contact_display_name = cursor_Android_Contacts.getString(cursor_Android_Contacts.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
//</ init >

//----< set >----
                android_contact.android_contact_Name = contact_display_name;


//----< get phone number >---
                int hasPhoneNumber = Integer.parseInt(cursor_Android_Contacts.getString(cursor_Android_Contacts.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)));
                if (hasPhoneNumber > 0) {

                    Cursor phoneCursor = contentResolver.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI
                            , null
                            , ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?"
                            , new String[]{contact_id}
                            , null);

                    assert phoneCursor != null;
                    while (phoneCursor.moveToNext()) {
                        String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
//< set >
                        android_contact.android_contact_TelefonNr = phoneNumber;
//</ set >
                    }
                    phoneCursor.close();
                }
//----</ set >----
//----</ get phone number >----

// Add the contact to the ArrayList
                arrayList_Android_Contacts.add(android_contact);
            }
//----</ @Loop: all Contacts >----

//< show results >

            Adapter_for_Android_Contacts Adapter = new Adapter_for_Android_Contacts(this, arrayList_Android_Contacts);
            ContactList.setAdapter(Adapter);
//</ show results >

            ContactList.setOnItemClickListener(new AdapterView.OnItemClickListener() {


                Context mContext;

                @Override
                public void onItemClick(AdapterView<?> adapterView, final View view, final int position, long l) {
                    //   PickContact();
                    final int p = position;
                    final String phonenumber1 = Adapter_for_Android_Contacts.mList_Android_Contacts.get(position).android_contact_TelefonNr;
                    if (phonenumber1.length() < 10 & phonenumber1.length() > 14) {
                        Toast.makeText(MapsActivity.this, "select contact is not valid", Toast.LENGTH_LONG).show();
                    } else if (phonenumber1.length() == 10) {
                        LayoutInflater inflater = getLayoutInflater();
                        final View viewcountrycode = inflater.inflate(R.layout.countrycodemissing, null);
                        EditText Phone = (EditText) viewcountrycode.findViewById(R.id.phonenumber1);
                        Button submit = (Button) viewcountrycode.findViewById(R.id.updatetrackerlist);
                        Phone.setText(phonenumber1);
                        final Spinner spinner = (Spinner) viewcountrycode.findViewById(R.id.spinnercontriescode);
                        spinner.setAdapter(new ArrayAdapter<String>(MapsActivity.this, android.R.layout.simple_spinner_dropdown_item, CountryData.countryNames));


                        submit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(final View view) {
                                String countrycode = CountryData.countryAreaCodes[spinner.getSelectedItemPosition()];
                                String phonenumberf = "+" + countrycode + phonenumber1;
                                if (phonenumberf.length() > 10) {
                                    DatabaseReference mdatabase = FirebaseDatabase.getInstance().getReference();
                                    mdatabase.child("Users").child(GlobalInfo.phonenumber).child("MyTrackers").child(phonenumberf).setValue("allowtotrack");
                                    savedata(phonenumberf, Adapter_for_Android_Contacts.mList_Android_Contacts.get(p).android_contact_Name);
                                    Adapter_for_Android_Contacts.mList_Android_Contacts.get(position).android_contact_TelefonNr = phonenumberf;
                                    Refresh();
                                    //  Adapter_for_Android_Contacts.mList_Android_Contacts.remove(p);
                                    mdatabase.child("Users").child(phonenumberf).child("letstrack").child(GlobalInfo.phonenumber).setValue("trackable");
                                    final Animation animdel = AnimationUtils.loadAnimation(MapsActivity.this, R.anim.shake);
                                    animdel.setAnimationListener(new Animation.AnimationListener() {
                                        @Override
                                        public void onAnimationStart(Animation animation) {

                                        }

                                        @Override
                                        public void onAnimationEnd(Animation animation) {
                                            view.setAlpha((float) 0.0);
                                        }

                                        @Override
                                        public void onAnimationRepeat(Animation animation) {

                                        }
                                    });
                                    view.startAnimation(animdel);

                                }
                            }


                        });

                        AlertDialog alertDialog = new AlertDialog.Builder(MapsActivity.this).setView(viewcountrycode).create();


                        alertDialog.show();


                    } else if (phonenumber1.length() == 12 || phonenumber1.length() == 13 || phonenumber1.length() == 14) {
                        DatabaseReference mdatabase = FirebaseDatabase.getInstance().getReference();
                        mdatabase.child("Users").child(GlobalInfo.phonenumber).child("MyTrackers").child(phonenumber1).setValue("allowtotrack");
                        // (  puting value of phone number and coressponding contact name in globalfile
                        savedata(Adapter_for_Android_Contacts.mList_Android_Contacts.get(position).android_contact_TelefonNr, Adapter_for_Android_Contacts.mList_Android_Contacts.get(position).android_contact_Name);
                        Refresh();

                        // adapterForAndroidContacts.notifyDataSetChanged();
                        mdatabase.child("Users").child(phonenumber1).child("letstrack").child(GlobalInfo.phonenumber).setValue("trackable");

                        final Animation animdel = AnimationUtils.loadAnimation(MapsActivity.this, R.anim.shake);
                        animdel.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                view.setAlpha((float) 0.0);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                        view.startAnimation(animdel);


                        //  puting value of phone number and coressponding contact name in globalfile )
                        // saving the data )
                    } else {
                        Toast.makeText(MapsActivity.this, "select contact is not valid", Toast.LENGTH_LONG).show();
                    }


                }


            });
        }
//----</ Check: hasContacts >----

// ----------------</ fp_get_Android_Contacts() >--------------

    }

    public void savedata(String number, String name) {

        GlobalInfo.myTrackers.put(number, name);
        GlobalInfo.TrackersMobileNumbers.add(number);
        GlobalInfo globalInfo = new GlobalInfo(this);

        globalInfo.SaveData();
        //   adapterForAndroidContacts.notifyDataSetChanged();
    }

    public static class Adapter_for_Android_Contacts extends BaseAdapter {

        //---------------< Adapter_for_Android_Contacts() >---------------
//< Variables >
        Context mContext;
        static List<Android_Contact> mList_Android_Contacts;
//</ Variables >

        //< constructor with ListArray >
        public Adapter_for_Android_Contacts(Context mContext, List<Android_Contact> mContact) {
            this.mContext = mContext;
            this.mList_Android_Contacts = mContact;
        }
//</ constructor with ListArray >

        @Override
        public int getCount() {
            return mList_Android_Contacts.size();
        }

        @Override
        public Object getItem(int position) {
            return mList_Android_Contacts.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        //----< show items >----
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.contactlist_ticket, parent, false);
            }
            if (mList_Android_Contacts.get(position).android_contact_TelefonNr.length() < 10 || mList_Android_Contacts.get(position).android_contact_TelefonNr.length() > 14) {
                //    @SuppressLint("ViewHolder") View view=View.inflate(mContext,R.layout.contactlist_ticket,null);
//< get controls >
                TextView textview_contact_Name = (TextView) convertView.findViewById(R.id.contactname);
                TextView textview_contact_TelefonNr = (TextView) convertView.findViewById(R.id.phonenumber);
//</ get controls >

//< show values >
                textview_contact_Name.setText(mList_Android_Contacts.get(position).android_contact_Name);
                textview_contact_TelefonNr.setText(mList_Android_Contacts.get(position).android_contact_TelefonNr);
//</ show values >
            } else if (mList_Android_Contacts.get(position).android_contact_TelefonNr.length() == 10) {


                mList_Android_Contacts.get(position).android_contact_TelefonNr = "+" + GlobalInfo.countrycode + mList_Android_Contacts.get(position).android_contact_TelefonNr;

                DatabaseReference mdatabase = FirebaseDatabase.getInstance().getReference();
                final View finalConvertView = convertView;
                if (GlobalInfo.TrackersMobileNumbers.contains(mList_Android_Contacts.get(position).android_contact_TelefonNr.trim())) {

                } else {
                    //< get controls >
                    TextView textview_contact_Name = (TextView) finalConvertView.findViewById(R.id.contactname);
                    TextView textview_contact_TelefonNr = (TextView) finalConvertView.findViewById(R.id.phonenumber);
//</ get controls >

//< show values >
                    phonenumberlist.add(mList_Android_Contacts.get(position).android_contact_TelefonNr);
                    textview_contact_Name.setText(mList_Android_Contacts.get(position).android_contact_Name);
                    textview_contact_TelefonNr.setText(mList_Android_Contacts.get(position).android_contact_TelefonNr);
//</ show values >
                }


            } else if (mList_Android_Contacts.get(position).android_contact_TelefonNr.length() == 12 || mList_Android_Contacts.get(position).android_contact_TelefonNr.length() == 13 || mList_Android_Contacts.get(position).android_contact_TelefonNr.length() == 14) {
                DatabaseReference mdatabase = FirebaseDatabase.getInstance().getReference();
                final View finalConvertView = convertView;
                if (GlobalInfo.TrackersMobileNumbers.contains(mList_Android_Contacts.get(position).android_contact_TelefonNr.trim())) {

                } else {
                    //< get controls >
                    TextView textview_contact_Name = (TextView) finalConvertView.findViewById(R.id.contactname);
                    TextView textview_contact_TelefonNr = (TextView) finalConvertView.findViewById(R.id.phonenumber);
//</ get controls >

//< show values >
                    phonenumberlist.add(mList_Android_Contacts.get(position).android_contact_TelefonNr);
                    textview_contact_Name.setText(mList_Android_Contacts.get(position).android_contact_Name);
                    textview_contact_TelefonNr.setText(mList_Android_Contacts.get(position).android_contact_TelefonNr);
//</ show values >
                }

            } else {
                //< get controls >
                TextView textview_contact_Name = (TextView) convertView.findViewById(R.id.contactname);
                TextView textview_contact_TelefonNr = (TextView) convertView.findViewById(R.id.phonenumber);
//</ get controls >

//< show values >
                textview_contact_Name.setText(mList_Android_Contacts.get(position).android_contact_Name);
                textview_contact_TelefonNr.setText(mList_Android_Contacts.get(position).android_contact_TelefonNr);
//</ show values >
            }


            //  (adding animation in listview
            // Animation animation = AnimationUtils.loadAnimation(mContext,R.anim.fade_in);
            //   Animation animation = AnimationUtils.loadAnimation(mContext,R.anim.scale);
            //   Animation animation = AnimationUtils.loadAnimation(mContext,R.anim.shake);
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.slide_left);
            //  Animation animation = AnimationUtils.loadAnimation(mContext,R.anim.slide_up);
            convertView.startAnimation(animation);
            //  adding animation in listview )
            convertView.setTag(mList_Android_Contacts.get(position).android_contact_Name);
            return convertView;

        }

//----</ show items >----
//----------------</ Adapter_for_Android_Contacts() >---------------


    }
//=============================</ Functions: Android.Contacts >============================
// ( this is the adapter jisse jaise hi list ke  kisi item par click krenge toh uska data firebase mein mytracker list mein store hoga

    // ( this is the adapter jisse jaise hi list ke  kisi item par click krenge toh uska data firebase mein mytracker list mein store hoga
    }