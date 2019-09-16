package com.lx.adapterdelegates;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

/**
 * @author leixin
 * 将UI刷新的部分操作代理到界面中，使UI能够配置界面
 */
public interface DelegateViewBindFilter<T> {

    void onBindViewHolder(T value, int position, @NonNull RecyclerView.ViewHolder holder);
}
