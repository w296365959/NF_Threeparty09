package com.lx.adapterdelegates;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * @author leixin
 * 封装item底部的divider显示
 */
public class BaseViewHolder extends RecyclerView.ViewHolder {

    public BaseViewHolder(View itemView) {
        super(itemView);
    }

    public boolean showDivider() {
        return false;
    }
}
