package com.sscf.investment.alipay.observer;

import android.text.TextUtils;

import com.sscf.investment.alipay.bean.AuthResult;

import java.util.Map;

/**
 * 授权登陆回调
 */
public abstract class AlipayAuthObserver extends AlipayObserver {

    /**
     * 获取户授权信息成功
     * @param resultStatus
     * @param result
     * @param success
     */
    public abstract void onAuthResult(String resultStatus, AuthResult result, boolean success);

    /**
     * 用户取消操作
     */
    public abstract void onUserCancel();

    @Override
    void onAlipayResult(Map<String, String> result) {
        if (result != null) {
            AuthResult authResult = new AuthResult(result, true);
            String resultStatus = authResult.getResultStatus();
            // 为“200”则代表授权成功，具体状态码代表含义可参考授权接口文档
            if (TextUtils.equals(resultStatus, ALI_SUCCESS_RESULT) && TextUtils.equals(authResult.getResultCode(), "200")) {
                // 获取alipay_open_id，调支付时作为参数extern_token 的value
                // 传入，则支付账户为该授权账户
                onAuthResult(resultStatus, authResult, true);
            } else if (TextUtils.equals(resultStatus, ALI_USER_CANCEL)) {
                onUserCancel();
            } else {
                // 其他状态值则为授权失败
                onAuthResult(resultStatus, authResult, false);
            }
        }
    }

    @Override
    public void onError(Throwable e) {
        onAuthResult(ALI_ERROR_UNKNOWN, null, false);
    }
}
