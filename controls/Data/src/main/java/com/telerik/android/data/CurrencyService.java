package com.telerik.android.data;

import java.util.ArrayList;
import java.util.List;

public class CurrencyService <E> {
    private E currentItem;
    private ArrayList<CurrentItemChangedListener<E>> changeListeners = new ArrayList<CurrentItemChangedListener<E>>();
    private List<E> data;

    public CurrencyService(List<E> data) {
        this.data = data;
    }

    public E getCurrentItem() {
        return this.currentItem;
    }

    public boolean isCurrent(E item) {
        return this.currentItem == item;
    }

    public void setCurrentItem(E value) {
        if(this.currentItem == value) {
            return;
        }

        E oldItem = this.currentItem;
        this.currentItem = value;
        this.onCurrentItemChanged(oldItem, value);
    }

    public boolean moveNext() {
        E oldItem = this.currentItem;
        E nextItem = this.getNextItem();
        this.currentItem = nextItem;

        this.onCurrentItemChanged(oldItem, nextItem);
        return false;
    }

    public boolean movePrevious() {
        E oldItem = this.currentItem;
        E previousItem = this.getPreviousItem();
        this.currentItem = previousItem;

        this.onCurrentItemChanged(oldItem, previousItem);
        return false;
    }

    public void moveFirst() {
        E oldItem = this.currentItem;
        E firstItem = this.getFirstItem();
        this.currentItem = firstItem;

        this.onCurrentItemChanged(oldItem, firstItem);
    }

    public void moveLast() {
        E oldItem = this.currentItem;
        E lastItem = this.getLastItem();
        this.currentItem = lastItem;

        this.onCurrentItemChanged(oldItem, lastItem);
    }

    public void addCurrentChangedListener(CurrentItemChangedListener<E> listener) {
        this.changeListeners.add(listener);
    }

    public boolean removeCurrentChangedListener(CurrentItemChangedListener<E> listener) {
        return this.changeListeners.remove(listener);
    }

    private E getLastItem() {
        if(this.data.isEmpty()) {
            return null;
        }

        return this.data.get(this.data.size() - 1);
    }

    private E getFirstItem() {
        if(this.data.isEmpty()) {
            return null;
        }

        return this.data.get(0);
    }

    private E getPreviousItem() {
        int currentIndex = this.data.indexOf(this.currentItem);
        int previousIndex = currentIndex - 1;
        if(previousIndex < 0) {
            return null;
        }

        return this.data.get(previousIndex);
    }

    private E getNextItem() {
        int currentIndex = this.data.indexOf(this.currentItem);
        int nextIndex = currentIndex + 1;
        if(nextIndex == this.data.size()) {
            return null;
        }

        return this.data.get(nextIndex);
    }

    protected void onCurrentItemChanged(E oldItem, E newItem) {
        for(CurrentItemChangedListener<E> listener : this.changeListeners) {
            listener.currentItemChanged(new CurrentItemChangedInfo<E>(oldItem, newItem));
        }
    }
}
