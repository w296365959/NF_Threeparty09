package com.sscf.investment.alipay.observer;

import android.text.TextUtils;

import com.sscf.investment.alipay.bean.PayResult;
import java.util.Map;

/**
 * 支付请求处理回调
 */
public abstract class AlipayPayObserver extends AlipayObserver {

    public abstract void onAlipaySuccess(Map<String, String> result);

    public abstract void onAlipayUnknown(Map<String, String> result);

    public abstract void onAlipayCancel(Map<String, String> result);

    public abstract void onAlipayError(Map<String, String> result);

    @Override
    void onAlipayResult(Map<String, String> resultMap) {
        /**
         对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
         */
        if (resultMap != null) {
            PayResult result = new PayResult(resultMap);
            String resultStatus = result.getResultStatus();
            final String resultStr = result.getResult();
            // 判断resultStatus 为9000则代表支付成功
            if (TextUtils.equals(resultStatus, ALI_SUCCESS_RESULT)) {
                onAlipaySuccess(TextUtils.isEmpty(resultStr) ? null : resultMap);
            } else if(TextUtils.equals(resultStatus, ALI_PAYING_RESULT)
                    || TextUtils.equals(resultStatus, ALI_ERROR_UNKNOWN)){
                onAlipayUnknown(TextUtils.isEmpty(resultStr) ? null : resultMap);
            } else if (TextUtils.equals(resultStatus, ALI_USER_CANCEL)) {
                onAlipayCancel(resultMap);
            } else {
                onAlipayError(TextUtils.isEmpty(resultStr) ? null : resultMap);
            }
        }
    }

    @Override
    public void onError(Throwable e) {
        onAlipayError(null);
    }
}
