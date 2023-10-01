package com.example.mangatn.monitor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

public class ConnectionStateMonitor {

    private final Context context;
    private ConnectivityReceiver connectivityReceiver;
    private OnNetworkStateChangeListener listener;

    public ConnectionStateMonitor(Context context) {
        this.context = context;
    }

    public void startMonitoring(OnNetworkStateChangeListener listener) {
        this.listener = listener;
        connectivityReceiver = new ConnectivityReceiver();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        context.registerReceiver(connectivityReceiver, filter);
    }

    public void stopMonitoring() {
        if (connectivityReceiver != null) {
            context.unregisterReceiver(connectivityReceiver);
            connectivityReceiver = null;
            listener = null;
        }
    }

    private class ConnectivityReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (listener != null) {
                boolean isConnected = isNetworkConnected(context);
                listener.onNetworkStateChange(isConnected);
            }
        }
    }

    private boolean isNetworkConnected(@NonNull Context context) {
        ConnectivityManager cm = ContextCompat.getSystemService(context, ConnectivityManager.class);
        
        if (cm != null) {
            Network activeNetwork = cm.getActiveNetwork();

            if (activeNetwork != null) {
                NetworkCapabilities capabilities = cm.getNetworkCapabilities(activeNetwork);

                if (capabilities != null) {
                    return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
                }
            }
        }

        return false;
    }

    public interface OnNetworkStateChangeListener {
        void onNetworkStateChange(boolean isConnected);
    }
}
