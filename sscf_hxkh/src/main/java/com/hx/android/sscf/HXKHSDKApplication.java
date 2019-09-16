package com.hx.android.sscf;

import com.android.thinkive.framework.ThinkiveInitializer;
import com.hx.android.application.OpenAcApplication;

public class HXKHSDKApplication extends OpenAcApplication {

    @Override
    public void onCreate() {
        super.onCreate();

        // 华西证券开户SDK初始化
        ThinkiveInitializer.getInstance().initialze(this);
    }
}
