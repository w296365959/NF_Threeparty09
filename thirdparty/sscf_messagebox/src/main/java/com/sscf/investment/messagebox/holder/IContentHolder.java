package com.sscf.investment.messagebox.holder;

import android.view.View;
import android.view.ViewGroup;

import com.sscf.investment.messagebox.MessageBox;

/**
 * 对话框View
 */
public interface IContentHolder {

    View contentView(MessageBox messageBox, ViewGroup parent);
}
