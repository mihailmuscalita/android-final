package com.ubb.mylicenseapplication.Api;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import com.ubb.mylicenseapplication.model.CompetitionModel;
import com.ubb.mylicenseapplication.model.Friend;
import com.ubb.mylicenseapplication.model.RegisterUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ApiRequests {

    private static final ApiRequests ourInstance = new ApiRequests();

    public static boolean isInternetConnection = false;

    private static final String URL = "http://192.168.0.10:8080/";
    public static final String wsUrl = "http://192.168.0.10:8080/ws";

    public static String userToken = "";

    public static Integer role;

    public static ApiRequests getInstance() {
        return ourInstance;
    }

    private ApiRequests() {
    }

    public String filterFriends(String newFriend)  {

        String inputLine;
        StringBuilder stringBuilder;
        java.net.URL url = null;
        try {
            url = new URL(URL+"friend/" + newFriend);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setConnectTimeout(9000);
            httpURLConnection.setReadTimeout(9000);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setRequestProperty("Content-Type", "application/json");
            httpURLConnection.setRequestProperty("Accept", "application/json");
            httpURLConnection.setRequestProperty("Authorization","Bearer  "+userToken);

            httpURLConnection.connect();
            InputStreamReader streamReader = new
                    InputStreamReader(httpURLConnection.getInputStream());
            BufferedReader reader = new BufferedReader(streamReader);
            stringBuilder = new StringBuilder();
            while((inputLine = reader.readLine()) != null){
                stringBuilder.append(inputLine);
            }

            reader.close();
            streamReader.close();

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return e.getMessage();
        }
        return stringBuilder.toString();
    }


    public boolean registerNewUser(RegisterUser newUser)  {

        String inputLine;
        String result;
        java.net.URL url = null;
        try {
            url = new URL(URL+"register");
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setConnectTimeout(9000);
            httpURLConnection.setReadTimeout(9000);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            httpURLConnection.setRequestProperty("Content-Type", "application/json");
            httpURLConnection.setRequestProperty("Accept", "application/json");
            httpURLConnection.setRequestProperty("Authorization","Bearer  "+userToken);
            JSONObject registerModel = new JSONObject();
            registerModel.put("username",newUser.getUsername());
            registerModel.put("password",newUser.getPassword());
            registerModel.put("name",newUser.getName());
            registerModel.put("email",newUser.getEmail());
            String requestBody = buildPostParameters(registerModel);

            OutputStream os = httpURLConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os));
            writer.write(requestBody);
            writer.flush();
            writer.close();
            os.close();


            httpURLConnection.connect();
            InputStreamReader streamReader = new
                    InputStreamReader(httpURLConnection.getInputStream());
            BufferedReader reader = new BufferedReader(streamReader);
            StringBuilder stringBuilder = new StringBuilder();
            while((inputLine = reader.readLine()) != null){
                stringBuilder.append(inputLine);
            }

            reader.close();
            streamReader.close();

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Eroare=" + e.getMessage());
            return false;
        }
        return true;
    }

    public boolean sendRequest(String friendName) throws Exception {
        List<Friend> friends = new ArrayList<>();
        System.out.println("Se face call la server pentru a se accepta requestul");

        String inputLine;
        String result;
        java.net.URL url = new URL(URL+"friendRequest/" + friendName);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setConnectTimeout(9000);
        httpURLConnection.setReadTimeout(9000);
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setRequestProperty("Content-Type", "application/json");
        httpURLConnection.setRequestProperty("Accept", "application/json");
        httpURLConnection.setRequestProperty("Authorization","Bearer  "+userToken);

        httpURLConnection.connect();
        InputStreamReader streamReader = new
                InputStreamReader(httpURLConnection.getInputStream());
        BufferedReader reader = new BufferedReader(streamReader);
        StringBuilder stringBuilder = new StringBuilder();
        while((inputLine = reader.readLine()) != null){
            if (inputLine.contains("true")){
                return true;
            }
        }
        reader.close();
        streamReader.close();
        return false;
    }

    public boolean cancelRequest(String friendName) throws Exception {
        List<Friend> friends = new ArrayList<>();
        System.out.println("Se face call la server pentru a se accepta requestul");

        String inputLine;
        String result;
        java.net.URL url = new URL(URL+"friendRequestCanceled/"+friendName);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setConnectTimeout(9000);
        httpURLConnection.setReadTimeout(9000);
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setRequestProperty("Content-Type", "application/json");
        httpURLConnection.setRequestProperty("Accept", "application/json");
        httpURLConnection.setRequestProperty("Authorization","Bearer  "+userToken);

        httpURLConnection.connect();
        InputStreamReader streamReader = new
                InputStreamReader(httpURLConnection.getInputStream());
        BufferedReader reader = new BufferedReader(streamReader);
        StringBuilder stringBuilder = new StringBuilder();
        while((inputLine = reader.readLine()) != null){
            if (inputLine.contains("true")){
                return true;
            }
        }
        reader.close();
        streamReader.close();
        System.out.println(friends);
        return false;
    }

    public boolean acceptRequest(String friendName) throws Exception {
        List<Friend> friends = new ArrayList<>();
        System.out.println("Se face call la server pentru a se accepta requestul");

        String inputLine;
        String result;
        java.net.URL url = new URL(URL+"friendRequestAccepted/"+friendName);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setConnectTimeout(9000);
        httpURLConnection.setReadTimeout(9000);
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setRequestProperty("Content-Type", "application/json");
        httpURLConnection.setRequestProperty("Accept", "application/json");
        httpURLConnection.setRequestProperty("Authorization","Bearer  "+userToken);

        httpURLConnection.connect();
        InputStreamReader streamReader = new
                InputStreamReader(httpURLConnection.getInputStream());
        BufferedReader reader = new BufferedReader(streamReader);
        StringBuilder stringBuilder = new StringBuilder();
        while((inputLine = reader.readLine()) != null){
            if (inputLine.contains("true")){
                return true;
            }
        }
        reader.close();
        streamReader.close();
        System.out.println(friends);
        return false;
    }

    public Integer getStepsForMyFriend(String friendName) throws Exception {
        List<Friend> friends = new ArrayList<>();
        System.out.println("Se face call la server pentru a vedea cati au prietenii");

        String inputLine;
        String result;
        java.net.URL url = new URL(URL+"friends/"+friendName);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setConnectTimeout(9000);
        httpURLConnection.setReadTimeout(9000);
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setRequestProperty("Content-Type", "application/json");
        httpURLConnection.setRequestProperty("Accept", "application/json");
        httpURLConnection.setRequestProperty("Authorization","Bearer  "+userToken);

        httpURLConnection.connect();
        InputStreamReader streamReader = new
                InputStreamReader(httpURLConnection.getInputStream());
        BufferedReader reader = new BufferedReader(streamReader);
        StringBuilder stringBuilder = new StringBuilder();
        while((inputLine = reader.readLine()) != null){
            return Integer.parseInt(inputLine.toString());
        }
        reader.close();
        streamReader.close();
        System.out.println(friends);
        return -1;
    }

    public List<Friend> searchToNewFriends() throws Exception {
        List<Friend> friends = new ArrayList<>();
        System.out.println("Se face call");

        String inputLine;
        String result;
        java.net.URL url = new URL(URL+"search");
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setConnectTimeout(9000);
        httpURLConnection.setReadTimeout(9000);
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setRequestProperty("Content-Type", "application/json");
        httpURLConnection.setRequestProperty("Accept", "application/json");
        httpURLConnection.setRequestProperty("Authorization","Bearer  "+userToken);

        httpURLConnection.connect();
        InputStreamReader streamReader = new
                InputStreamReader(httpURLConnection.getInputStream());
        BufferedReader reader = new BufferedReader(streamReader);
        StringBuilder stringBuilder = new StringBuilder();
        while((inputLine = reader.readLine()) != null){
            System.out.println(inputLine);
            String[] data = inputLine.replace("[","").replace("]","").split(",");
            for (String str :data){
                JSONObject jsonObject = new JSONObject(str);
                Friend friend = new Friend(jsonObject.getString("name"));
                friends.add(friend);
            }
        }
        reader.close();
        streamReader.close();
        System.out.println(friends);
        return friends;
    }

    public List<Friend> getRequestsForCurrentUser() throws Exception {
        List<Friend> friends = new ArrayList<>();
        System.out.println("Se face call");

        String inputLine;
        String result;
        java.net.URL url = new URL(URL+"friendsRequest");
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setConnectTimeout(9000);
        httpURLConnection.setReadTimeout(9000);
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setRequestProperty("Content-Type", "application/json");
        httpURLConnection.setRequestProperty("Accept", "application/json");
        httpURLConnection.setRequestProperty("Authorization","Bearer  "+userToken);

        httpURLConnection.connect();
        InputStreamReader streamReader = new
                InputStreamReader(httpURLConnection.getInputStream());
        BufferedReader reader = new BufferedReader(streamReader);
        StringBuilder stringBuilder = new StringBuilder();
        while((inputLine = reader.readLine()) != null){
            System.out.println(inputLine);
            String[] data = inputLine.replace("[","").replace("]","").split(",");
            if ( !inputLine.equals("[]")){
                for (String str :data){
                    JSONObject jsonObject = new JSONObject(str);
                    Friend friend = new Friend(jsonObject.getString("friendsRequest"));
                    friends.add(friend);
                }
            }
        }
        reader.close();
        streamReader.close();
        System.out.println(friends);
        return friends;
    }


    public List<Friend> getFriendsForCurrentUser() throws Exception {
        List<Friend> friends = new ArrayList<>();
        System.out.println("Se face call");

        String inputLine;
        String result;
        java.net.URL url = new URL(URL+"friends");
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setConnectTimeout(9000);
        httpURLConnection.setReadTimeout(9000);
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setRequestProperty("Content-Type", "application/json");
        httpURLConnection.setRequestProperty("Accept", "application/json");
        httpURLConnection.setRequestProperty("Authorization","Bearer  "+userToken);

        httpURLConnection.connect();
        InputStreamReader streamReader = new
                InputStreamReader(httpURLConnection.getInputStream());
        BufferedReader reader = new BufferedReader(streamReader);
        StringBuilder stringBuilder = new StringBuilder();
        while((inputLine = reader.readLine()) != null){
            String[] data = inputLine.replace("[","").replace("]","").split(",");
            for (String str :data){
                JSONObject jsonObject = new JSONObject(str);
                Friend friend = new Friend(jsonObject.getString("name"));
                friends.add(friend);
            }
        }
        reader.close();
        streamReader.close();
        System.out.println(friends);
        return friends;
    }

    public CompetitionModel getActiveCompetition() throws Exception{
        String inputLine;
        CompetitionModel competitionModel = new CompetitionModel();
        java.net.URL myUrl = new URL(URL + "license/competitions/active");
        HttpURLConnection httpURLConnection = (HttpURLConnection) myUrl.openConnection();

        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setConnectTimeout(9000);
        httpURLConnection.setReadTimeout(9000);
        httpURLConnection.setRequestProperty("Authorization","Bearer  "+userToken);

        httpURLConnection.connect();
        InputStreamReader streamReader = new
                InputStreamReader(httpURLConnection.getInputStream());

        BufferedReader reader = new BufferedReader(streamReader);
        StringBuilder stringBuilder = new StringBuilder();
        while((inputLine = reader.readLine()) != null){
            stringBuilder.append(inputLine);
        }

        reader.close();
        streamReader.close();

        if (!stringBuilder.toString().equals("")) {
            JSONObject jsonObject = new JSONObject(stringBuilder.toString());
            competitionModel = new CompetitionModel(jsonObject.getInt("competitionId"), jsonObject.getString("competitionTitle"),
                    jsonObject.getString("competitionReward"), jsonObject.getString("isRegistered"));
            return competitionModel;
        }
        return null;
    }

    public String registerToActiveCompetition(CompetitionModel competitionModel) throws Exception{
        String inputLine;
        String result;
        java.net.URL url = new URL(URL + "license/steps");
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setConnectTimeout(9000);
        httpURLConnection.setReadTimeout(9000);
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setDoInput(true);
        httpURLConnection.setRequestProperty("Content-Type", "application/json");
        httpURLConnection.setRequestProperty("Accept", "application/json");
        httpURLConnection.setRequestProperty("Authorization","Bearer  "+userToken);
        JSONObject registerModel = new JSONObject();
        registerModel.put("competitionId",competitionModel.getIdCompetition());
        String requestBody = buildPostParameters(registerModel);

        OutputStream os = httpURLConnection.getOutputStream();
        BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(os));
        writer.write(requestBody);
        writer.flush();
        writer.close();
        os.close();


        httpURLConnection.connect();
        InputStreamReader streamReader = new
                InputStreamReader(httpURLConnection.getInputStream());
        BufferedReader reader = new BufferedReader(streamReader);
        StringBuilder stringBuilder = new StringBuilder();
        while((inputLine = reader.readLine()) != null){
            stringBuilder.append(inputLine);
        }

        reader.close();
        streamReader.close();
        if (stringBuilder.toString().contains("You are already registered!")){
            result="You are already in!";
        }
        else if(stringBuilder!=null){
            result = "Registered succesful!";
        }
        else{
            result = "There is an internal error from application!";
        }
        return result;
    }

    public static String authentification(String userName, String password) throws IOException, JSONException, InterruptedException {
        String inputLine="";
        java.net.URL myUrl = new URL(URL + "authenticate");
        HttpURLConnection httpURLConnection = (HttpURLConnection) myUrl.openConnection();

        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setConnectTimeout(9000);
        httpURLConnection.setReadTimeout(9000);
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setDoInput(true);
        httpURLConnection.setRequestProperty("Content-Type", "application/json");
        httpURLConnection.setRequestProperty("Accept", "application/json");
        JSONObject authentificationModel = new JSONObject();
        authentificationModel.put("username",userName);
        authentificationModel.put("password",password);
        String requestBody = buildPostParameters(authentificationModel);

        OutputStream os = httpURLConnection.getOutputStream();
        BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(os));
        writer.write(requestBody);
        writer.flush();
        writer.close();
        os.close();

        httpURLConnection.connect();
        InputStreamReader streamReader = new
                InputStreamReader(httpURLConnection.getInputStream());

        BufferedReader reader = new BufferedReader(streamReader);
        StringBuilder stringBuilder = new StringBuilder();
        while((inputLine = reader.readLine()) != null){
            stringBuilder.append(inputLine);
        }
        //Close our InputStream and Buffered reader
        reader.close();
        streamReader.close();
        return stringBuilder.toString();
    }

    public String updateStepsToServer(int steps) throws Exception{
        String inputLine;
        String result;
        java.net.URL url = new URL(URL + "license/steps"+"/"+steps);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

        httpURLConnection.setRequestMethod("PUT");
        httpURLConnection.setConnectTimeout(9000);
        httpURLConnection.setReadTimeout(9000);
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setRequestProperty("Content-Type", "application/json");
        httpURLConnection.setRequestProperty("Accept", "application/json");
        httpURLConnection.setRequestProperty("Authorization","Bearer  "+userToken);

        httpURLConnection.connect();
        InputStreamReader streamReader = new
                InputStreamReader(httpURLConnection.getInputStream());
        BufferedReader reader = new BufferedReader(streamReader);
        StringBuilder stringBuilder = new StringBuilder();
        while((inputLine = reader.readLine()) != null){
            stringBuilder.append(inputLine);
        }

        reader.close();
        streamReader.close();
        if (stringBuilder.toString().contains("You are not registered to current competition!")){
            result="You are not registered already in!";
        }
        else{
            result = stringBuilder.toString();
        }
        return result;
    }

    public static String buildPostParameters(Object content) {
        String output = null;
        if ((content instanceof String) ||
                (content instanceof JSONObject) ||
                (content instanceof JSONArray)) {
            output = content.toString();
        } else if (content instanceof Map) {
            Uri.Builder builder = new Uri.Builder();
            HashMap hashMap = (HashMap) content;
            if (hashMap != null) {
                Iterator entries = hashMap.entrySet().iterator();
                while (entries.hasNext()) {
                    Map.Entry entry = (Map.Entry) entries.next();
                    builder.appendQueryParameter(entry.getKey().toString(), entry.getValue().toString());
                    entries.remove();
                }
                output = builder.build().getEncodedQuery();
            }
        }

        return output;
    }

    public boolean isNetworkInterfaceAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

}
