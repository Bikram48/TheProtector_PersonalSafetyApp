package com.example.theprotector;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {

    private static final String TAG ="BootReceiver" ;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: Hellllllo working!!!");
        context.startService(new Intent(context,PowerButtonService.class));
    }
}
