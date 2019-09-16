package com.sscf.investment.alipay.observer;

import com.sscf.investment.alipay.bean.AuthResult;

import java.util.Map;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * 处理授权结果回调
 */
abstract class AlipayObserver implements Observer<Map<String, String>> {

    // 请求处理成功
    public static final String ALI_SUCCESS_RESULT = "9000";

    // 正在处理中，支付结果未知（有可能已经支付成功），请查询商户订单列表中订单的支付状态
    public static final String ALI_PAYING_RESULT = "8000";

    // 系统异常
    public static final String ALI_ERROR_SERVER = "4000";

    // 用户中途取消
    public static final String ALI_USER_CANCEL = "6001";

    // 网络连接出错
    public static final String ALI_ERROR_NETERROR = "6002";

    // 其他异常
    public static final String ALI_ERROR_UNKNOWN = "6004";

    /**
     *  返回数据回调
     * @param result {@link AuthResult}
     */
    abstract void onAlipayResult(Map<String, String> result);

    @Override
    public void onSubscribe(Disposable d) {

    }

    @Override
    public final void onNext(Map<String, String> stringStringMap) {
        onAlipayResult(stringStringMap);
    }

    @Override
    public final void onComplete() {
    }
}
