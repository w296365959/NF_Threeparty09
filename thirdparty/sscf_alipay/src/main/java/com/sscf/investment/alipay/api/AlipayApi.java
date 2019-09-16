package com.sscf.investment.alipay.api;

import android.app.Activity;

import com.alipay.sdk.app.AuthTask;
import com.alipay.sdk.app.EnvUtils;
import com.alipay.sdk.app.PayTask;
import com.sscf.investment.alipay.observer.AlipayAuthObserver;
import com.sscf.investment.alipay.observer.AlipayPayObserver;

import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * 支付宝SDK封装
 */
public class AlipayApi {

    private static Map<String, String> authV2(Activity activity, String authInfo) {
        AuthTask authTask = new AuthTask(activity);
        Map<String, String> result = authTask.authV2(authInfo, true);
        return result;
    }

    private static Map<String, String> payV2(Activity activity, String sign) {
        PayTask payTask = new PayTask(activity);
        Map<String, String> result = payTask.payV2(sign, true);
        return result;
    }

    /**
     * 授权登陆
     * @return
     */
    public static void authV2(Activity activity, AlipayAuthObserver observer, String authInfo) {
        Observable.create((ObservableOnSubscribe<Map<String, String>>) subscribe -> {
            subscribe.onNext(authV2(activity, authInfo));
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    /**
     * 调用支付宝支付
     * @param activity
     * @param observer
     * @param sign
     */
    public static void payV2(Activity activity, AlipayPayObserver observer, String sign) {
        Observable.create((ObservableOnSubscribe<Map<String, String>>) subscribe -> {
            subscribe.onNext(payV2(activity, sign));
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    /**
     * 开启沙箱环境
     * @param enable
     */
    public static void setAliSanboxEnable(boolean enable) {
        if(enable) {
            EnvUtils.setEnv(EnvUtils.EnvEnum.SANDBOX);
        }
    }
}
