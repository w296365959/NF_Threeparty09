package com.sscf.third.jsproxy.impl;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.tencent.smtt.sdk.WebView;

/**
 * WebPage
 */
public interface IWebPage {

    FragmentManager fragmentManager();

    WebView getWebView();

    AppCompatActivity getActivity();
}
