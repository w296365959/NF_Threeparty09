package com.lx.adapterdelegates.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Space;

import com.lx.adapterdelegates.AdapterDelegatesManager;

import java.util.List;

/**
 * @author leixin
 */
public abstract class DelegationAdapter<T> extends RecyclerView.Adapter {

    protected AdapterDelegatesManager<T> delegatesManager;
    protected T items;

    public DelegationAdapter() {
        this(new AdapterDelegatesManager<T>());
    }

    protected DelegationAdapter(@NonNull AdapterDelegatesManager<T> delegatesManager) {
        if (delegatesManager == null) {
            throw new NullPointerException("AdapterDelegatesManager is null");
        }

        this.delegatesManager = delegatesManager;
    }

    @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return delegatesManager.onCreateViewHolder(parent, viewType);
    }

    @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        delegatesManager.onBindViewHolder(items, position, holder, null);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, List payloads) {
        delegatesManager.onBindViewHolder(items, position, holder, payloads);
    }

    @Override public int getItemViewType(int position) {
        return delegatesManager.getItemViewType(items, position);
    }

    @Override public void onViewRecycled(RecyclerView.ViewHolder holder) {
        delegatesManager.onViewRecycled(holder);
    }

    @Override public boolean onFailedToRecycleView(RecyclerView.ViewHolder holder) {
        return delegatesManager.onFailedToRecycleView(holder);
    }

    @Override public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        delegatesManager.onViewAttachedToWindow(holder);
    }

    @Override public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        delegatesManager.onViewDetachedFromWindow(holder);
    }

    /**
     * Get the items / data source of this adapter
     * @return The items / data source
     */
    protected T getSource() {
        return items;
    }

    /**
     * Set the items / data source of this adapter
     * @param items The items / data source
     */
    protected void setSoure(T items) {
        this.items = items;
    }

    @Override
    public int getItemCount() {
        return 0;
    }
}