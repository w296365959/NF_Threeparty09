package com.sscf.investment.messagebox.holder;

import android.content.Context;

public class ContentCreater {

    /**
     * 创建无图标提醒框
     * @param context
     * @param titleMessage
     * @param subTitleMessage
     * @return
     */
    public static IContentHolder createTips(Context context, String titleMessage, String subTitleMessage) {
        return createTips(context, titleMessage, subTitleMessage, -1);
    }

    /**
     * 创建有图标提醒框
     * @param context
     * @param titleMessage
     * @param subTitleMessage
     * @param iconRes
     * @return
     */
    public static IContentHolder createTips(Context context, String titleMessage, String subTitleMessage, int iconRes) {
        return new MessageTipsHolder(context, titleMessage, subTitleMessage, iconRes);
    }


}
