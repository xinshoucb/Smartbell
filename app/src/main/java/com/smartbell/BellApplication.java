package com.smartbell;

import android.app.Application;

import io.realm.Realm;

/**
 * Created by Curry on 18/3/17.
 */

public class BellApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
    }
}
