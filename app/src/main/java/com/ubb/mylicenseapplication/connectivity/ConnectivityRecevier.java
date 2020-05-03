package com.ubb.mylicenseapplication.connectivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.ubb.mylicenseapplication.Api.ApiRequests;

public class ConnectivityRecevier extends BroadcastReceiver {


    public ConnectivityRecevier(){}

    @Override
    public void onReceive(final Context context, final Intent intent) {

        if(isOnline(context)){
            System.out.println("Exista internet in aplicatie!");
            ApiRequests.isInternetConnection = true;
        }
        else {
            System.out.println("Nu mai exista internet in aplicatie!");
            ApiRequests.isInternetConnection = false;
        }

    }

    private boolean isOnline(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            return (netInfo != null && netInfo.isConnected());
        } catch (NullPointerException e) {
            e.printStackTrace();
            return false;
        }
    }
}
