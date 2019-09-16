package com.sscf.investment.messagebox.bean;

import android.support.annotation.Keep;

import java.util.List;

@SuppressWarnings("All")
@Keep
public class DialogEntity {

    public String title;

    public String titleColor;

    public String content;

    public String contentColor;

    public List<ButtonEntity> buttons;

    public String bgColor;

    public MBPadding contentPadding;

    // 按钮栏背景色
    public String bottomBg;

    // 点击对话框外面销毁对话框
    public boolean cancelOnTouchOutside;

}
