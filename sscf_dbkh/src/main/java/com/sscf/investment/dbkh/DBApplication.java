package com.sscf.investment.dbkh;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

/**
 * (Hangzhou) <br/>
 *
 * @author: wzm <br/>
 * @date :  2019/9/3 16:55 <br/>
 * Summary:
 */
public class DBApplication extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
    }
}
