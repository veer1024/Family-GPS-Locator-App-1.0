package com.vaishnavas.familygpslocator;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

public class restarter extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("Brodcast listen","service gets stop");
        Log.i("Brodcast action","restarting the service");
        if(Build.VERSION.SDK_INT>=26) {
            context.startForegroundService((new Intent(context, ExampleService.class)));
            Log.i("service restart status","restart done");
        }  else{
            context.startService((new Intent(context, ExampleService.class)));
            Log.i("service restart status","restart done");

        }

    }
}
