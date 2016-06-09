package com.smartbell;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {

    private final static String TAG = "BootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.e("chenbo", TAG + " ACTION = " + action);
//		if (action.equals("android.intent.action.BOOT_COMPLETED")) {
        Intent startIntent = new Intent(context, DataService.class);
        context.startService(startIntent);
//		}
    }

}
