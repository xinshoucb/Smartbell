package com.smartbell;

import android.app.Application;

import com.smartbell.db.SPUtil;

/**
 * Created by Curry on 18/3/17.
 */

public class BellApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SPUtil.sContext = this;
    }
}
