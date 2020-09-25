package com.tsolution.base.utils;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.tsolution.base.R;
import com.tsolution.base.dto.EventDTO;
import com.tsolution.base.dto.GuestActionDTO;

import org.greenrobot.eventbus.EventBus;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Objects;

import retrofit2.http.POST;

/**
 * Created by PhamBien.
 */

public class GuestDialog extends Dialog implements View.OnClickListener {
    public static final float NO_RADIUS = -1f;
    private static final float RADIUS = 12f;

    @Retention(RetentionPolicy.RUNTIME)
    @interface Orientation {
    }


    private float dialogRadius;
    private boolean cancelable;
    private TextInputEditText edUserName, edPassword;
    private MaterialButton buttonLogin;

    private GuestDialog(@NonNull Context context,
                        boolean cancelable) {
        super(context);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        this.dialogRadius = dialogRadius == 0 ? RADIUS : dialogRadius;
        if (dialogRadius == NO_RADIUS) {
            this.dialogRadius = 0f;
        }
        this.cancelable = cancelable;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_guest);
        initMainWindow();
        setCancelable(cancelable);
        initView();
        initListener();

    }

    private void initView() {
        edUserName = findViewById(R.id.txtUserName);
        edPassword = findViewById(R.id.txtPassword);
        buttonLogin =  findViewById(R.id.btnLogin);

        TextWatcher loginTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String userName = edUserName.getText().toString().trim();
                String password = edPassword.getText().toString().trim();
                buttonLogin.setEnabled(!userName.isEmpty() && !password.isEmpty());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };
        edUserName.addTextChangedListener(loginTextWatcher);
        edPassword.addTextChangedListener(loginTextWatcher);
    }

    private void initListener() {

        findViewById(R.id.btnLogin).setOnClickListener(this);
        findViewById(R.id.btnRegister).setOnClickListener(this);
        findViewById(R.id.close).setOnClickListener(this);
    }


    private void initMainWindow() {
        Objects.requireNonNull(getWindow()).setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnLogin) {
            // Process login
            EventBus.getDefault().post(GuestActionDTO.builder()
                    .isProcessLogin(true)
                    .userName(edUserName.getText().toString())
                    .password(edPassword.getText().toString())
                    .isProcessRegister(false)
                    .build());
            dismiss();
        } else if (view.getId() == R.id.btnRegister) {
            // Process Register
            EventBus.getDefault().post(GuestActionDTO.builder()
                    .isProcessLogin(false)
                    .isProcessRegister(true)
                    .build());
            dismiss();
        } else if (view.getId() == R.id.close) {
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

        public GuestDialog build() {
            return new GuestDialog(context, cancelable);
        }
    }

}
