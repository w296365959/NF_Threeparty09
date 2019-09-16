package com.lx.adapterdelegates;

import android.view.View;

/**
 * Item点击事件.
 */
public interface ItemClickListener<T>{

    void onItemClicked(View view, T value, int position);
}
