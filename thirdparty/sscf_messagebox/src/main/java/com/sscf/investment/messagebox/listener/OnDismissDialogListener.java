package com.sscf.investment.messagebox.listener;

import android.os.Bundle;

import com.sscf.investment.messagebox.MessageBox;
import com.sscf.investment.messagebox.holder.IContentHolder;

public interface OnDismissDialogListener {

    /**
     * 对话框销毁回调
     * @param messagebox MessageBox
     * @param bundle Bundle
     * @param holder IContentHolder
     * @param initiativeDiasmiss boolean 是否主动销毁对话框
     */
    void onDismiss(MessageBox messagebox, Bundle bundle, IContentHolder holder, boolean initiativeDiasmiss);
}
