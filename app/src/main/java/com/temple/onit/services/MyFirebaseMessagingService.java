package com.temple.onit.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.temple.onit.OnitApplication;

public class MyFirebaseMessagingService extends FirebaseMessagingService {


    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d("Firebase", "OnMessageReceived");
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);

        //Post request to change token in server
        if(OnitApplication.instance.getAccountManager() != null && OnitApplication.instance.getAccountManager().username != null){
            OnitApplication.instance.getAccountManager().updateFBToken(s, this, OnitApplication.instance.getAccountManager().username);
        }

    }
}
