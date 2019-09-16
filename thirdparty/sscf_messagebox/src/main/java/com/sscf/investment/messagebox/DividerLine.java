package com.sscf.investment.messagebox;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * 分割线
 */
public class DividerLine {

    public static final int VERTICAL = 1;

    public static final int HORIZONTAL = 2;

    // 线条方向
    public int orientation;

    // 线条间隔
    public int margin;

    // 线条宽度
    public int width;

    private boolean visable = true;

    public DividerLine() {
        this(HORIZONTAL);
    }

    public DividerLine(int orientation) {
        this(orientation, 0, 1);
    }

    public DividerLine(int orientation, int margin) {
        this(orientation, margin, 1);
    }

    public DividerLine(int orientation, int margin, int width) {
        this.orientation = orientation;
        this.margin = margin;
        this.width = width;
    }

    public DividerLine orientation(int orientation) {
        this.orientation = orientation;
        return this;
    }

    public DividerLine margin(int margin) {
        this.margin = margin;
        return this;
    }

    public DividerLine width(int width) {
        this.width = width;
        return this;
    }

    public DividerLine visable(boolean visable) {
        this.visable = visable;
        return this;
    }

    public boolean isVisable() {
        return visable;
    }

    public View obtain(Context context, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.messagebox_dialog_divider, parent, false);
        applyTo(view, parent);
        return view;
    }

    /**
     * 将属性应用于View
     * @param view
     */
    public void applyTo(@NonNull View view, ViewGroup parent) {
        LinearLayout.LayoutParams params;
        if (orientation == VERTICAL) {
            params = new LinearLayout.LayoutParams(width, LinearLayout.LayoutParams.MATCH_PARENT);
            params.topMargin = margin;
            params.bottomMargin = margin;
            view.setLayoutParams(params);
        } else {
            params=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, width);
            params.leftMargin = margin;
            params.rightMargin = margin;
            view.setLayoutParams(params);
        }
    }

    public static View obtainDivider(Context context, ViewGroup parent) {
        DividerLine dividerLine = new DividerLine(VERTICAL);
        return dividerLine.obtain(context, parent);
    }

    public static View obtainDivider(Context context, ViewGroup parent, int orientation) {
        DividerLine dividerLine = new DividerLine(orientation);
        return dividerLine.obtain(context, parent);
    }

    public static View obtainDivider(Context context, ViewGroup parent, int orientation, int width) {
        DividerLine dividerLine = new DividerLine(orientation, width);
        return dividerLine.obtain(context, parent);
    }

    public static View obtainDivider(Context context, ViewGroup parent, int orientation, int width, int margin) {
        DividerLine dividerLine = new DividerLine(orientation, width, margin);
        return dividerLine.obtain(context, parent);
    }
}
