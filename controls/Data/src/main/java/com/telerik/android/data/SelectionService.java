package com.telerik.android.data;

import java.util.ArrayList;
import java.util.List;

public class SelectionService<E> {
    private SelectionMode selectionMode = SelectionMode.SINGLE;
    private ArrayList<E> selectedItems = new ArrayList<E>();
    private ArrayList<SelectionChangeListener<E>> changeListeners = new ArrayList<SelectionChangeListener<E>>();

    public SelectionService() {
    }

    public void addSelectionChangeListener(SelectionChangeListener<E> listener) {
        this.changeListeners.add(listener);
    }

    public boolean removeSelectionChangeListener(SelectionChangeListener<E> listener) {
        return this.changeListeners.remove(listener);
    }

    public SelectionMode getSelectionMode() {
        return this.selectionMode;
    }

    public void setSelectionMode(SelectionMode value) {
        if(value == this.selectionMode) {
            return;
        }

        this.selectionMode = value;
    }

    public boolean isItemSelected(E item) {
        return this.selectedItems.contains(item);
    }

    public List<E> selectedItems() {
        return this.selectedItems;
    }

    public E get(int index) {
        return this.selectedItems.get(index);
    }

    public int selectedItemsSize() {
        return this.selectedItems.size();
    }

    public void selectItem(E item) {
        if(this.selectionMode == SelectionMode.NONE) {
            return;
        }

        List<E> deselectedItems = new ArrayList<E>();
        List<E> selectedItems = new ArrayList<E>();

        if(this.selectionMode == SelectionMode.SINGLE) {
            for(E selectedItem : this.selectedItems) {
                deselectedItems.add(selectedItem);
            }

            boolean wasItemSelected = this.isItemSelected(item);
            this.selectedItems.clear();
            if(!wasItemSelected) {
                this.selectedItems.add(item);
                selectedItems.add(item);
            }

        } else {
            if (this.isItemSelected(item)) {
                this.selectedItems.remove(item);
                deselectedItems.add(item);
            } else {
                this.selectedItems.add(item);
                selectedItems.add(item);
            }
        }

        this.onSelectionChanged(selectedItems, deselectedItems);
    }

    public void deselectItem(E item) {
        if(this.selectionMode == SelectionMode.NONE) {
            return;
        }

        if(this.selectedItems.isEmpty()) {
            return;
        }

        if(this.selectedItems.remove(item)) {
            ArrayList<E> deselectedItems = new ArrayList<E>();
            deselectedItems.add(item);
            this.onSelectionChanged(null, deselectedItems);
        }
    }

    public void selectItems(List<E> items) {
        if(items == this.selectedItems) {
            return;
        }

        if(this.selectionMode == SelectionMode.NONE || this.selectionMode == SelectionMode.SINGLE) {
            return;
        }

        for(E item : items) {
            this.selectedItems.add(item);
        }

        this.onSelectionChanged(items, null);
    }

    public void clearSelection() {
        if(this.selectionMode == SelectionMode.NONE) {
            return;
        }

        ArrayList<E> deselectedItems = new ArrayList<E>();
        for(E item : this.selectedItems) {
            deselectedItems.add(item);
        }

        this.selectedItems.clear();

        this.onSelectionChanged(null, deselectedItems);
    }

    protected void onSelectionChanged(List<E> selectedItems, List<E> deselectedItems) {
        for(SelectionChangeListener<E> listener : this.changeListeners) {
            listener.selectionChanged(new SelectionChangeInfo<E>(selectedItems, deselectedItems));
        }
    }
}
