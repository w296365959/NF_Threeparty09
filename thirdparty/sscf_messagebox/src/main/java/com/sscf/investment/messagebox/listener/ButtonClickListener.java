package com.sscf.investment.messagebox.listener;

import android.os.Bundle;
import android.view.View;

/**
 * 按钮点击事件处理
 */
public interface ButtonClickListener {

    /**
     * 按钮点击事件回调
     * @param buttonView
     * @param tag
     * @param text
     */
    void onButtonClick(View buttonView, Bundle arguments, Object tag, CharSequence text, int position);
}
