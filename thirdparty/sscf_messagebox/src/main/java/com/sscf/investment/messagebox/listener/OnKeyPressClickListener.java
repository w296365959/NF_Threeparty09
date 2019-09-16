package com.sscf.investment.messagebox.listener;

import android.view.KeyEvent;

import com.sscf.investment.messagebox.MessageBox;

public interface OnKeyPressClickListener {

    void onKeyClick(MessageBox messageBox, int keyCode, KeyEvent event);
}
