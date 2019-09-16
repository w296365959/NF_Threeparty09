package com.sscf.investment.messagebox.bean;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.sscf.investment.messagebox.R;
import com.sscf.investment.messagebox.listener.ButtonClickListener;

/**
 * 对话框按钮配置
 */
@SuppressWarnings("All")
@Keep
public class MessageButton {

    private final int COLOR_NONE = -1;

    public @NonNull CharSequence text;

    public int color = -1;

    public int colorRes = -1;

    public int bgColor = Color.WHITE;

    public int width;

    public int height;

    public boolean enable = true;

    public boolean visable = true;

    public int gravity = Gravity.CENTER;

    public ButtonClickListener clickListener;

    public MessageButton() {
        text = "";
    }

    public MessageButton(@NonNull CharSequence text) {
        this(text, -1);
    }

    public MessageButton (@NonNull CharSequence text, int color) {
        this(text, color, null);
    }

    public MessageButton (@NonNull CharSequence text, int color, ButtonClickListener listener) {
        this.text = text;
        this.color = color;
        this.clickListener = listener;
    }

    public MessageButton (@NonNull CharSequence text, int color, int bgColor, ButtonClickListener listener) {
        this.text = text;
        this.bgColor = bgColor;
        this.color = color;
        this.clickListener = listener;
    }

    public MessageButton text(@NonNull CharSequence text) {
        this.text = text;
        return this;
    }

    public MessageButton color(int color) {
        this.color = color;
        return this;
    }

    public MessageButton colorRes(int colorRes) {
        this.colorRes = colorRes;
        return this;
    }

    public MessageButton bgColor(int bgColor) {
        this.bgColor = bgColor;
        return this;
    }

    public MessageButton width(int width) {
        this.width = width;
        return this;
    }

    public MessageButton height(int height) {
        this.height = height;
        return this;
    }

    public MessageButton clickListener(ButtonClickListener listener) {
        this.clickListener = listener;
        return this;
    }

    public MessageButton gravity(int gravity) {
        this.gravity = gravity;
        return this;
    }

    public MessageButton enable(boolean enable) {
        this.enable = enable;
        return this;
    }

    public MessageButton visable(boolean visable) {
        this.visable = visable;
        return this;
    }

    public Button obtainButton(Context context, ViewGroup parent) {
        return obtainWhiteButton(context, parent);
    }

    /**
     * 生成一个button
     * @param context
     * @return
     */
    public Button obtainWhiteButton(Context context, ViewGroup parent) {
        Button button = (Button) LayoutInflater.from(context).inflate(R.layout.messagebox_dialog_button_white, parent, false);
        updateButtonAppearance(context, button);
        return button;
    }

    public Button obtainBlackButton(Context context, ViewGroup parent) {
        Button button = (Button) LayoutInflater.from(context).inflate(R.layout.messagebox_dialog_button_black, parent, false);
        updateButtonAppearance(context, button);
        return button;
    }

    public void updateButtonAppearance(Context context, Button button) {
        Resources resources = context.getResources();

        if (!TextUtils.isEmpty(text)) {
            button.setText(text);
        }

        if (bgColor != COLOR_NONE) {
            button.setBackgroundColor(bgColor);
        }

        if (colorRes != COLOR_NONE) {
            button.setTextColor(resources.getColor(colorRes));
        }

        if (color != COLOR_NONE) {
            button.setTextColor(color);
        }

        if (width > 0 && height > 0) {
            if (button.getLayoutParams() != null) {
                button.getLayoutParams().width = width;
                button.getLayoutParams().height = height;
            } else {
                ViewGroup.MarginLayoutParams params=new ViewGroup.MarginLayoutParams(width, height);
                button.setLayoutParams(params);
            }
        }

        button.setEnabled(enable);

        button.setVisibility(visable ? View.VISIBLE : View.GONE);

        button.setGravity(gravity);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return obj instanceof MessageButton && text.equals(((MessageButton) obj).text);
    }

    public boolean isValid() {
        return !TextUtils.isEmpty(text);
    }
}
