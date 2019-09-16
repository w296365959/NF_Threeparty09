package com.sscf.investment.x5sdk.widget;

import android.content.Context;
import android.net.Uri;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebView;

/**
 * Created by xuebinliu on 2015/8/12.d \]
 */
public abstract class FileChooserWebClient extends WebChromeClient {

    private ValueCallback<Uri[]> mValueCallback;

    public static enum ChooserType {
        IMAGE, VIDE0, OTHER
    }

    protected abstract void onFileChooserShow(Context context, FileChooserParams params, ChooserType type);

    @Override
    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> valueCallback, FileChooserParams fileChooserParams) {
        if (mValueCallback != null) {
            mValueCallback.onReceiveValue(null);
        }
        mValueCallback = valueCallback;

        if (fileChooserParams != null) {
            String [] accept = fileChooserParams.getAcceptTypes();
            for (String a : accept) {
                if (a.contains("image")) {
                    onFileChooserShow(webView.getContext(), fileChooserParams, ChooserType.IMAGE);
                } else if (a.contains("video")) {
                    onFileChooserShow(webView.getContext(), fileChooserParams, ChooserType.VIDE0);
                } else {
                    onFileChooserShow(webView.getContext(), fileChooserParams, ChooserType.OTHER);
                }
            }
        } else {
            onFileChooserShow(webView.getContext(), fileChooserParams, ChooserType.OTHER);
        }
        return true;
    }

    public void onChooserSuccess(Uri uri) {
        if (mValueCallback != null) {
            mValueCallback.onReceiveValue(uri == null ? null : new Uri[] { uri });
            mValueCallback = null;
        }
    }

    public void onChooserCancel() {
        if (mValueCallback != null) {
            mValueCallback.onReceiveValue(null);
            mValueCallback = null;
        }
    }

    public void onChooserError() {
        if (mValueCallback != null) {
            mValueCallback.onReceiveValue(null);
            mValueCallback = null;
        }
    }

    @Override
    public void onReceivedTitle(WebView webView, String s) {
        super.onReceivedTitle(webView, s);
    }
}
