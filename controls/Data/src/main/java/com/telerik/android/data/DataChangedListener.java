package com.telerik.android.data;

public interface DataChangedListener<E> {
    void dataChanged(DataChangeInfo<E> info);
}
