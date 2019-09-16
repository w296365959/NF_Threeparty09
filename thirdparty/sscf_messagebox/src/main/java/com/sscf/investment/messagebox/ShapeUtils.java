package com.sscf.investment.messagebox;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;

@SuppressWarnings("ALL")
public class ShapeUtils {

    /**
     * 任意渐变色填充形状配置
     * @param shape {@link GradientDrawable#RECTANGLE} {@link GradientDrawable#OVAL} {@link GradientDrawable#RING}
     * @param colors 渐变色
     * @param gradient {@link GradientDrawable#LINEAR_GRADIENT}，{@link GradientDrawable#RADIAL_GRADIENT} ，{@link GradientDrawable#SWEEP_GRADIENT}
     * @param radius 圆角
     * @return GradientDrawable
     */
    public static GradientDrawable shape(int shape, int gradient, int[] colors, float radius) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(shape);
        shape(drawable, gradient, colors, radius);
        return drawable;
    }

    /**
     * 为形状添加渐变
     * @param colors 渐变色
     * @param gradient {@link GradientDrawable#LINEAR_GRADIENT}，{@link GradientDrawable#RADIAL_GRADIENT} ，{@link GradientDrawable#SWEEP_GRADIENT}
     * @param radius 圆角
     * @return GradientDrawable
     */
    public static void shape(GradientDrawable drawable, int gradient, int[] colors, float radius) {
        if (radius >= 0) {
            drawable.setCornerRadius(radius);
        }
        if (gradient != -1 &&
                (gradient == GradientDrawable.LINEAR_GRADIENT || gradient == GradientDrawable.RADIAL_GRADIENT || gradient == GradientDrawable.SWEEP_GRADIENT)) {
            drawable.setGradientType(gradient);
        }
        if (colors != null && colors.length > 0) {
            drawable.setColors(colors);
        }
    }

    /**
     * 设置Drawable圆角
     * @param drawable GradientDrawable
     * @param topLeft float
     * @param topRight float
     * @param bottomLeft float
     * @param bottomRight float
     */
    public static void shapeRadius(GradientDrawable drawable, float topLeft, float topRight, float bottomLeft, float bottomRight) {
        // 1、2两个参数表示左上角，3、4表示右上角，5、6表示右下角，7、8表示左下角
        drawable.setCornerRadii(new float[] {topLeft, topLeft, topRight, topRight, bottomRight, bottomRight, bottomLeft, bottomLeft});
    }

    /**
     * 任意渐变色填充形状配置
     * @param shape {@link GradientDrawable#RECTANGLE} {@link GradientDrawable#OVAL} {@link GradientDrawable#RING}
     * @param color 填充色
     * @param radius {@link GradientDrawable#LINEAR_GRADIENT}，{@link GradientDrawable#RADIAL_GRADIENT} ，{@link GradientDrawable#SWEEP_GRADIENT}
     * @return GradientDrawable
     */
    public static GradientDrawable shape(int shape, int color, float radius) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(shape);
        if (radius >= 0) {
            drawable.setCornerRadius(radius);
        }
        drawable.setColor(color);
        return drawable;
    }

    /**
     * 创建一个圆角矩形
     * @param color
     * @param radius
     * @return
     */
    public static GradientDrawable rectangle(int color, float radius) {
        return shape(GradientDrawable.RECTANGLE, color, radius);
    }

    /**
     * 矩形 圆角 边框 颜色填充
     * @param color
     * @param radius
     * @param stockWidth
     * @param stockColor
     * @return
     */
    public static GradientDrawable rectangle(int color, float radius, int stockWidth, int stockColor) {
        GradientDrawable drawable = shape(GradientDrawable.RECTANGLE, color, radius);
        drawable.setStroke(stockWidth, stockColor, 0, 0);
        return drawable;
    }

    /**
     * 创建一个矩形背景
     * @param color 填充色
     * @param radius 圆角
     * @param stockWidth 线宽
     * @param stockColor 线条颜色
     * @param dashWidth 虚线宽
     * @param dashGap 虚线间隔
     * @return  GradientDrawable
     */
    public static GradientDrawable rectangle(int color, float radius, int stockWidth, int stockColor, float dashWidth, float dashGap) {
        GradientDrawable drawable = shape(GradientDrawable.RECTANGLE, color, radius);
        drawable.setStroke(stockWidth, stockColor, dashWidth, dashGap);
        return drawable;
    }

    /**
     * 矩形 圆角 边框 颜色填充
     * @param color
     * @param radius
     * @param stockWidth
     * @param stockColor
     * @return
     */
    public static GradientDrawable oval(int color, int stockWidth, int stockColor) {
        GradientDrawable drawable = shape(GradientDrawable.OVAL, color, -1);
        drawable.setStroke(stockWidth, stockColor, 0, 0);
        return drawable;
    }

    /**
     * 创建一个圆形背景
     * @param color 填充色
     * @param radius 圆角
     * @param stockWidth 线宽
     * @param stockColor 线条颜色
     * @param dashWidth 虚线宽
     * @param dashGap 虚线间隔
     * @return  GradientDrawable
     */
    public static GradientDrawable oval(int color, int stockWidth, int stockColor, float dashWidth, float dashGap) {
        GradientDrawable drawable = shape(GradientDrawable.OVAL, color, -1);
        drawable.setStroke(stockWidth, stockColor, dashWidth, dashGap);
        return drawable;
    }


    /**
     * 创建StateList
     * @param normalDraw
     * @param pressedDraw
     * @return
     */
    public static StateListDrawable selector(Drawable normalDraw, Drawable pressedDraw) {
        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(new int[]{android.R.attr.state_pressed}, pressedDraw);
        stateListDrawable.addState(new int[]{}, normalDraw);
        return stateListDrawable;
    }

    /**
     * 创建StateList
     * @param normalDraw
     * @param pressedDraw
     * @return
     */
    public static StateListDrawable selector(Drawable normalDraw, Drawable pressedDraw, Drawable selectorDraw) {
        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(new int[]{android.R.attr.state_pressed}, pressedDraw);
        stateListDrawable.addState(new int[]{android.R.attr.state_selected}, selectorDraw);
        stateListDrawable.addState(new int[]{}, normalDraw);
        return stateListDrawable;
    }
}
