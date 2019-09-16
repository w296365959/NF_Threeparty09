package com.sscf.investment.messagebox.holder;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sscf.investment.messagebox.MessageBox;
import com.sscf.investment.messagebox.R;

/**
 * 构造一个Tips描述对象
 */
public class MessageTipsHolder implements IContentHolder {

    private Context context;
    // 属性值
    private String titleMessage;
    private String subTitleMessage;
    private int iconRes;

    // View值
    private TextView title;
    private TextView subTitle;
    private ImageView titleIcon;

    public MessageTipsHolder(Context context, String titleMessage) {
        this(context, titleMessage, null);
    }

    public MessageTipsHolder(Context context, String titleMessage, String subTitleMessage) {
        this(context, titleMessage, subTitleMessage, -1);
    }

    public MessageTipsHolder(Context context, String titleMessage, String subTitleMessage, int iconRes) {
        this.context = context;
        this.titleMessage = titleMessage;
        this.subTitleMessage = subTitleMessage;
        this.iconRes = iconRes;
    }

    @Override
    public View contentView(MessageBox messageBox, ViewGroup parent) {
        View tipsContent = LayoutInflater.from(context).inflate(R.layout.messagebox_vertical_tips, parent, false);
        title = tipsContent.findViewById(R.id.titleMessage);
        updateTitleMessage(titleMessage);
        subTitle = tipsContent.findViewById(R.id.subMessage);
        updateSubTitleMessage(subTitleMessage);
        titleIcon = tipsContent.findViewById(R.id.titleIcon);
        updateIcon(iconRes);
        return tipsContent;
    }

    /**
     * 更新标题信息
     * @param titleMessage
     */
    public void updateTitleMessage(String titleMessage) {
        this.titleMessage = titleMessage;
        if (!TextUtils.isEmpty(titleMessage)) {
            title.setText(titleMessage);
            title.setVisibility(View.VISIBLE);
        } else {
            title.setVisibility(View.GONE);
        }
    }

    /**
     * 更新附标题信息
     * @param subTitleMessage
     */
    public void updateSubTitleMessage(String subTitleMessage) {
        this.subTitleMessage = subTitleMessage;
        if (!TextUtils.isEmpty(subTitleMessage)) {
            subTitle.setText(subTitleMessage);
            subTitle.setVisibility(View.VISIBLE);
        } else {
            subTitle.setVisibility(View.GONE);
        }
    }

    /**
     * 更新图片Icon
     * @param iconRes
     */
    public void updateIcon(int iconRes) {
        this.iconRes = iconRes;
        if (iconRes != 0 && iconRes != -1) {
            titleIcon.setImageResource(iconRes);
            titleIcon.setVisibility(View.VISIBLE);
        } else {
            titleIcon.setVisibility(View.GONE);
        }
    }
}
