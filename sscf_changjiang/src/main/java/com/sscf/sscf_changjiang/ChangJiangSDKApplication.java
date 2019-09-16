package com.sscf.sscf_changjiang;

import android.app.Application;

import com.android.thinkive.framework.ThinkiveInitializer;

public class ChangJiangSDKApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ThinkiveInitializer.getInstance().initialze(getApplicationContext());
    }
}
