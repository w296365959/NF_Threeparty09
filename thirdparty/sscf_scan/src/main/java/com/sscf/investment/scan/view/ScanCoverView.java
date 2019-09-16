package com.sscf.investment.scan.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.sscf.investment.component.scan.R;
import com.sscf.investment.scan.camera.CameraManager;

/**
 * 根据layout中子View的位置，确定局部透明区域
 * Created by juan on 2018/07/20.
 */
public class ScanCoverView extends FrameLayout {

    private ScanCoverDrawable background;
    private int scanViewId;
    private RectF scanValidRect;

    public ScanCoverView(@NonNull Context context) {
        super(context);
        initView(context, null, 0);
    }

    public ScanCoverView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs, 0);
    }

    public ScanCoverView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs, defStyleAttr);
    }

    private void initView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        TypedArray t = context.obtainStyledAttributes(attrs, R.styleable.ScanCoverView);
        scanViewId = t.getResourceId(R.styleable.ScanCoverView_scanViewId, NO_ID);
        t.recycle();

        background = new ScanCoverDrawable(getBackground());
        ViewCompat.setBackground(this, background);
        scanValidRect = new RectF();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        resetBackgroundHoleArea();
    }

    private void resetBackgroundHoleArea() {
        Path path = null;
        // 以子View为范围构造需要透明显示的区域
        View view = findViewById(scanViewId);
        if (view != null) {
            path = new Path();
            // 矩形透明区域
            scanValidRect.set(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
            path.addRect(scanValidRect, Path.Direction.CW);
        }
        if (path != null) {
            background.setSrcPath(path);
        }
    }

    public RectF getScanValidRectF() {
        return scanValidRect;
    }

    public Rect getScanValidRect() {
        return new Rect((int) scanValidRect.left, (int) scanValidRect.top, (int) scanValidRect.right, (int) scanValidRect.bottom);
    }
}