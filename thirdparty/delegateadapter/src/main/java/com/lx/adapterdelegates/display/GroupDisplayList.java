package com.lx.adapterdelegates.display;

import android.support.annotation.CallSuper;

import java.util.ArrayList;

/**
 * @author leixin
 * 按照分组显示List
 */
public abstract class GroupDisplayList<T> extends BaseDisplayList<T> {

    private static final int POSITION_NONE = -1;

    /**
     * 根据value信息返回Group信息
     * @param value
     * @param position
     * @return
     */
    public abstract GroupNode forGroup(T value, int position);

    private ArrayList groupDisplay = new ArrayList();

    /**
     * 缓存当前所有显示元素，避免在获取DisplayPosition的时候重复计算位置
     */
    private ArrayList<T> groupDisplayItems = new ArrayList();

    @Override
    public void display() {
        clearDisplay();
        int size = size();
        for (int i = 0; i < size; i++) {
            T currentValue = get(i);
            GroupNode group = forGroup(currentValue, i);
            if (group != null) {
                int indexofGroup = groupDisplay.indexOf(group);
                if (indexofGroup == -1) {
                    group.array.add(currentValue);
                    // 插入一个新分组
                    inserGroupDisplay(group, i);
                } else {
                    group = (GroupNode) groupDisplay.get(indexofGroup);
                    group.array.add(currentValue);
                    groupDisplay.set(indexofGroup, group);
                }
            } else {
                groupDisplay.add(currentValue);
            }
        }
        analyCurssor();
    }

    private boolean isGroupNode(Object node) {
        return node != null && node.getClass().getSimpleName().equals(GroupNode.class.getSimpleName());
    }

    @Override
    public int size() {
        return super.size();
    }

    int displayGroupItemCount;
    /**
     * 分析重置GroupNode中的size
     */
    private void analyCurssor() {
        displayGroupItemCount = 0;
        groupDisplayItems.clear();
        int groupDisplaySize = groupDisplay.size();
        for (int i = 0; i < groupDisplaySize; i++) {
            Object node = groupDisplay.get(i);
            if (isGroupNode(node)) {
                GroupNode<T> groupNode = (GroupNode) node;
                groupNode.sort();
                // 更新size标记
                groupNode.index = displayGroupItemCount;
                if (groupNode.showGroup) {
                    groupDisplayItems.add(groupNode.group);
                }
                groupDisplayItems.addAll(groupNode.array);
                displayGroupItemCount = displayGroupItemCount + groupNode.size();
            } else {
                displayGroupItemCount++;
                groupDisplayItems.add((T)groupDisplay.get(i));
            }
        }
    }

    /**
     * 将一组数据插入显示列表
     *
     * @param groupNode
     * @param displayposition
     */
    @CallSuper
    protected boolean inserGroupDisplay(GroupNode<T> groupNode, int displayposition) {
        boolean add = groupDisplay.add(groupNode);
        return add;
    }

    @Override
    public T getByDisplayPosition(int position) {
        int size = groupDisplayItems.size();
        int headerSize = headers.size();
        if (position < headerSize) {
            return headers.get(position);
        } else if (position >= headerSize && position < size + headerSize) {
            int listPosition = position - headerSize;
            return groupDisplayItems.get(listPosition);
        } else if (position >= position && position < size + headerSize + footers.size()) {
            return footers.get(position);
        }
        return null;
    }

    @Override
    public int getDisplayCount() {
        return groupDisplayItems.size() + headers.size() + footers.size();
    }

    @Override
    public int displayPosition(T value) {
        if (headers.contains(value)) {
            return headers.indexOf(value);
        }
        else if (groupDisplayItems.contains(value)) {
            return groupDisplayItems.indexOf(value) + headers.size();
        }
        else if (footers.contains(value)){
            return headers.size() + groupDisplayItems.size() + footers.indexOf(value);
        }
        return POSITION_NONE;
    }

    /**
     * 获取value的Group信息
     * @param value
     * @return
     */
    public GroupNode getValueGroup(T value) {
        GroupNode<T> node = getGroupValue(value);
        return node;
    }

    /**
     * 获取group节点信息
     *
     * @param group
     * @return
     */
    private GroupNode getGroupNode(GroupNode group) {
        for (Object object : groupDisplay) {
            if (object instanceof GroupNode) {
                if (object.equals(group)) {
                    return (GroupNode) object;
                }
            }
        }
        return null;
    }

    private GroupNode getGroupValue(T value) {
        for (Object object : groupDisplay) {
            if (object instanceof GroupNode) {
                if (((GroupNode) object).array.contains(value)) {
                    return (GroupNode) object;
                }
            }
        }
        return null;
    }

    @Override
    public boolean insertAfter(T value, T after) {
        int index = indexOf(after);
        if (index != -1) {
            add(index + 1, value);
            display();
            return true;
        }
        return false;
    }

    @Override
    public boolean insertBefor(T value, T befor) {
        int index = indexOf(befor);
        if (index != -1) {
            add(index, value);
            display();
            return true;
        }
        return false;
    }

    @Override
    public int update(T value) {
        int index = indexOf(value);
        if (index != -1) {
            set(index, value);
        }
        display();
        return displayPosition(value);
    }

    @Override
    public int insertOrUpdate(T value) {
        int index = indexOf(value);
        if (index != -1) {
            set(index, value);
            display();
        } else {
            int displayIndex = groupDisplay.indexOf(value);
            if (displayIndex != -1) {
                groupDisplay.set(displayIndex, value);
            } else {
                groupDisplay.add(0, value);
            }
            analyCurssor();
        }
        return displayPosition(value);
    }

    @Override
    public int removeDisplay(T value) {
        remove(value);
        display();
        analyCurssor();
        return displayPosition(value);
    }

    /**
     * 插入一个元素
     * @param value
     * @return
     */
    public int insert(int index, T value) {
        groupDisplay.add(index, value);
        analyCurssor();
        return displayPosition(value);
    }

    public void insertWithoutAsync(T value) {
        groupDisplay.add(value);
    }

    public int insert(T value) {
        add(value);
        display();
        return displayPosition(value);
    }

    /**
     * 清除页面显示
     */
    public void clearDisplay() {
        for (Object object : groupDisplay) {
            if (object instanceof GroupNode) {
                ((GroupNode) object).array.clear();
            }
        }
        groupDisplay.clear();
    }
}
