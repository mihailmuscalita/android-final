package com.ubb.mylicenseapplication.Receviers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.ubb.mylicenseapplication.Services.StepService;

public class MyBroadCastRecevier extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(MyBroadCastRecevier.class.getSimpleName(), "Service Stops! Oooooooooooooppppssssss!!!!");
        context.startService(new Intent(context, StepService.class));;
    }
}
