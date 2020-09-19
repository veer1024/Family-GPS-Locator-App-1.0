package com.vaishnavas.familygpslocator;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GlobalInfo{
    public static final String RESTART_INTENT = "com.vaishnavas.familygpslocator";
    public static String phonenumber = "";
    public static String countrycode = "";
    public static Map<String,String> myTrackers = new HashMap<>();
    public static Map<String,String> totrack = new HashMap<>();
    public static String presentlytotrack = "" ;
    public static String presentlytotracklatitude = "";
  public static String presentlytotracklongitude = "";
    public static String lastupdatetotrack = "";
    public static final String myprefrences = "myRef";
    public static final String MyTrackers = "MyTrackers";
    public static final String ToTrack = "ToTrack";
    public static final String PhoneNumber = "PhoneNumber";
    public static final String Countrycode = "Countrycode";
    public static List TrackersMobileNumbers = new ArrayList();
    public static void updatesInfo(String userphone){
        DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:MM:ss");
        Date date = new Date();
       DatabaseReference mdatabase = FirebaseDatabase.getInstance().getReference();
        mdatabase.child("Users").child(userphone).child("Updates").setValue(df.format(date).toString());

    }

    //format phone number mtlb 10 digit number bana deta hai

    public static String FormatPhoneNumber(String Oldnmber){

        //for filtering number to 10 digit number
        try{
            String numberOnly= Oldnmber.replaceAll("[^0-9]", "");
            if(Oldnmber.charAt(0)=='+') numberOnly="+" +numberOnly ;
            if (numberOnly.length()>=10)
                numberOnly=numberOnly.substring(numberOnly.length()-10,numberOnly.length());
            return(numberOnly);
        }
        catch (Exception ex){
            return(" ");
        } // for filtering number to 10 digit number

    }

    // (niche vala data app data ko hamesha ke liye app mein store krne ke liye hai
    Context context;
    SharedPreferences ShredRef;
    public  GlobalInfo(Context context){
        this.context=context;
        ShredRef=context.getSharedPreferences(myprefrences,Context.MODE_PRIVATE);
    }

    void SaveData(){
        String totracklist = "";
        String MyTrackersList="" ;
        for (Map.Entry  m:GlobalInfo.myTrackers.entrySet()){
            if (MyTrackersList.length()==0)
                MyTrackersList=m.getKey() + "%" + m.getValue();
            else
                MyTrackersList =MyTrackersList+ "%" + m.getKey() + "%" + m.getValue();

        }
        for (Map.Entry  m:GlobalInfo.totrack.entrySet()){
            if (totracklist.length()==0)
                totracklist=m.getKey() + "%" + m.getValue();
            else
                totracklist =totracklist+ "%" + m.getKey() + "%" + m.getValue();

        }

        if (MyTrackersList.length()==0)
            MyTrackersList="empty";
        if(totracklist.length()==0)
        {
            totracklist="empty";
        }

        SharedPreferences.Editor editor=ShredRef.edit();
        editor.putString(MyTrackers,MyTrackersList);
        editor.putString(PhoneNumber,phonenumber);
        editor.putString(ToTrack,totracklist);
        editor.putString(Countrycode,countrycode);
        editor.commit();
    }

    void LoadData(){
        myTrackers.clear();
        TrackersMobileNumbers.clear();
        totrack.clear();
        phonenumber= ShredRef.getString(PhoneNumber,"empty");
        countrycode=ShredRef.getString(Countrycode,"Countrycode");
        String MyTrackersList= ShredRef.getString(MyTrackers,"empty");
        String totracklist = ShredRef.getString(ToTrack,"empty");
        if (!MyTrackersList.equals("empty")){
            String[] users=MyTrackersList.split("%");
            for (int i=0;i<users.length;i=i+2){
                myTrackers.put(users[i],users[i+1]);
                TrackersMobileNumbers.add(users[i]);
            }
        }
        if (!totracklist.equals("empty")){
            String[] people=totracklist.split("%");
            for (int i=0;i<people.length;i=i+2){
                totrack.put(people[i],people[i+1]);

            }
        }

        if (phonenumber.equals("empty")){
// app kisi java class se koi activity start nahi kr sakte and agar krna chatein hai toh appko context use krna padega
            Intent intent=new Intent(context, loginactivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
        else{
            DatabaseReference database;
            database = FirebaseDatabase.getInstance().getReference();
            database.child("Users").child(GlobalInfo.phonenumber).child("status").child("Mapping").setValue("notrunnig");
            database.child("Users").child(GlobalInfo.phonenumber).child("status").child("normally").setValue("nottracking");
            database.child("Users").child(GlobalInfo.phonenumber).child("status").child("presently").setValue(null);
            Intent intent=new Intent(context, loading.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }
// upar vala data app data ko hamesha ke liye app mein store krne ke liye hai )
}
