package com.sscf.investment.messagebox;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 封装自定义Toast对象, 避免同时弹出多个Toast，可定制图标
 * @author leixin
 */
public class MessageToast {

    private static Toast mToast;

    public static void showToast(Context context, CharSequence info){
        showToast(context, info, -1);
    }

    public static void showToast(Context context, CharSequence info, int imgRes){
        if(TextUtils.isEmpty(info)) {
            return;
        }

        showToast(context, null, info, imgRes);
    }

    /**
     * 自定义View样式Toast
     * @param context Context
     * @param contentHolder IToastContent
     */
    public static void showToast(Context context, IToastContent contentHolder, CharSequence info, int imgRes) {
        if(mToast == null){
            mToast = Toast.makeText(context.getApplicationContext(), "", Toast.LENGTH_SHORT);
        }

        IToastContent holder;
        if(mToast.getView() == null || mToast.getView().getTag() == null || !(mToast.getView().getTag() instanceof ContentHolder)){
            holder = contentHolder;
            if (holder == null) {
                holder = new ContentHolder();
            }

            mToast.setView(holder.createContentView(context));
        } else {
            holder = (ContentHolder) mToast.getView().getTag();
        }

        holder.updateContent(info, imgRes);

        mToast.setGravity(Gravity.BOTTOM, 0, (int) context.getResources().getDimension(R.dimen.messagebox_default_bottom_margin));
        int duration;
        if(info.length() > 40) {
            duration = Toast.LENGTH_LONG;
        }else{
            duration = Toast.LENGTH_SHORT;
        }
        mToast.setDuration(duration);
        try {
            mToast.show();
        } catch (Exception exception) {
            exception.printStackTrace();
        }

    }

    public static View getContentView(Context context){
        return LayoutInflater.from(context).inflate(R.layout.messagebox_toast, null);
    }

    private static class ContentHolder implements IToastContent{
        TextView mbMessage;
        ImageView mbIcon;
        View content;

        @Override
        public View createContentView(Context context) {
            content = getContentView(context);
            mbMessage = content.findViewById(R.id.mbMessage);
            mbIcon = content.findViewById(R.id.mbIcon);
            content.setTag(this);
            return content;
        }

        @Override
        public void updateContent(CharSequence message, int imgRes) {
            if (mbMessage != null) {
                mbMessage.setText(message);
            }
            if (mbIcon != null) {
                if (imgRes > 0) {
                    mbIcon.setImageResource(imgRes);
                    mbIcon.setVisibility(View.VISIBLE);
                } else {
                    mbIcon.setVisibility(View.GONE);
                }
            }
        }
    }
}
