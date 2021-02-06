package com.mayank.rucky.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;

public class Networks {

    public boolean isNetworkPresent(Context context) {
        final ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null)
            return false;
        final Network activeNetwork = connectivityManager.getActiveNetwork();
        if (activeNetwork == null)
                return false;
        final NetworkCapabilities nc = connectivityManager.getNetworkCapabilities(activeNetwork);
        if (nc == null)
            return false;
        return nc.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || nc.hasTransport(NetworkCapabilities.TRANSPORT_WIFI);
    }

}
