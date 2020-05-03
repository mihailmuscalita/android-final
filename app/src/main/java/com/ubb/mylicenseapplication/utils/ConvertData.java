package com.ubb.mylicenseapplication.utils;

import com.ubb.mylicenseapplication.model.CompetitionModel;
import com.ubb.mylicenseapplication.model.UserModel;

import org.json.JSONException;
import org.json.JSONObject;


public class ConvertData {

    public static CompetitionModel getCompetition(String competition){
        CompetitionModel competitionModel;
        System.out.println("Aici este convertita competitia  = "+competition);
        try {
            JSONObject jsonObject = new JSONObject(competition);
            competitionModel = new CompetitionModel(jsonObject.getInt("competitionId"),jsonObject.getString("competitionTitle"),
                                                                     jsonObject.getString("competitionReward"),jsonObject.getString("isRegistered"));
            return competitionModel;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static UserModel getUser(String user){
        UserModel userModel;
        try{
            JSONObject jsonObject = new JSONObject(user.substring(user.indexOf("{"), user.lastIndexOf("}") + 1));
            userModel = new UserModel(jsonObject.getString("userName"),jsonObject.getString("name"),jsonObject.getInt("userRole"));
            return userModel;
        }
        catch (JSONException e){
            e.printStackTrace();
        }
        return null;
    }



}
