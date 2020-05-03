package com.ubb.mylicenseapplication.sockets;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.ubb.mylicenseapplication.MainActivity;
import com.ubb.mylicenseapplication.R;
import com.ubb.mylicenseapplication.utils.NotificationConst;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public  class SocketListener extends WebSocketListener {

    private Context context;
    private MainActivity mainActivity;

    public SocketListener(Context context, MainActivity mainActivity) {
        this.context = context;
        this.mainActivity = mainActivity;
    }

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        super.onOpen(webSocket, response);
        System.out.println("S-a pornit conexiunea la webSocket!");
    }

    @Override
    public void onMessage(WebSocket webSocket,String text) {
        super.onMessage(webSocket, text);
        this.mainActivity.runOnUiThread(()-> {
            try {
                checkResponse(text);
            } catch (JSONException e) {
                System.out.println(e.getMessage());
            }
        });
    }

    @Override
    public void onClosing(WebSocket webSocket, int code, String reason) {
        super.onClosing(webSocket, code, reason);
    }

    @Override
    public void onClosed(WebSocket webSocket, int code, String reason) {
        super.onClosed(webSocket, code, reason);
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, @Nullable Response response) {
        super.onFailure(webSocket, t, response);
    }


    private void checkResponse(String text) throws JSONException {
        System.out.println("La notificarea prietenilor pe websocket=" + text);
        JSONObject jsonObject = new JSONObject(text.toString());
        if (jsonObject.has("scope")){
            String scope = jsonObject.getString("scope");
            String textType = jsonObject.getString("message");
            if (scope.equals("send")){
                System.out.println("Se incearca o notificare chilluta!");
                System.out.println("Userul este = "+textType);
                sendNotificationForRequest(textType);
                sendDataToFriends(textType);
            }
            else if (scope.equals("accept")){
                System.out.println("Se incearca acceptarea !");
                sendNotificationForRequestAccepted(textType);
                sendDataForAcceptRequest(textType);
            }
            else if (scope.equals("close")){
                System.out.println("Se inchide competitia!");
                sendNotificationForClosedCompetition();
                closeCompetition();
            }
        }
        else{
            sendNotificationForCompetition();
            sendCompetition(text);
        }
    }

    private void sendNotificationForRequest(String sendBy){
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        int notificationId = 1;
        String channelId = NotificationConst.NotificationIdSendRequest;
        String channelName = NotificationConst.ChanellSendRequest;
        int importance = NotificationManager.IMPORTANCE_HIGH;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    channelId, channelName, importance);
            notificationManager.createNotificationChannel(mChannel);
        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_sms_black_24dp)
                .setContentTitle("You have a new friend request !")
                .setContentText(sendBy + " want to be your friend !")
                .setAutoCancel(true)
                .setVibrate(new long[] {2000})
                .setDefaults(NotificationCompat.DEFAULT_SOUND)
                ;


        notificationManager.notify(notificationId, mBuilder.build());
        Vibrator v = (Vibrator) this.context.getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(500);
    }

    private void sendDataToFriends(String sendBy)
    {
        System.out.println("Se incearca trimiterea!");
        Intent sendLevel = new Intent();
        sendLevel.setAction("GET_LEVEL_REQUEST");
        sendLevel.putExtra( "LEVEL_SEND", sendBy);
        this.context.sendBroadcast(sendLevel);
        System.out.println("S-a trimis mesajul la broadcast !");
    }

    private void sendNotificationForRequestAccepted(String acceptedBy){
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        int notificationId = 1;
        String channelId = NotificationConst.NotificationIdSendRequest;
        String channelName = NotificationConst.ChanellSendRequest;
        int importance = NotificationManager.IMPORTANCE_HIGH;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    channelId, channelName, importance);
            notificationManager.createNotificationChannel(mChannel);
        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_person)
                .setContentTitle("You have a new friend!")
                .setContentText(acceptedBy + " is your friend !")
                .setAutoCancel(true)
                .setVibrate(new long[] {2000})
                .setDefaults(NotificationCompat.DEFAULT_SOUND)
                ;


        notificationManager.notify(notificationId, mBuilder.build());
        Vibrator v = (Vibrator) this.context.getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(500);
    }

    private void sendDataForAcceptRequest(String acceptedBy){
        Intent sendLevel = new Intent();
        sendLevel.setAction("GET_LEVEL_ACCEPT");
        sendLevel.putExtra( "LEVEL_ACCEPT", acceptedBy);
        this.context.sendBroadcast(sendLevel);
    }

    private void sendNotificationForCompetition(){
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        int notificationId = 1;
        String channelId = NotificationConst.NotificationIdSendRequest;
        String channelName = NotificationConst.ChanellSendRequest;
        int importance = NotificationManager.IMPORTANCE_HIGH;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    channelId, channelName, importance);
            notificationManager.createNotificationChannel(mChannel);
        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_person)
                .setContentTitle("A new competition already started!")
                .setContentText("Join fast ! You will win many rewards !")
                .setAutoCancel(true)
                .setVibrate(new long[] {2000})
                .setDefaults(NotificationCompat.DEFAULT_SOUND)
                ;


        notificationManager.notify(notificationId, mBuilder.build());
        Vibrator v = (Vibrator) this.context.getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(500);
    }

    private void sendCompetition(String competition){
        Intent sendLevel = new Intent();
        sendLevel.setAction("GET_LEVEL_COMPETITION");
        sendLevel.putExtra( "LEVEL_COMPETITION", competition);
        this.context.sendBroadcast(sendLevel);
    }

    private void sendNotificationForClosedCompetition(){
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        int notificationId = 1;
        String channelId = NotificationConst.NotificationIdSendRequest;
        String channelName = NotificationConst.ChanellSendRequest;
        int importance = NotificationManager.IMPORTANCE_HIGH;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    channelId, channelName, importance);
            notificationManager.createNotificationChannel(mChannel);
        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_person)
                .setContentTitle(" The competition is closed  !")
                .setContentText("Check your email to see your final position in contest !")
                .setAutoCancel(true)
                .setVibrate(new long[] {2000})
                .setDefaults(NotificationCompat.DEFAULT_SOUND)
                ;


        notificationManager.notify(notificationId, mBuilder.build());
        Vibrator v = (Vibrator) this.context.getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(500);
    }

    private void closeCompetition(){
        Intent sendLevel = new Intent();
        sendLevel.setAction("GET_LEVEL_CLOSE");
        sendLevel.putExtra( "LEVEL_CLOSE", "CLOSE");
        this.context.sendBroadcast(sendLevel);
    }
}
