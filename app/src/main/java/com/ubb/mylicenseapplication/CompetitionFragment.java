package com.ubb.mylicenseapplication;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ubb.mylicenseapplication.Api.ApiRequests;
import com.ubb.mylicenseapplication.model.CompetitionModel;
import com.ubb.mylicenseapplication.utils.ConvertData;


public class CompetitionFragment extends Fragment implements View.OnClickListener {

    private CompetitionModel competitionModel;
    private EditText titleEditText;
    private EditText informationText;
    private Button registerButton;
    private Button closeButton;
    private Button informationButton;
    private Button closeCompetitionButton;

    private Context context;
    private MainActivity mainActivity;
    private NavController navController;
    private Dialog informationDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_competition, container, false);
        String competition= getArguments() != null ? getArguments().getString("message") : null;

        competitionModel = ConvertData.getCompetition(competition);
        this.titleEditText = (EditText) view.findViewById(R.id.titleEditText);
        this.registerButton = (Button) view.findViewById(R.id.registerStart);
        this.closeButton = (Button) view.findViewById(R.id.closeButton);
        this.informationButton = (Button) view.findViewById(R.id.informationReward);
        this.titleEditText.setText(this.competitionModel.getCompetitionTitle());
        this.titleEditText.setEnabled(false);

        this.informationDialog = new Dialog(this.context);
        this.informationDialog.setContentView(R.layout.competitionpopup);

        this.informationText = (EditText) this.informationDialog.findViewById(R.id.textPopupCompetitionInfo);
        this.closeCompetitionButton = (Button) this.informationDialog.findViewById(R.id.closePopupCompetitionButton);

        this.informationText.setEnabled(false);

        this.registerButton.setOnClickListener(this);
        this.closeButton.setOnClickListener(this);
        this.informationButton.setOnClickListener(this);


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

    private void showRewardInformaton(){
        this.informationText.setText(this.competitionModel.getCompetitionReward());
        this.closeCompetitionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                informationDialog.dismiss();
            }
        });
        this.informationDialog.show();
    }

    private void closeActivity(){
        navController.popBackStack();
    }

    private void closeAfterRegister(){
        Bundle bundle=new Bundle();
        bundle.putString("restart", "Yes");
        navController.navigate(R.id.mainFragment,bundle);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.registerStart:
                if (ApiRequests.isInternetConnection) {
                    this.mainActivity.showProgressBar();
                    new AsyncRegisterCompetitionToDb().execute(this.competitionModel);
                }
                else
                    Toast.makeText(context,"Check your internet connection!",Toast.LENGTH_LONG).show();
                break;
            case R.id.closeButton:
                closeActivity();
                break;
            case R.id.informationReward:
                showRewardInformaton();
                break;
        }
    }

    public class AsyncRegisterCompetitionToDb extends AsyncTask<CompetitionModel,Void,String> {

        @Override
        protected String doInBackground(CompetitionModel... competitionModels) {
            String data;
            CompetitionModel competitionModel = competitionModels[0];
            RegisterCompetitionModel registerCompetitionModel = new RegisterCompetitionModel(competitionModel.getIdCompetition());
            try {
                System.out.println("Comp+=" + competitionModel);
                data = ApiRequests.getInstance().registerToActiveCompetition(competitionModel);
                return  data;
            }
            catch (Exception ex){
                System.out.println("Eroare inregistrare="+ex.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String string) {
            super.onPostExecute(string);
            System.out.println("Rezultatul="+string);
            switch (string) {
                case "There is an internal error from application!":
                    Toast.makeText(context, "There is an internal problem with server!", Toast.LENGTH_LONG).show();
                    break;
                case "You are already in!":
                    Toast.makeText(context, "You are already registered!", Toast.LENGTH_LONG).show();
                    break;
                case "Registered succesful!":
                    //DatabaseManager.getDatabaseInstance().deleteCompetition();
                    //DatabaseManager.getDatabaseInstance().insertCompetition(competitionModel);
                    Toast.makeText(context, "You are registered! Wait some seconds...", Toast.LENGTH_LONG).show();
                    //serviceNotification();
                    new Handler().postDelayed(
                            CompetitionFragment.this::closeAfterRegister, 2500
                    );
                    break;
            }
            mainActivity.closeProgressBar();
        }

        private class RegisterCompetitionModel{
            private Integer competitionId;


            public RegisterCompetitionModel(Integer competitionId) {
                this.competitionId = competitionId;
            }

            public Integer getCompetitionId() {
                return competitionId;
            }

            public void setCompetitionId(Integer competitionId) {
                this.competitionId = competitionId;
            }

            @Override
            public String toString() {
                return "RegisterCompetitionModel{" +
                        ", competitionId=" + competitionId +
                        '}';
            }
        }
    }

}
