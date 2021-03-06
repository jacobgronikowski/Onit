package com.temple.onit.services;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.temple.onit.Constants;
import com.temple.onit.GeofencedReminder.GeofencedReminderActivity;
import com.temple.onit.OnitApplication;
import com.temple.onit.R;

public class LocationService extends Service {
    LocationManager locationManager;
    LocationListener locationListener;

    public LocationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d("Oncreate", "Location Service on create");
        locationManager = getSystemService(LocationManager.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startMyOwnForeground();
        else
            startForeground(1, new Notification());
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                updateLocation(OnitApplication.instance.getAccountManager().username, location);
                Log.d("LocationUpdate", "OnLocationChangeCalled");
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

    }
    private void updateLocation(String username, Location location){
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = Constants.API_UPDATE_USER_REMINDER + "?username="+username+"&currentLatitude="+location.getLatitude()+"&currentLongitude="+location.getLongitude();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
            Log.d("LocationUpdate", "Successfully Sent Location");
        }, error -> {
            Log.d("LocationUpdate", error.toString());
        });
        queue.add(stringRequest);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("Started", "Location Service Started");
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            // GPS is the only really useful provider here, since we need
            // high fidelity meter-level accuracy
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    5*1000, //send location updates every 5 seconds
                    10,
                    locationListener);
        }



        return super.onStartCommand(intent, flags, startId);
    }
    private void startMyOwnForeground(){
        String NOTIFICATION_CHANNEL_ID = "com.example.simpleapp";
        String channelName = "My Background Service";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Refreshing Reminders in background")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(2, notification);
    }
}