package com.sscf.investment.messagebox;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;

import com.sscf.investment.messagebox.bean.MBPadding;

/**
 * 公共引用参数，后续根据需求补充
 */
@SuppressWarnings("ALL")
public class Reference {

    /**
     * 动效处理
     */
    public static class Animation {

        public static final int Animation_Dialog_Center = R.style.MessageBox_Animations_Dialog_Center;

        public static final int Animation_Dialog_Bottom = R.style.MessageBox_Animations_Dialog_Bottom;

        public static final int Animation_Dialog_Top = R.style.MessageBox_Animations_Dialog_Top;

        public static final int Animation_Dialog_Fade = R.style.MessageBox_Animations_Dialog_Fade;

        public static final int Animation_Dialog_Fade_Offset = R.style.MessageBox_Animations_Dialog_Fade_Offset;

        public static final int Animation_Dialog_Bounce = R.style.MessageBox_Animations_Dialog_Bounce;

    }

    /**
     * 默认底部弹窗样式
     */
    public static MessageBox.Builder BOTTOM_DIALOG = new MessageBox.Builder()
            .bgColor(Color.TRANSPARENT)
            .bgContentColor(Color.TRANSPARENT)
            .autoFitWindowWidth(true)
            .autoFitWindowWidth(1f)
            .contentPadding(new MBPadding(0, 0, 0 , 0))
            .dialogGravity(Gravity.BOTTOM)
            .animationStyleRes(Animation.Animation_Dialog_Bottom);

    /**
     * 默认全屏样式弹窗
     */
    public static MessageBox.Builder FULL_SCREEN_DIALOG = new MessageBox.Builder()
            .bgColor(Color.TRANSPARENT)
            .bgContentColor(Color.TRANSPARENT)
            .fullScreen(true)
            .styleRes(R.style.MessageBox_Full_Screen);

    /**
     * 创建一个圆角矩形
     * @param color
     * @param radius
     * @return
     */
    public static GradientDrawable shapeDrawable(int color, float radius) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setCornerRadius(radius);
        drawable.setColor(color);
        return drawable;
    }

    /**
     * 创建一个drawable背景
     * @param color 填充色
     * @param radius 圆角
     * @param stockWidth 线宽
     * @param stockColor 线条颜色
     * @param dashWidth 虚线宽
     * @param dashGap 虚线间隔
     * @return  GradientDrawable
     */
    public static GradientDrawable shapeDrawable(int color, float radius, int stockWidth, int stockColor, float dashWidth, float dashGap) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setCornerRadius(radius);
        drawable.setColor(color);
        drawable.setStroke(stockWidth, stockColor, dashWidth, dashGap);
        return drawable;
    }
}
