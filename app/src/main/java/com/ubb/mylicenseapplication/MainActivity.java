package com.ubb.mylicenseapplication;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.navigation.NavController;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.OnDataPointListener;
import com.google.android.gms.fitness.result.DailyTotalResult;
import com.ubb.mylicenseapplication.Api.ApiRequests;
import com.ubb.mylicenseapplication.database.DatabaseManager;
import com.ubb.mylicenseapplication.sockets.SocketListener;


import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;


public class MainActivity extends BaseActivity implements View.OnClickListener {



    private WebSocket webSocket;
    private  ProgressDialog progressDialog;
    private Dialog internetConnection;
    private Button closeInformation;

    private GoogleService googleService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.progressDialog  = new ProgressDialog(MainActivity.this);
        this.internetConnection = new Dialog(this);
        this.internetConnection.setContentView(R.layout.no_network);
        this.closeInformation = this.internetConnection.findViewById(R.id.closeInternet);
        this.closeInformation.setOnClickListener(this);
        this.googleService = new GoogleService(this);
        connectSocket();
        }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.webSocket.cancel();
        this.googleService.disconnect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.googleService.connectAccount();
    }


    private void connectSocket(){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(ApiRequests.wsUrl).build();
        webSocket = client.newWebSocket(request,new SocketListener(getApplicationContext(),this));

    }

    public void showInformationAboutInternet(){
        this.internetConnection.show();
    }

    public void closeInformationAboutInternet(){
        this.internetConnection.dismiss();
        if (this.progressDialog.isShowing()){
            this.progressDialog.dismiss();
        }
    }

    public void showProgressBar(){
        this.progressDialog.show();
        this.progressDialog .setContentView(R.layout.progress_dialog);
        this.progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    public void closeProgressBar(){
        this.progressDialog.dismiss();
    }

    public void sendMessage(String message){
        this.webSocket.send(message);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.closeInternet:
                closeInformationAboutInternet();
                break;
        }
    }
}
