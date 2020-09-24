package com.tsolution.base.utils.checkConnection;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.IntentFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Guideline;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;

import com.tsolution.base.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Objects;
import java.util.Random;

/**
 * Created by DUYTIEN on 09/05/2020.
 */

public class NoInternetDialog extends Dialog implements View.OnClickListener, ConnectionListener, ConnectionCallback {
    public static final float NO_RADIUS = -1f;
    private static final float RADIUS = 12f;
    private static final float FLIGHT_THERE_START = -200f;
    private static final float FLIGHT_THERE_END = 1300f;
    private static final float FLIGHT_BACK_START = 1000f;
    private static final float FLIGHT_BACK_END = -400f;
    private static final long FLIGHT_DURATION = 2500;

    @Retention(RetentionPolicy.RUNTIME)
    @IntDef({ORIENTATION_TOP_BOTTOM,
            ORIENTATION_BOTTOM_TOP,
            ORIENTATION_RIGHT_LEFT,
            ORIENTATION_LEFT_RIGHT,
            ORIENTATION_BL_TR,
            ORIENTATION_TR_BL,
            ORIENTATION_BR_TL,
            ORIENTATION_TL_BR})
    @interface Orientation {
    }

    public static final int ORIENTATION_TOP_BOTTOM = 10;
    public static final int ORIENTATION_BOTTOM_TOP = 11;
    public static final int ORIENTATION_RIGHT_LEFT = 12;
    public static final int ORIENTATION_LEFT_RIGHT = 13;
    public static final int ORIENTATION_BL_TR = 14;
    public static final int ORIENTATION_TR_BL = 15;
    public static final int ORIENTATION_BR_TL = 16;
    public static final int ORIENTATION_TL_BR = 17;

    private Guideline topGuide;
    private AppCompatImageView close;
    private AppCompatImageView plane;
    private AppCompatImageView tomb;
    private AppCompatTextView noInternetBody;
    private AppCompatButton wifiOn;
    private AppCompatButton mobileOn;
    private AppCompatButton airplaneOff;
    private ProgressBar wifiLoading;

    private float dialogRadius;
    private int buttonColor;
    private int buttonTextColor;
    private int buttonIconsColor;
    private int wifiLoaderColor;
    private boolean cancelable;

    private WifiReceiver wifiReceiver;
    private NetworkStatusReceiver networkStatusReceiver;
    private ObjectAnimator wifiAnimator;
    private ConnectionCallback connectionCallback;

    private NoInternetDialog(@NonNull Context context,
                             boolean cancelable) {
        super(context);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        this.dialogRadius = dialogRadius == 0 ? RADIUS : dialogRadius;
        if (dialogRadius == NO_RADIUS) {
            this.dialogRadius = 0f;
        }

        this.buttonTextColor = ContextCompat.getColor(getContext(), R.color.colorWhite);
        this.buttonColor = ContextCompat.getColor(getContext(), R.color.colorAccent);
        this.buttonIconsColor = ContextCompat.getColor(getContext(), R.color.colorWhite);
        this.wifiLoaderColor = ContextCompat.getColor(getContext(), R.color.colorWhite);
        this.cancelable = cancelable;
        initReceivers(context);
    }

    private void initReceivers(Context context) {
        wifiReceiver = new WifiReceiver();
        context.registerReceiver(wifiReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        networkStatusReceiver = new NetworkStatusReceiver();
        context.registerReceiver(networkStatusReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        wifiReceiver.setConnectionListener(this);
        networkStatusReceiver.setConnectionCallback(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_no_internet);

        initMainWindow();
        initView();
        initGuideLine();
        initButtonStyle();
        initListeners();
        initAnimations();
        initWifiLoading();
        initClose();
        initAnimationsNoInternet();


    }

    private void initAnimationsNoInternet() {
        wifiAnimator = ObjectAnimator.ofFloat(tomb, "alpha", 0f, 1f);
        wifiAnimator.setDuration(6000);
        wifiAnimator.setRepeatMode(ValueAnimator.RESTART);
        wifiAnimator.setRepeatCount(ValueAnimator.INFINITE);

        wifiAnimator.start();
    }

    private void initMainWindow() {
        Objects.requireNonNull(getWindow()).setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.MATCH_PARENT);
    }

    private void initView() {
        close = findViewById(R.id.close);
        plane = findViewById(R.id.plane);
        tomb = findViewById(R.id.tomb);
        noInternetBody = findViewById(R.id.no_internet_body);
        wifiOn = findViewById(R.id.wifi_on);
        mobileOn = findViewById(R.id.mobile_on);
        airplaneOff = findViewById(R.id.airplane_off);
        wifiLoading = findViewById(R.id.wifi_loading);
        topGuide = findViewById(R.id.top_guide);
    }

    private void initGuideLine() {
        ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) topGuide.getLayoutParams();
        topGuide.setLayoutParams(lp);
    }

    private void initButtonStyle() {
        wifiOn.getBackground().mutate().setColorFilter(buttonColor, PorterDuff.Mode.SRC_IN);
        mobileOn.getBackground().mutate().setColorFilter(buttonColor, PorterDuff.Mode.SRC_IN);
        airplaneOff.getBackground().mutate().setColorFilter(buttonColor, PorterDuff.Mode.SRC_IN);

        wifiOn.setTextColor(buttonTextColor);
        mobileOn.setTextColor(buttonTextColor);
        airplaneOff.setTextColor(buttonTextColor);

        Drawable wifi = ContextCompat.getDrawable(getContext(), R.drawable.ic_wifi_white);
        Drawable mobileData = ContextCompat.getDrawable(getContext(), R.drawable.ic_4g_white);
        Drawable airplane = ContextCompat.getDrawable(getContext(), R.drawable.ic_airplane_off);
        if (wifi != null){
            wifi.mutate().setColorFilter(buttonIconsColor, PorterDuff.Mode.SRC_ATOP);
        }
        if (mobileData != null){
            mobileData.mutate().setColorFilter(buttonIconsColor, PorterDuff.Mode.SRC_ATOP);
        }
        if (airplane != null){
            airplane.mutate().setColorFilter(buttonIconsColor, PorterDuff.Mode.SRC_ATOP);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            wifiOn.setCompoundDrawablesRelativeWithIntrinsicBounds(wifi, null, null, null);
            mobileOn.setCompoundDrawablesRelativeWithIntrinsicBounds(mobileData, null, null, null);
            airplaneOff.setCompoundDrawablesRelativeWithIntrinsicBounds(airplane, null, null, null);
        } else {
            wifiOn.setCompoundDrawablesWithIntrinsicBounds(wifi, null, null, null);
            mobileOn.setCompoundDrawablesWithIntrinsicBounds(mobileData, null, null, null);
            airplaneOff.setCompoundDrawablesWithIntrinsicBounds(airplane, null, null, null);
        }
    }

    private void initListeners() {
        close.setOnClickListener(this);
        wifiOn.setOnClickListener(this);
        mobileOn.setOnClickListener(this);
        airplaneOff.setOnClickListener(this);
    }

    private void initAnimations() {
        startFlight();
    }

    private void startFlight() {
        if (!NoInternetUtils.isAirplaneModeOn(getContext())) {
            plane.setVisibility(View.GONE);
            return;
        }

        plane.setVisibility(View.VISIBLE);
        noInternetBody.setText(R.string.airplane_on);
        airplaneOff.setVisibility(View.VISIBLE);
        wifiOn.setVisibility(View.INVISIBLE);
        mobileOn.setVisibility(View.INVISIBLE);

        final ObjectAnimator flightThere = ObjectAnimator.ofFloat(plane, "translationX", FLIGHT_THERE_START, FLIGHT_THERE_END);
        final ObjectAnimator flightBack = ObjectAnimator.ofFloat(plane, "translationX", FLIGHT_BACK_START, FLIGHT_BACK_END);

        flightThere.setDuration(FLIGHT_DURATION);
        flightBack.setDuration(FLIGHT_DURATION);

        flightThere.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                plane.setRotationY(180);
                flightBack.start();
            }
        });

        flightBack.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                plane.setRotationY(0);
                flightThere.start();
            }
        });

        flightThere.start();
    }

    private void initWifiLoading() {
        wifiLoading.getIndeterminateDrawable().setColorFilter(wifiLoaderColor, PorterDuff.Mode.SRC_IN);
        ViewCompat.setElevation(wifiLoading, 10);
    }

    private void initClose() {
        setCancelable(cancelable);
        close.setVisibility(cancelable ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.close) {
            dismiss();
        } else if (id == R.id.wifi_on) {
            turnOnWifiWithAnimation();
        } else if (id == R.id.mobile_on) {
            NoInternetUtils.turnOn3g(getContext());
            dismiss();
        } else if (id == R.id.airplane_off) {
            NoInternetUtils.turnOffAirplaneMode(getContext());
            dismiss();
        }
    }

    private void turnOnWifiWithAnimation() {
        ValueAnimator widthAnimator = ValueAnimator.ofFloat(
                getContext().getResources().getDimensionPixelSize(R.dimen.button_width),
                getContext().getResources().getDimensionPixelSize(R.dimen.button_width) + 10,
                getContext().getResources().getDimensionPixelSize(R.dimen.button_size2));
        widthAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float value = (float) valueAnimator.getAnimatedValue();
                ConstraintLayout.LayoutParams lpWifi = (ConstraintLayout.LayoutParams) wifiOn.getLayoutParams();
                lpWifi.width = (int) value;
                wifiOn.setLayoutParams(lpWifi);
            }
        });
        ObjectAnimator translateXAnimatorWifi = ObjectAnimator.ofFloat(wifiOn, "translationX", 1f, 110f);
        ObjectAnimator translateYAnimatorWifi = ObjectAnimator.ofFloat(wifiOn, "translationY", 1f, 0f);
        ObjectAnimator translateXAnimatorLoading = ObjectAnimator.ofFloat(wifiLoading, "translationX", 1f, 104f);
        ObjectAnimator translateYAnimatorLoading = ObjectAnimator.ofFloat(wifiLoading, "translationY", 1f, -10f);

        ValueAnimator textSizeAnimator = ValueAnimator.ofFloat(13f, 0f);
        textSizeAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float value = (float) valueAnimator.getAnimatedValue();
                wifiOn.setTextSize(value);
            }
        });

        translateXAnimatorWifi.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                wifiLoading.setVisibility(View.VISIBLE);
            }
        });

        AnimatorSet set = new AnimatorSet();
        set.playTogether(widthAnimator, translateXAnimatorWifi, translateYAnimatorWifi, translateXAnimatorLoading, translateYAnimatorLoading, textSizeAnimator);
        set.setDuration(400);
        set.start();
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                NoInternetUtils.turnOnWifi(getContext());
            }
        });
    }
    private int animateDirection() {
        Random r = new Random();
        return r.nextInt(2);
    }

    @Override
    public void onWifiTurnedOn() {
        if (wifiAnimator != null && wifiAnimator.isStarted()) {
            wifiAnimator.cancel();
            getContext().unregisterReceiver(wifiReceiver);
        }
    }

    @Override
    public void onWifiTurnedOff() {

    }

    @Override
    public void hasActiveConnection(boolean hasActiveConnection) {
        if (this.connectionCallback != null)
            this.connectionCallback.hasActiveConnection(hasActiveConnection);
        if (!hasActiveConnection) {
            showDialog();
        } else {
            dismiss();
        }
    }

    @Override
    public void show() {
        super.show();
        startFlight();
    }

    public void showDialog() {
        Ping ping = new Ping();
        ping.setConnectionCallback(new ConnectionCallback() {
            @Override
            public void hasActiveConnection(boolean hasActiveConnection) {
                if (!hasActiveConnection) {
                    show();
                }
            }
        });
        ping.execute(getContext());
    }

    @Override
    public void dismiss() {
        reset();
        super.dismiss();
    }

    private void reset() {
        if (airplaneOff != null) {
            airplaneOff.setVisibility(View.GONE);
        }
        if (wifiOn != null) {
            wifiOn.setVisibility(View.VISIBLE);
            ConstraintLayout.LayoutParams wifiParams = (ConstraintLayout.LayoutParams) wifiOn.getLayoutParams();
            wifiParams.width = getContext().getResources().getDimensionPixelSize(R.dimen.button_width);
            wifiOn.setLayoutParams(wifiParams);
            wifiOn.setTextSize(13f);
            wifiOn.setTranslationX(1f);
            wifiOn.setTranslationY(1f);
        }
        if (mobileOn != null) {
            mobileOn.setVisibility(View.VISIBLE);
        }
        if (wifiLoading != null) {
            wifiLoading.setTranslationX(1f);
            wifiLoading.setVisibility(View.INVISIBLE);
        }
    }

    public void onDestroy() {
        try {
            getContext().unregisterReceiver(networkStatusReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            getContext().unregisterReceiver(wifiReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setConnectionCallback(ConnectionCallback connectionCallback) {
        this.connectionCallback = connectionCallback;
    }

    public static class Builder {
        private Context context;
        private ConnectionCallback connectionCallback;
        private boolean cancelable;

        public Builder(Context context) {
            this.context = context;
        }
        public Builder setCancelable(boolean cancelable) {
            this.cancelable = cancelable;
            return this;
        }
        public NoInternetDialog build() {
            NoInternetDialog dialog = new NoInternetDialog(context, cancelable);
            dialog.setConnectionCallback(connectionCallback);
            return dialog;
        }
    }
}
