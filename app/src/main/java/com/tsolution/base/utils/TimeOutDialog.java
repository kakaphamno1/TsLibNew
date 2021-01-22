package com.tsolution.base.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.tsolution.base.R;
import com.tsolution.base.dto.EventDTO;
import com.tsolution.base.dto.GuestActionDTO;
import com.tsolution.base.listener.AdapterListener;
import com.tsolution.base.utils.broadcast.TimeOutBroadcast;
import com.workable.errorhandler.ErrorHandler;

import org.greenrobot.eventbus.EventBus;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Objects;

/**
 * Created by PhamBien.
 */

public class TimeOutDialog extends Dialog implements View.OnClickListener, AdapterListener {
    public static final float NO_RADIUS = -1f;
    public static final int REFRESH_TOKEN_CODE = 564488;
    private static final float RADIUS = 12f;

    @Retention(RetentionPolicy.RUNTIME)
    @interface Orientation {
    }

    private float dialogRadius;
    private boolean cancelable;
    private TimeOutBroadcast timeOutBroadcast;
    private Context mContext;

    private TimeOutDialog(@NonNull Context context,
                          boolean cancelable) {
        super(context);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        this.dialogRadius = dialogRadius == 0 ? RADIUS : dialogRadius;
        if (dialogRadius == NO_RADIUS) {
            this.dialogRadius = 0f;
        }
        this.cancelable = cancelable;
        timeOutBroadcast = new TimeOutBroadcast();
        timeOutBroadcast.setAdapterListener(this);
        this.mContext = context;
        initReceivers(context);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_timeout);
        initMainWindow();
        setCancelable(cancelable);
        initListener();

    }

    private void initReceivers(Context context) {
        ErrorHandler.create().run(() -> {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(TimeOutBroadcast.SHOW_TOKEN_DIALOG);
            context.registerReceiver(timeOutBroadcast, intentFilter);
        });
    }

    private void initListener() {
        findViewById(R.id.btnRefresh).setOnClickListener(this);
        findViewById(R.id.btnLogout).setOnClickListener(this);
    }


    private void initMainWindow() {
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        Objects.requireNonNull(getWindow()).setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnRefresh) {
            // Process refresh
            EventBus.getDefault().post(EventDTO.builder()
                    .code(REFRESH_TOKEN_CODE)
                    .message("REFRESH_TOKEN_CODE").build());
        }else if (view.getId() == R.id.btnLogout){
            EventBus.getDefault().post(GuestActionDTO.builder()
//                    .userName(edUserName.getText().toString())
//                    .password(edPassword.getText().toString())
                    .isProcessLogin(true)
                    .isProcessRegister(false)
                    .isActiveAccount(false)
                    .build());
            dismiss();
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }


    public static class Builder {
        private Context context;
        private boolean cancelable;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setCancelable(boolean cancelable) {
            this.cancelable = cancelable;
            return this;
        }

        public TimeOutDialog build() {
            return new TimeOutDialog(context, cancelable);
        }
    }

    public void onDestroy() {
        try {
            mContext.unregisterReceiver(timeOutBroadcast);
            if (isShowing())
                dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemClick(View v, Object o) {

    }

    @Override
    public void onItemLongClick(View v, Object o) {

    }


    @Override
    public void onShowDialogTimeOut() {
        if (!isShowing()) {
            show();
        }
    }
}
