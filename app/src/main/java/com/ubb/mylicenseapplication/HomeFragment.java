package com.ubb.mylicenseapplication;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.ubb.mylicenseapplication.Api.ApiRequests;
import com.ubb.mylicenseapplication.database.DatabaseManager;

import org.json.JSONException;
import org.json.JSONObject;




public class HomeFragment extends Fragment implements View.OnClickListener {

    private EditText userName;
    private EditText userPassword;
    private Button loginButton;
    private Context context;
    private NavController navController;

    private MainActivity mainActivity;
    private ProgressDialog progressDialog;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        this.userName = (EditText) view.findViewById(R.id.userName);
        this.userPassword = (EditText) view.findViewById(R.id.userPassword);
        this.loginButton = (Button) view.findViewById(R.id.loginButton);

        Toast.makeText(context,"S-a incarcat mainul!",Toast.LENGTH_SHORT).show();

        this.progressDialog = new ProgressDialog(getActivity());

        this.loginButton.setOnClickListener(this);

        this.mainActivity = (MainActivity) getActivity();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.context = null;
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        this.context = context;
    }


    private void login(){
        String userNameInnerText = this.userName.getText().toString();
        String userPasswordInnerText = this.userPassword.getText().toString();
        new LoginAsyncTask(context).execute(userNameInnerText,userPasswordInnerText);
    }

    @Override
    public void onClick(View v) {
        this.mainActivity.showProgressBar();
        switch (v.getId()) {
            case R.id.loginButton:
                if (ApiRequests.isInternetConnection)
                    loginApp();
                else
                    Toast.makeText(context, "Check your internet connection!", Toast.LENGTH_LONG).show();
                break;
        }
    }

    private boolean deviceHasGoogleAccount(){
        AccountManager accMan = AccountManager.get(this.context);
        Account[] accArray = accMan.getAccountsByType("com.google");
        System.out.println("Nr conturi=" + accArray.length);
        return accArray.length >= 0;
    }

    private void loginApp(){
        if (deviceHasGoogleAccount()){
            login();
        }
        else{
            Toast.makeText(context, "You should have a Google Account!", Toast.LENGTH_LONG).show();
        }
    }

//    public String getUsername() {
//        AccountManager manager = AccountManager.get(this.context);
//        Account[] accounts = manager.getAccountsByType("com.google");
//        List<String> possibleEmails = new LinkedList<String>();
//
//        for (Account account : accounts) {
//            // TODO: Check possibleEmail against an email regex or treat
//            // account.name as an email address only for certain account.type
//            // values.
//            possibleEmails.add(account.name);
//        }
//
//        if (!possibleEmails.isEmpty() && possibleEmails.get(0) != null) {
//            String email = possibleEmails.get(0);
//            String[] parts = email.split("@");
//            if (parts.length > 0 && parts[0] != null)
//                return parts[0];
//            else
//                return null;
//        } else
//            return null;
//    }

    public class LoginAsyncTask extends AsyncTask<String,Void,String> {

        private Context mContext;
        String userName;
        String userPassword;

        public LoginAsyncTask(Context context) {
            mContext = context;
        }

        @Override
        protected String doInBackground(String... strings) {
            userName = strings[0];
            userPassword = strings[1];
            try {
                String resultAuthentificationData = ApiRequests.authentification(userName, userPassword);
                System.out.println("Ia sa vedem tokenul="+resultAuthentificationData);
                return resultAuthentificationData;
            } catch (Exception e) {
                System.out.println(e.getMessage());
                if (e.getMessage().contains("Failed to connect")){
                    return "";
                }
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if(result != null && !result.equals("")){
                JSONObject authData = null;
                try {
                    authData = new JSONObject(result);
                    ApiRequests.userToken = authData.getString("token");
                    ApiRequests.role = authData.getInt("role");
                    if (ApiRequests.role != 1){
                        showInvalidCreditentials();
                    } else {
                        DatabaseManager.init(getContext());
//                    DatabaseManager.getDatabaseInstance().deleteCompetition();
//                    DatabaseManager.getDatabaseInstance().deleteSteps();
                        mainActivity = (MainActivity) getActivity();
                        mainActivity.sendMessage(ApiRequests.userToken);
                        navController.navigate(R.id.mainFragment);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    System.out.println(e.getMessage());
                }
            }
            else if(result == null){
                showInvalidCreditentials();
            }
            else{
                mainActivity.showInformationAboutInternet();
            }
            mainActivity.closeProgressBar();

        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }

    private void showInvalidCreditentials(){
        Toast.makeText(this.context,"Invalid creditentials!",Toast.LENGTH_SHORT).show();
    }
}
