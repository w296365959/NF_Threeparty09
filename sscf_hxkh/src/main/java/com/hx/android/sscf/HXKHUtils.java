package com.hx.android.sscf;


import android.content.Context;
import android.content.Intent;

import com.hx.android.ui.OpenMainActivity;

public class HXKHUtils {

    /**
     * 启动华西证券SDK
     * @param context Context
     * @param url String
     */
    public static void startKH(Context context, String url) {
        Intent intent = new Intent(context, OpenMainActivity.class);
        //设置开户url
        intent.putExtra("url", url);
        context.startActivity(intent);
    }
}
