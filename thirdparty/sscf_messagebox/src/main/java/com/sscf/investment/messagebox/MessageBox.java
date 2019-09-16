package com.sscf.investment.messagebox;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sscf.investment.messagebox.bean.ButtonEntity;
import com.sscf.investment.messagebox.bean.DialogEntity;
import com.sscf.investment.messagebox.bean.MBPadding;
import com.sscf.investment.messagebox.bean.MessageButton;
import com.sscf.investment.messagebox.holder.IContentHolder;
import com.sscf.investment.messagebox.listener.ButtonClickListener;
import com.sscf.investment.messagebox.listener.OnContentCreateListener;
import com.sscf.investment.messagebox.listener.OnDismissDialogListener;
import com.sscf.investment.messagebox.listener.OnKeyPressClickListener;

import static android.text.TextUtils.*;

/**
 * 消息类对话框基类.
 */
@SuppressWarnings("All")
public class MessageBox extends DialogFragment{

    private Builder builder;

    private TextView title;

    private LinearLayout titleLine;

    private LinearLayout dlgContent;

    private FrameLayout contentLayout;

    private View dividerline;

    private LinearLayout contentPannel;

    private LinearLayout bottombar;

    private Dialog dialog;

    private boolean hasInvokeDismissed;

    private int slop;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public final void show(FragmentManager manager) {
        if (builder != null) {
            try {
                Fragment fragment=manager.findFragmentByTag(builder.tag);
                if (fragment instanceof MessageBox
                        && fragment.isVisible()
                        && !fragment.isRemoving()
                        && !fragment.isDetached()
                        && !hasInvokeDismissed) {
                    MessageBox messageBox=(MessageBox) fragment;
                    messageBox.updateContent();
                } else {
                    super.show(manager, builder.tag);
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    @Override
    public final void dismiss() {
        try {
            hasInvokeDismissed = true;
            super.dismiss();
            dialog.setOnKeyListener(null);
            dialog = null;
            Log.d("MessageBox", "=======================>>>>> dismiss");
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    protected void initContentView() {
        if (dlgContent != null) {
            View childContent = builder.contentHolder != null ? builder.contentHolder.contentView(this, dlgContent) : null;
            if (childContent != null) {
                dlgContent.removeAllViews();
                if (builder.fullScreen) {
                    dlgContent.addView(childContent,
                            FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
                } else {
                    dlgContent.addView(childContent,
                            FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                }
                dlgContent.setVisibility(View.VISIBLE);
            } else {
                if (!isEmpty(builder.contentMessage)) {
                    TextView boldText = (TextView) dlgContent.findViewById(R.id.boldText);
                    boldText.setText(builder.contentMessage);
                    if (builder.contentGravity != -1) {
                        boldText.setGravity(builder.contentGravity);
                    }
                    if (builder.contentMessageColor != Builder.COLOR_NOSET) {
                        boldText.setTextColor(builder.contentMessageColor);
                    }
                    if (builder.lineSpacingAdd > 0 && builder.lineSpacingmult > 0) {
                        boldText.setLineSpacing(builder.lineSpacingAdd, builder.lineSpacingmult);
                    }
                    dlgContent.setVisibility(View.VISIBLE);
                } else {
                    dlgContent.setVisibility(View.GONE);
                }
            }
            if (builder.contentPadding != null) {
                dlgContent.setPadding(builder.contentPadding.left,
                        builder.contentPadding.top,
                        builder.contentPadding.right,
                        builder.contentPadding.bottom);
            }

            // contentHeight
            if (builder.contentHeight > 0 && contentLayout != null) {
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) contentLayout.getLayoutParams();
                params.height = builder.contentHeight;
                contentLayout.setLayoutParams(params);
            }

            // background
            if (builder.bgContentRes != 0) {
                dlgContent.setBackgroundResource(builder.bgContentRes);
            } else if (builder.bgContentDrawable != null) {
                dlgContent.setBackground(builder.bgContentDrawable);
            } else {
                // 设置顶部显示背景图，支持圆角处理
                boolean titleShow = titleLine.getVisibility() == View.VISIBLE;
                int color=(builder.bgContentColor == Builder.COLOR_NOSET ? builder.bgColor : builder.bgContentColor);
                GradientDrawable drawable=ShapeUtils.rectangle(color, 0);
                if (titleShow) {
                    ShapeUtils.shapeRadius(drawable, 0, 0, builder.radiusBottomLeft, builder.radiusBottomRight);
                } else {
                    ShapeUtils.shapeRadius(drawable, builder.radiusTopLeft, builder.radiusTopRight, builder.radiusBottomLeft, builder.radiusBottomRight);
                }
                dlgContent.setBackground(drawable);
            }

            invokeDialogContentCreateInterface();
        }
    }

    protected void invokeDialogContentCreateInterface() {
        if (builder.onContentCreateListener != null) {
            builder.onContentCreateListener.onContentCreate(builder.contentHolder);
        }
    }

    /**
     * 初始化标题
     */
    protected void initTitle(TextView titleView, View titleLayout) {
        if (!isEmpty(builder.title)) {
            if (titleView != null) {
                titleView.setText(builder.title);
                if (builder.titleColor != Builder.COLOR_NOSET) {
                    titleView.setTextColor(builder.titleColor);
                }

                // titleHeight
                if (builder.titleHeight != 0) {
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) titleLayout.getLayoutParams();
                    params.height = builder.titleHeight;
                    titleLayout.setLayoutParams(params);
                }

                // background
                if (builder.titleBgRes != 0) {
                    titleLayout.setBackgroundResource(builder.titleBgRes);
                } else {
                    // 设置顶部显示背景图，支持圆角处理
                    GradientDrawable drawable = ShapeUtils.rectangle(builder.titleBgColor, 0);
                    ShapeUtils.shapeRadius(drawable, builder.radiusTopLeft, builder.radiusTopRight, 0, 0);
                    titleLayout.setBackground(drawable);
                }
            }
            if (titleLayout != null && builder.showTitle) {
                titleLayout.setVisibility(View.VISIBLE);
            }
        } else {
            if (titleLayout != null) {
                titleLayout.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 更新底部线条显示
     * @param viewParent
     * @param dividerLine
     */
    protected void initBottomDivider(@NonNull LinearLayout viewParent, @NonNull View dividerLine) {
        if (builder.bottomDivider != null && builder.bottomDivider.isVisable() && builder.buttons.hasButtons()) {
            builder.bottomDivider.applyTo(dividerLine, viewParent);
        } else {
            dividerLine.setVisibility(View.GONE);
        }
    }

    protected void initButtonLayout(ViewGroup bottomLayout) {
        boolean showBottom = builder.buttons.hasButtons();
        // 设置底部按钮栏显示隐藏
        bottomLayout.setVisibility(showBottom ? View.VISIBLE : View.GONE);
        bottomLayout.removeAllViews();

        if (showBottom) {
            if (builder.buttonBgDrawable != null) {
                bottomLayout.setBackground(builder.buttonBgDrawable);
            }
            if (builder.buttonBgRes != 0) {
                bottomLayout.setBackgroundResource(builder.buttonBgRes);
            }
            if (builder.buttonBgColor != Builder.COLOR_NOSET) {
                bottomLayout.setBackgroundColor(builder.buttonBgColor);
            }

            // 更新确认按钮显示
            int buttonSize = builder.buttons.size();
            for (int i=0; i<buttonSize; i++) {
                final int position = i;
                final MessageButton msgButton = builder.buttons.getButton(i);
                Button button = msgButton.obtainButton(getContext(), bottomLayout);

                // 有圆角，则设置圆角
                if (builder.radiusBottomLeft != 0 && builder.radiusBottomRight != 0) {
                    if (buttonSize == 1) {
                        button.setBackground(createStateListRadius(msgButton.bgColor, builder.radiusBottomLeft, builder.radiusBottomRight));
                    } else {
                        if (i == 0) {
                            button.setBackground(createStateListRadius(msgButton.bgColor, builder.radiusBottomLeft, 0));
                        } else if (i == buttonSize - 1) {
                            button.setBackground(createStateListRadius(msgButton.bgColor, 0, builder.radiusBottomRight));
                        }
                    }
                }

                bottomLayout.addView(button);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (builder == null) return;

                        if (msgButton.clickListener != null) {
                            msgButton.clickListener.onButtonClick(v, getArguments(), v.getTag(), msgButton.text, position);
                        }
                        if (builder != null && builder.dismissOnButtonClick) {
                            dismiss();
                        }
                    }
                });

                // 增加按钮之间的分割线
                if (builder.buttonDivider != null && builder.buttonDivider.isVisable() && i<(buttonSize - 1)) {
                    bottomLayout.addView(builder.buttonDivider.obtain(getContext(), bottomLayout));
                }
            }
        }
    }

    /**
     * on按钮点击事件
     * @param listener
     */
    public void setButtonClickListener (String buttonText, ButtonClickListener listener) {
        int childCount = bottombar.getChildCount();
        for (int i=0; i< childCount; i++) {
            View childen = bottombar.getChildAt(i);
            if (childen instanceof Button) {
                if (buttonText.equals(((Button) childen).getText())) {
                    final int position = i;
                    childen.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (builder == null) return;

                            MessageButton button = builder.buttons.getButton(buttonText);
                            button = button.clickListener(listener);
                            if (button.clickListener != null) {
                                button.clickListener.onButtonClick(v, getArguments(), v.getTag(), button.text, position);
                            }
                            if (builder.dismissOnButtonClick) {
                                dismiss();
                            }
                        }
                    });
                }
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View parent;
        if (builder.fullScreen) {
            parent = inflater.inflate(R.layout.messagebox2, container, false);
        } else {
            parent = inflater.inflate(R.layout.messagebox, container, false);
        }

        title = (TextView) parent.findViewById(R.id.title_name);
        titleLine = (LinearLayout) parent.findViewById(R.id.titleLayout);

        contentLayout = (FrameLayout) parent.findViewById(R.id.contentLayout);
        dlgContent = (LinearLayout) parent.findViewById(R.id.dialogContent);

        dividerline = parent.findViewById(R.id.bottomDivider);
        contentPannel = (LinearLayout)parent.findViewById(R.id.contentPannel);

        // 底部按钮更新
        bottombar  = (LinearLayout) parent.findViewById(R.id.bottombar);

        try {
            updateContent();
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return parent;
    }

    /**
     * 更新对话框显示信息
     */
    protected void updateContent() {
        if (builder == null)
            return;

        initTitle(title, titleLine);
        initContentView();
        initBottomDivider(contentPannel, dividerline);
        initButtonLayout(bottombar);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        dialog = new Dialog(getActivity(), builder.styleRes == 0 ? this.getTheme() : builder.styleRes);
        //最小识别距离
        slop = ViewConfiguration.get(getContext()).getScaledWindowTouchSlop();

        // request a window without the title
        try {
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            if (builder.animationStyleRes != 0) {
                dialog.getWindow().setWindowAnimations(builder.animationStyleRes);
            } else {
                dialog.getWindow().setWindowAnimations(R.style.MessageBox_Animations_Dialog_Fade_Offset);
            }
            if (builder.dimAmount >= 0 && builder.dimAmount <= 1) {
                dialog.getWindow().setDimAmount(builder.dimAmount);
            }

            dialog.setCanceledOnTouchOutside(builder.cancelOnTouchOutside);
            // 屏蔽按键操作
            if (!builder.cancelOnTouchOutside) {
                dialog.setOnKeyListener((dialog1, keyCode, event) -> {
                    if (builder.onKeyPressClickListener != null) {
                        builder.onKeyPressClickListener.onKeyClick(MessageBox.this, keyCode, event);
                    }
                    if (keyCode == KeyEvent.KEYCODE_SEARCH || keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_MENU) {
                        return true;
                    } else {
                        return false;
                    }
                });
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null && builder != null) {
            // 是否调节对话框固定宽度
            FragmentActivity activity = getActivity();
            if (builder.autoFitWindowWidth && activity != null) {
                DisplayMetrics display = getScreenDisplay(activity);
                int width = display.widthPixels;
                Window window = dialog.getWindow();
                try {
                    window.getDecorView().setPadding(0, 0, 0, 0);
                    window.setLayout((int) (width * builder.fitWindowWidthRate), ViewGroup.LayoutParams.WRAP_CONTENT);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }

            // 设置整体背景色
            if (builder.bgColor != Builder.COLOR_NOSET) {
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(builder.bgColor));
            }

            if (builder.bgRes != 0) {
                dialog.getWindow().setBackgroundDrawableResource(builder.bgRes);
            }

            if (builder.bgDrawable != null) {
                dialog.getWindow().setBackgroundDrawable(builder.bgDrawable);
            }

//            enableDialogTranslucentStatus(dialog.getWindow(), builder.statusBarColor);

            if (builder.dialogGravity != -1) {
                dialog.getWindow().setGravity(builder.dialogGravity);
            }
        }
    }

    private StateListDrawable createStateListRadius(int color, float leftRadius, float rightRadius) {
        // 左右均设置圆角
        StateListDrawable listDrawable = new StateListDrawable();
        GradientDrawable drawablePressed = ShapeUtils.rectangle(color, 0);
        ShapeUtils.shapeRadius(drawablePressed, 0, 0 , leftRadius, rightRadius);
        drawablePressed.setAlpha(59);
        GradientDrawable drawable = ShapeUtils.rectangle(color, 0);
        ShapeUtils.shapeRadius(drawable, 0, 0 , leftRadius, rightRadius);
        listDrawable.addState(new int[] { android.R.attr.state_pressed, android.R.attr.state_enabled }, drawablePressed);
        listDrawable.addState(new int[] {}, drawable);
        return listDrawable;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        try {
            super.onDismiss(dialog);
            contentPannel = null;
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        if (builder != null) {
            if (builder.onDismissDialogListener != null) {
                builder.onDismissDialogListener.onDismiss(MessageBox.this, getArguments(), builder.contentHolder, hasInvokeDismissed);
            }
        }
        builder = null;
        dialog = null;
    }

    private DisplayMetrics getScreenDisplay(FragmentActivity activity) {
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm;
    }

    private boolean isOutOfBounds(MotionEvent event) {
        //相对弹窗左上角的x坐标
        final int x = (int) event.getX();
        //相对弹窗左上角的y坐标
        final int y = (int) event.getY();
        if (dialog != null) {
            final View decorView=dialog.getWindow().getDecorView();//弹窗的根View
            return (x < -slop) || (y < -slop) || (x > (decorView.getWidth() + slop))
                    || (y > (decorView.getHeight() + slop));
        } else {
            return false;
        }
    }

    public static final Builder builder() {
        return new Builder();
    }

    public static class Builder {

        public static final int COLOR_NOSET = -1;

        private int bgColor = Color.WHITE;

        private boolean fullScreen;

        private int bgRes;

        private Drawable bgDrawable;

        private String title;

        private int titleHeight;

        private boolean showTitle;

        private int titleColor = Color.BLACK;

        private int titleBgRes;

        private int titleBgColor = COLOR_NOSET;

        // 对话框展示内容部分
        private IContentHolder contentHolder;

        /**
         *  消息内容，如果 {@link #contentHolder}不为空，则生效，否则将忽略，使用自定义Content
         */
        private CharSequence contentMessage;

        // 设置行间距
        private float lineSpacingAdd, lineSpacingmult;


        private int contentMessageColor = -1;

        private int contentGravity = -1;

        private int contentHeight;

        private int bgContentColor = -1;

        private int bgContentRes;

        private Drawable bgContentDrawable;

        // 内容间距
        private MBPadding contentPadding;

        // 内容底部分割线是否显示
        private DividerLine bottomDivider = new DividerLine();

        // 按钮之间的分割线，配置后，会在按钮之间增加分割线
        private DividerLine buttonDivider = new DividerLine(DividerLine.VERTICAL);

        // 存储点击按钮
        private ButtonParams buttons = new ButtonParams();

        // 点击按钮后，自动dismissDialog
        private boolean dismissOnButtonClick = true;

        // 是否自动根据Window宽度适配对话框宽度
        private boolean autoFitWindowWidth = false;

        // 配置自动适配Window宽度的宽度比
        private float fitWindowWidthRate = 0.9f;

        private MessageButton okButton = new MessageButton();

        private MessageButton cancelButton = new MessageButton();

        private int buttonBgRes;

        private int buttonBgColor = COLOR_NOSET;

        private Drawable buttonBgDrawable;

        // 点击对话框区域外，dismiss对话框
        private boolean cancelOnTouchOutside = true;

        // Content内容被创建回调
        private OnContentCreateListener onContentCreateListener;

        // Dismiss监听
        private OnDismissDialogListener onDismissDialogListener;

        // 物理按键点击事件
        private OnKeyPressClickListener onKeyPressClickListener;

        // 配置动画效果
        private int animationStyleRes;

        // 配置Style
        private int styleRes;

        // window tag
        private String tag;

        //dialog window dimAmount
        private float dimAmount = 0.6f;

        // 对话框在屏幕对齐位置
        private int dialogGravity = -1;

        // 圆角配置
        private float radiusTopLeft, radiusTopRight, radiusBottomLeft, radiusBottomRight;

        private int statusBarColor = Color.TRANSPARENT;

        public Builder() {}

        /**
         * 通过DialogEntity实体创建对象
         * @param dialogEntity DialogEntity
         */
        public Builder(DialogEntity dialogEntity, ButtonClickListener listener) {
            if (!TextUtils.isEmpty(dialogEntity.title)) {
                title(title);
            }
            if (!TextUtils.isEmpty(dialogEntity.titleColor)) {
                titleColor(Color.parseColor(dialogEntity.titleColor));
            }
            if (!TextUtils.isEmpty(dialogEntity.content)) {
                contentMessage(dialogEntity.content);
            }
            if (!TextUtils.isEmpty(dialogEntity.contentColor)) {
                contentMessageColor(Color.parseColor(dialogEntity.contentColor));
            }
            if (dialogEntity.buttons != null && !dialogEntity.buttons.isEmpty()) {
                for (ButtonEntity entity : dialogEntity.buttons) {
                    MessageButton button = new MessageButton(entity.value,
                            TextUtils.isEmpty(entity.color) ? -1 : Color.parseColor(entity.color));
                    button.enable = entity.enable;
                    button.visable = entity.visable;
                    button.gravity = entity.gravity;
                    if (listener != null) {
                        button.clickListener = listener;
                    }
                    addButton(button);
                }
            }
            if (!TextUtils.isEmpty(dialogEntity.bgColor)) {
                bgColor(Color.parseColor(dialogEntity.bgColor));
            }
            if (dialogEntity.contentPadding != null) {
                contentPadding(dialogEntity.contentPadding);
            }
            if (!TextUtils.isEmpty(dialogEntity.bottomBg)) {
                bottomBgColor(Color.parseColor(dialogEntity.bottomBg));
            }
            cancelOnTouchOutside(dialogEntity.cancelOnTouchOutside);
        }

        /**
         * 是否全屏Dialog
         * @param fullScreen
         * @return
         */
        public Builder fullScreen(boolean fullScreen) {
            this.fullScreen = fullScreen;
            return this;
        }

        public Builder bgColor(int color) {
            bgColor = color;
            return this;
        }

        public Builder bgRes(int res) {
            bgRes = res;
            return this;
        }

        public Builder bgDrawable(Drawable drawable) {
            bgDrawable = drawable;
            return this;
        }

        public Builder title(String text) {
            title = text;
            return this;
        }

        public Builder titleHeight(int height) {
            titleHeight = height;
            return this;
        }

        public Builder showTitle(boolean show) {
            showTitle = show;
            return this;
        }

        public Builder titleColor(int color) {
            titleColor = color;
            return this;
        }

        public Builder titleBgRes(int res) {
            titleBgRes = res;
            return this;
        }

        public Builder titleBgColor(int color) {
            titleBgColor = color;
            return this;
        }

        public Builder contentHolder(IContentHolder holder) {
            contentHolder = holder;
            return this;
        }

        public Builder contentMessage(CharSequence message) {
            contentMessage = message;
            return this;
        }

        public Builder contentMessage(CharSequence message, float spacingAdd, float spacingmult) {
            contentMessage = message;
            lineSpacingAdd = spacingAdd;
            lineSpacingmult = spacingmult;
            return this;
        }

        public Builder contentMessageColor(int messageColor) {
            contentMessageColor = messageColor;
            return this;
        }

        public Builder contentGravity(int gravity) {
            this.contentGravity = gravity;
            return this;
        }

        public Builder contentHeight(int contentHeight) {
            this.contentHeight = contentHeight;
            return this;
        }

        public Builder bgContentColor(int color) {
            this.bgContentColor = color;
            return this;
        }

        public Builder bgContentRes(int res) {
            this.bgContentRes = res;
            return this;
        }

        public Builder bgContentDrawable(Drawable drawable) {
            this.bgContentDrawable = drawable;
            return this;
        }

        public Builder contentPadding(MBPadding padding) {
            this.contentPadding = padding;
            return this;
        }

        public Builder showBottomDivider(boolean show) {
            bottomDivider = bottomDivider.visable(show);
            return this;
        }

        /**
         * @param height
         * @return
         */
        public Builder bottomDividerHeight(int height) {
            bottomDivider = bottomDivider.width(height);
            return this;
        }

        public Builder bottomDividerMargin(int margin) {
            bottomDivider = bottomDivider.margin(margin);
            return this;
        }

        public Builder showButtonDivider(boolean show) {
            buttonDivider = buttonDivider.visable(show);
            return this;
        }

        public Builder buttonDividerHeight(int height) {
            buttonDivider = buttonDivider.width(height);
            return this;
        }

        public Builder buttonDividerMargin(int margin) {
            buttonDivider = buttonDivider.margin(margin);
            return this;
        }

        public Builder Ok(CharSequence text) {
            okButton = okButton.text(text);
            return this;
        }

        public Builder Cancel(CharSequence text) {
            cancelButton = cancelButton.text(text);
            return this;
        }

        public Builder okTextColorRes(int res) {
            if (okButton != null) {
                okButton = okButton.colorRes(res);
            }
            return this;
        }

        public Builder okTextColor(int color) {
            if (okButton != null) {
                okButton = okButton.color(color);
            }
            return this;
        }

        public Builder cancelTextColorRes(int res) {
            if (cancelButton != null) {
                cancelButton = cancelButton.colorRes(res);
            }
            return this;
        }

        public Builder cancelTextColor(int color) {
            if (cancelButton != null) {
                cancelButton = cancelButton.color(color);
            }
            return this;
        }

        public Builder dismissOnButtonClick(boolean dismiss) {
            dismissOnButtonClick = dismiss;
            return this;
        }

        public Builder animationStyleRes(int style) {
            animationStyleRes = style;
            return this;
        }

        public Builder autoFitWindowWidth(boolean autoFit) {
            autoFitWindowWidth = autoFit;
            return this;
        }

        public Builder autoFitWindowWidth(float rate) {
            fitWindowWidthRate = rate;
            return this;
        }

        public Builder bottomBgRes(int res) {
            buttonBgRes = res;
            return this;
        }

        public Builder bottomBgColor(int color) {
            buttonBgColor = color;
            return this;
        }

        public Builder bottomBgDrawable(Drawable drawable) {
            buttonBgDrawable = drawable;
            return this;
        }

        public Builder cancelOnTouchOutside(boolean cancel) {
            cancelOnTouchOutside = cancel;
            return this;
        }

        public Builder okClickListener(ButtonClickListener listener) {
            okButton = okButton.clickListener(listener);
            return this;
        }

        public Builder cancelClickListener(ButtonClickListener listener) {
            cancelButton = cancelButton.clickListener(listener);
            return this;
        }

        public Builder onContentCreateListener(OnContentCreateListener listener) {
            onContentCreateListener = listener;
            return this;
        }

        public Builder onDismissDialogListener(OnDismissDialogListener listener) {
            onDismissDialogListener = listener;
            return this;
        }

        public Builder onKeyPressClickListener(OnKeyPressClickListener keyPressClickListener) {
            onKeyPressClickListener = keyPressClickListener;
            return this;
        }

        public Builder addButton(MessageButton button) {
            buttons.addButton(button);
            return this;
        }

        public Builder removeButton(String text) {
            buttons.removeButton(text);
            return this;
        }

        public Builder dimAmount(float dimAmount){
            this.dimAmount = dimAmount;
            return this;
        }

        public Builder styleRes(int styleRes) {
            this.styleRes = styleRes;
            return this;
        }

        public Builder dialogGravity(int gravity) {
            this.dialogGravity = gravity;
            return this;
        }

        public Builder radius(float topleft, float topRight, float bottomLeft, float bottomRight) {
            this.radiusTopLeft = topleft;
            this.radiusTopRight = topRight;
            this.radiusBottomLeft = bottomLeft;
            this.radiusBottomRight = bottomRight;
            return this;
        }

        public Builder statusBarColor(int color) {
            this.statusBarColor = color;
            return this;
        }

        public MessageBox build(@NonNull String tag) {
            return build(tag, null);
        }

        public MessageBox build(@NonNull String tag, FragmentManager manager) {
            this.tag = tag;

            if (cancelButton.isValid()) {
                buttons.addButton(cancelButton);
            }
            if (okButton.isValid()) {
                buttons.addButton(okButton);
            }

            if (manager != null) {
                Fragment fragment = manager.findFragmentByTag(tag);
                if (fragment instanceof MessageBox
                        && fragment.isVisible()
                        && !fragment.isDetached()) {
                    MessageBox messageBox = (MessageBox) fragment;
                    messageBox.builder = this;
                    return messageBox;
                }
            }

            MessageBox messageBox = new MessageBox();
            messageBox.builder = this;
            return messageBox;
        }
    }
}
