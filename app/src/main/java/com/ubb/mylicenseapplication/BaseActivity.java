package com.ubb.mylicenseapplication;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.ubb.mylicenseapplication.connectivity.ConnectivityRecevier;

public class BaseActivity extends AppCompatActivity {

    final IntentFilter intentFilter = new IntentFilter();
    private ConnectivityRecevier connectivityRecevier;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ConnectivityRecevier connectivityRecevier = new ConnectivityRecevier();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(connectivityRecevier,intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
