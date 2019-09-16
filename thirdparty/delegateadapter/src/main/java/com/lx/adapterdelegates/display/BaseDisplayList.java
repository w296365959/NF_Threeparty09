package com.lx.adapterdelegates.display;

import java.util.ArrayList;
import java.util.List;

/**
 * 根据条件进行分组，对数据有分组需求的数据，需要实现此接口
 */
public abstract class BaseDisplayList<T> extends ArrayList<T> {

    /**
     * 将存储List转换为显示List
     * @return
     */
    public abstract void display();

    /**
     * 获取显示位置的元素
     * @param position
     * @return
     */
    public abstract T getByDisplayPosition(int position);

    /**
     * 显示元素的count
     * @return
     */
    public abstract int getDisplayCount();

    /**
     * 获取value在显示列表中的绝对位置
     * @param value
     * @return
     */
    public abstract int displayPosition(T value);

    /**
     * 在after元素后掺入元素, 插入的元素不作为实际有效数据，仅显示时使用
     * @param value
     * @param after
     * @return
     */
    public abstract boolean insertAfter(T value, T after);

    /**
     * 在befer元素前插入, 插入的元素不作为实际有效数据，仅显示时使用
     * @return
     */
    public abstract boolean insertBefor(T value, T befor);

    /**
     * 更新元素
     * @param value
     * @return
     */
    public abstract int update(T value);

    /**
     * insert or update
     * @param value
     * @return
     */
    public abstract int insertOrUpdate(T value);

    /**
     * 移除元素
     * @param value
     * @return
     */
    public abstract int removeDisplay(T value);

    public boolean isEmptyList() {
        return getDisplayCount() == (getHeaderCount() + getFooterCount());
    }

    ArrayList<T> headers = new ArrayList();

    /**
     * 插入header
     * @param header
     */
    public void insertHeader(T header) {
        if (headers != null) {
            headers.add(header);
        }
    }

    /**
     * 更新header数据
     * @param header
     * @return
     */
    public boolean updateHeader(T header) {
        int index = headers.indexOf(header);
        if (index != -1) {
            headers.set(index, header);
            return true;
        }
        return false;
    }

    public void removeHeader(T header) {
        headers.remove(header);
    }

    /**
     * headerCount
     * @return
     */
    public int getHeaderCount() {
        return headers.size();
    }

    /**
     * 判断元素是否为一个header
     * @param header
     * @return
     */
    public int indexOfHeader(T header) {
        return headers.indexOf(header);
    }

    public List<T> getHeaders() {
        return headers;
    }

    ArrayList<T> footers = new ArrayList();

    /**
     * 插入header
     * @param footer
     */
    public void insertFooter(T footer) {
        if (!footers.contains(footer)) {
            footers.add(footer);
        }
    }

    /**
     * 更新header数据
     * @param footer
     * @return
     */
    public boolean updateFooter(T footer) {
        int index = footers.indexOf(footer);
        if (index != -1) {
            footers.set(index, footer);
            return true;
        }
        return false;
    }

    public void removeFooter(T footer) {
        footers.remove(footer);
    }

    /**
     * 判断元素是否为一个header
     * @param footer
     * @return
     */
    public int indexOfFooter(T footer) {
        return footers.indexOf(footer);
    }

    /**
     * headerCount
     * @return
     */
    public int getFooterCount() {
        return footers.size();
    }
}
