package com.sscf.third.jsproxy;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;

import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import java.util.Map;

/**
 * 考虑到对腾讯 WEB_SDK的封装处理
 */
@SuppressWarnings("ALL")
public class CommonWebView extends WebView {

    private VelocityTracker mTracker = null;
    private int mMinimumVelocity;
    private OnScrollListener mListener;
    private OnTouchDownListener mOnTouchDownListener;

    public CommonWebView(Context context) {
        this(context, null);
    }

    public CommonWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        final ViewConfiguration configuration = ViewConfiguration.get(context);
        mMinimumVelocity = configuration.getScaledMinimumFlingVelocity() * 3;

        init();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            if (mOnTouchDownListener != null) {
                mOnTouchDownListener.onTouchDown();
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    public void init() {
        //mWebView.addJavascriptInterface(mNativeProxy, NativeProxy.TAG);
        //Android4.4之前的系统会自动注入以下对象，有漏洞，需要remove掉
        try {
            removeJavascriptInterface("searchBoxJavaBridge_");
            removeJavascriptInterface("accessibility");
            removeJavascriptInterface("accessibilityTraversal");
        } catch (Exception e) { // 2.3以下机型，可能会NullPointerException
            e.printStackTrace();
        }

        final WebSettings settings = getSettings();

        //启用支持javascript
        settings.setJavaScriptEnabled(true);

        // 将图片调整到适合webview的大小
        settings.setUseWideViewPort(true);

        // 页面缩放至屏幕的大小
        settings.setLoadWithOverviewMode(true);

        // web storage支持
        settings.setDomStorageEnabled(true);
        settings.setAppCachePath(getContext().getCacheDir().getAbsolutePath());
        settings.setAllowFileAccess(true);
        settings.setAppCacheEnabled(true);
        settings.setUserAgentString(settings.getUserAgentString() + " beacon");
        if (Build.VERSION.SDK_INT >= 21) {
            // 5.0及以上系统，指定可以HTTPS跨域访问HTTP
            settings.setMixedContentMode(android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        // 这一行会引起首次启动webview时卡住几秒
//        setOnLongClickListener(mLongClickListener);
    }

    @Override
    public void destroy() {
        try {
            super.destroy();
        } catch (Throwable e) {
            // java.lang.Throwable: Error: WebView.destroy() called while still attached!
            e.printStackTrace();
        }
        getSettings().setJavaScriptEnabled(false);
        setWebViewClient(null);
        setWebChromeClient(null);
    }

    public void setOnScrollListener(OnScrollListener l) {
        this.mListener = l;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (mListener == null) {
            return super.onInterceptTouchEvent(event);
        }
        int action = event.getAction();
        switch(action){
            case MotionEvent.ACTION_DOWN:
                if(mTracker == null){
                    mTracker = VelocityTracker.obtain();
                }else{
                    mTracker.clear();
                }
                mTracker.addMovement(event);
                break;
            case MotionEvent.ACTION_MOVE:
                mTracker.addMovement(event);
                mTracker.computeCurrentVelocity(1000);
                final int yVelocity = (int) mTracker.getYVelocity();

                if (yVelocity > mMinimumVelocity * 5) {
                    if (mListener != null) {
                        mListener.onScrollDown();
                    }
                } else if (yVelocity < -mMinimumVelocity) {
                    if (mListener != null) {
                        mListener.onScrollUp();
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (mTracker != null) {
                    mTracker.recycle();
                    mTracker = null;
                }
                break;
        }
        return super.onInterceptTouchEvent(event);
    }

    public interface OnScrollListener {
        void onScrollUp();
        void onScrollDown();
    }

    public void setOnTouchDownListener(OnTouchDownListener l) {
        this.mOnTouchDownListener = l;
    }

    public interface OnTouchDownListener {
        void onTouchDown();
    }

    @Override
    public void loadUrl(String s) {
        loadUrl(s, null);
    }

    @Override
    public void loadUrl(String s, Map<String, String> map) {
        try {
            super.loadUrl(s, map);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
