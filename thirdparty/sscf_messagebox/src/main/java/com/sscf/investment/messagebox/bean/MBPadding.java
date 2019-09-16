package com.sscf.investment.messagebox.bean;

import android.support.annotation.Keep;

@Keep
public class MBPadding {

    public int left;

    public int right;

    public int top;

    public int bottom;

    public MBPadding(int left, int right, int top, int bottom) {
        this.left=left;
        this.right=right;
        this.top=top;
        this.bottom=bottom;
    }

    public int getLeft() {
        return left;
    }

    public void setLeft(int left) {
        this.left=left;
    }

    public int getRight() {
        return right;
    }

    public void setRight(int right) {
        this.right=right;
    }

    public int getTop() {
        return top;
    }

    public void setTop(int top) {
        this.top=top;
    }

    public int getBottom() {
        return bottom;
    }

    public void setBottom(int bottom) {
        this.bottom=bottom;
    }
}
