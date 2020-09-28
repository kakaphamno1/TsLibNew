package com.tsolution.base.utils.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.tsolution.base.listener.AdapterListener;

public class TimeOutBroadcast extends BroadcastReceiver {
    private AdapterListener adapterListener;
    public static final String SHOW_TOKEN_DIALOG = "dialog-token-broadcast";

    public TimeOutBroadcast() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (SHOW_TOKEN_DIALOG.equals(intent.getAction())) {
            adapterListener.onShowDialogTimeOut();
        }
    }

    public void setAdapterListener(AdapterListener adapterListener) {
        this.adapterListener = adapterListener;
    }
}
