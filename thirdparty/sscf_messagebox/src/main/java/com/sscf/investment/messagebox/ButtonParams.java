package com.sscf.investment.messagebox;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.sscf.investment.messagebox.bean.MessageButton;
import com.sscf.investment.messagebox.listener.ButtonClickListener;

import java.util.ArrayList;
import java.util.Iterator;

class ButtonParams {

    ArrayList<MessageButton> buttons = new ArrayList<>();

    public boolean hasButtons() {
        return (buttons != null && !buttons.isEmpty());
    }

    /**
     * 新增按钮
     * @param button
     */
    public void addButton(MessageButton button) {
        if (!buttons.contains(button)) {
            buttons.add(button);
        }
    }

    /**
     * 移除按钮
     * @param text
     */
    public void removeButton(String text) {
        Iterator<MessageButton> it = buttons.iterator();
        if (it.hasNext()) {
            MessageButton button = it.next();
            if (text.equals(button.text)) {
                it.remove();
            }
        }
    }

    /**
     * 获取按钮
     * @param position
     * @return
     */
    public MessageButton getButton(int position) {
        if (hasButtons() && position < buttons.size()) {
            return buttons.get(position);
        }
        return null;
    }

    public MessageButton getButton(@NonNull String buttonText) {
        if (hasButtons() && !TextUtils.isEmpty(buttonText)) {
            Iterator<MessageButton> it = buttons.iterator();
            if (it.hasNext()) {
                MessageButton button = it.next();
                if (buttonText.equals(button.text)) {
                    return button;
                }
            }
        }
        return null;
    }

    public MessageButton setButtonClickListener(String buttonText, ButtonClickListener listener) {
        MessageButton button = getButton(buttonText);
        if (button != null) {
            button = button.clickListener(listener);
        }
        return button;
    }

    public void clear() {
        buttons.clear();
        return;
    }

    public int size() {
        return buttons.size();
    }
}
