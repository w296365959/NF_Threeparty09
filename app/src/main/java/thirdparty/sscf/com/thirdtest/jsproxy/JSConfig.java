package thirdparty.sscf.com.thirdtest.jsproxy;

import com.sscf.third.jsproxy.annotation.JsReq;
import com.sscf.third.jsproxy.impl.IJSMethodCall;

public class JSConfig implements IJSMethodCall {

    @JsReq(method="clickBtn", require={"value", "position"})
    public static final int MSG_CLICK_BTN = 1;

    @JsReq(method="dismiss")
    public static final int MSG_DIALOG_DISMISS = 2;
}
