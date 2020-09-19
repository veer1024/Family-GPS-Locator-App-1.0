package com.vaishnavas.familygpslocator;

import android.app.ActivityManager;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

public class Jobservice extends JobService {
    private static final String TAG = "jobscheduler";
    private boolean jobCancelled = false;
    ExampleService servicebg;
    @Override
    public boolean onStartJob(JobParameters params) {
        backgroundtask(params);
        return true;
    }

    private void backgroundtask(final JobParameters params)
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                servicebg = new ExampleService();
                for (int i = 0; i < 4; i++) {

                    Intent serviceIntent = new Intent(getApplicationContext(), ExampleService.class);
                    if(!isMyServiceRunning(servicebg.getClass())){

                        ContextCompat.startForegroundService(getApplicationContext(), serviceIntent);

                    }
                    try {
                        Thread.sleep(2*60*1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Log.d(TAG, "Job finished");
                jobFinished(params, true);
            }
        }).start();
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
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
}