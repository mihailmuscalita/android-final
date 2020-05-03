package com.ubb.mylicenseapplication.Services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.ubb.mylicenseapplication.Api.ApiRequests;
import com.ubb.mylicenseapplication.GoogleService;
import com.ubb.mylicenseapplication.MainActivity;

import java.util.Timer;
import java.util.TimerTask;

import static com.ubb.mylicenseapplication.MainFragment.EXTRA_Competition;


public class StepService extends Service {

    private MainActivity context;
    private Timer timer;
    private TimerTask timerTask;
    private GoogleService googleService;
    private String competitionModel;
    private final IBinder mBinder = new LocalBinder();


    public StepService(){}

    public StepService(Context context) {
        this.context = (MainActivity) context;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        String competition = intent.getStringExtra(EXTRA_Competition);
        this.competitionModel = competition;
        googleService = new GoogleService(getApplicationContext());
        this.googleService.connectAccount();
        startTimer();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("EXIT", "ondestroy!");
//        Intent broadcastIntent = new Intent(this, MyBroadCastRecevier.class);
//
//        sendBroadcast(broadcastIntent);
        stoptimertask();
        googleService.disconnect();
    }

    public void startTimer() {
        //set a new Timer
        timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask();

        //sendDataToActivity();
        //schedule the timer, to wake up every 15 second
        timer.schedule(timerTask, 30000, 30000); //
    }

    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            @Override
            public void run() {
                if (ApiRequests.getInstance().isNetworkInterfaceAvailable(getApplicationContext())) {
                    googleService.ExecuteTask();
                    try {
                        Thread.sleep(1500);
                        if (competitionModel != null) {
                            if (competitionModel.contains("Yes")){
                                ApiRequests.getInstance().updateStepsToServer(Integer.parseInt(googleService.getNumberOfSteps().toString()));
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else{
                    Log.i("NoCon", "no internet connection!!");
                }
            }
        };
    }

    public void stoptimertask() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class LocalBinder extends Binder {
        StepService getService() {
            return StepService.this;
        }
    }
}
