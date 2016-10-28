package com.telerik.android.data;

public interface CurrentItemChangedListener <E> {
    void currentItemChanged(CurrentItemChangedInfo<E> changeInfo);
}
