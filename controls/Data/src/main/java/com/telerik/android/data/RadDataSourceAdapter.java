package com.telerik.android.data;

import android.content.Context;

import java.util.List;

public class RadDataSourceAdapter<E> extends DataSourceAdapterBase<E> {

    public RadDataSourceAdapter(List<E> data, Context context) {
        super(data, context);

    }
}