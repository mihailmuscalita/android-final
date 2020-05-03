package com.ubb.mylicenseapplication;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.ubb.mylicenseapplication.Api.ApiRequests;
import com.ubb.mylicenseapplication.Services.StepService;
import com.ubb.mylicenseapplication.database.DatabaseManager;
import com.ubb.mylicenseapplication.model.CompetitionModel;
import com.ubb.mylicenseapplication.model.StepModel;
import com.ubb.mylicenseapplication.utils.ConvertData;

import java.util.ArrayList;


public class MainFragment extends Fragment implements View.OnClickListener {

    private Button registerButton;
    private Button friendsButton;
    private Button requestButton;
    private Button searchButton;
    private TextView registerText;
    private EditText stepText;

    private CompetitionModel competitionModel;
    private StepService myTestService;
    private Intent mServiceIntent;
    private StepRecevier stepRecevier;

    private Context context;
    private MainActivity mainActivity;
    private NavController navController;

    private String needsRestart = "";

    public static final String EXTRA_Competition = "com.ubb.mylicenseapplcation.EXTRA_Competition_Id";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_fragment, container, false);

        this.needsRestart = getArguments() != null ? getArguments().getString("restart") : "";

        this.stepText = (EditText) view.findViewById(R.id.step);
        this.registerText = (TextView) view.findViewById(R.id.messageRegistered);
        this.registerButton = (Button) view.findViewById(R.id.registerButton);
        this.friendsButton = (Button) view.findViewById(R.id.friendsButton);
        this.requestButton = (Button) view.findViewById(R.id.requestButton);
        this.searchButton = (Button) view.findViewById(R.id.searchButton);

        this.registerButton.setOnClickListener(this);
        this.friendsButton.setOnClickListener(this);
        this.requestButton.setOnClickListener(this);
        this.searchButton.setOnClickListener(this);

        this.mainActivity = (MainActivity) getActivity();

        return view;
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
    }

    @Override
    public void onStart() {
        super.onStart();
        stepText.setEnabled(false);
        new ActiveCompetition(context).execute();
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter("GET_SIGNAL_AVAILABLE");
        intentFilter.addAction("GET_LEVEL_COMPETITION");
        stepRecevier = new MainFragment.StepRecevier();
        this.context.registerReceiver(stepRecevier, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        this.context.unregisterReceiver(stepRecevier);
    }

    @Override
    public void onStop()
    {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (isMyServiceRunning(myTestService.getClass())) {
            this.context.stopService(mServiceIntent);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.registerButton:
                switchToCompetitionActivy();
                break;
            case R.id.friendsButton:
                switchToFriends();
                break;

            case R.id.requestButton:
                switchToRequests();
                break;
            case R.id.searchButton:
                switchToNewFriends();
                break;
        }
    }

    private void switchToNewFriends(){ navController.navigate(R.id.searchFragment); }

    private void switchToRequests(){ navController.navigate(R.id.requestsFragment);}

    private void switchToFriends(){
        navController.navigate(R.id.friendsFragment);
    }


    private void switchToCompetitionActivy(){
        Bundle bundle=new Bundle();
        bundle.putString("message", competitionModel.toString());
        navController.navigate(R.id.competitionFragment,bundle);
    }

    protected void setCompetition(CompetitionModel competitionModel){
        this.competitionModel = competitionModel;
    }

    private void setButton(){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                registerButton.setVisibility(View.VISIBLE);
            }
        });
    }

    private void setScreen(){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
               registerText.setVisibility(View.VISIBLE);
            }
        });
    }

    private void restartService(){
        if (!isMyServiceRunning(StepService.class)) {
            context.stopService(mServiceIntent);
        }
        myTestService = new StepService(context);
        mServiceIntent = new Intent(context, myTestService.getClass());
        mServiceIntent.putExtra(EXTRA_Competition,competitionModel != null ? competitionModel.toString() : null);
        context.startService(mServiceIntent);
    }

    protected CompetitionModel getCompetitionModel(){
        return this.competitionModel;
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i ("isMyServiceRunning?", true+"");
                return true;
            }
        }
        Log.i ("isMyServiceRunning?", false+"");
        return false;
    }

    class StepRecevier extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            if(intent.getAction().equals("GET_SIGNAL_AVAILABLE"))
            {
                System.out.println("S-a trimis deja pasul!");
                String level = intent.getStringExtra("LEVEL_DATA");
                stepText.setText(level);
            }
            else if(intent.getAction().equals("GET_LEVEL_COMPETITION"))
            {
                System.out.println("S-a intrat pe comeptitie");
                String competition = intent.getStringExtra("LEVEL_COMPETITION");
                competitionModel = ConvertData.getCompetition(competition);
                competitionModel.setIsRegistered("No");
                setButton();
                restartService();
            }
            else if (intent.getAction().equals("GET_LEVEL_CLOSE")){
                System.out.println("Se inchide competitia !");
                registerButton.setVisibility(View.GONE);
                competitionModel = null;
                restartService();
            }
        }

    }

    private void startService(){
        myTestService = new StepService(context);
        mServiceIntent = new Intent(context, myTestService.getClass());
        mServiceIntent.putExtra(EXTRA_Competition,competitionModel != null ? competitionModel.toString() : null);
        if (!isMyServiceRunning(myTestService.getClass())) {
            context.startService(mServiceIntent);
        }
    }

    private void setSteps(){
        getActivity().runOnUiThread(()->{
            ArrayList<StepModel> arrayList = DatabaseManager.getDatabaseInstance().fetchSteps();
            if (arrayList.size() != 0 ){
                stepText.setText(arrayList.get(0).getSteps().toString());
            }
        });
    }

    public class ActiveCompetition extends AsyncTask<Void,Void, CompetitionModel> {

        private Context mContext;
        private String url;

        public ActiveCompetition(Context context) {
            mContext = context.getApplicationContext();
        }

        @Override
        protected CompetitionModel doInBackground(Void... data) {
            try {
                setSteps();
                if (ApiRequests.getInstance().isNetworkInterfaceAvailable(context)){
                    CompetitionModel competitionModel = ApiRequests.getInstance().getActiveCompetition();
                    return competitionModel;
                }
                else{
                    ArrayList<CompetitionModel> competitions = DatabaseManager.getDatabaseInstance().fetchCompetitions();
                    if (competitions.size() != 0){
                        return competitions.get(0);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mainActivity.showProgressBar();
        }

        @Override
        protected void onPostExecute(CompetitionModel competitionModel) {
            super.onPostExecute(competitionModel);
            setCompetition(competitionModel);
            if (competitionModel!=null){
                if (needsRestart.equals("Yes")){
                    setServiceRestarted();
                    restartService();
                }
                if (competitionModel.getIsRegistered().equals("No")){
                    setButton();
                }
                else{
                    setScreen();
                }
            }
            startService();
            mainActivity.closeProgressBar();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }

    private void setServiceRestarted(){
        this.needsRestart = "";
    }

}
