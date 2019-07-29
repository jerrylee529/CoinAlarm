package com.unlimited.coinalarm.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.unlimited.coinalarm.okex.OkExQuotationService;

public class AlarmReceiver extends BroadcastReceiver {
    private static final String TAG = "AlarmReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "receive broadcast " + intent.getAction());
        Intent intentOne = new Intent(context, OkExQuotationService.class);
        context.startService(intentOne);
    }
}
