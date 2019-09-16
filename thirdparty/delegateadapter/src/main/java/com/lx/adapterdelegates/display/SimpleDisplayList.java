package com.lx.adapterdelegates.display;

/**
 *  @author leixin
 */
public class SimpleDisplayList<T> extends BaseDisplayList<T> {

    @Override
    public void display() {
    }

    @Override
    public T getByDisplayPosition(int position) {
        int size = size();
        int headerSize = headers.size();
        if (position < headerSize) {
            return headers.get(position);
        } else if (position >= headerSize && position < size + headerSize) {
            return get(position-headerSize);
        } else if (position >= (size + headerSize) && position < (size + headerSize) + footers.size()){
            return footers.get(position-headerSize-size);
        }
        return null;
    }

    @Override
    public int getDisplayCount() {
        return headers.size() + footers.size() + size();
    }

    @Override
    public int displayPosition(T value) {
        int position = indexOfHeader(value);
        if (position != -1) {
            return position;
        }
        position = indexOf(value);
        if (position != -1) {
            return getHeaderCount() + position;
        }
        position = footers.indexOf(value);
        if (position != -1) {
            return getHeaderCount() + size() + position;
        }
        return -1;
    }

    @Override
    public boolean insertAfter(T value, T after) {
        int index = indexOf(after);
        if (index != -1) {
            add(index + 1, value);
            return true;
        }
        return false;
    }

    @Override
    public boolean insertBefor(T value, T befor) {
        int index = indexOf(befor);
        if (index != -1) {
            add(index, value);
            return true;
        }
        return false;
    }

    @Override
    public int update(T value) {
        int index = displayPosition(value);
        if (index != -1) {
            int listIndex = index - getHeaderCount();
            set(listIndex, value);
            return index;
        }
        return -1;
    }

    @Override
    public int insertOrUpdate(T value) {
        int index = indexOf(value);
        if (index != -1) {
            set(index, value);
        } else {
            add(0, value);
        }
        return index;
    }

    @Override
    public int removeDisplay(T value) {
        int index = displayPosition(value);
        if (index != -1) {
            int listIndex = index - getHeaderCount();
            remove(listIndex);
            return listIndex;
        }
        return index;
    }
}
