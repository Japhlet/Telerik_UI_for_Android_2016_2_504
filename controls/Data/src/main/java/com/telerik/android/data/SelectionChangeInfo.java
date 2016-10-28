package com.telerik.android.data;

import java.util.ArrayList;
import java.util.List;

public class SelectionChangeInfo <E> {
    private List<E> selected;
    private List<E> deselected;

    public SelectionChangeInfo(List<E> selectedItems, List<E> deselectedItems) {
        this.selected = selectedItems;
        this.deselected = deselectedItems;

        if(this.selected == null) {
            this.selected = new ArrayList<E>();
        }

        if(this.deselected == null) {
            this.deselected = new ArrayList<E>();
        }
    }

    public Iterable<E> selectedItems() {
        return this.selected;
    }

    public Iterable<E> deselectedItems() {
        return this.deselected;
    }
}
