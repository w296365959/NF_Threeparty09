package com.lx.adapterdelegates;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * 缺省的 AdapterDelegate
 * Created on 2018/10/8 下午3:00
 * leo linxiaotao1993@vip.qq.com
 */
public final class EmptyAdapterDelegate extends AdapterDelegate {

    @Override
    protected boolean isForViewType(@NonNull Object items, int position) {
        return false;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent) {
        View emptyView = new View(parent.getContext());
        emptyView.setLayoutParams(
                new ViewGroup.LayoutParams(0, 0)
        );
        return new ViewHolder(emptyView);
    }

    @Override
    protected void onBindViewHolder(@NonNull Object items, int position, @NonNull RecyclerView.ViewHolder holder, @NonNull List payloads) {
        // 抓取异常数据

    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
