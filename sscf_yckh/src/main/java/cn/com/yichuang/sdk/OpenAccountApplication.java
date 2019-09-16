package cn.com.yichuang.sdk;

import android.app.Application;
import android.content.Context;

import com.android.thinkive.framework.ThinkiveInitializer;
import com.qihoo360.replugin.RePlugin;
import com.tencent.smtt.sdk.QbSdk;

/**
 * @author zhangfeng
 * @date 2019/3/13  15:01.
 * QQ: 623351511
 * Copyright (c) https://www.firstcapital.com.cn/ All rights reserved.
 */
public class OpenAccountApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        setTheme(R.style.Theme_AppCompat);

        Context context = getApplicationContext();
        if (null != context) {
            ThinkiveInitializer.getInstance().initialze(context);
            initX5();
        }
    }

    //初始化X5内核
    private void initX5() {
        QbSdk.PreInitCallback callback = new QbSdk.PreInitCallback() {
            @Override
            public void onCoreInitFinished() {

            }

            @Override
            public void onViewInitFinished(boolean b) {
            }
        };
        QbSdk.initX5Environment(getApplicationContext(), callback);
    }

    @Override
    public Context getApplicationContext() {
        try {
            Context context=RePlugin.getHostContext().getApplicationContext();
            if (context == null) {
                context=getApplicationContext();
            }
            return context;
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return super.getApplicationContext();
    }
}
