package com.lx.adapterdelegates.display;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author leixin 分组信息
 */
public class GroupNode<T> {
    T group;
    List<T> array;

    public List<T> getArray() {
        return array;
    }

    // 当前分组所在的节点
    int index;

    boolean showGroup = true;

    public GroupNode<T> setVisable(boolean visable) {
        showGroup = visable;
        return this;
    }

    private Comparator sort;

    public GroupNode<T> setSort(Comparator sort) {
        this.sort = sort;
        sort();
        return this;
    }

    protected GroupNode<T> sort() {
        if (array != null && sort != null) {
            Collections.sort(array, sort);
        }
        return this;
    }

    public GroupNode(T group){
        this(group, new ArrayList<T>());
    }

    public GroupNode(T group, List<T> array) {
        this.group = group;
        this.array = array;
    }

    public int size() {
        return array.size() + (showGroup ? 1 : 0);
    }

    public int indexOfGroup(T obj) {
        if (obj.equals(group)) {
            return showGroup ? 0 : -1;
        } else {
            return array.indexOf(obj) + (showGroup ? 1 : 0);
        }
    }

    public T getObject(int position) {
        if (position == 0 && showGroup) {
            return group;
        } else {
            return array.get(position - (showGroup ? 1 : 0));
        }
    }

    boolean inThisGroup(int listPosition) {
        return listPosition >= index && listPosition < index + size();
    }

    public void update(T value) {
        int index = array.indexOf(value);
        if (index != -1) {
            array.set(index, value);
        }
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof GroupNode && group.equals(((GroupNode) obj).group);
    }
}
