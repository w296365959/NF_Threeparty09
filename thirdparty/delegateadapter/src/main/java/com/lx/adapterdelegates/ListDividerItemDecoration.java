package com.lx.adapterdelegates;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

/**
 * ListDividerItemDecoration is a {@link RecyclerView.ItemDecoration} that can be used as a divider
 * between items of a {@link android.support.v7.widget.LinearLayoutManager}. It supports both {@link #HORIZONTAL} and
 * {@link #VERTICAL} orientations.
 * <p>
 * <pre>
 *     mDividerItemDecoration = new ListDividerItemDecoration(recyclerView.getContext(), mLayoutManager.getOrientation());
 *     recyclerView.addItemDecoration(mDividerItemDecoration);
 * </pre>
 * <h3>更新记录</h3>
 * <p>
 * 1. 依赖接口{@link DividerOperateInterface}获取divider，ViewHolder实现此接口即可拥有divider显示功能
 * 2. 2017/04/25 支持了是否显示最后一条divider的功能，这样免去了每个页面都要控制最后一条显示不显示的逻辑
 * </p>
 */
public class ListDividerItemDecoration extends RecyclerView.ItemDecoration {
    public static final int HORIZONTAL = LinearLayout.HORIZONTAL;
    public static final int VERTICAL = LinearLayout.VERTICAL;

    /**
     * 最后一条数据是否显示divider
     */
    private final boolean lastDividerVisible;

    /**
     * Current orientation. Either {@link #HORIZONTAL} or {@link #VERTICAL}.
     */
    private int mOrientation;

    private final Rect mBounds = new Rect();

    public ListDividerItemDecoration(Context context) {
        this(context, VERTICAL);
    }

    /**
     * Creates a divider {@link RecyclerView.ItemDecoration} that can be used with a
     * {@link android.support.v7.widget.LinearLayoutManager}.
     *
     * @param context     Current context, it will be used to access resources.
     * @param orientation Divider orientation. Should be {@link #HORIZONTAL} or {@link #VERTICAL}.
     */
    public ListDividerItemDecoration(Context context, int orientation) {
        this(context, orientation, false);
    }

    public ListDividerItemDecoration(Context context, int orientation, boolean lastDividerVisible) {
        setOrientation(orientation);
        this.lastDividerVisible = lastDividerVisible;
    }


    /**
     * Sets the orientation for this divider. This should be called if
     * {@link RecyclerView.LayoutManager} changes orientation.
     *
     * @param orientation {@link #HORIZONTAL} or {@link #VERTICAL}
     */
    public void setOrientation(int orientation) {
        if (orientation != HORIZONTAL && orientation != VERTICAL) {
            throw new IllegalArgumentException(
                    "Invalid orientation. It should be either HORIZONTAL or VERTICAL");
        }
        mOrientation = orientation;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        if (parent.getLayoutManager() == null) {
            return;
        }
        if (mOrientation == VERTICAL) {
            drawVertical(c, parent);
        } else {
            drawHorizontal(c, parent);
        }
    }

    @SuppressLint("NewApi")
    private void drawVertical(Canvas canvas, RecyclerView parent) {
        canvas.save();
        final int left;
        final int right;
        if (parent.getClipToPadding()) {
            left = parent.getPaddingLeft();
            right = parent.getWidth() - parent.getPaddingRight();
            canvas.clipRect(left, parent.getPaddingTop(), right,
                    parent.getHeight() - parent.getPaddingBottom());
        } else {
            left = 0;
            right = parent.getWidth();
        }

        // view的数量
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            Drawable dividerByChild = getDividerByChild(parent, child);
            if (dividerByChild != null) {
                parent.getDecoratedBoundsWithMargins(child, mBounds);
                final int bottom = mBounds.bottom + Math.round(ViewCompat.getTranslationY(child));
                final int top = bottom - dividerByChild.getIntrinsicHeight();
                dividerByChild.setBounds(left, top, right, bottom);
                dividerByChild.draw(canvas);
            }
        }
        canvas.restore();
    }

    /**
     * 根据view获取分隔线
     *
     * @param child
     * @return
     */
    private Drawable getDividerByChild(RecyclerView parent, View child) {
        // 数据的数量
        final int itemCount = parent.getAdapter().getItemCount();
        // 找出对应的holder
        RecyclerView.ViewHolder holder = parent.findContainingViewHolder(child);
        // 是不是DividerOperateInterface
        if (holder instanceof DividerOperateInterface) {
            int childAdapterPosition = parent.getChildAdapterPosition(child);
            // 最后一条显示 或者 不是最后一条
            if (lastDividerVisible || childAdapterPosition < itemCount - 1) {
                Drawable divider = ((DividerOperateInterface) holder).getDivider(childAdapterPosition);
                if (divider != null && getNextHolderLetCurrentVisible(parent, child)) {
                    //此处检测下一条holder是否支持上一条holder的divider是否隐藏
                    return divider;
                }
            }
        }
        return null;
    }

    private boolean getNextHolderLetCurrentVisible(RecyclerView parent, View currentChild) {
        int currentPosInLayout = parent.getChildLayoutPosition(currentChild);

        if (currentPosInLayout != RecyclerView.NO_POSITION) {
            RecyclerView.ViewHolder holder = parent.findViewHolderForLayoutPosition(currentPosInLayout + 1);
            if (holder instanceof PreviousDividerInterface) {
                return ((PreviousDividerInterface) holder).previousDividerVisible();
            }
        }
        return true;
    }

    @SuppressLint("NewApi")
    private void drawHorizontal(Canvas canvas, RecyclerView parent) {
        canvas.save();
        final int top;
        final int bottom;
        if (parent.getClipToPadding()) {
            top = parent.getPaddingTop();
            bottom = parent.getHeight() - parent.getPaddingBottom();
            canvas.clipRect(parent.getPaddingLeft(), top,
                    parent.getWidth() - parent.getPaddingRight(), bottom);
        } else {
            top = 0;
            bottom = parent.getHeight();
        }

        // view的数量
        final int childCount = parent.getChildCount();

        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            Drawable dividerByChild = getDividerByChild(parent, child);
            if (dividerByChild != null) {
                parent.getLayoutManager().getDecoratedBoundsWithMargins(child, mBounds);
                final int right = mBounds.right + Math.round(ViewCompat.getTranslationX(child));
                final int left = right - dividerByChild.getIntrinsicWidth();
                dividerByChild.setBounds(left, top, right, bottom);
                dividerByChild.draw(canvas);
            }
        }
        canvas.restore();
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {
        Drawable dividerByChild = getDividerByChild(parent, view);
        if (dividerByChild != null) {
            if (mOrientation == VERTICAL) {
                outRect.set(0, 0, 0, dividerByChild.getIntrinsicHeight());
            } else {
                outRect.set(0, 0, dividerByChild.getIntrinsicWidth(), 0);
            }
        }
    }

    /**
     * 分隔线处理接口，此接口返回不为null的drawable，将会在列中画出来
     */
    public interface DividerOperateInterface {
        Drawable getDivider(int position);
    }

    /**
     * 前面的分隔线接口，实现此接口可以将此条数据前一条的分隔线隐藏
     */
    public interface PreviousDividerInterface {

        /**
         * 上一条数据的对应的界面divider为空
         *
         * @return
         */
        boolean previousDividerVisible();
    }
}
