package thirdparty.sscf.com.thirdtest.jsproxy;

import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.sscf.investment.messagebox.MessageBox;
import com.sscf.investment.messagebox.bean.DialogEntity;
import com.sscf.third.jsproxy.annotation.NativeMethod;
import com.sscf.third.jsproxy.impl.INativeMethodCall;
import com.sscf.third.jsproxy.impl.IWebPage;
import com.sscf.third.jsproxy.jscall.JSCall;

/**
 * 本地Method实现类
 */
public class NativeConfig implements INativeMethodCall {

    private IWebPage mPage;

    /**
     * 创建完对象之后，必须立即调用此方法设置页面容器
     * @param webpage
     */
    @Override
    public void setWebpage(IWebPage webpage) {
        this.mPage = webpage;
    }

    // 调用起本地对话框
    public static final int MSG_INVOKE_DIALOG = 1000;

    public static final int MSG_INVOKE_TOAST = 1001;

    /**
     * 显示公共对话框
     * @param message String
     */
    @JavascriptInterface
    @NativeMethod(method=MSG_INVOKE_TOAST, clazz={String.class}, type=NativeMethod.STRING)
    public void showToast(String message) {
        Toast.makeText(mPage.getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @JavascriptInterface
    @NativeMethod(method=MSG_INVOKE_DIALOG, clazz={DialogEntity.class})
    public void showDialog(DialogEntity entity) {
        new MessageBox.Builder(entity, (buttonView, arguments, tag, text, position) -> JSCall.call(mPage.getWebView(), JSConfig.MSG_CLICK_BTN, text, position))
                .onDismissDialogListener((messagebox, bundle, holder, initiativeDiasmiss) -> JSCall.call(mPage.getWebView(), JSConfig.MSG_DIALOG_DISMISS))
                .build("commonDialog")
                .show(mPage.getActivity().getSupportFragmentManager());
    }
}
