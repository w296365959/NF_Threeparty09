package com.sscf.investment.messagebox.bean;

import android.support.annotation.Keep;
import android.view.Gravity;

/**
 * 按钮样式
 */
@SuppressWarnings("All")
@Keep
public class ButtonEntity {

    public String value;

    public String color;

    public String bgColor;

    public int width;

    public int height;

    public boolean enable = true;

    public boolean visable = true;

    public int gravity = Gravity.CENTER;

}
