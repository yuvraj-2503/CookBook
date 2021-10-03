package com.example.cookbook.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;

public class ConnectionManager {
    public boolean checkConnectivity(Context context){
        ConnectivityManager connectivityManager= (ConnectivityManager)
                                    context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork= connectivityManager.getActiveNetworkInfo();
        if (activeNetwork != null){
            return activeNetwork.isConnected();
        }else
            return false;
    }
}
