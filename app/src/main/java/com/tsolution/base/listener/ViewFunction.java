package com.tsolution.base.listener;

import com.workable.errorhandler.ErrorHandler;

public interface ViewFunction extends BaseListener {
    void process(String action, Object... params);


    default void action(String action) {
        try {
            process(action, null);
        } catch (Exception e) {
            ErrorHandler.create().handle(e);
        }
    }

    default void actionWithParam(String action, Object... params) {
        try {
            process(action, params);
        } catch (Exception e) {
            ErrorHandler.create().handle(e);
        }
    }
}
