package com.ubb.mylicenseapplication;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ubb.mylicenseapplication.Api.ApiRequests;
import com.ubb.mylicenseapplication.model.RegisterUser;
import com.ubb.mylicenseapplication.utils.CheckLocalData;

import java.util.Objects;

public class RegisterFragment extends Fragment implements View.OnClickListener {

    private Context context;

    private EditText usernameNewUserText;
    private EditText firstPasswordNewUsertText;
    private EditText secondPasswordNewUsertText;
    private EditText nameText;
    private EditText emailText;
    private Button registerNewUserButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        this.usernameNewUserText = (EditText) view.findViewById(R.id.userNameRegister);
        this.firstPasswordNewUsertText = (EditText) view.findViewById(R.id.userPasswordRegister1);
        this.secondPasswordNewUsertText = (EditText) view.findViewById(R.id.userPasswordRegister2);
        this.nameText = (EditText) view.findViewById(R.id.appName);
        this.emailText = (EditText) view.findViewById(R.id.email);
        this.registerNewUserButton = (Button) view.findViewById(R.id.registerNewUser);
        this.registerNewUserButton.setOnClickListener(this);

        return view;
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.context = null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.registerNewUser:
                if (ApiRequests.isInternetConnection) {
                    this.register();
                } else{
                    Toast.makeText(context, "Check your internet connection !", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    private void register(){
        Objects.requireNonNull(getActivity()).runOnUiThread(()->{
            if (!CheckLocalData.comparePasswords(this.firstPasswordNewUsertText.getText().toString(),this.secondPasswordNewUsertText.getText().toString())){
                Toast.makeText(context, "Password should be equal !", Toast.LENGTH_LONG).show();
            }
            else if (!CheckLocalData.checkLenght(this.usernameNewUserText.getText().toString(),this.firstPasswordNewUsertText.getText().toString(),
                                                this.nameText.getText().toString(),this.emailText.getText().toString())){
                Toast.makeText(context, "The fields should have length under 100 !", Toast.LENGTH_LONG).show();
            }
            else if (!CheckLocalData.checkEmail(this.emailText.getText().toString())){
                Toast.makeText(context, "Invalid email !", Toast.LENGTH_LONG).show();
            }
            else if (!CheckLocalData.checkEmpty(this.usernameNewUserText.getText().toString(),this.firstPasswordNewUsertText.getText().toString(),
                                                this.nameText.getText().toString(),this.emailText.getText().toString())){
                Toast.makeText(context, "You should complete all fields!", Toast.LENGTH_LONG).show();
            }
            else{
                RegisterUser registerUser = new RegisterUser(this.usernameNewUserText.getText().toString(),this.firstPasswordNewUsertText.getText().toString(),
                        this.nameText.getText().toString(),this.emailText.getText().toString());
                new RegisterNewUser().execute(registerUser);
            }
        });
    }


    private class RegisterNewUser extends AsyncTask<RegisterUser,Void,Boolean>{


        @Override
        protected Boolean doInBackground(RegisterUser... registerNewUsers) {
            RegisterUser registerUser = registerNewUsers[0];
            boolean ok = ApiRequests.getInstance().registerNewUser(registerUser);
            System.out.println("S a inregistrat=" + ok);
            return ok;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (aBoolean){
                Toast.makeText(context, "You account is registered ! Enjoy !", Toast.LENGTH_LONG).show();
                //serviceNotification();
                new Handler().postDelayed(
                        () -> getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeFragment()).commit(), 2500
                );
            } else{
                Toast.makeText(context, "Server internal error ! Wait to solve !", Toast.LENGTH_LONG).show();
            }
        }
    }
}
