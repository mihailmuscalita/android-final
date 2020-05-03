package com.ubb.mylicenseapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

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
import com.ubb.mylicenseapplication.database.DatabaseManager;

import java.util.concurrent.TimeUnit;

public class GoogleService {

    private static final String TAG = "FitActivity";
    private GoogleApiClient mClient = null;
    private OnDataPointListener mListener;
    boolean authInProgress;
    private Context context;
    private Long numberOfSteps;

    public GoogleService(Context context){
        System.out.println("S-a instantiat clasa google=!");
        this.context =  context;
    }

    public void connectAccount(){
        System.out.println("Sa vedem aici contextul="+this.context);
        mClient = new GoogleApiClient.Builder(this.context)
                .addApi(Fitness.HISTORY_API)
                .addApi(Fitness.CONFIG_API)
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ))
                .useDefaultAccount()
                .addConnectionCallbacks(
                        new GoogleApiClient.ConnectionCallbacks() {

                            @Override
                            public void onConnected(Bundle bundle) {
                                //Async To fetch steps
                                new FetchStepsAsync().execute();
                                Log.i(TAG, "Connection done!!!");
                            }

                            @Override
                            public void onConnectionSuspended(int i) {
                                // If your connection to the sensor gets lost at some point,
                                // you'll be able to determine the reason and react to it here.
                                if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_NETWORK_LOST) {
                                    Log.i(TAG, "Connection lost.  Cause: Network Lost.");
                                } else if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED) {
                                    Log.i(TAG, "Connection lost.  Reason: Service Disconnected");
                                }
                            }
                        }
                ).addOnConnectionFailedListener(
                        new GoogleApiClient.OnConnectionFailedListener() {
                            // Called whenever the API client fails to connect.
                            @Override
                            public void onConnectionFailed(ConnectionResult result) {
                                Log.i(TAG, "Connection failed. Cause: " + result.toString());
                                if (!result.hasResolution()) {
                                    // Show the localized error dialog
                                    GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(),
                                            (Activity) context, 0).show();
                                    return;
                                }
                                // The failure has a resolution. Resolve it.
                                // Called typically when the app is not yet authorized, and an
                                if (!authInProgress) {
                                    Log.i(TAG, "Attempting to resolve failed connection");
                                    authInProgress = true;
                                }
                            }
                        }
                ).build();
        mClient.connect();

    }

    public void disconnect(){
        this.mClient.disconnect();
    }

    public void ExecuteTask(){
        new FetchStepsAsync().execute();
    }

    public Long getNumberOfSteps() {
        return numberOfSteps;
    }


    private class FetchStepsAsync extends AsyncTask<Object, Object, Long> {
        protected Long doInBackground(Object... params) {
            long total = 0;
            PendingResult<DailyTotalResult> result = Fitness.HistoryApi.readDailyTotal(mClient, DataType.TYPE_STEP_COUNT_DELTA);
            DailyTotalResult totalResult = result.await(30, TimeUnit.SECONDS);
            if (totalResult.getStatus().isSuccess()) {
                DataSet totalSet = totalResult.getTotal();
                if (totalSet != null) {
                    total = totalSet.isEmpty()
                            ? 0
                            : totalSet.getDataPoints().get(0).getValue(Field.FIELD_STEPS).asInt();
                    Long stepsToDb = total;
                    if (DatabaseManager.getDatabaseInstance() !=null){
                        DatabaseManager.getDatabaseInstance().updateSteps(stepsToDb.intValue());
                    }
                }
            } else {
                Log.w(TAG, "There was a problem getting the step count.");
            }
            return total;
        }


        @Override
        protected void onPostExecute(Long aLong) {
            super.onPostExecute(aLong);

            Log.i(TAG, "Total steps: " + aLong);
            numberOfSteps = aLong;
            sendDataToActivity(aLong);
        }
    }

    private void sendDataToActivity(Long steps) {
        Intent sendLevel = new Intent();
        sendLevel.setAction("GET_SIGNAL_AVAILABLE");
        sendLevel.putExtra( "LEVEL_DATA",steps.toString());
        this.context.sendBroadcast(sendLevel);

    }
}
