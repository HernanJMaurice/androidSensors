package com.mauricesoa.mauricesensores;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

public class MyBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "com.mauricesoa.mauricesensores.MyBroadcastReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {

        if(!isOnline(context)){
            Toast.makeText(context, R.string.no_internet, Toast.LENGTH_LONG).show();
        }else
        {
            Toast.makeText(context, R.string.con_internet, Toast.LENGTH_LONG).show();
        }

    }

    private boolean isOnline(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            return (netInfo != null && netInfo.isConnected());
        } catch (NullPointerException e) {
            e.printStackTrace();
            return false;
        }
    }

}
