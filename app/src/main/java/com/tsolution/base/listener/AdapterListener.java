package com.tsolution.base.listener;

import android.view.View;

public interface AdapterListener {
    void onItemClick(View v, Object o);

    void onItemLongClick(View v, Object o);

    default void onShowDialogRegister() {

    }
    default void onShowDialogTimeOut() {

    }
}
