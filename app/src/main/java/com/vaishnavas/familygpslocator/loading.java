package com.vaishnavas.familygpslocator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class loading extends AppCompatActivity {
    private EditText editTextInput;
    ExampleService servicebg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        editTextInput = findViewById(R.id.edit_text_input);
    checking();
    }

    @SuppressLint("BatteryLife")
    public void startService(View v) {
        JobScheduler jobscheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        JobInfo jobinfo = new JobInfo.Builder(11,new ComponentName(this,Jobservice.class))
                .setPersisted(true)
                .setPeriodic(15*60*1000)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY).build();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent();
            String packagename = getPackageName();
            PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
            if (!pm.isIgnoringBatteryOptimizations(packagename)) {
                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + packagename));
                startActivity(intent);
            }
                servicebg = new ExampleService();


                Intent serviceIntent = new Intent(this, ExampleService.class);

                if(!isMyServiceRunning(servicebg.getClass())){

                 ContextCompat.startForegroundService(this, serviceIntent);

                    if(isjobservicerunning(this)){
                        Log.i("jobservice","job is running already");
                        goingtomap();
                    }else
                    { jobscheduler.schedule(jobinfo);
                        goingtomap();}


                }else {
                    if(isjobservicerunning(this)){
                        Log.i("jobservice","job is running already");
                        goingtomap();
                    }else
                    { jobscheduler.schedule(jobinfo);
                        goingtomap();}

                }

        }
        else{
            servicebg = new ExampleService();


            Intent serviceIntent = new Intent(this, ExampleService.class);

            if(!isMyServiceRunning(servicebg.getClass())){
 
                ContextCompat.startForegroundService(this, serviceIntent);
                goingtomap();

            }else {

                goingtomap();}

        }

    }
    public void stopService (View v){
        Intent serviceIntent = new Intent(this, ExampleService.class);
        stopService(serviceIntent);
    }
    private boolean isMyServiceRunning(Class<?> serviceClass){
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for(ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)){
            if(serviceClass.getName().equals(service.service.getClassName())){
                Log.i("Service Status","Running");
                return true;
           }
        }
        Log.i("Service Status","notRunning");
        return false;
    }

public static boolean isjobservicerunning(Context context){
    JobScheduler scheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
    boolean jobisrunning = false;
    for(JobInfo jobinfo: scheduler.getAllPendingJobs()){
        if(jobinfo.getId()==11){
            jobisrunning = true;
            break;
        }else{
            scheduler.cancel(jobinfo.getId());
        }
    }
    return jobisrunning;
}
    @Override
    protected void onDestroy() {
      Log.i("loadingactivity","swipeout");
        super.onDestroy();
    }
    @SuppressLint("BatteryLife")
    private void checking(){
        JobScheduler jobscheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        JobInfo jobinfo = new JobInfo.Builder(11,new ComponentName(this,Jobservice.class))
                .setPersisted(true)
                .setPeriodic(15*60*1000)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY).build();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent();
            String packagename = getPackageName();
            PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
            if (!pm.isIgnoringBatteryOptimizations(packagename)) {
                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + packagename));
               startActivity(intent);
            }else{
                Toast.makeText(loading.this,"app is ignoring battery optimization",Toast.LENGTH_LONG).show();
            }
            servicebg = new ExampleService();


            Intent serviceIntent = new Intent(this, ExampleService.class);

            if(!isMyServiceRunning(servicebg.getClass())){

                ContextCompat.startForegroundService(this, serviceIntent);
                if(isjobservicerunning(this)){
                    Log.i("jobservice","job is running already");
                    goingtomap();
                }else
                { jobscheduler.schedule(jobinfo);
                    goingtomap();}

            }else {
                if(isjobservicerunning(this)){
                    Log.i("jobservice","job is running already");
                    goingtomap();
                }else
                { jobscheduler.schedule(jobinfo);
                    goingtomap();}

            }

        }
        else{
            servicebg = new ExampleService();


            Intent serviceIntent = new Intent(this, ExampleService.class);

            if(!isMyServiceRunning(servicebg.getClass())){

                ContextCompat.startForegroundService(this, serviceIntent);
                   goingtomap();

            }else {

                    goingtomap();}

            }




    }
    private void goingtomap(){
        Intent mapintent = new Intent(this,MapsActivity.class);
       startActivity(mapintent);
       finish();
    }
}